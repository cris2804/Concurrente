package com.mycompany.libro;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServidorMaestro {
    private static List<Socket> nodos = new ArrayList<>();
    private static List<String> modelosEntrenados = new ArrayList<>();  // Lista para almacenar los modelos entrenados
    private static int puerto = 6000;

    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(puerto);
        System.out.println("Servidor Maestro escuchando en el puerto " + puerto);

        // Aceptar conexiones de los nodos
        while (true) {
            Socket nodo = servidor.accept();
            System.out.println("Nodo conectado: " + nodo.getInetAddress().getHostName());
            nodos.add(nodo);

            // Iniciar hilo para manejar la comunicación con el nodo
            new Thread(new ManejadorNodo(nodo)).start();
        }
    }

    // Método para distribuir libros a los nodos
    public static void enviarLibrosAClientes(String libro) throws IOException {
        for (Socket nodo : nodos) {
            PrintWriter salida = new PrintWriter(nodo.getOutputStream(), true);
            salida.println(libro);  // Envía el contenido del libro al nodo
            System.out.println("Enviado libro al nodo: " + nodo.getInetAddress().getHostName());
        }
    }

    // Método para recibir modelos entrenados de los nodos
    public static void recibirModelo(String modelo) {
        modelosEntrenados.add(modelo);
    }

    // Clase manejador para los nodos
    static class ManejadorNodo implements Runnable {
        private Socket nodo;

        public ManejadorNodo(Socket nodo) {
            this.nodo = nodo;
        }

        @Override
        public void run() {
            try {
                BufferedReader entrada = new BufferedReader(new InputStreamReader(nodo.getInputStream()));
                String modelo = entrada.readLine();
                System.out.println("Modelo recibido del nodo: " + nodo.getInetAddress().getHostName() + ": " + modelo);
                recibirModelo(modelo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

