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

    // Recibe el libro del ClienteLibro como una lista de oraciones y lo distribuye entre los nodos
    public void recibirLibro(List<String> oraciones) throws IOException {
        List<List<String>> fragmentos = dividirOracionesEnFragmentos(oraciones, nodos.size());
        enviarFragmentosANodos(fragmentos);
    }

    // Divide la lista de oraciones en fragmentos equitativos para los nodos
    private List<List<String>> dividirOracionesEnFragmentos(List<String> oraciones, int numNodos) {
        List<List<String>> fragmentos = new ArrayList<>();
        int numOracionesPorNodo = oraciones.size() / numNodos;

        // Distribuir las oraciones de manera equitativa en fragmentos
        for (int i = 0; i < numNodos; i++) {
            int inicio = i * numOracionesPorNodo;
            int fin = (i == numNodos - 1) ? oraciones.size() : inicio + numOracionesPorNodo;
            fragmentos.add(new ArrayList<>(oraciones.subList(inicio, fin)));
        }

        return fragmentos;
    }

    // Envía los fragmentos de oraciones a cada uno de los nodos conectados
    private void enviarFragmentosANodos(List<List<String>> fragmentos) throws IOException {
        for (int i = 0; i < nodos.size(); i++) {
            Socket nodo = nodos.get(i);
            PrintWriter out = new PrintWriter(nodo.getOutputStream(), true);
            List<String> fragmento = fragmentos.get(i);

            System.out.println("Enviando fragmento al nodo " + (i + 1) + ": " + fragmento);
            
            // Enviar cada oración del fragmento
            for (String oracion : fragmento) {
                out.println(oracion);
            }

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
            System.out.println("Matriz W1 recibida:");
            //imprimirMatriz(W1);
            System.out.println("Matriz W2 recibida:");
            //imprimirMatriz(W2);

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

    private void imprimirMatriz(double[][] matriz) {
        for (double[] fila : matriz) {
            for (double valor : fila) {
                System.out.print(valor + "\t");  // Imprimir cada valor separado por un tabulador
            }
            System.out.println();  // Nueva línea después de cada fila
        }
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
                    List<String> oraciones = new ArrayList<>();
                    String linea;

                    // Leer cada oración enviada por el ClienteLibro
                    while ((linea = in.readLine()) != null && !linea.equals("FIN_LIBRO")) {
                        oraciones.add(linea);
                    }

                    if (!oraciones.isEmpty()) {
                        System.out.println("Libro recibido con " + oraciones.size() + " oraciones.");
                        maestro.recibirLibro(oraciones);
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
