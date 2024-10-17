package com.mycompany.libro;

import java.io.*;
import java.net.*;
import java.util.*;

public class Maestro {
    private List<Socket> nodos;
    private List<double[][]> pesosW1;  // Almacenar las matrices W1 de todos los nodos
    private List<double[][]> pesosW2;  // Almacenar las matrices W2 de todos los nodos

    public Maestro() {
        nodos = new ArrayList<>();
        pesosW1 = new ArrayList<>();
        pesosW2 = new ArrayList<>();
    }

    // Agrega un nodo a la lista de nodos conectados
    public void agregarNodo(Socket nodo) {
        nodos.add(nodo);
        System.out.println("Nodo conectado: " + nodo.getInetAddress());
    }

    // Recibe el libro del ClienteLibro y lo divide en fragmentos para los nodos
    public void recibirLibro(String libro) throws IOException {
        List<String> fragmentos = dividirLibro(libro, nodos.size());
        enviarFragmentosANodos(fragmentos);
    }

    // Divide el libro en fragmentos del tamaño adecuado para cada nodo
    private List<String> dividirLibro(String libro, int partes) {
        int fragmentoTamano = libro.length() / partes;
        List<String> fragmentos = new ArrayList<>();
        for (int i = 0; i < partes; i++) {
            int inicio = i * fragmentoTamano;
            int fin = (i == partes - 1) ? libro.length() : inicio + fragmentoTamano;
            fragmentos.add(libro.substring(inicio, fin));
        }
        return fragmentos;
    }

    // Envía los fragmentos de texto a cada uno de los nodos conectados
    private void enviarFragmentosANodos(List<String> fragmentos) throws IOException {
        for (int i = 0; i < nodos.size(); i++) {
            Socket nodo = nodos.get(i);
            PrintWriter out = new PrintWriter(nodo.getOutputStream(), true);
            System.out.println("Enviando fragmento al nodo " + (i + 1) + ": " + fragmentos.get(i));
            out.println(fragmentos.get(i)); // Enviar el fragmento al nodo
            out.println("END");  // Indicar el final del fragmento
        }
    }

    // Recibe las matrices procesadas de los nodos (W1 y W2)
    private void recibirPesosDeNodo(Socket nodo) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(nodo.getInputStream()));
            String linea;
            List<String> matrizTextoW1 = new ArrayList<>();
            List<String> matrizTextoW2 = new ArrayList<>();
            boolean leyendoW1 = true;

            // Leer las líneas del nodo hasta que se reciba "END_MATRIX" dos veces
            while ((linea = in.readLine()) != null) {
                if (linea.equals("END_MATRIX")) {
                    leyendoW1 = false;  // Cambiar a la matriz W2
                    continue;  // Continuar para leer la segunda matriz (W2)
                }
                if (leyendoW1) {
                    matrizTextoW1.add(linea);  // Agregar líneas a la matriz W1
                } else {
                    matrizTextoW2.add(linea);  // Agregar líneas a la matriz W2
                }
            }

            // Convertir las matrices de texto a double[][]
            double[][] W1 = convertirTextoAMatriz(matrizTextoW1);
            double[][] W2 = convertirTextoAMatriz(matrizTextoW2);

            // Almacenar las matrices en las listas de pesos
            agregarPesosNodo(W1, W2);

            System.out.println("Matrices W1 y W2 recibidas y almacenadas correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Convierte una lista de strings (cada string es una fila) a una matriz de doubles
    private double[][] convertirTextoAMatriz(List<String> matrizTexto) {
        int numFilas = matrizTexto.size();
        int numColumnas = matrizTexto.get(0).trim().split(" ").length;  // Suponemos que todas las filas tienen el mismo número de columnas
        double[][] matriz = new double[numFilas][numColumnas];

        for (int i = 0; i < numFilas; i++) {
            String[] valores = matrizTexto.get(i).trim().split(" ");
            for (int j = 0; j < valores.length; j++) {
                matriz[i][j] = Double.parseDouble(valores[j]);
            }
        }

        return matriz;
    }

    // Método para agregar las matrices de cada nodo
    public void agregarPesosNodo(double[][] W1, double[][] W2) {
        pesosW1.add(W1);
        pesosW2.add(W2);
    }

    // Retorna las matrices W1 de todos los nodos
    public List<double[][]> getPesosW1() {
        return pesosW1;
    }

    // Retorna las matrices W2 de todos los nodos
    public List<double[][]> getPesosW2() {
        return pesosW2;
    }

    public static void main(String[] args) throws IOException {
        Maestro maestro = new Maestro();

        // Un ServerSocket para los nodos en el puerto 5000
        ServerSocket serverSocketNodos = new ServerSocket(3000);
        // Un ServerSocket para los clientes (ClienteLibro y ClienteModelo) en el puerto 6000
        ServerSocket serverSocketClientes = new ServerSocket(3001);

        System.out.println("Maestro iniciado... esperando nodos y clientes...");
        
        // Hilo para aceptar nodos
        new Thread(() -> {
            try {
                while (true) {
                    Socket nodo = serverSocketNodos.accept();
                    maestro.agregarNodo(nodo);

                    // Crear un hilo para cada nodo que procese el fragmento
                    new Thread(() -> {
                        maestro.recibirPesosDeNodo(nodo);
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Hilo para aceptar clientes (ClienteLibro y ClienteModelo)
        new Thread(() -> {
            try {
                while (true) {
                    Socket cliente = serverSocketClientes.accept();
                    System.out.println("Cliente conectado: " + cliente.getInetAddress());

                    BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                    String libro = in.readLine();

                    if (libro != null) {
                        System.out.println("Libro recibido: " + libro);
                        maestro.recibirLibro(libro);
                    }

                    PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
                    // Enviar fragmentos procesados al cliente modelo (pesos W1 y W2)
                    for (double[][] W1 : maestro.getPesosW1()) {
                        out.println(Arrays.deepToString(W1));
                    }
                    for (double[][] W2 : maestro.getPesosW2()) {
                        out.println(Arrays.deepToString(W2));
                    }

                    out.println("FIN PROCESAMIENTO");  // Indicar al cliente que se ha enviado todo el contenido
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
