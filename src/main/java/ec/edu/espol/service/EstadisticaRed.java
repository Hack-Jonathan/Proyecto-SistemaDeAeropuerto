package ec.edu.espol.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import ec.edu.espol.graph.RedArea;
import ec.edu.espol.model.Aeropuerto;
import ec.edu.espol.model.CodigoIATA;
import ec.edu.espol.model.Vuelo;

public class EstadisticaRed {
    private final RedArea red;

    public EstadisticaRed(RedArea red) {
        this.red = red;
    }

    // ==================== Conexiones ====================

    /**
     * Devuelve la cantidad de vuelos que salen de un aeropuerto.
     * Retorna -1 si el aeropuerto no existe.
     */
    public int conexionesDeAeropuerto(String codigo) {
        CodigoIATA codigoIATA = crearCodigoIATA(codigo);
        if (codigoIATA == null) {
            return -1;
        }
        Aeropuerto aeropuerto = red.buscarAeropuerto(codigoIATA);
        if (aeropuerto == null) {
            return -1;
        }
        return red.obtenerVuelosDesde(aeropuerto).size();
    }

    public Aeropuerto aeropuertoMasConectado() {
        Aeropuerto masConectado = null;
        int maxConexiones = 0;

        for (Aeropuerto aeropuerto : red.obtenerAeropuertos()) {
            int conexiones = red.obtenerVuelosDesde(aeropuerto).size();
            if (conexiones > maxConexiones) {
                maxConexiones = conexiones;
                masConectado = aeropuerto;
            }
        }
        return masConectado;
    }

    public Aeropuerto aeropuertoMenosConectado() {
        Aeropuerto menosConectado = null;
        int minConexiones = Integer.MAX_VALUE;

        for (Aeropuerto aeropuerto : red.obtenerAeropuertos()) {
            int conexiones = red.obtenerVuelosDesde(aeropuerto).size();
            if (conexiones < minConexiones) {
                minConexiones = conexiones;
                menosConectado = aeropuerto;
            }
        }
        return menosConectado;
    }

    // ==================== Búsquedas ====================
    public List<Vuelo> buscarVuelosPorAerolinea(String aerolinea) {
        List<Vuelo> encontrados = new ArrayList<>();

        for (Aeropuerto aeropuerto : red.obtenerAeropuertos()) {
            for (Vuelo vuelo : red.obtenerVuelosDesde(aeropuerto)) {
                if (vuelo.getAerolinea() != null && vuelo.getAerolinea().equalsIgnoreCase(aerolinea)) {
                    encontrados.add(vuelo);
                }
            }
        }
        return encontrados;
    }

    public Set<Aeropuerto> aeropuertosAlcanzables(String codigoOrigen) {
        Set<Aeropuerto> alcanzables = new HashSet<>();
        CodigoIATA codigoIATA = crearCodigoIATA(codigoOrigen);
        if (codigoIATA == null) return alcanzables;
        Aeropuerto origen = red.buscarAeropuerto(codigoIATA);
        if (origen == null) return alcanzables;

        Queue<Aeropuerto> cola = new LinkedList<>();
        cola.add(origen);
        alcanzables.add(origen);

        while (!cola.isEmpty()) {
            Aeropuerto actual = cola.poll();
            for (Vuelo v : red.obtenerVuelosDesde(actual)) {
                Aeropuerto siguiente = v.getDestino();
                if (alcanzables.add(siguiente)) {
                    cola.add(siguiente);
                }
            }
        }

        alcanzables.remove(origen);
        return alcanzables;
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
