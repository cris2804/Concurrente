# Proyecto de Procesamiento Distribuido con Redes Neuronales

## Descripción General

Este proyecto implementa un sistema de procesamiento distribuido para entrenar redes neuronales sobre textos utilizando una arquitectura maestro-nodo. El sistema divide un texto (libro) en fragmentos, los distribuye entre múltiples nodos de procesamiento, cada uno entrena su propia red neuronal, y finalmente el maestro recolecta los resultados.

## Arquitectura del Sistema

El proyecto utiliza una arquitectura cliente-servidor distribuida con tres componentes principales:

```
┌─────────────────┐
│  ClienteLibro   │
│  (Puerto 3001)  │
└────────┬────────┘
         │ Envía libro
         ↓
┌─────────────────┐         ┌──────────────┐
│     Maestro     │←────────│    Nodo 1    │
│  Puerto 3000    │         │              │
│  Puerto 3001    │←────────│    Nodo 2    │
└────────┬────────┘         │              │
         │                  │    Nodo N    │
         │                  └──────────────┘
         ↓
┌─────────────────┐
│ ClienteModelo   │
└─────────────────┘
```

### Componentes

#### 1. **Maestro** (Maestro.java)
- **Puerto 3000**: Acepta conexiones de nodos de procesamiento
- **Puerto 3001**: Acepta conexiones de clientes (ClienteLibro y ClienteModelo)
- **Funciones**:
  - Recibe el texto completo del libro desde ClienteLibro
  - Divide el libro en fragmentos equitativos según el número de nodos conectados
  - Distribuye los fragmentos a cada nodo
  - Recolecta las matrices de pesos (W1 y W2) entrenadas de cada nodo
  - Envía los modelos procesados a ClienteModelo cuando se solicita

#### 2. **Nodo** (Nodo.java / nodo.py)
- **Conecta al puerto 3000** del Maestro
- **Funciones**:
  - Recibe un fragmento del texto del Maestro
  - Realiza el preprocesamiento del texto:
    - Tokenización de palabras
    - Creación de vocabulario
    - Conversión a secuencias numéricas
  - Entrena una red neuronal con el fragmento:
    - Arquitectura: Capa de entrada → Capa oculta (2048 neuronas) → Capa de salida
    - 10,000 iteraciones de entrenamiento (Java) o 1,000 (Python)
    - Codificación one-hot para entrada y salida
    - Backpropagation con descenso de gradiente
  - Envía las matrices de pesos entrenadas (W1 y W2) de vuelta al Maestro

#### 3. **ClienteLibro** (ClienteLibro.java)
- **Conecta al puerto 3001** del Maestro
- **Funciones**:
  - Lee el archivo "Libro.txt" línea por línea
  - Envía cada oración al Maestro
  - Indica el fin del libro con "FIN_LIBRO"

#### 4. **ClienteModelo** (ClienteModelo.java)
- **Conecta al puerto 3001** del Maestro
- **Funciones**:
  - Solicita los fragmentos procesados (modelos entrenados)
  - Recibe las matrices de pesos de todos los nodos
  - Muestra los resultados procesados

## Red Neuronal

### Arquitectura
- **Capa de entrada**: Tamaño = Tamaño del vocabulario
- **Capa oculta**: 2048 neuronas
- **Capa de salida**: Tamaño = Tamaño del vocabulario
- **Función de activación**: Sigmoide
- **Tasa de aprendizaje**: 0.001

### Proceso de Entrenamiento
1. **Preprocesamiento**:
   - Convertir texto a minúsculas
   - Tokenizar en palabras
   - Crear mapeo palabra → índice
   - Convertir texto a secuencia de índices

2. **Entrenamiento**:
   - Seleccionar secuencias aleatorias de 5 palabras
   - Predecir la siguiente palabra en la secuencia
   - Forward propagation para obtener predicción
   - Backpropagation para ajustar pesos
   - Repetir por N iteraciones

3. **Resultado**:
   - Matrices W1 (entrada → oculta) y W2 (oculta → salida)
   - Estas matrices capturan patrones del texto aprendido

## Implementaciones

El proyecto incluye dos implementaciones:

### 1. Implementación Java (Completa)
Ubicación: `/Libro/src/main/java/com/mycompany/libro/`

Archivos principales:
- `Maestro.java` - Coordinador central
- `Nodo.java` - Procesador de fragmentos
- `ClienteLibro.java` - Envía el libro
- `ClienteModelo.java` - Recibe modelos procesados
- `NeuralNetwork.java` - Implementación de red neuronal
- `Preprocessor.java` - Preprocesamiento de texto
- `Main.java` - Programa independiente de entrenamiento

### 2. Implementación Python (Nodo)
Archivo: `nodo.py`

Implementación alternativa del nodo en Python usando:
- `socket` para comunicación de red
- `numpy` para operaciones matriciales
- Misma lógica de red neuronal que la versión Java

## Cómo Ejecutar el Proyecto

### Requisitos Previos
- **Java**: JDK 17 o superior
- **Maven**: Para compilar el proyecto Java
- **Python** (opcional): Python 3.x con numpy para usar nodo.py

### Pasos para Ejecutar (Versión Java)

1. **Compilar el proyecto**:
```bash
cd Libro
mvn clean compile
```

2. **Iniciar el Maestro** (Terminal 1):
```bash
mvn exec:java -Dexec.mainClass="com.mycompany.libro.Maestro"
```
El Maestro esperará conexiones en los puertos 3000 y 3001.

3. **Iniciar los Nodos** (Terminales 2, 3, 4, ...):
```bash
mvn exec:java -Dexec.mainClass="com.mycompany.libro.Nodo"
```
Inicia tantos nodos como desees para distribuir el procesamiento.

4. **Enviar el Libro** (Terminal N):
```bash
mvn exec:java -Dexec.mainClass="com.mycompany.libro.ClienteLibro"
```
Asegúrate de que el archivo `Libro.txt` esté en el directorio de ejecución.

5. **Obtener Resultados** (opcional):
```bash
mvn exec:java -Dexec.mainClass="com.mycompany.libro.ClienteModelo"
```

### Pasos para Ejecutar (Nodo Python)

1. **Instalar dependencias**:
```bash
pip install numpy
```

2. **Ejecutar nodo Python** (en lugar del paso 3 anterior):
```bash
python nodo.py
```

## Datos de Entrada

El archivo `Libro.txt` contiene texto de ejemplo:
```
alicia empezaba a sentirse muy cansada
alicia come mucho cuando no duerme
es horaa de levantarse y alistarse
alicia es una buena mejor amiga
```

Puedes reemplazar este archivo con cualquier texto que desees procesar.

## Características Principales

### Procesamiento Distribuido
- **Escalabilidad**: Soporta múltiples nodos trabajando en paralelo
- **División automática**: El maestro divide el trabajo equitativamente
- **Comunicación por sockets**: Protocolo TCP para comunicación confiable

### Entrenamiento de Redes Neuronales
- **Modelo de lenguaje**: Aprende a predecir la siguiente palabra
- **One-hot encoding**: Representación vectorial de palabras
- **Backpropagation**: Optimización mediante descenso de gradiente

### Arquitectura Concurrente
- **Multihilo**: El Maestro maneja múltiples conexiones simultáneamente
- **No bloqueante**: Cada nodo y cliente opera independientemente
- **Sincronización**: El Maestro coordina la recepción de resultados

## Aplicaciones Potenciales

1. **Generación de texto**: Predecir la siguiente palabra en una secuencia
2. **Análisis de sentimientos**: Entrenar en textos clasificados
3. **Traducción**: Adaptar para pares de idiomas
4. **Autocompletado**: Sugerir palabras mientras se escribe
5. **Análisis de corpus**: Descubrir patrones en grandes volúmenes de texto

## Limitaciones Actuales

1. **Modelo simple**: Red neuronal básica (no usa LSTM o Transformers)
2. **Sin agregación de modelos**: Los pesos de diferentes nodos no se combinan
3. **Sin persistencia**: Los modelos no se guardan en disco
4. **Codificación one-hot**: Ineficiente para vocabularios grandes
5. **Sin validación**: No hay conjunto de validación o prueba

## Posibles Mejoras

1. **Agregación federada**: Combinar pesos de múltiples nodos (promedio, consenso)
2. **Arquitecturas avanzadas**: Implementar LSTM, GRU o Transformers
3. **Embeddings**: Usar representaciones densas en lugar de one-hot
4. **Checkpoint**: Guardar y cargar modelos entrenados
5. **Métricas**: Calcular perplejidad, precisión, etc.
6. **Balanceo de carga**: Asignar más trabajo a nodos más rápidos
7. **Tolerancia a fallos**: Recuperarse si un nodo falla

## Estructura del Código

```
Concurrente/
├── README.md                          # Este archivo
├── nodo.py                            # Implementación del nodo en Python
├── Libro/
│   ├── pom.xml                        # Configuración Maven
│   ├── Libro.txt                      # Datos de entrada (texto)
│   └── src/main/java/com/mycompany/libro/
│       ├── Maestro.java               # Coordinador central
│       ├── Nodo.java                  # Procesador de fragmentos
│       ├── ClienteLibro.java          # Cliente que envía el libro
│       ├── ClienteModelo.java         # Cliente que recibe modelos
│       ├── NeuralNetwork.java         # Red neuronal
│       ├── Preprocessor.java          # Preprocesamiento de texto
│       └── Main.java                  # Programa independiente
```

## Tecnologías Utilizadas

- **Java 17**: Lenguaje principal
- **Maven**: Gestión de dependencias y compilación
- **Python 3**: Implementación alternativa del nodo
- **NumPy**: Operaciones matriciales en Python
- **Java Sockets**: Comunicación de red
- **Programación concurrente**: Threads y sincronización

## Autor

Este proyecto es parte del curso de Computación Concurrente y Distribuida.

## Licencia

Este proyecto es con fines educativos.
