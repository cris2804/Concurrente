package com.mycompany.libro;

import java.io.*;
import java.net.*;
import java.util.*;

public class Nodo {

    public static void main(String[] args) {
        try {
            // Conectar al Maestro en el puerto 5000
            Socket socket = new Socket("localhost", 3000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Recibir fragmento del Maestro (con múltiples líneas)
            StringBuilder fragmentoCompleto = new StringBuilder();
            String linea;
            while (!(linea = in.readLine()).equals("END")) {
                fragmentoCompleto.append(linea).append("\n");
            }
            String fragmento = fragmentoCompleto.toString();
            System.out.println("Fragmento recibido: " + fragmento);

            // Procesar el fragmento con IA
            String resultado = procesarTexto(fragmento);

            // Enviar el resultado (matrices de pesos) al Maestro
            out.println(resultado);
            System.out.println("Matrices de pesos enviadas al Maestro.");

            // Cerrar las conexiones
            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String procesarTexto(String texto) {
        // Crear instancias de Preprocessor y NeuralNetwork
        Preprocessor preprocessor = new Preprocessor();
        int[] secuencia = preprocessor.procesarTexto(texto);

        // Parámetros de la red neuronal
        int vocabSize = preprocessor.getVocabSize();
        NeuralNetwork nn = new NeuralNetwork(vocabSize, 2048, vocabSize, 0.001);

        // Entrenamiento
        int nIteraciones = 10000;  // Reducir para pruebas
        int longitudSecuencia = 5;

        for (int epoch = 0; epoch < nIteraciones; epoch++) {
            int idx = (int) (Math.random() * (secuencia.length - longitudSecuencia));
            int[] xSeq = Arrays.copyOfRange(secuencia, idx, idx + longitudSecuencia);
            int yIdx = secuencia[idx + longitudSecuencia];

            double[] x = new double[vocabSize];
            for (int i : xSeq) {
                x[i] = 1;  // One-hot encoding
            }

            double[] y = new double[vocabSize];
            y[yIdx] = 1;

            double[] hiddenLayer = nn.forwardPropagationHidden(x);
            double[] outputLayer = nn.forwardPropagationOutput(hiddenLayer);

            nn.backPropagation(x, y, hiddenLayer, outputLayer);
        }

        // Obtener las matrices de pesos
        double[][] W1 = nn.getW1();
        double[][] W2 = nn.getW2();

        // Convertir las matrices a string para enviar
        return convertirMatrizAString(W1) + "END_MATRIX\n" + convertirMatrizAString(W2) + "END_MATRIX";
    }

    private static String convertirMatrizAString(double[][] matriz) {
        StringBuilder sb = new StringBuilder();
        for (double[] fila : matriz) {
            for (double valor : fila) {
                sb.append(valor).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    static class Preprocessor {
        private Map<String, Integer> wordToIdx = new HashMap<>();
        private int vocabSize;

        public int[] procesarTexto(String texto) {
            String[] palabras = texto.toLowerCase().split("\\s+");
            Set<String> vocabulario = new HashSet<>(Arrays.asList(palabras));

            int idx = 0;
            for (String palabra : vocabulario) {
                wordToIdx.put(palabra, idx++);
            }
            vocabSize = vocabulario.size();

            int[] secuencia = new int[palabras.length];
            for (int i = 0; i < palabras.length; i++) {
                secuencia[i] = wordToIdx.get(palabras[i]);
            }
            return secuencia;
        }

        public int getVocabSize() {
            return vocabSize;
        }
    }

    static class NeuralNetwork {
        private double[][] W1;
        private double[][] W2;
        private int inputSize, hiddenSize, outputSize;
        private double learningRate;

        public NeuralNetwork(int inputSize, int hiddenSize, int outputSize, double learningRate) {
            this.inputSize = inputSize;
            this.hiddenSize = hiddenSize;
            this.outputSize = outputSize;
            this.learningRate = learningRate;
            W1 = new double[inputSize][hiddenSize];
            W2 = new double[hiddenSize][outputSize];
            Random rand = new Random();
            for (int i = 0; i < inputSize; i++) {
                for (int j = 0; j < hiddenSize; j++) {
                    W1[i][j] = rand.nextDouble();
                }
            }
            for (int i = 0; i < hiddenSize; i++) {
                for (int j = 0; j < outputSize; j++) {
                    W2[i][j] = rand.nextDouble();
                }
            }
        }

        public double[] forwardPropagationHidden(double[] input) {
            double[] hiddenLayer = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) {
                for (int i = 0; i < inputSize; i++) {
                    hiddenLayer[j] += input[i] * W1[i][j];
                }
                hiddenLayer[j] = sigmoid(hiddenLayer[j]);
            }
            return hiddenLayer;
        }

        public double[] forwardPropagationOutput(double[] hiddenLayer) {
            double[] outputLayer = new double[outputSize];
            for (int k = 0; k < outputSize; k++) {
                for (int j = 0; j < hiddenSize; j++) {
                    outputLayer[k] += hiddenLayer[j] * W2[j][k];
                }
                outputLayer[k] = sigmoid(outputLayer[k]);
            }
            return outputLayer;
        }

        public void backPropagation(double[] input, double[] target, double[] hiddenLayer, double[] outputLayer) {
            double[] outputError = new double[outputSize];
            for (int k = 0; k < outputSize; k++) {
                outputError[k] = outputLayer[k] - target[k];
            }

            double[] hiddenError = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) {
                for (int k = 0; k < outputSize; k++) {
                    hiddenError[j] += outputError[k] * W2[j][k];
                }
                hiddenError[j] *= sigmoidDerivative(hiddenLayer[j]);
            }

            for (int j = 0; j < hiddenSize; j++) {
                for (int k = 0; k < outputSize; k++) {
                    W2[j][k] -= learningRate * outputError[k] * hiddenLayer[j];
                }
            }

            for (int i = 0; i < inputSize; i++) {
                for (int j = 0; j < hiddenSize; j++) {
                    W1[i][j] -= learningRate * hiddenError[j] * input[i];
                }
            }
        }

        private double sigmoid(double x) {
            return 1.0 / (1.0 + Math.exp(-x));
        }

        private double sigmoidDerivative(double x) {
            return x * (1 - x);
        }

        public double[][] getW1() {
            return W1;
        }

        public double[][] getW2() {
            return W2;
        }
    }
}
