/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libro;
import java.util.*;

public class Preprocessor {
    private Map<String, Integer> wordToIdx;
    private Map<Integer, String> idxToWord;
    private int vocabSize;

    public Preprocessor() {
        wordToIdx = new HashMap<>();
        idxToWord = new HashMap<>();
    }

    public int getVocabSize() {
        return vocabSize;
    }

    public Map<String, Integer> getWordToIdx() {
        return wordToIdx;
    }

    public Map<Integer, String> getIdxToWord() {
        return idxToWord;
    }

    public int[] procesarTexto(String texto) {
        // Convertir a minúsculas y dividir en palabras
        String[] palabras = texto.toLowerCase().split("\\s+");
        Set<String> vocabulario = new HashSet<>(Arrays.asList(palabras));

        // Crear los diccionarios wordToIdx e idxToWord
        int idx = 0;
        for (String palabra : vocabulario) {
            wordToIdx.put(palabra, idx);
            idxToWord.put(idx, palabra);
            idx++;
        }

        vocabSize = vocabulario.size();

        // Convertir el texto en una secuencia de índices
        int[] secuencia = new int[palabras.length];
        for (int i = 0; i < palabras.length; i++) {
            secuencia[i] = wordToIdx.get(palabras[i]);
        }

        return secuencia;
    }
}




