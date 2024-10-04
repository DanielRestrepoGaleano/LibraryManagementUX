package com;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La clase `ConexionBD` proporciona métodos para interactuar con una base de
 * datos MySQL para realizar
 * operaciones relacionadas con la gestión de libros y préstamos.
 */
public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // El método `getConnection()` en la clase `ConexionBD` es un método estático
    // privado que establece
    // una conexión con una base de datos MySQL utilizando la URL, el nombre de
    // usuario y la contraseña
    // proporcionados. Lanza una excepción `SQLException` si hay un problema con la
    // conexión.
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * El método `crearLibro` inserta un nuevo registro de libro en una tabla de
     * base de datos con los
     * detalles del libro proporcionados.
     * 
     * @param libro El método `crearLibro` se utiliza para insertar un nuevo
     *              registro en una tabla de
     *              base de datos llamada `libros` con la información proporcionada
     *              de un libro (`Libro` objeto).
     *              Los parámetros requeridos para este método son los siguientes:
     */
    public static void crearLibro(Libro libro) throws SQLException {
        String query = "INSERT INTO libros (id, titulo, autor, fechaPublicacion, numPaginas, disponible, isbn, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, libro.getId());
            pstmt.setString(2, libro.getTitulo());
            pstmt.setString(3, libro.getAutor());
            pstmt.setInt(4, libro.getfechaPublicacion());
            pstmt.setInt(5, libro.getNumPaginas());
            pstmt.setBoolean(6, libro.isDisponible());
            pstmt.setString(7, libro.getIsbn());
            pstmt.setString(8, libro.getDescripcion());
            pstmt.executeUpdate();
        }
    }
    public static boolean isLibroDisponible(int idLibro) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT disponible FROM libros WHERE id = ?");
        statement.setInt(1, idLibro);
        ResultSet resultSet = statement.executeQuery();
        boolean disponible = false;
        if (resultSet.next()) {
            disponible = resultSet.getBoolean("disponible");
        }
        conn.close();
        return disponible;
    }

    /**
     * El método `leerLibro` lee un libro de una base de datos según el ID
     * proporcionado.
     * 
     * @param id El método `leerLibro` toma un entero `id` como parámetro. Este `id`
     *           se utiliza para
     *           consultar la base de datos para un libro específico con el ID
     *           coincidente. Si se encuentra un
     *           libro con ese ID en la base de datos, el método crea un nuevo
     *           objeto `Libro` con los detalles
     *           obtenidos de la base de datos.
     * @return El método `leerLibro` devuelve un objeto `Libro` con los detalles
     *         obtenidos de la base
     *         de datos según el ID proporcionado. Si se encuentra un registro
     *         coincidente en la base de datos,
     *         se crea un nuevo objeto `Libro` con los datos obtenidos y se
     *         devuelve. Si no se encuentra un
     *         registro, el método devuelve `null`.
     */
    public static Libro leerLibro(int id) throws SQLException {
        String query = "SELECT * FROM libros WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Libro(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getInt("fechaPublicacion"),
                            rs.getInt("numPaginas"),
                            rs.getBoolean("disponible"),
                            rs.getString("isbn"),
                            rs.getString("descripcion"), id);
                }
            }
        }
        return null;
    }


    public static List<Libro> buscarLibrosPrestados(String documento) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM prestamos WHERE documento = ? AND devuelto = false");
        pstmt.setString(1, documento);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Libro libro = new Libro(0, documento, documento, 0, 0, false, documento, documento, 0);
            libro.setTitulo(rs.getString("titulo_libro"));
            libro.setAutor(rs.getString("autor_libro"));
            libro.setIsbn(rs.getString("isbn_libro"));
            libro.setFechaPrestamo(rs.getDate("fecha_prestamo"));
            libros.add(libro);
        }
        conn.close();
        return libros;
    }

    public static List<Libro> buscarLibrosDevolvidos(String documento) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM prestamos WHERE documento = ? AND devuelto = true");
        pstmt.setString(1, documento);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Libro libro = new Libro(0, documento, documento, 0, 0, false, documento, documento, 0);
            libro.setTitulo(rs.getString("titulo_libro"));
            libro.setAutor(rs.getString("autor_libro"));
            libro.setIsbn(rs.getString("isbn_libro"));
            libro.setFechaDevolucion(rs.getDate("fecha_devolucion"));
            libros.add(libro);
        }
        conn.close();
        return libros;
    }

    /**
     * El método `actualizarLibro` actualiza un registro de libro en una tabla de
     * base de datos con la
     * información proporcionada según el ID del libro.
     * 
     * @param id    El parámetro `id` en el método `actualizarLibro` es el
     *              identificador único del libro
     *              que se desea actualizar en la base de datos. Se utiliza en la
     *              consulta SQL para especificar qué
     *              libro debe actualizarse según su ID.
     * @param libro El parámetro `libro` en el método `actualizarLibro` representa
     *              un objeto de la
     *              clase `Libro`, que contiene información sobre un libro como su
     *              título, autor, fecha de publicación,
     *              número de páginas, estado de disponibilidad, ISBN y descripción.
     */
    public static void actualizarLibro(int id, Libro libro) throws SQLException {
        String query = "UPDATE libros SET titulo = ?, autor = ?, fechaPublicacion = ?, numPaginas = ?, disponible = ?, isbn = ?, descripcion = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAutor());
            pstmt.setInt(3, libro.getfechaPublicacion());
            pstmt.setInt(4, libro.getNumPaginas());
            pstmt.setBoolean(5, libro.isDisponible());
            pstmt.setString(6, libro.getIsbn());
            pstmt.setString(7, libro.getDescripcion());
            pstmt.setInt(8, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * El método `eliminarLibro` elimina un libro de una base de datos según su ID.
     * 
     * @param id El parámetro `id` en el método `eliminarLibro` es un valor entero
     *           que representa el
     *           identificador único del libro que se debe eliminar de la base de
     *           datos. Este método es
     *           responsable de eliminar un registro de libro de la tabla `libros`
     *           en la base de datos según el
     *           ID proporcionado.
     */
    public static void eliminarLibro(int id) throws SQLException {
        String query = "DELETE FROM libros WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * El método `obtenerSiguienteId` obtiene el próximo ID disponible para un libro
     * consultando el ID
     * máximo de la tabla de base de datos e incrementándolo en 1.
     * 
     * @return El método `obtenerSiguienteId` devuelve un valor entero, que es el
     *         próximo ID disponible
     *         para un nuevo registro de libro en la tabla de base de datos. Si hay
     *         registros de libros
     *         existentes en la base de datos, recupera el ID máximo de la tabla
     *         `libros` y devuelve el próximo
     *         ID secuencial incrementándolo en 1. Si no hay registros de libros
     *         existentes, devuelve 1 como
     *         ID inicial.
     */
    public static int obtenerSiguienteId() throws SQLException {
        String query = "SELECT MAX(id) FROM libros";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        }
        return 1; // Si no hay libros, empezamos desde 1
    }

    /**
     * El método `crearPrestamo` inserta un nuevo registro de préstamo en una tabla
     * de base de datos y recupera
     * la clave generada para el registro insertado.
     * 
     * @param prestamo El método `crearPrestamo` se utiliza para insertar un nuevo
     *                 registro de préstamo en una
     *                 tabla de base de datos llamada `prestamos`.
     */
    public static void crearPrestamo(Prestamo prestamo) throws SQLException {
        String query = "INSERT INTO prestamos (nombre_usuario, documento, id_libro, isbn_libro, titulo_libro, autor_libro, fecha_prestamo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, prestamo.getNombreUsuario());
            pstmt.setString(2, prestamo.getDocumento());
            pstmt.setInt(3, prestamo.getIdLibro());
            pstmt.setString(4, prestamo.getIsbnLibro());
            pstmt.setString(5, prestamo.getTituloLibro());
            pstmt.setString(6, prestamo.getAutorLibro());
            pstmt.setTimestamp(7, new Timestamp(prestamo.getFechaPrestamo().getTime()));
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prestamo.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // El método `actualizarDisponibilidadLibro` en la clase `ConexionBD` es
    // responsable de actualizar el
    // estado de disponibilidad de un libro en la base de datos según el ID del
    // libro proporcionado (`idLibro`)
    // y el nuevo estado de disponibilidad (`disponible`).
    public static void actualizarDisponibilidadLibro(int idLibro, boolean disponible) throws SQLException {
        String query = "UPDATE libros SET disponible = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setBoolean(1, disponible);
            pstmt.setInt(2, idLibro);
            pstmt.executeUpdate();
        }
    }
    /**
     * Verifica si el documento existe en la base de datos
     * 
     * @param documento Los documentos para revisar
     * @return retorna true si existe de lo contrario falso
     */
    public static boolean existeDocumento(String documento) throws SQLException {
        String query = "SELECT COUNT(*) FROM prestamos WHERE documento = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, documento);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Busca un usuario en la base de datos por su número de documento y devuelve la
     * información del usuario si se encuentra.
     * 
     * @param documento El número de documento del usuario a buscar.
     * @return Un objeto Usuario si se encuentra un usuario con el número de
     *         documento especificado, null en caso contrario.
     */
    public static Usuario buscarUsuarioPorDocumento(String documento) throws SQLException {
        String query = "SELECT * FROM usuarios WHERE documento = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, documento);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre_usuario"),
                            rs.getString("contrasena"),
                            rs.getString("email"),
                            rs.getString("documento"),
                            rs.getBoolean("es_administrador"));
                }
            }
        }
        return null;
    }

    /**
     * Crea un nuevo usuario en la base de datos y devuelve true si se realiza con
     * éxito.
     * 
     * @param usuario El objeto Usuario que se va a crear.
     * @return true si se crea el usuario con éxito, false en caso contrario.
     */
    public static boolean crearUsuario(Usuario usuario) throws SQLException {
        String query = "INSERT INTO usuarios (nombre_usuario, contrasena, email, documento, es_administrador) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getDocumento());
            pstmt.setBoolean(5, usuario.esAdministrador());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        }
    }

    /**
     * Busca libros en la base de datos según un criterio de búsqueda (título o
     * ISBN) y devuelve una lista de libros que coinciden.
     * 
     * @param criterio El criterio de búsqueda para encontrar libros.
     * @return Una lista de objetos Libro que coinciden con el criterio de búsqueda.
     */
    public static List<Libro> buscarLibros(String criterio) throws SQLException {
        String query = "SELECT id, titulo, isbn, disponible FROM libros WHERE titulo LIKE ? OR isbn LIKE ?";
        List<Libro> resultados = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + criterio + "%");
            pstmt.setString(2, "%" + criterio + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = new Libro(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            "", // autor no incluido en esta búsqueda
                            0, // fechaPublicacion no incluida
                            0, // numPaginas no incluido
                            rs.getBoolean("disponible"),
                            rs.getString("isbn"),
                            "" // descripcion no incluida
                            , 0
                    );
                    resultados.add(libro);
                }
            }
        }

        return resultados;
    }

    /**
     * Busca un préstamo activo en la base de datos según el nombre de usuario,
     * documento y ID de libro, excluyendo préstamos devueltos.
     * 
     * @param nombreUsuario El nombre de usuario del préstamo a buscar.
     * @param documento     El número de documento del usuario del préstamo a
     *                      buscar.
     * @param idLibro       El ID del libro del préstamo a buscar.
     * @return Un objeto Prestamo si se encuentra un préstamo activo que coincide
     *         con los parámetros, null en caso contrario.
     */
    public static Prestamo buscarPrestamoActivo(String nombreUsuario, String documento, int idLibro) throws SQLException {
    String query = "SELECT * FROM prestamos WHERE nombre_usuario = ? AND documento = ? AND id_libro = ? AND fecha_devolucion IS NULL";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, nombreUsuario);
        pstmt.setString(2, documento);
        pstmt.setInt(3, idLibro);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Prestamo(
                    rs.getInt("id"),
                    rs.getString("nombre_usuario"),
                    rs.getString("documento"),
                    rs.getInt("id_libro"),
                    rs.getString("isbn_libro"),
                    rs.getString("titulo_libro"),
                    rs.getString("autor_libro"),
                    rs.getTimestamp("fecha_prestamo"),
                    rs.getBoolean("devuelto") // <--- Cambia a getTimestamp
                
                );
            }
        }
    }
    return null;
}
    /**
     * Registra una devolución en la base de datos con el ID del préstamo y la fecha
     * actual como fecha de devolución.
     * 
     * @param idPrestamo El ID del préstamo que se va a registrar como devuelto.
     */
   public static boolean registrarDevolucion(int idPrestamo) throws SQLException {
    Connection conn = null;
    try {
        conn = getConnection();
        conn.setAutoCommit(false);

        // Obtener la información del préstamo, incluyendo el id_libro
        String getPrestamo = "SELECT id_libro FROM prestamos WHERE id = ? AND devuelto = false";
        int idLibro;
        try (PreparedStatement pstmt = conn.prepareStatement(getPrestamo)) {
            pstmt.setInt(1, idPrestamo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    idLibro = rs.getInt("id_libro");
                } else {
                    System.err.println("No se encontró un préstamo activo con el ID proporcionado.");
                    return false;
                }
            }
        }

        // Marcar el préstamo como devuelto y establecer la fecha de devolución
        String updatePrestamo = "UPDATE prestamos SET devuelto = true, fecha_devolucion = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updatePrestamo)) {
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(2, idPrestamo);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                System.err.println("No se pudo actualizar el préstamo, posiblemente no existe.");
                return false;
            }
        }

        // Actualizar la disponibilidad del libro usando el ID del libro
        String updateLibro = "UPDATE libros SET disponible = true WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateLibro)) {
            pstmt.setInt(1, idLibro);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                System.err.println("No se pudo actualizar la disponibilidad del libro.");
                conn.rollback();
                return false;
            }
        }

        conn.commit();
        System.out.println("Devolución registrada exitosamente. Libro ID: " + idLibro);
        return true;
    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        System.err.println("Error al registrar la devolución: " + e.getMessage());
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
    return false;
}
    

public static List<Map<String, Object>> buscarUsuarioYLibrosPrestados(String nombreUsuario, String documento) throws SQLException {
    List<Map<String, Object>> resultados = new ArrayList<>();
    String query = "SELECT p.id, p.id_libro, l.titulo, l.isbn " +
                   "FROM prestamos p " +
                   "JOIN libros l ON p.id_libro = l.id " +
                   "WHERE p.nombre_usuario = ? AND p.documento = ? AND p.devuelto = false";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, nombreUsuario);
        pstmt.setString(2, documento);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> libro = new HashMap<>();
                libro.put("id_prestamo", rs.getInt("id"));
                libro.put("id_libro", rs.getInt("id_libro"));
                libro.put("titulo", rs.getString("titulo"));
                libro.put("isbn", rs.getString("isbn"));
                resultados.add(libro);
            }
        }
    }
    return resultados;
}

public static void crearLibrosMultiples(Libro libro) throws SQLException {
    String query = "INSERT INTO libros (titulo, autor, fechaPublicacion, numPaginas, disponible, isbn, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        for (int i = 0; i < libro.getCantidad(); i++) {
            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAutor());
            pstmt.setInt(3, libro.getfechaPublicacion());
            pstmt.setInt(4, libro.getNumPaginas());
            pstmt.setBoolean(5, libro.isDisponible());
            pstmt.setString(6, libro.getIsbn());
            pstmt.setString(7, libro.getDescripcion());
            pstmt.executeUpdate();
        }
    }
}

public static List<Usuario> obtenerTodosLosUsuarios() throws SQLException {
    String query = "SELECT * FROM usuarios";
    List<Usuario> usuarios = new ArrayList<>();

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            Usuario usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("nombre_usuario"),
                    rs.getString("contrasena"),
                    rs.getString("email"),
                    rs.getString("documento"),
                    rs.getBoolean("es_administrador")
            );
            usuarios.add(usuario);
        }
    }
    return usuarios;
}

}