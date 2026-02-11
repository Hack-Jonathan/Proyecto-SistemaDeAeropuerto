package ec.edu.espol.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.edu.espol.model.Aeropuerto;
import ec.edu.espol.model.CodigoIATA;
import ec.edu.espol.model.Vuelo;

public class RedArea implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<CodigoIATA, Aeropuerto> aeropuertos;
    private Map<Aeropuerto, List<Vuelo>> adyacencia;

    public RedArea() {
        this.aeropuertos = new HashMap<>();
        this.adyacencia = new HashMap<>();
    }

    // ====================== Gestion de Aeropuertos ======================
    public boolean agregarAeropuerto(Aeropuerto nuevo){
        if(nuevo == null || aeropuertos.containsKey(nuevo.getCodigo())) return false;
        this.aeropuertos.put(nuevo.getCodigo(), nuevo);
        this.adyacencia.put(nuevo, new ArrayList<>());
        return true;
    }

    public boolean eliminarAeropuerto(CodigoIATA codigo){
        if(codigo == null || !aeropuertos.containsKey(codigo)) return false;
        Aeropuerto eliminar = aeropuertos.remove(codigo);
        // Eliminar vuelos de salida
        this.adyacencia.remove(eliminar);
        // Eliminar vuelos de llegada
        for(List<Vuelo> vuelos : adyacencia.values()){
            vuelos.removeIf(vuelo -> vuelo.getDestino().equals(eliminar));
        } 
        return true;
    }

    public Aeropuerto buscarAeropuerto(CodigoIATA codigo){
        return aeropuertos.get(codigo);
    }

    public boolean existeAeropuerto(CodigoIATA codigo){
        return aeropuertos.containsKey(codigo);
    }

    // ====================== Gestion de Vuelos ======================
    public boolean agregarVuelo(CodigoIATA origen, CodigoIATA destino, int duracion, double precio, String aerolinea){
        Aeropuerto origenAeropuerto = aeropuertos.get(origen);
        Aeropuerto destinoAeropuerto = aeropuertos.get(destino);
        if(origenAeropuerto == null || destinoAeropuerto == null) return false;
        Vuelo nuevoVuelo = new Vuelo(origenAeropuerto, destinoAeropuerto, duracion, origenAeropuerto.getCoordenadas().distanciaA(destinoAeropuerto.getCoordenadas()), precio, aerolinea);
        return adyacencia.get(origenAeropuerto).add(nuevoVuelo);
    }

    public boolean eliminarVuelo(CodigoIATA origen, CodigoIATA destino){
        Aeropuerto origenAeropuerto = aeropuertos.get(origen);
        Aeropuerto destinoAeropuerto = aeropuertos.get(destino);
        if(origenAeropuerto == null || destinoAeropuerto == null) return false;
        return adyacencia.get(origenAeropuerto).removeIf(v -> v.getDestino().equals(destinoAeropuerto));
    }

    // ====================== Consultas ======================
    public List<Vuelo> obtenerVuelosDesde(Aeropuerto aeropuerto) {
        return adyacencia.getOrDefault(aeropuerto, new ArrayList<>());
    }
    public List<Vuelo> obtenerVuelosDesde(CodigoIATA origen){
        Aeropuerto origenAeropuerto = aeropuertos.get(origen);
        if(origenAeropuerto == null) return null;
        return adyacencia.get(origenAeropuerto);
    }

    public List<Vuelo> obtenerVuelosHacia(CodigoIATA destino){
        Aeropuerto destinoAeropuerto = aeropuertos.get(destino);
        if(destinoAeropuerto == null) return null;
        List<Vuelo> vuelosHacia = new ArrayList<>();
        for(List<Vuelo> vuelos : adyacencia.values()){
            for(Vuelo vuelo : vuelos){
                if(vuelo.getDestino().equals(destinoAeropuerto)){
                    vuelosHacia.add(vuelo);
                }
            }
        }
        return vuelosHacia;
    }

    public List<Aeropuerto> obtenerAeropuertos(){
        return new ArrayList<>(aeropuertos.values());
    }

    public int cantidadAeropuertos(){
        return aeropuertos.size();
    }

    public int cantidadVuelos(){
        int total = 0;
        for(List<Vuelo> vuelos : adyacencia.values()){
            total += vuelos.size();
        }
        return total;
    }

}
