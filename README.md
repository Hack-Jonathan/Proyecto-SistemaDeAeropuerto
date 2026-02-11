# Sistema de Gestión de Red Aérea

Aplicación de escritorio desarrollada en **Java 21** con **JavaFX** que modela una red de aeropuertos y vuelos mediante un grafo dirigido ponderado, permitiendo visualizar rutas sobre un mapa interactivo, buscar caminos óptimos y gestionar la red en tiempo real.

---

## Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Estructura de Paquetes](#estructura-de-paquetes)
- [Estructuras de Datos Utilizadas](#estructuras-de-datos-utilizadas)
- [Algoritmos Implementados](#algoritmos-implementados)
- [Principios de Diseño Aplicados](#principios-de-diseño-aplicados)
- [Prevención de Code Smells](#prevención-de-code-smells)
- [Interfaz Gráfica (JavaFX)](#interfaz-gráfica-javafx)
- [Persistencia de Datos](#persistencia-de-datos)
- [Requisitos](#requisitos)
- [Ejecución](#ejecución)

---

## Descripción General

El sistema representa una **red aérea mundial** donde los aeropuertos son nodos y los vuelos son aristas dirigidas con peso (distancia, duración, precio). El usuario puede:

- Agregar y eliminar aeropuertos haciendo clic sobre el mapa.
- Crear y eliminar vuelos entre aeropuertos existentes.
- Buscar la ruta más corta entre dos puntos (Dijkstra).
- Explorar todas las rutas alternativas (BFS).
- Consultar vuelos directos y estadísticas de conectividad.
- Visualizar todo sobre un mapa mundi interactivo con Canvas.

---

## Arquitectura del Proyecto

El proyecto sigue una **arquitectura en capas** con separación clara de responsabilidades:

```
┌─────────────────────────────┐
│           UI (ui/)          │  ← Renderizado y datos iniciales
├─────────────────────────────┤
│       Main (Application)    │  ← Orquestación y eventos JavaFX
├─────────────────────────────┤
│      Servicios (service/)   │  ← Lógica de negocio y algoritmos
├─────────────────────────────┤
│    Persistencia (persist/)  │  ← Serialización a disco
├─────────────────────────────┤
│        Grafo (graph/)       │  ← Estructura del grafo dirigido
├─────────────────────────────┤
│       Modelo (model/)       │  ← Entidades del dominio
└─────────────────────────────┘
```

---

## Estructura de Paquetes

| Paquete | Responsabilidad | Clases |
|---|---|---|
| `model` | Entidades del dominio con validación | `Aeropuerto`, `Vuelo`, `CodigoIATA`, `Coordenadas`, `Ubicacion` |
| `graph` | Representación del grafo (lista de adyacencia) | `RedArea` |
| `service` | Algoritmos de búsqueda y estadísticas | `BuscadorRutas`, `EstadisticaRed` |
| `persistence` | Serialización y carga de datos | `PersistenciaRed` |
| `ui` | Renderizado del mapa y datos de prueba | `MapaRenderer`, `DatosIniciales` |

---

## Estructuras de Datos Utilizadas

### Grafo Dirigido Ponderado — Lista de Adyacencia

La red aérea se modela con dos `HashMap`:

```java
Map<CodigoIATA, Aeropuerto>       // Acceso O(1) por código IATA
Map<Aeropuerto, List<Vuelo>>      // Lista de adyacencia: vuelos salientes
```

**¿Por qué lista de adyacencia?** La red aérea es un grafo disperso (cada aeropuerto no se conecta con todos los demás), por lo que una matriz de adyacencia desperdiciaría memoria. La lista permite acceso eficiente a los vecinos de cada nodo.

### Otras estructuras clave

| Estructura | Uso | Justificación |
|---|---|---|
| `HashMap` | Mapa de distancias en Dijkstra, mapa de predecesores | Acceso O(1) para consultas y actualizaciones |
| `PriorityQueue` | Cola de prioridad en Dijkstra | Extracción del nodo con menor distancia en O(log n) |
| `LinkedList` (como Queue) | BFS para rutas alternativas | Cola FIFO para recorrido por niveles |
| `HashSet` | Control de nodos visitados | Verificación de pertenencia en O(1) |
| `ArrayList` | Almacenamiento de rutas y resultados | Acceso indexado O(1), iteración eficiente |

---

## Algoritmos Implementados

### Dijkstra — Ruta Más Corta

Encuentra el camino con menor distancia entre dos aeropuertos. Utiliza una `PriorityQueue` con comparador basado en la distancia acumulada y un mapa de predecesores para reconstruir la ruta.

- **Complejidad:** O((V + E) log V)
- **Criterio de peso:** Distancia geográfica (Haversine)

### BFS — Rutas Alternativas

Explora todas las rutas posibles entre origen y destino sin repetir aeropuertos, utilizando una cola de caminos parciales. Cada camino se extiende nivel por nivel hasta alcanzar el destino.

- **Complejidad:** O(V!) en el peor caso (acotado por la prevención de ciclos)
- **Uso:** Mostrar al usuario todas las opciones de viaje

### Haversine — Cálculo de Distancias

Calcula la distancia real entre dos coordenadas geográficas sobre la superficie terrestre, considerando la curvatura del planeta.

---

## Principios de Diseño Aplicados

### Single Responsibility Principle (SRP)

Cada clase tiene una única razón de cambio:

- `RedArea` → gestiona la estructura del grafo
- `BuscadorRutas` → ejecuta algoritmos de búsqueda
- `EstadisticaRed` → calcula métricas de la red
- `MapaRenderer` → renderiza elementos visuales
- `PersistenciaRed` → maneja lectura/escritura en disco

### Encapsulamiento y Value Objects

- `CodigoIATA` encapsula y valida códigos de 3 caracteres, evitando que circulen `String` sin validar por el sistema (*Primitive Obsession*).
- `Coordenadas` valida rangos de latitud/longitud y encapsula el cálculo de distancias.
- `Ubicacion` agrupa ciudad y país como un objeto de valor.

### Inmutabilidad Controlada

Los campos que no cambian tras la construcción del objeto se declaran `final`, garantizando:

- No se reasignan accidentalmente.
- Seguridad en contextos concurrentes.
- Intención clara en el código.

### Inyección de Dependencias (Constructor Injection)

Las clases de servicio (`BuscadorRutas`, `EstadisticaRed`) reciben `RedArea` por constructor, lo que:

- Facilita testing con diferentes instancias.
- Elimina acoplamiento a la creación del grafo.
- Hace explícitas las dependencias.

---

## Prevención de Code Smells

| Code Smell | Cómo se evita |
|---|---|
| **Primitive Obsession** | `CodigoIATA`, `Coordenadas` y `Ubicacion` reemplazan el uso de `String` y `double` sueltos |
| **God Class** | La lógica se distribuye en capas: `Main` no contiene algoritmos ni acceso a datos |
| **Feature Envy** | El cálculo de distancias vive en `Coordenadas`, no en clases externas |
| **Long Parameter List** | `Aeropuerto` recibe objetos compuestos en lugar de 6 primitivos |
| **Magic Numbers** | Constantes con nombre descriptivo; validaciones explícitas en constructores |
| **Duplicated Code** | Método utilitario `crearCodigoIATA()` centraliza la conversión `String → CodigoIATA` |

---

## Interfaz Gráfica (JavaFX)

### Componentes Principales

- **Canvas:** Renderizado del mapa mundi con aeropuertos y vuelos dibujados mediante `GraphicsContext`.
- **Proyección geográfica:** Conversión de coordenadas (latitud/longitud) a píxeles del canvas con proyección equirectangular.
- **Interacción:** Click sobre el mapa para agregar aeropuertos y seleccionar origen/destino de vuelos.
- **Detección de click:** Búsqueda del aeropuerto más cercano al punto clickeado con distancia euclidiana.

### Visualización de Rutas

- **Rutas alternativas:** Se dibujan en rojo.
- **Ruta más corta:** Se resalta en dorado con mayor grosor.
- **Flechas direccionales:** Indican el sentido del vuelo.
- **Etiquetas:** Muestran la distancia en km sobre cada tramo.

### Adaptabilidad

La ventana se ajusta automáticamente al tamaño de la pantalla del usuario mediante `Screen.getPrimary().getVisualBounds()`.

---

## Persistencia de Datos

El sistema utiliza **serialización nativa de Java** (`ObjectOutputStream` / `ObjectInputStream`) para guardar y restaurar el estado completo de la red.

- Todas las clases del modelo implementan `Serializable`.
- Al cerrar la aplicación, el estado se guarda automáticamente.
- Al iniciar, se carga el estado previo o se generan datos iniciales.
- El botón "Reiniciar" permite volver al estado por defecto.

---

## Requisitos

- **Java:** 21 o superior
- **Maven:** 3.8+
- **JavaFX:** 21.0.2 (gestionado automáticamente por Maven)

---

## Ejecución

```bash
# Compilar y ejecutar
mvn clean javafx:run
```

---

## Autor
Jonathan Pacalla
Proyecto desarrollado como parte del curso de Estructuras de Datos — ESPOL.
