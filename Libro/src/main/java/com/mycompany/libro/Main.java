
package com.mycompany.libro;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Leer el archivo de texto "libro.txt"
            BufferedReader libroReader = new BufferedReader(new FileReader("libro.txt"));
            String linea;
            StringBuilder libro = new StringBuilder();
            while ((linea = libroReader.readLine()) != null) {
                libro.append(linea).append(" ");
            }
            libroReader.close();

            String texto = libro.toString();

            // Preprocesamiento del texto
            Preprocessor preprocessor = new Preprocessor();
            int[] secuencia = preprocessor.procesarTexto(texto);

            // Parámetros de la red
            int vocabSize = preprocessor.getVocabSize();
            NeuralNetwork nn = new NeuralNetwork(vocabSize, 2048, vocabSize, 0.001);

            // Entrenamiento
            int nIteraciones = 100000;
            int longitudSecuencia = 5; // Secuencia de 5 palabras

            for (int epoch = 0; epoch < nIteraciones; epoch++) {
                int idx = (int) (Math.random() * (secuencia.length - longitudSecuencia));
                int[] xSeq = Arrays.copyOfRange(secuencia, idx, idx + longitudSecuencia);
                int yIdx = secuencia[idx + longitudSecuencia];

                double[] x = new double[vocabSize];
                for (int i : xSeq) {
                    x[i] = 1;  // One-hot encoding
                }

                double[] y = new double[vocabSize];
                y[yIdx] = 1;  // One-hot encoding para la salida

                // Forward propagation
                double[][] activaciones = nn.forwardPropagation(x);

                // Backpropagation
                nn.backPropagation(x, y, activaciones[0], activaciones[1]);

                // Imprimir pérdida cada 1000 iteraciones
                if (epoch % 1000 == 0) {
                    double loss = -Arrays.stream(y).map(v -> v * Math.log(activaciones[1][(int)v])).sum(); // Cross-entropy loss
                    System.out.println("Epoch " + epoch + ", Pérdida: " + loss);
                }
            }

            // Ingresar una secuencia de palabras para predecir la siguiente palabra
            Scanner scanner = new Scanner(System.in);
            System.out.println("Ingrese una secuencia de palabras para predecir la siguiente palabra:");
            String parrafo = scanner.nextLine();
            String[] palabras = parrafo.toLowerCase().split("\\s+");
            int[] secuenciaParrafo = Arrays.stream(palabras).mapToInt(preprocessor.getWordToIdx()::get).toArray();

            double[] xPrediccion = new double[vocabSize];
            for (int i : secuenciaParrafo) {
                xPrediccion[i] = 1;  // One-hot encoding
            }

            double[][] activaciones = nn.forwardPropagation(xPrediccion);
            int siguientePalabraIdx = getMaxIndex(activaciones[1]);
            String siguientePalabra = preprocessor.getIdxToWord().get(siguientePalabraIdx);

            System.out.println("La siguiente palabra después de '" + parrafo + "' es: " + siguientePalabra);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Obtener el índice del valor máximo (predicción)
    public static int getMaxIndex(double[] arr) {
        int maxIdx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIdx]) {
                maxIdx = i;
            }
        }
        return maxIdx;
    }
}


