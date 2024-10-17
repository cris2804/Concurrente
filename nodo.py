import socket
import random
import numpy as np

class Preprocessor:
    def __init__(self):
        self.word_to_idx = {}
        self.vocab_size = 0

    def procesar_texto(self, texto):
        palabras = texto.lower().split()
        vocabulario = set(palabras)
        for idx, palabra in enumerate(vocabulario):
            self.word_to_idx[palabra] = idx
        self.vocab_size = len(vocabulario)
        secuencia = [self.word_to_idx[palabra] for palabra in palabras]
        return secuencia

    def get_vocab_size(self):
        return self.vocab_size


class NeuralNetwork:
    def __init__(self, input_size, hidden_size, output_size, learning_rate):
        self.input_size = input_size
        self.hidden_size = hidden_size
        self.output_size = output_size
        self.learning_rate = learning_rate
        self.W1 = np.random.rand(input_size, hidden_size)
        self.W2 = np.random.rand(hidden_size, output_size)

    def forward_propagation_hidden(self, x):
        return self.sigmoid(np.dot(x, self.W1))

    def forward_propagation_output(self, hidden_layer):
        return self.sigmoid(np.dot(hidden_layer, self.W2))

    def back_propagation(self, x, y, hidden_layer, output_layer):
        output_error = output_layer - y
        hidden_error = np.dot(output_error, self.W2.T) * self.sigmoid_derivative(hidden_layer)

        self.W2 -= self.learning_rate * np.dot(hidden_layer.T, output_error)
        self.W1 -= self.learning_rate * np.dot(x.T, hidden_error)

    def sigmoid(self, x):
        return 1 / (1 + np.exp(-x))

    def sigmoid_derivative(self, x):
        return x * (1 - x)

    def get_weights(self):
        return self.W1, self.W2


def convertir_matriz_a_string(matriz):
    return '\n'.join(' '.join(map(str, fila)) for fila in matriz)


def procesar_texto(fragmento):
    preprocessor = Preprocessor()
    secuencia = preprocessor.procesar_texto(fragmento)

    vocab_size = preprocessor.get_vocab_size()
    nn = NeuralNetwork(vocab_size, 2048, vocab_size, 0.001)

    n_iteraciones = 1000
    longitud_secuencia = 5

    for epoch in range(n_iteraciones):
        idx = random.randint(0, len(secuencia) - longitud_secuencia - 1)
        x_seq = secuencia[idx: idx + longitud_secuencia]
        y_idx = secuencia[idx + longitud_secuencia]

        x = np.zeros((1, vocab_size))
        x[0, x_seq] = 1

        y = np.zeros((1, vocab_size))
        y[0, y_idx] = 1

        hidden_layer = nn.forward_propagation_hidden(x)
        output_layer = nn.forward_propagation_output(hidden_layer)

        nn.back_propagation(x, y, hidden_layer, output_layer)

    W1, W2 = nn.get_weights()
    return convertir_matriz_a_string(W1) + "\nEND_MATRIX\n" + convertir_matriz_a_string(W2) + "\nEND_MATRIX"


def nodo():
    try:
        # Conectar al Maestro en el puerto 3000
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect(('localhost', 3000))
            print("Conectado al maestro")

            # Recibir fragmento del Maestro
            fragmento = ""
            while True:
                data = s.recv(1024).decode('utf-8')
                if "END" in data:
                    break
                fragmento += data

            print(f"Fragmento recibido:\n{fragmento}")

            # Procesar el fragmento con IA
            resultado = procesar_texto(fragmento)

            # Enviar el resultado (matrices de pesos) al Maestro
            s.sendall(resultado.encode('utf-8'))
            print("Matrices de pesos enviadas al maestro.")

    except Exception as e:
        print(f"Error: {e}")


if __name__ == "__main__":
    nodo()

