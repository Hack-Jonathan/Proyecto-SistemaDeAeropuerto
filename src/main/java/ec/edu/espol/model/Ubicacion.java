package ec.edu.espol.model;

import java.io.Serializable;

public class Ubicacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String ciudad;
    private final String pais;

    public Ubicacion(String ciudad, String pais) {
        this.ciudad = ciudad;
        this.pais = pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getPais() {
        return pais;
    }
    
}
