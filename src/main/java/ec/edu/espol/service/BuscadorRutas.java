package ec.edu.espol.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import ec.edu.espol.graph.RedArea;
import ec.edu.espol.model.Aeropuerto;
import ec.edu.espol.model.CodigoIATA;
import ec.edu.espol.model.Vuelo;

public class BuscadorRutas{
    private final RedArea red;

    public BuscadorRutas(RedArea red) {
        this.red = red;
    }

    // ================== Dijkstra ==================
    public List<Vuelo> dijkstra(CodigoIATA codigoOrigen, CodigoIATA codigoDestino) {
        Aeropuerto origen = red.buscarAeropuerto(codigoOrigen);
        Aeropuerto destino = red.buscarAeropuerto(codigoDestino);
        if (origen == null || destino == null) return new ArrayList<>();
        
        Map<Aeropuerto, Double> distancia = new HashMap<>();
        Map<Aeropuerto, Vuelo> previo = new HashMap<>();
        Set<Aeropuerto> visitados = new HashSet<>();

        for (Aeropuerto a : red.obtenerAeropuertos()) {
            distancia.put(a, Double.POSITIVE_INFINITY);
        }
        distancia.put(origen, 0.0);

        PriorityQueue<Aeropuerto> cola = new PriorityQueue<>(
            (a1, a2) -> Double.compare(distancia.get(a1), distancia.get(a2))
        );
        cola.add(origen);

        while (!cola.isEmpty()) {
            Aeropuerto actual = cola.poll();

            if (actual.equals(destino)) {
                return reconstruirRuta(previo, origen, destino);
            }

            if (visitados.contains(actual)) continue;
            visitados.add(actual);

            for (Vuelo v : red.obtenerVuelosDesde(actual)) {
                Aeropuerto vecino = v.getDestino();
                double nuevaDist = distancia.get(actual) + v.getDistancia();

                if (!visitados.contains(vecino) && nuevaDist < distancia.get(vecino)) {
                    distancia.put(vecino, nuevaDist);
                    previo.put(vecino, v);
                    cola.add(vecino);
                }
            }
        }

        return new ArrayList<>();
    }

    private List<Vuelo> reconstruirRuta(Map<Aeropuerto, Vuelo> previo, Aeropuerto origen, Aeropuerto destino) {
        List<Vuelo> ruta = new ArrayList<>();
        Aeropuerto actual = destino;

        while (previo.containsKey(actual)) {
            Vuelo v = previo.get(actual);
            ruta.add(0, v);
            actual = v.getOrigen();
        }

        if (ruta.isEmpty() || !ruta.get(0).getOrigen().equals(origen)) {
            return new ArrayList<>();
        }
        return ruta;
    }

    // ==================== BFS: Rutas Alternativas ====================
    public List<List<Vuelo>> buscarRutasAlternativas(String codigoOrigen, String codigoDestino) {
        List<List<Vuelo>> rutas = new ArrayList<>();
        CodigoIATA origenCodigo = crearCodigoIATA(codigoOrigen);
        CodigoIATA destinoCodigo = crearCodigoIATA(codigoDestino);
        if (origenCodigo == null || destinoCodigo == null) return rutas;
        Aeropuerto origen = red.buscarAeropuerto(origenCodigo);
        Aeropuerto destino = red.buscarAeropuerto(destinoCodigo);
        if (origen == null || destino == null) return rutas;

        Queue<List<Vuelo>> cola = new LinkedList<>();

        // Inicializar con los vuelos que salen del origen
        for (Vuelo vuelo : red.obtenerVuelosDesde(origen)) {
            List<Vuelo> rutaInicial = new ArrayList<>();
            rutaInicial.add(vuelo);
            cola.add(rutaInicial);
        }

        while (!cola.isEmpty()) {
            List<Vuelo> rutaActual = cola.poll();
            Vuelo ultimoVuelo = rutaActual.get(rutaActual.size() - 1);
            Aeropuerto actual = ultimoVuelo.getDestino();

            if (actual.equals(destino)) {
                rutas.add(rutaActual);
            } else {
                Set<Aeropuerto> visitados = obtenerAeropuertosEnRuta(rutaActual, origen);

                for (Vuelo siguiente : red.obtenerVuelosDesde(actual)) {
                    if (!visitados.contains(siguiente.getDestino())) {
                        List<Vuelo> nuevaRuta = new ArrayList<>(rutaActual);
                        nuevaRuta.add(siguiente);
                        cola.add(nuevaRuta);
                    }
                }
            }
        }
        return rutas;
    }

    //Obtiene todos los aeropuertos que ya fueron visitados en una ruta.
    private Set<Aeropuerto> obtenerAeropuertosEnRuta(List<Vuelo> ruta, Aeropuerto origen) {
        Set<Aeropuerto> visitados = new HashSet<>();
        visitados.add(origen);
        for (Vuelo v : ruta) {
            visitados.add(v.getOrigen());
            visitados.add(v.getDestino());
        }
        return visitados;
    }

    // ==================== Vuelos Directos ====================
    public List<Vuelo> vuelosDirectos(String codigoOrigen, String codigoDestino) {
        CodigoIATA origenCodigo = crearCodigoIATA(codigoOrigen);
        CodigoIATA destinoCodigo = crearCodigoIATA(codigoDestino);
        List<Vuelo> directos = new ArrayList<>();
        if (origenCodigo == null || destinoCodigo == null) return directos;
        Aeropuerto destino = red.buscarAeropuerto(destinoCodigo);
        if (destino == null) return directos;

        for (Vuelo v : red.obtenerVuelosDesde(origenCodigo)) {
            if (v.getDestino().equals(destino)) {
                directos.add(v);
            }
        }
        return directos;
    }

    // ==================== Cálculo de Costos ====================
    public double calcularCostoTotal(List<Vuelo> ruta, String tipo) {
        double total = 0;
        for (Vuelo v : ruta) {
            switch (tipo.toLowerCase()) {
                case "precio":    total += v.getPrecio(); break;
                case "minuto":    total += v.getDuracion(); break;
                case "distancia": total += v.getDistancia(); break;
            }
        }
        return total;
    }

    private CodigoIATA crearCodigoIATA(String codigo) {
        if (codigo == null) return null;
        try {
            return new CodigoIATA(codigo);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
