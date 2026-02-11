package ec.edu.espol.model;

import java.io.Serializable;
import java.util.Objects;

public class Vuelo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Aeropuerto origen;
    private Aeropuerto destino;
    private int duracion;
    private double distancia;
    private double precio;
    private String aerolinea; //Nombre de la aerolínea que pertenece el vuelo

    // Constructor 
    public Vuelo(Aeropuerto origen, Aeropuerto destino, int duracion, 
        double distancia, double precio, String aerolinea) {
        this.origen = origen;
        this.destino = destino;
        this.duracion = duracion;
        this.distancia = origen.getCoordenadas().distanciaA(destino.getCoordenadas());
        this.precio = precio;
        this.aerolinea = aerolinea;
    }

    // Getters
    public Aeropuerto getOrigen() {
        return origen;
    }

    public Aeropuerto getDestino() {
        return destino;
    }

    public int getDuracion() {
        return duracion;
    }

    public double getDistancia() {
        return distancia;
    }

    public double getPrecio() {
        return precio;
    }

    public String getAerolinea() {
        return aerolinea;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vuelo vuelo = (Vuelo) obj;
        return origen.equals(vuelo.origen) && destino.equals(vuelo.destino);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origen, destino, aerolinea);
    }

    @Override
    public String toString() {
        return aerolinea + ": " + origen.getCodigo() + " -> " + destino.getCodigo()
            + " | " + duracion + "min"
            + " | " + distancia + "km"
            + " | $" + precio;
    }
}
