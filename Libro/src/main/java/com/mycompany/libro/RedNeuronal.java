/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libro;
import java.util.Random;

public class RedNeuronal {

    private double[][] pesosEntradaOculta;
    private double[] pesosOcultaSalida;
    private double tasaAprendizaje;

    // Constructor
    public RedNeuronal(int numEntradas, int numNeuronasOcultas, double tasaAprendizaje) {
        this.tasaAprendizaje = tasaAprendizaje;
        pesosEntradaOculta = new double[numNeuronasOcultas][numEntradas];
        pesosOcultaSalida = new double[numNeuronasOcultas];
        inicializarPesosAleatorios();
    }

    // Inicializa los pesos con valores aleatorios
    private void inicializarPesosAleatorios() {
        Random random = new Random();
        for (int i = 0; i < pesosEntradaOculta.length; i++) {
            for (int j = 0; j < pesosEntradaOculta[i].length; j++) {
                pesosEntradaOculta[i][j] = random.nextDouble();
            }
            pesosOcultaSalida[i] = random.nextDouble();
        }
    }

    // Función de activación (sigmoide)
    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    // Derivada de la función sigmoide
    private double derivadaSigmoid(double x) {
        return x * (1 - x);
    }

    // Entrenar el modelo con el algoritmo de backpropagation
    public void entrenar(double[] entradas, double salidaEsperada) {
        // Paso hacia adelante (forward pass)
        double[] salidaOculta = new double[pesosEntradaOculta.length];
        for (int i = 0; i < pesosEntradaOculta.length; i++) {
            salidaOculta[i] = 0;
            for (int j = 0; j < entradas.length; j++) {
                salidaOculta[i] += entradas[j] * pesosEntradaOculta[i][j];
            }
            salidaOculta[i] = sigmoid(salidaOculta[i]);
        }

        // Salida de la red
        double salidaRed = 0;
        for (int i = 0; i < salidaOculta.length; i++) {
            salidaRed += salidaOculta[i] * pesosOcultaSalida[i];
        }
        salidaRed = sigmoid(salidaRed);

        // Paso hacia atrás (backward pass - retropropagación)
        double errorSalida = salidaEsperada - salidaRed;
        double deltaSalida = errorSalida * derivadaSigmoid(salidaRed);

        // Ajuste de pesos para la capa oculta a la salida
        for (int i = 0; i < pesosOcultaSalida.length; i++) {
            pesosOcultaSalida[i] += tasaAprendizaje * deltaSalida * salidaOculta[i];
        }

        // Ajuste de pesos para las entradas a la capa oculta
        for (int i = 0; i < pesosEntradaOculta.length; i++) {
            double deltaOculta = deltaSalida * derivadaSigmoid(salidaOculta[i]);
            for (int j = 0; j < pesosEntradaOculta[i].length; j++) {
                pesosEntradaOculta[i][j] += tasaAprendizaje * deltaOculta * entradas[j];
            }
        }
    }

    // Método para predecir una salida dada una entrada
    public double predecir(double[] entradas) {
        double[] salidaOculta = new double[pesosEntradaOculta.length];
        for (int i = 0; i < pesosEntradaOculta.length; i++) {
            salidaOculta[i] = 0;
            for (int j = 0; j < entradas.length; j++) {
                salidaOculta[i] += entradas[j] * pesosEntradaOculta[i][j];
            }
            salidaOculta[i] = sigmoid(salidaOculta[i]);
        }

        double salidaRed = 0;
        for (int i = 0; i < salidaOculta.length; i++) {
            salidaRed += salidaOculta[i] * pesosOcultaSalida[i];
        }
        return sigmoid(salidaRed);
    }
}

