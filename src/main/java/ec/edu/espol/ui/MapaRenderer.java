package ec.edu.espol.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ec.edu.espol.graph.RedArea;
import ec.edu.espol.model.Aeropuerto;
import ec.edu.espol.model.Vuelo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// Se encarga de dibujar el mapa, aeropuertos y vuelos en el canvas.
public class MapaRenderer {
    private final Random random = new Random();
    private final Map<Aeropuerto, Color> coloresAeropuertos = new HashMap<>();

    // ==================== Conversión de Coordenadas ====================

    public double lonToX(double lon, double canvasWidth) {
        return ((lon + 180) / 360.0) * canvasWidth;
    }

    public double latToY(double lat, double canvasHeight) {
        return ((90 - lat) / 180.0) * canvasHeight;
    }

    // ==================== Dibujo ====================

    /**
     * Redibuja el mapa completo: imagen de fondo, aeropuertos y vuelos.
     */
    public void redibujarMapa(GraphicsContext gc, Image mapa, double canvasWidth, double canvasHeight, RedArea red) {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        gc.drawImage(mapa, 0, 0, canvasWidth, canvasHeight);

        // Dibujar aeropuertos
        for (Aeropuerto a : red.obtenerAeropuertos()) {
            Color color = generarColorAleatorio();
            coloresAeropuertos.put(a, color);

            double x = lonToX(a.getCoordenadas().getLongitud(), canvasWidth);
            double y = latToY(a.getCoordenadas().getLatitud(), canvasHeight);

            gc.setFill(color);
            gc.fillOval(x - 5, y - 5, 10, 10);
            gc.setFill(Color.BLACK);
            gc.fillText(a.getCodigo().getValor(), x + 8, y);
        }

        // Dibujar vuelos
        for (Aeropuerto origen : red.obtenerAeropuertos()) {
            for (Vuelo vuelo : red.obtenerVuelosDesde(origen)) {
                Aeropuerto destino = vuelo.getDestino();

                double x1 = lonToX(origen.getCoordenadas().getLongitud(), canvasWidth);
                double y1 = latToY(origen.getCoordenadas().getLatitud(), canvasHeight);
                double x2 = lonToX(destino.getCoordenadas().getLongitud(), canvasWidth);
                double y2 = latToY(destino.getCoordenadas().getLatitud(), canvasHeight);

                gc.setStroke(coloresAeropuertos.get(origen));
                gc.setLineWidth(2);
                gc.strokeLine(x1, y1, x2, y2);
                dibujarFlecha(gc, x1, y1, x2, y2);

                double midX = (x1 + x2) / 2;
                double midY = (y1 + y2) / 2;
                gc.setFill(Color.BLACK);
                gc.fillText(vuelo.getDistancia() + " km", midX, midY - 5);
            }
        }
    }

    /**
     * Dibuja solo las rutas encontradas, atenuando el resto del mapa.
     */
    public void dibujarRutas(GraphicsContext gc, Image mapa, double canvasWidth, double canvasHeight,
        RedArea red, List<List<Vuelo>> rutas, List<Vuelo> rutaMasCorta) {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        gc.drawImage(mapa, 0, 0, canvasWidth, canvasHeight);

        // Dibujar aeropuertos en gris
        for (Aeropuerto a : red.obtenerAeropuertos()) {
            double x = lonToX(a.getCoordenadas().getLongitud(), canvasWidth);
            double y = latToY(a.getCoordenadas().getLatitud(), canvasHeight);
            gc.setFill(Color.DARKGRAY);
            gc.fillOval(x - 5, y - 5, 10, 10);
            gc.setFill(Color.BLACK);
            gc.fillText(a.getCodigo().getValor(), x + 8, y);
        }

        // Dibujar cada ruta
        for (List<Vuelo> ruta : rutas) {
            boolean esMasCorta = (ruta == rutaMasCorta);
            Color colorRuta = esMasCorta ? Color.GOLD : Color.RED;

            for (Vuelo vuelo : ruta) {
                double x1 = lonToX(vuelo.getOrigen().getCoordenadas().getLongitud(), canvasWidth);
                double y1 = latToY(vuelo.getOrigen().getCoordenadas().getLatitud(), canvasHeight);
                double x2 = lonToX(vuelo.getDestino().getCoordenadas().getLongitud(), canvasWidth);
                double y2 = latToY(vuelo.getDestino().getCoordenadas().getLatitud(), canvasHeight);

                gc.setStroke(colorRuta);
                gc.setLineWidth(esMasCorta ? 4 : 2);
                gc.strokeLine(x1, y1, x2, y2);
                dibujarFlecha(gc, x1, y1, x2, y2);

                double midX = (x1 + x2) / 2;
                double midY = (y1 + y2) / 2;
                gc.setFill(colorRuta.darker());
                gc.fillText(vuelo.getDistancia() + "km", midX, midY - 5);
            }
        }
    }

    /**
     * Dibuja una flecha en el extremo de una línea.
     */
    private void dibujarFlecha(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double arrowLength = 15;
        double arrowAngle = Math.toRadians(20);

        double xArrow1 = x2 - arrowLength * Math.cos(angle - arrowAngle);
        double yArrow1 = y2 - arrowLength * Math.sin(angle - arrowAngle);
        double xArrow2 = x2 - arrowLength * Math.cos(angle + arrowAngle);
        double yArrow2 = y2 - arrowLength * Math.sin(angle + arrowAngle);

        gc.strokeLine(x2, y2, xArrow1, yArrow1);
        gc.strokeLine(x2, y2, xArrow2, yArrow2);
    }

    // ==================== Utilidades ====================

    /**
     * Busca el aeropuerto más cercano a un punto en el canvas.
     * Retorna null si no hay ninguno dentro de la distancia mínima.
     */
    public Aeropuerto buscarAeropuertoEnClick(double x, double y, double canvasWidth, double canvasHeight, RedArea red) {
        double minDist = 10.0;
        Aeropuerto cercano = null;

        for (Aeropuerto a : red.obtenerAeropuertos()) {
            double ax = lonToX(a.getCoordenadas().getLongitud(), canvasWidth);
            double ay = latToY(a.getCoordenadas().getLatitud(), canvasHeight);
            double dist = Math.hypot(x - ax, y - ay);
            if (dist <= minDist) {
                cercano = a;
                minDist = dist;
            }
        }
        return cercano;
    }

    public Color generarColorAleatorio() {
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    public Map<Aeropuerto, Color> getColoresAeropuertos() {
        return coloresAeropuertos;
    }
}
