package com;

import java.util.Scanner;
import java.io.Serializable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.LogRecord;

@SuppressWarnings("unused")
/**
 * La clase `Libro` representa un libro con varios atributos y métodos para administrar información del libro.
 */
/**
 * Esta clase de Java representa un libro con propiedades como id, título, autor, fecha de publicación,
 * número de páginas, disponibilidad, ISBN y descripción.
 */
public class Libro {
    private static int nextTempId = 1;
    private int id;
    private String titulo;
    private String autor;
    private int fechaPublicacion;
    private int numPaginas;
    private static boolean disponible;
    private String isbn;
    private String descripcion;

    
    // Este es un constructor para la clase `Libro` en Java. Inicializa una nueva instancia de la clase `Libro`
    // con los parámetros proporcionados: `titulo`, `autor`, `fechaPublicacion`, `numPaginas`,
    // `disponible`, `isbn` y `descripcion`.
    public Libro(String titulo, String autor, int fechaPublicacion, int numPaginas, boolean disponible,
            String isbn, String descripcion) {
        this.id = nextTempId++;
        this.titulo = titulo;
        this.autor = autor;
        this.fechaPublicacion = fechaPublicacion;
        this.numPaginas = numPaginas;
        this.disponible = disponible;
        this.isbn = isbn;
        this.descripcion = descripcion;
    }

    // Constructor existente para cuando ya tenemos un ID (por ejemplo, al leer de
    // la BD)

  // Este constructor particular en la clase `Libro` se utiliza cuando se crea una nueva instancia de un libro
  // (`Libro`) objeto con un ID existente.
    public Libro(int id, String titulo, String autor, int fechaPublicacion, int numPaginas, boolean disponible,
            String isbn, String descripcion) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.fechaPublicacion = fechaPublicacion;
        this.numPaginas = numPaginas;
        this.disponible = disponible;
        this.isbn = isbn;
        this.descripcion = descripcion;

        // Asegurarse de que nextTempId siempre sea mayor que cualquier ID existente
        if (id >= nextTempId) {
            nextTempId = id + 1;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(Libro.class.getName());

    static {
        // Remover los manejadores por defecto
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        // Configurar el logger para que escriba los mensajes en la consola
        Handler consoleHandler = new ConsoleHandler();
        Formatter formatter = new SimpleFormatter();
        consoleHandler.setFormatter(formatter);
        LOGGER.addHandler(consoleHandler);
        LOGGER.setLevel(Level.ALL); // Establecer el nivel de logueo
    }

    private static void removeDefaultHandlers(Logger logger) {
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            logger.removeHandler(handler);
        }
    }

    // Clase que define un formateador personalizado
    static class CustomFormatter extends SimpleFormatter {
        @Override
        public String format(LogRecord record) {
            return record.getMessage() + "\n";
        }
    }

    private static void configurarLogger() {
        Logger rootLogger = Logger.getLogger("");
        ConsoleHandler handler = new ConsoleHandler();

        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getLevel() + ": " + record.getMessage() + "\n";
            }
        });

        // Eliminar otros handlers y agregar el personalizado
        rootLogger.setUseParentHandlers(false);
        rootLogger.addHandler(handler);
    }

    // Métodos getter y setter
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getfechaPublicacion() {
        return fechaPublicacion;
    }

    public void setfechaPublicacion(int fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getNumPaginas() {
        return numPaginas;
    }

    public void setNumPaginas(int numPaginas) {
        this.numPaginas = numPaginas;
    }

    public static boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

   

  /**
 * La función `aLibro` analiza una cadena para crear un objeto `Libro` con atributos específicos, maneja
 * errores de formato durante el proceso.
 * 
 * @param texto El método `aLibro` toma una entrada de cadena `texto` que representa una cadena separada
 * por comas que contiene información sobre un libro. La información se espera que esté en el siguiente
 * orden:
 * @return El método `aLibro` devuelve una instancia de la clase `Libro` si la entrada `texto` se analiza
 * correctamente y contiene 7 partes separadas por comas. Si el análisis es exitoso, se crea un nuevo
 * objeto `Libro` con los datos extraídos y se devuelve. Si hay errores durante el análisis (por ejemplo,
 * NumberFormatException), se registran mensajes de advertencia adecuados y se devuelve `null`.
 */
    public String aTexto() {
        return id + "," + titulo + "," + autor + "," + fechaPublicacion + "," + numPaginas + "," + disponible + ","
                + isbn + "," + descripcion;
    }

/**
 * La función `aLibro` analiza una cadena para crear un objeto `Libro` con atributos específicos, maneja
 * errores de formato durante el proceso.
 * 
 * @param texto El método `aLibro` toma una entrada de cadena `texto` que representa una cadena separada
 * por comas que contiene información sobre un libro. La información se espera que esté en el siguiente
 * orden:
 * @return El método `aLibro` devuelve una instancia de la clase `Libro` si la entrada `texto` se analiza
 * correctamente y contiene 7 partes separadas por comas. Si el análisis es exitoso, se crea un nuevo
 * objeto `Libro` con los datos extraídos y se devuelve. Si hay errores durante el análisis (por ejemplo,
 * NumberFormatException), se registran mensajes de advertencia adecuados y se devuelve `null`.
 */
    public static Libro aLibro(String texto) {
        String[] partes = texto.split(",");
        if (partes.length == 7) {
            try {
                String titulo = partes[0];
                String autor = partes[1];
                int fechaPublicacion = Integer.parseInt(partes[2]);
                int numPaginas = Integer.parseInt(partes[3]);
                boolean disponible = Boolean.parseBoolean(partes[4]);
                String isbn = partes[5];
                String descripcion = partes[6];

                return new Libro(titulo, autor, fechaPublicacion, numPaginas, disponible, isbn, descripcion);
            } catch (NumberFormatException e) {
                LOGGER.warning("Error al convertir datos numéricos en la línea: " + texto);
                LOGGER.warning("Mensaje de error: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Formato incorrecto en la línea: " + texto);
            LOGGER.warning("Número de campos encontrados: " + partes.length + ", se esperaban 7");
        }
        return null;
    }

  
/**
 * El método `editarLibro` en Java permite al usuario actualizar varios atributos de un objeto libro
 * tomando entrada del usuario a través de la clase Scanner.
 * 
 * @param scanner El método `editarLibro` se utiliza para editar los detalles de un objeto libro tomando
 * entrada del usuario a través del objeto `Scanner`.
 */
    public void editarLibro(Scanner scanner) {
        LOGGER.info("Información actual del libro:");
        LOGGER.info(this.toString());

        LOGGER.info("Ingrese el nuevo título del libro:");
        String nuevoTitulo = scanner.nextLine();
        this.setTitulo(nuevoTitulo);

        LOGGER.info("Ingrese el nuevo autor del libro:");
        String nuevoAutor = scanner.nextLine();
        this.setAutor(nuevoAutor);

        LOGGER.info("Ingrese el nuevo año de publicación:");
        int nuevaFechaPublicacion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        this.setfechaPublicacion(nuevaFechaPublicacion);

        LOGGER.info("Ingrese el nuevo número de páginas:");
        int nuevasPaginas = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        this.setNumPaginas(nuevasPaginas);

        LOGGER.info("Actualizar estado del libro (true/false):");
        boolean libroDisponible = scanner.nextBoolean();
        scanner.nextLine(); // Consumir el salto de línea
        this.setDisponible(libroDisponible);

        LOGGER.info("Ingrese el nuevo ISBN del libro:");
        String nuevoIsbn = scanner.nextLine();
        this.setIsbn(nuevoIsbn);

        LOGGER.info("Ingrese una nueva descripción del libro:");
        String nuevaDescripcion = scanner.nextLine();
        this.setDescripcion(nuevaDescripcion);

        LOGGER.info("El libro ha sido editado exitosamente.");
    }

   /**
 * La función `cambiarDisponibilidad` cambia el estado de disponibilidad de un libro y registra un mensaje
 * según sea necesario.
 */
    public void cambiarDisponibilidad() {
        if (this.disponible) {
            this.setDisponible(false);
            LOGGER.info("Libro ocultado exitosamente.");
        } else {
            this.setDisponible(true);
            LOGGER.info("Libro mostrado exitosamente.");
        }
    }

// La anotación `@Override` en Java se utiliza para indicar que un método se está sobreescribiendo desde una
// clase padre o interfaz. En este caso, el método `toString()` se está sobreescribiendo en la clase `Libro`.
//@Override
    @Override
    public String toString() {
        return "Libro [id=" + id + ", titulo=" + titulo + ", autor=" + autor + ", fechaPublicacion=" + fechaPublicacion
                +
                ", numPaginas=" + numPaginas + ", disponible=" + disponible + ", isbn=" + isbn +
                ", descripcion=" + descripcion + "]";
    }
}

