/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libro;
import java.util.Random;

public class NeuralNetwork {
    private double[][] W1;  // Pesos entre la capa de entrada y la capa oculta
    private double[][] W2;  // Pesos entre la capa oculta y la capa de salida
    private int inputSize, hiddenSize, outputSize;
    private double learningRate;

    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize, double learningRate) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.learningRate = learningRate;
        Random rand = new Random();

        // Inicialización de los pesos
        W1 = new double[inputSize][hiddenSize];
        W2 = new double[hiddenSize][outputSize];

        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                W1[i][j] = rand.nextGaussian() * 0.01;
            }
        }
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                W2[i][j] = rand.nextGaussian() * 0.01;
            }
        }
    }

    private double[] softmax(double[] x) {
        double[] exp_x = new double[x.length];
        double sum = 0.0;
        double max = Double.NEGATIVE_INFINITY;

        for (double v : x) {
            if (v > max) {
                max = v;
            }
        }

        for (int i = 0; i < x.length; i++) {
            exp_x[i] = Math.exp(x[i] - max);  // Evitar overflow numérico
            sum += exp_x[i];
        }

        for (int i = 0; i < exp_x.length; i++) {
            exp_x[i] /= sum;
        }

        return exp_x;
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double sigmoidDerivada(double x) {
        return x * (1 - x);
    }

    // Propagación hacia adelante
    public double[][] forwardPropagation(double[] x) {
        // Capa oculta
        double[] z1 = new double[hiddenSize];
        double[] a1 = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                z1[i] += x[j] * W1[j][i];
            }
            a1[i] = sigmoid(z1[i]);
        }

        // Capa de salida
        double[] z2 = new double[outputSize];
        double[] a2 = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                z2[i] += a1[j] * W2[j][i];
            }
        }

        a2 = softmax(z2);

        return new double[][]{a1, a2};  // Retornamos las activaciones para usar en backpropagation
    }

    // Backpropagation y actualización de pesos
    public void backPropagation(double[] x, double[] y, double[] a1, double[] a2) {
        // Error de salida
        double[] errorSalida = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            errorSalida[i] = a2[i] - y[i];
        }

        // Gradientes para W2
        double[][] dW2 = new double[hiddenSize][outputSize];
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                dW2[i][j] = a1[i] * errorSalida[j];
            }
        }

        // Error en la capa oculta
        double[] errorOculto = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                errorOculto[i] += errorSalida[j] * W2[i][j];
            }
            errorOculto[i] *= sigmoidDerivada(a1[i]);
        }

        // Gradientes para W1
        double[][] dW1 = new double[inputSize][hiddenSize];
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                dW1[i][j] = x[i] * errorOculto[j];
            }
        }

        // Actualización de los pesos
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                W2[i][j] -= learningRate * dW2[i][j];
            }
        }
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                W1[i][j] -= learningRate * dW1[i][j];
            }
        }
    }
}





