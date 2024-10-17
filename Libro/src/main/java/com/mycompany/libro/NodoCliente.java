package com.mycompany.libro;
import java.io.*;
import java.net.*;
import java.util.Random;

public class NodoCliente {
    private static String servidorHost = "localhost"; 
    private static int puerto = 6000;

    public static void main(String[] args) throws IOException {
        // Conectar al servidor maestro
        Socket socket = new Socket(servidorHost, puerto);
        System.out.println("Conectado al servidor maestro.");

        // Recibir contenido del libro
        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder contenidoLibro = new StringBuilder();
        String linea;
        while (!(linea = entrada.readLine()).equals("FIN")) {
            contenidoLibro.append(linea).append("\n");
        }
        System.out.println("Contenido del libro recibido: \n" + contenidoLibro);

        // Simular entrenamiento de modelo con backpropagation
        String modeloEntrenado = entrenarModelo(contenidoLibro.toString());

        // Enviar modelo entrenado de vuelta al servidor
        PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
        salida.println(modeloEntrenado);
        System.out.println("Modelo entrenado enviado al servidor.");

        socket.close();
    }

    private static String entrenarModelo(String libro) {
        // Crear un perceptrón multicapa con backpropagation
        RedNeuronal red = new RedNeuronal(3, 4, 0.1);  // Ejemplo con 3 entradas, 4 neuronas ocultas, y tasa de aprendizaje 0.1

        // Entrenar con datos simulados
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            double[] entradas = {random.nextDouble(), random.nextDouble(), random.nextDouble()};
            double salidaEsperada = random.nextDouble();
            red.entrenar(entradas, salidaEsperada);
        }

        return "Modelo entrenado con backpropagation para el libro: " + libro.hashCode();
    }
}

