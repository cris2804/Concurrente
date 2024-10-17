/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libro;

import java.io.*;
import java.net.*;
import java.util.*;

public class Maestro {
    private List<Socket> nodos;
    private List<String> fragmentosProcesados;

    public Maestro() {
        nodos = new ArrayList<>();
        fragmentosProcesados = new ArrayList<>();
    }

    public void agregarNodo(Socket nodo) {
        nodos.add(nodo);
        System.out.println("Nodo conectado: " + nodo.getInetAddress());
    }

    public void recibirLibro(String libro) throws IOException {
        List<String> fragmentos = dividirLibro(libro, nodos.size());
        enviarFragmentosANodos(fragmentos);
    }

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

    private void enviarFragmentosANodos(List<String> fragmentos) throws IOException {
        for (int i = 0; i < nodos.size(); i++) {
            Socket nodo = nodos.get(i);
            PrintWriter out = new PrintWriter(nodo.getOutputStream(), true);
            System.out.println("Enviando fragmento al nodo " + (i + 1) + ": " + fragmentos.get(i));
            out.println(fragmentos.get(i)); // Enviar el fragmento al nodo
        }
    }

    public void recibirFragmentoProcesado(String fragmento) {
        fragmentosProcesados.add(fragmento);
    }

    public List<String> getFragmentosProcesados() {
        return fragmentosProcesados;
    }

    public static void main(String[] args) throws IOException {
        Maestro maestro = new Maestro();

        // Un ServerSocket para los nodos en el puerto 5000
        ServerSocket serverSocketNodos = new ServerSocket(5000);
        // Un ServerSocket para los clientes (ClienteLibro y ClienteModelo) en el puerto 6000
        ServerSocket serverSocketClientes = new ServerSocket(6000);

        System.out.println("Maestro iniciado... esperando nodos y clientes...");
        
        // Hilo para aceptar nodos
        new Thread(() -> {
            try {
                while (true) {
                    Socket nodo = serverSocketNodos.accept();
                    maestro.agregarNodo(nodo);

                    // Crear un hilo para cada nodo que procese el fragmento
                    new Thread(() -> {
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(nodo.getInputStream()));
                            String fragmentoProcesado = in.readLine();
                            if (fragmentoProcesado != null) {
                                System.out.println("Fragmento procesado recibido del nodo: " + fragmentoProcesado);
                                maestro.recibirFragmentoProcesado(fragmentoProcesado);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                    // Enviar fragmentos procesados al cliente modelo
                    for (String fragmento : maestro.getFragmentosProcesados()) {
                        out.println(fragmento);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}












             







