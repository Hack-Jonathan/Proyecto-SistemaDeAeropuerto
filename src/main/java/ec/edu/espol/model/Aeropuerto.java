package ec.edu.espol.model;

import java.io.Serializable;

public class Aeropuerto implements Serializable{
    //Atributos 
    private static final long serialVersionUID = 1L;
    private CodigoIATA codigo;
    private String nombre;
    private Ubicacion ubicacion;
    private Coordenadas coordenadas;

    //Constructor
    public Aeropuerto(CodigoIATA codigo, String nombre, Ubicacion ubicacion, Coordenadas coordenadas) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.coordenadas = coordenadas;
    }

    //Getters
    public CodigoIATA getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Aeropuerto aeropuerto = (Aeropuerto) obj;
        return codigo.equals(aeropuerto.codigo);
    }

    @Override
    public int hashCode() {
        return codigo != null ? codigo.hashCode() : 0;
    }
}
