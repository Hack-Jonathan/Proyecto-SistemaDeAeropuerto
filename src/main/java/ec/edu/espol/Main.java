package ec.edu.espol;
import java.io.File;
import java.util.List;
import java.util.Optional;

import ec.edu.espol.graph.RedArea;
import ec.edu.espol.model.Aeropuerto;
import ec.edu.espol.model.CodigoIATA;
import ec.edu.espol.model.Coordenadas;
import ec.edu.espol.model.Ubicacion;
import ec.edu.espol.model.Vuelo;
import ec.edu.espol.persistence.PersistenciaRed;
import ec.edu.espol.service.BuscadorRutas;
import ec.edu.espol.service.EstadisticaRed;
import ec.edu.espol.ui.DatosIniciales;
import ec.edu.espol.ui.MapaRenderer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application{
    private static final String ARCHIVO_DATOS = System.getProperty("user.home") + File.separator + "datosAeropuertos.dat";

    private double canvasWidth;
    private double canvasHeight;

    private RedArea red;
    private PersistenciaRed persistencia;
    private MapaRenderer renderer;
    private BuscadorRutas buscadorRutas;
    private EstadisticaRed estadisticas;

    @Override
    public void start(Stage stage) {
        // Inicializar servicios
        persistencia = new PersistenciaRed(ARCHIVO_DATOS);
        renderer = new MapaRenderer();

        red = persistencia.cargar();
        if (red == null) {
            red = new RedArea();
            DatosIniciales.cargar(red);
            persistencia.guardar(red);
        }

        buscadorRutas = new BuscadorRutas(red);
        estadisticas = new EstadisticaRed(red);

        // Obtener tamaño de pantalla
        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        canvasWidth = screenBounds.getWidth();
        canvasHeight = screenBounds.getHeight() - 40; // margen para barra de título

        // UI
        Image mapa = new Image(getClass().getResourceAsStream("/mapa.png"));
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Pane root = new Pane();
        root.getChildren().add(canvas);

        // Botones
        Button btnAgregar = crearBoton("Agregar Aeropuerto", 20);
        Button btnReiniciar = crearBoton("Reiniciar Aeropuertos", 60);
        Button btnEliminar = crearBoton("Eliminar Aeropuerto", 100);
        Button btnCrearVuelo = crearBoton("Crear Vuelo", 140);
        Button btnVerVuelos = crearBoton("Ver vuelos de Aeropuerto", 180);
        Button btnBuscarRuta = crearBoton("Buscar Ruta", 220);
        Button btnRegresar = crearBoton("Regresar", 260);
        Button btnEliminarVuelo = crearBoton("Eliminar Vuelo", 300);
        Button btnEstadisticas = crearBoton("Estadísticas", 340);
        btnRegresar.setVisible(false);

        root.getChildren().addAll(btnAgregar, btnReiniciar, btnEliminar, btnCrearVuelo,
                btnVerVuelos, btnBuscarRuta, btnRegresar, btnEliminarVuelo, btnEstadisticas);

        // Dibujar mapa inicial
        renderer.redibujarMapa(gc, mapa, canvasWidth, canvasHeight, red);

        // ==================== Handlers ====================

        btnAgregar.setOnAction(e -> accionAgregarAeropuerto(canvas, gc, mapa));
        btnEliminar.setOnAction(e -> accionEliminarAeropuerto(gc, mapa));
        btnCrearVuelo.setOnAction(e -> accionCrearVuelo(canvas, gc, mapa));
        btnEliminarVuelo.setOnAction(e -> accionEliminarVuelo(canvas, gc, mapa));
        btnVerVuelos.setOnAction(e -> accionVerVuelos());
        btnBuscarRuta.setOnAction(e -> accionBuscarRuta(gc, mapa, btnRegresar));
        btnRegresar.setOnAction(e -> {
            renderer.redibujarMapa(gc, mapa, canvasWidth, canvasHeight, red);
            btnRegresar.setVisible(false);
        });
        btnReiniciar.setOnAction(e -> {
            red = new RedArea();
            DatosIniciales.cargar(red);
            persistencia.guardar(red);
            buscadorRutas = new BuscadorRutas(red);
            estadisticas = new EstadisticaRed(red);
            renderer.redibujarMapa(gc, mapa, canvasWidth, canvasHeight, red);
        });
        btnEstadisticas.setOnAction(e -> accionEstadisticas());
        stage.setOnCloseRequest(e -> persistencia.guardar(red));

        // Mostrar ventana
        Scene scene = new Scene(root, canvasWidth, canvasHeight);
        stage.setTitle("Sistema de Gestión de Aeropuertos");
        stage.setScene(scene);
        stage.show();
    }

    // ==================== Acciones ====================

    private void accionAgregarAeropuerto(Canvas canvas, GraphicsContext gc, Image mapa) {
        String codigo = pedirTexto("Agregar Aeropuerto", "Ingrese el código del aeropuerto:");
        if (codigo == null) return;
        codigo = codigo.trim().toUpperCase();

        CodigoIATA codigoIATA;
        try {
            codigoIATA = new CodigoIATA(codigo);
        } catch (IllegalArgumentException ex) {
            mostrarError("El código IATA debe tener exactamente 3 caracteres.");
            return;
        }

        if (red.existeAeropuerto(codigoIATA)) {
            mostrarError("Ya existe un aeropuerto con ese código.");
            return;
        }

        mostrarInfo("Haz clic en el mapa para seleccionar la ubicación del aeropuerto:");
        final CodigoIATA codigoFinal = codigoIATA;

        canvas.setOnMouseClicked((MouseEvent ev) -> {
            double x = ev.getX();
            double y = ev.getY();

            // Verificar que no sea agua
            PixelReader pixelReader = mapa.getPixelReader();
            double escalaX = mapa.getWidth() / canvas.getWidth();
            double escalaY = mapa.getHeight() / canvas.getHeight();
            Color colorPixel = pixelReader.getColor((int)(x * escalaX), (int)(y * escalaY));

            if (colorPixel.getBlue() < 0.7 && colorPixel.getGreen() < 0.5 && colorPixel.getRed() < 0.4) {
                mostrarError("No se puede colocar un aeropuerto en el agua.");
                return;
            }

            double lon = (x / canvasWidth) * 360.0 - 180.0;
            double lat = 90.0 - (y / canvasHeight) * 180.0;

            String nombre = pedirTexto("Nombre", "Ingrese el nombre del aeropuerto:");
            if (nombre == null) return;
            String ciudad = pedirTexto("Ciudad", "Ingrese la ciudad:");
            if (ciudad == null) return;
            String pais = pedirTexto("País", "Ingrese el país:");
            if (pais == null) return;

            red.agregarAeropuerto(new Aeropuerto(codigoFinal, nombre.trim(),
                    new Ubicacion(ciudad.trim(), pais.trim()), new Coordenadas(lat, lon)));
            persistencia.guardar(red);
            renderer.redibujarMapa(gc, mapa, canvasWidth, canvasHeight, red);
            canvas.setOnMouseClicked(null);
        });
    }

    private void accionEliminarAeropuerto(GraphicsContext gc, Image mapa) {
        String codigo = pedirTexto("Eliminar Aeropuerto", "Ingrese el código del aeropuerto:");
        if (codigo == null) return;

        CodigoIATA codigoIATA;
        try {
            codigoIATA = new CodigoIATA(codigo.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            mostrarError("Código IATA inválido.");
            return;
        }

        if (red.eliminarAeropuerto(codigoIATA)) {
            persistencia.guardar(red);
            renderer.redibujarMapa(gc, mapa, canvasWidth, canvasHeight, red);
        } else {
            mostrarError("No existe un aeropuerto con ese código.");
        }
    }

    private void accionCrearVuelo(Canvas canvas, GraphicsContext gc, Image mapa) {
        mostrarInfo("Haga clic en el aeropuerto de ORIGEN");
        final Aeropuerto[] seleccion = new Aeropuerto[2]; // [0]=origen, [1]=destino

        canvas.setOnMouseClicked((MouseEvent ev) -> {
            Aeropuerto clickeado = renderer.buscarAeropuertoEnClick(ev.getX(), ev.getY(), canvasWidth, canvasHeight, red);
            if (clickeado == null) {
                mostrarError("No se hizo clic cerca de ningún aeropuerto.");
                return;
            }

            if (seleccion[0] == null) {
                seleccion[0] = clickeado;
                mostrarInfo("Origen: " + clickeado.getCodigo().getValor() + ". Ahora haga clic en el DESTINO.");
            } else {
                seleccion[1] = clickeado;
                if (seleccion[0].equals(seleccion[1])) {
                    mostrarError("Origen y destino no pueden ser el mismo.");
                    seleccion[0] = null;
                    seleccion[1] = null;
                    return;
                }

                try {
                    int minutos = Integer.parseInt(pedirTexto("Duración", "Duración en minutos:"));
                    double precio = Double.parseDouble(pedirTexto("Precio", "Precio en dólares:"));
                    String aerolinea = pedirTexto("Aerolínea", "Nombre de la aerolínea:");

                    if (red.agregarVuelo(seleccion[0].getCodigo(), seleccion[1].getCodigo(), minutos, precio, aerolinea)) {
                        persistencia.guardar(red);
                        renderer.redibujarMapa(gc, mapa, canvasWidth, canvasHeight, red);
                        mostrarInfo("Vuelo creado exitosamente.");
                    } else {
                        mostrarError("No se pudo crear el vuelo.");
                    }
                } catch (Exception ex) {
                    mostrarError("Datos inválidos.");
                }
                canvas.setOnMouseClicked(null);
            }
        });
    }

    private void accionEliminarVuelo(Canvas canvas, GraphicsContext gc, Image mapa) {
        mostrarInfo("Haga clic en el aeropuerto de ORIGEN");
        final Aeropuerto[] seleccion = new Aeropuerto[2];

        canvas.setOnMouseClicked((MouseEvent ev) -> {
            Aeropuerto clickeado = renderer.buscarAeropuertoEnClick(ev.getX(), ev.getY(), canvasWidth, canvasHeight, red);
            if (clickeado == null) {
                mostrarError("No se hizo clic cerca de ningún aeropuerto.");
                return;
            }

            if (seleccion[0] == null) {
                seleccion[0] = clickeado;
                mostrarInfo("Origen: " + clickeado.getCodigo().getValor() + ". Ahora haga clic en el DESTINO.");
            } else {
                seleccion[1] = clickeado;
                if (seleccion[0].equals(seleccion[1])) {
                    mostrarError("Origen y destino no pueden ser el mismo.");
                    seleccion[0] = null;
                    seleccion[1] = null;
                    return;
                }

                if (red.eliminarVuelo(seleccion[0].getCodigo(), seleccion[1].getCodigo())) {
                    persistencia.guardar(red);
                    renderer.redibujarMapa(gc, mapa, canvasWidth, canvasHeight, red);
                    mostrarInfo("Vuelo eliminado exitosamente.");
                } else {
                    mostrarError("No existe un vuelo entre esos aeropuertos.");
                }
                canvas.setOnMouseClicked(null);
            }
        });
    }

    private void accionVerVuelos() {
        String codigo = pedirTexto("Ver Vuelos", "Ingrese el código del aeropuerto:");
        if (codigo == null) return;

        CodigoIATA codigoIATA;
        try {
            codigoIATA = new CodigoIATA(codigo.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            mostrarError("Código IATA inválido.");
            return;
        }

        List<Vuelo> vuelos = red.obtenerVuelosDesde(codigoIATA);
        if (vuelos == null || vuelos.isEmpty()) {
            mostrarInfo("No hay vuelos disponibles desde este aeropuerto.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Vuelo v : vuelos) {
            sb.append(v.toString()).append(System.lineSeparator());
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION, sb.toString(), ButtonType.OK);
        alert.setTitle("Vuelos desde " + codigo.toUpperCase());
        alert.showAndWait();
    }

    private void accionBuscarRuta(GraphicsContext gc, Image mapa, Button btnRegresar) {
        String origen = pedirTexto("Buscar Ruta", "Código del aeropuerto de origen:");
        if (origen == null) return;
        String destino = pedirTexto("Buscar Ruta", "Código del aeropuerto destino:");
        if (destino == null) return;

        List<List<Vuelo>> rutas = buscadorRutas.buscarRutasAlternativas(origen.trim().toUpperCase(), destino.trim().toUpperCase());
        if (rutas.isEmpty()) {
            mostrarError("No existe ruta entre estos aeropuertos.");
            return;
        }

        // Encontrar la ruta más corta
        List<Vuelo> rutaMasCorta = rutas.get(0);
        for (List<Vuelo> ruta : rutas) {
            if (ruta.size() < rutaMasCorta.size()) {
                rutaMasCorta = ruta;
            }
        }

        renderer.dibujarRutas(gc, mapa, canvasWidth, canvasHeight, red, rutas, rutaMasCorta);
        btnRegresar.setVisible(true);
    }

    private void accionEstadisticas() {
        List<Aeropuerto> aeropuertos = red.obtenerAeropuertos();
        if (aeropuertos.isEmpty()) {
            mostrarInfo("No hay aeropuertos en la red.");
            return;
        }

        Aeropuerto max = estadisticas.aeropuertoMasConectado();
        Aeropuerto min = estadisticas.aeropuertoMenosConectado();

        StringBuilder sb = new StringBuilder();
        for (Aeropuerto a : aeropuertos) {
            int n = estadisticas.conexionesDeAeropuerto(a.getCodigo().getValor());
            sb.append(a.getCodigo().getValor()).append(" (").append(a.getNombre()).append("): ")
            .append(n).append(n == 1 ? " vuelo" : " vuelos").append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        if (max != null) sb.append("MÁS vuelos: ").append(max.getCodigo().getValor()).append(System.lineSeparator());
        if (min != null) sb.append("MENOS vuelos: ").append(min.getCodigo().getValor());

        Alert alert = new Alert(Alert.AlertType.INFORMATION, sb.toString(), ButtonType.OK);
        alert.setTitle("Estadísticas");
        alert.showAndWait();
    }

    // ==================== Utilidades de UI ====================

    private Button crearBoton(String texto, double y) {
        Button btn = new Button(texto);
        btn.setLayoutX(20);
        btn.setLayoutY(y);
        return btn;
    }

    private String pedirTexto(String titulo, String mensaje) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(titulo);
        dialog.setHeaderText(mensaje);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK).showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
