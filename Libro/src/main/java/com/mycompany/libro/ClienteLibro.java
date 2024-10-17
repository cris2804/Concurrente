package com.mycompany.libro;

import java.io.*;
import java.net.*;

public class ClienteLibro {

    public static void main(String[] args) {
        try {
            // Conectar al Maestro en el puerto 3001
            Socket socket = new Socket("localhost", 3001);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Leer el archivo de texto (Libro.txt)
            BufferedReader libroReader = new BufferedReader(new FileReader("Libro.txt"));
            String linea;

            // Leer cada línea (oración) y enviarla directamente
            while ((linea = libroReader.readLine()) != null) {
                out.println(linea.trim());  // Enviar cada línea/oración
            }

            // Enviar marcador de fin de libro
            out.println("FIN_LIBRO");

            // Cerrar el archivo y la conexión
            libroReader.close();
            out.close();
            socket.close();
            System.out.println("Libro enviado al Maestro.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
