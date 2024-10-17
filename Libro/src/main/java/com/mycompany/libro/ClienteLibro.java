/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libro;

import java.io.*;
import java.net.*;

public class ClienteLibro {

    public static void main(String[] args) {
        String libro = "Este es un ejemplo de libro que será dividido en fragmentos.";

        try {
            Socket socket = new Socket("localhost", 6000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(libro);  // Enviar el libro completo al maestro
            System.out.println("Libro enviado al maestro.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






