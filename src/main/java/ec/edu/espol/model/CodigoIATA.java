package ec.edu.espol.model;

import java.io.Serializable;

public class CodigoIATA implements Serializable {
    private static final long serialVersionUID = 1L;
    //Atributos
    private final String valor;

    // Constructor
    public CodigoIATA(String valor){
        if (valor == null || valor.length() != 3) {
            throw new IllegalArgumentException("El código IATA debe tener exactamente 3 caracteres.");
        }
        this.valor = valor.toUpperCase();
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CodigoIATA otro = (CodigoIATA) obj;
        return valor.equals(otro.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor;
    }
}
