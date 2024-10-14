package com.mycompany.libro;
import java.io.*;
import java.net.*;

public class NodoCliente {
    private static String servidorHost = "localhost"; // Dirección IP del servidor
    private static int puerto = 5000;

    public static void main(String[] args) throws IOException {
        // Conectar al servidor
        Socket socket = new Socket(servidorHost, puerto);
        System.out.println("Conectado al servidor maestro.");

        // Recibir libro para entrenar
        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String libro = entrada.readLine();
        System.out.println("Libro recibido: " + libro);

        // Simular entrenamiento de modelo
        String modeloEntrenado = entrenarModelo(libro);

        // Enviar modelo entrenado de vuelta al servidor
        PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
        salida.println(modeloEntrenado);
        System.out.println("Modelo entrenado enviado al servidor.");

        socket.close();
    }

    private static String entrenarModelo(String libro) {
        // Aquí se implementaría el algoritmo de backpropagation
        // Por ahora simulamos el entrenamiento
        System.out.println("Entrenando modelo con el libro: " + libro);
        try {
            Thread.sleep(3000); // Simulación de tiempo de entrenamiento
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Modelo entrenado con " + libro;
    }
}
