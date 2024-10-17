/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libro;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteModelo {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 6000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("libro");  // Solicitar los fragmentos procesados

            // Recibir fragmentos procesados
            List<String> fragmentosProcesados = new ArrayList<>();
            String fragmento;
            while ((fragmento = in.readLine()) != null) {
                fragmentosProcesados.add(fragmento);
            }

            System.out.println("Fragmentos procesados recibidos:");
            for (String f : fragmentosProcesados) {
                System.out.println(f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





