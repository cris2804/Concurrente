/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libro;

import java.io.*;
import java.net.*;

public class Nodo {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Recibir fragmento del maestro
            String fragmento = in.readLine();
            System.out.println("Fragmento recibido: " + fragmento);

            // Procesar el fragmento (aquí se puede implementar la lógica de IA)
            String fragmentoProcesado = procesarFragmento(fragmento);

            // Enviar fragmento procesado de vuelta al maestro
            out.println(fragmentoProcesado);
            System.out.println("Fragmento procesado enviado al maestro: " + fragmentoProcesado);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String procesarFragmento(String fragmento) {
        // Simular procesamiento (puedes agregar lógica de IA aquí)
        return fragmento.toUpperCase();  // Procesamiento ficticio
    }
}














