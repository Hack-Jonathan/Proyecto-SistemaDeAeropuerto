package ec.edu.espol.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ec.edu.espol.graph.RedArea;

public class PersistenciaRed{
    private final String rutaArchivo;

    public PersistenciaRed(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    /**
     * Guarda la red en disco usando serialización de Java.
     */
    public void guardar(RedArea red) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(red);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la red: " + e.getMessage(), e);
        }
    }

    /**
     * Carga la red desde disco.
     * Retorna null si el archivo no existe.
     */
    public RedArea cargar() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (RedArea) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar la red: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si ya existe un archivo de datos guardado.
     */
    public boolean existenDatos() {
        return new File(rutaArchivo).exists();
    }
}
