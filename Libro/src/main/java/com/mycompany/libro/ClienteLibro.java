package com.mycompany.libro;

import java.io.*;
import java.net.*;

public class ClienteLibro {

    public static void main(String[] args) {
        try {
            // Conectar al Maestro en el puerto 6000
            Socket socket = new Socket("localhost", 3001);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Leer el archivo de texto (libro.txt)
            BufferedReader libroReader = new BufferedReader(new FileReader("Libro.txt"));
            StringBuilder libroCompleto = new StringBuilder();
            String linea;
            while ((linea = libroReader.readLine()) != null) {
                libroCompleto.append(linea).append(" ");
            }

            // Enviar el libro completo al Maestro
            out.println(libroCompleto.toString());

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
