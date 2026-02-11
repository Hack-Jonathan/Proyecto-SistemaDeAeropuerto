package ec.edu.espol.model;

import java.io.Serializable;

public class Coordenadas implements Serializable {
    private static final long serialVersionUID = 1L;
    private final double latitud;
    private final double longitud;

    //Constructor
    public Coordenadas(double latitud, double longitud){
        if( latitud < -90 || latitud > 90){
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90 grados.");
        }
        if( longitud < -180 || longitud > 180){
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180 grados.");
        }
        this.latitud = latitud;
        this.longitud = longitud;
    }

    //Getters
    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    // Calcular distancias entre coordenadas
    public double distanciaA(Coordenadas otra){
        final int radioTierra = 6371;

        double latRad1 = Math.toRadians(this.latitud);
        double latRad2 = Math.toRadians(otra.latitud);
        double deltaLat = Math.toRadians(otra.latitud - this.latitud); // Diferencia de latitud
        double deltaLong = Math.toRadians(otra.longitud - this.longitud); // Diferencia de longitud
        
        // Fórmula de Haversine para calcular la distancia entre dos puntos en la superficie de una esfera
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(latRad1) * Math.cos(latRad2) *
                   Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
        
        // Calcular la distancia en kilómetros
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(radioTierra * c);
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Coordenadas coordenadas = (Coordenadas) obj;
        return Double.compare(coordenadas.latitud, latitud) == 0 &&
            Double.compare(coordenadas.longitud, longitud) == 0;
    }

    @Override
    public int hashCode(){
        long lat = Double.doubleToLongBits(latitud);
        long lon = Double.doubleToLongBits(longitud);
        return (int) (lat ^ (lat >>> 32)) ^ (int) (lon ^ (lon >>> 32));
    }

    @Override
    public String toString() {
        return "(" + latitud + ", " + longitud + ")";
    }
}
