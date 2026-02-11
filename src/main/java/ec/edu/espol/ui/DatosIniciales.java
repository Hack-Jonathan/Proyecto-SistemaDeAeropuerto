package ec.edu.espol.ui;

import ec.edu.espol.graph.RedArea;
import ec.edu.espol.model.Aeropuerto;
import ec.edu.espol.model.CodigoIATA;
import ec.edu.espol.model.Coordenadas;
import ec.edu.espol.model.Ubicacion;

public class DatosIniciales {

    public static void cargar(RedArea red) {
        // Aeropuertos
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("PKX"), "Daxing", new Ubicacion("Pekín", "China"), new Coordenadas(39.511944, 116.410556)));
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("UIO"), "Quito", new Ubicacion("Quito", "Ecuador"), new Coordenadas(-0.141, -78.488)));
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("MIA"), "Miami", new Ubicacion("Miami", "EEUU"), new Coordenadas(25.7617, -80.1918)));
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("MAD"), "Madrid", new Ubicacion("Madrid", "España"), new Coordenadas(40.4168, -3.7038)));
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("CDG"), "París", new Ubicacion("París", "Francia"), new Coordenadas(49.0097, 2.5479)));
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("NRT"), "Tokio", new Ubicacion("Tokio", "Japón"), new Coordenadas(35.7767, 140.3186)));
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("SYD"), "Sídney", new Ubicacion("Sídney", "Australia"), new Coordenadas(-33.8688, 151.2093)));
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("AKL"), "Auckland", new Ubicacion("Auckland", "Nueva Zelanda"), new Coordenadas(-37.0083, 174.7947)));
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("SDQ"), "Santo Domingo", new Ubicacion("Santo Domingo", "República Dominicana"), new Coordenadas(18.4861, -69.9312)));
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("DXB"), "Dubai International", new Ubicacion("Dubái", "Emiratos Árabes Unidos"), new Coordenadas(25.2532, 55.3657)));
        red.agregarAeropuerto(new Aeropuerto(new CodigoIATA("LIM"), "Jorge Chávez", new Ubicacion("Lima", "Perú"), new Coordenadas(-12.0219, -77.1143)));

        // Vuelos
        red.agregarVuelo(new CodigoIATA("PKX"), new CodigoIATA("DXB"), 220, 200.15, "China Airlines");
        red.agregarVuelo(new CodigoIATA("PKX"), new CodigoIATA("CDG"), 340, 230.23, "China Airlines");
        red.agregarVuelo(new CodigoIATA("UIO"), new CodigoIATA("MIA"), 230, 600.30, "LATAM Airlines");
        red.agregarVuelo(new CodigoIATA("NRT"), new CodigoIATA("MAD"), 542, 438, "Avianca");
        red.agregarVuelo(new CodigoIATA("UIO"), new CodigoIATA("LIM"), 70, 123.12, "LATAM Airlines");
        red.agregarVuelo(new CodigoIATA("LIM"), new CodigoIATA("MIA"), 70, 230.30, "LATAM Airlines");
        red.agregarVuelo(new CodigoIATA("MIA"), new CodigoIATA("NRT"), 340, 600.29, "American Airlines");
        red.agregarVuelo(new CodigoIATA("SYD"), new CodigoIATA("NRT"), 320, 502.53, "Avianca");
        red.agregarVuelo(new CodigoIATA("SDQ"), new CodigoIATA("MAD"), 130, 134.32, "Avianca");
    }
}
