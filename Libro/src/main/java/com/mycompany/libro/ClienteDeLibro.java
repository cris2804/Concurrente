/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libro;
import java.io.*;
import java.net.*;

public class ClienteDeLibro {
    private static String servidorHost = "localhost"; 
    private static int puerto = 6000;

    public static void main(String[] args) throws IOException {
        // Conectar al servidor maestro
        Socket socket = new Socket(servidorHost, puerto);
        System.out.println("Conectado al servidor maestro.");

        // Enviar contenido del libro
        File libro = new File("Libro.txt");
        BufferedReader reader = new BufferedReader(new FileReader(libro));
        PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
        String linea;
        while ((linea = reader.readLine()) != null) {
            salida.println(linea);  // Enviar línea por línea del libro
        }
        salida.println("FIN");  // Señal para indicar fin del archivo
        reader.close();

        // Esperar confirmación del servidor
        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String respuesta = entrada.readLine();
        System.out.println("Respuesta del servidor: " + respuesta);

        socket.close();
    }
}

