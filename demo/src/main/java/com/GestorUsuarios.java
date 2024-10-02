package com;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.*;
import com.mysql.cj.xdevapi.Statement;

/**
 * La clase GestorUsuarios en Java se encarga de gestionar
 *  el registro y autenticación de usuarios utilizando una conexión
 *  a la base de datos y funcionalidades de registro.
 */
public class GestorUsuarios {
    private static final Logger LOGGER = Logger.getLogger(GestorUsuarios.class.getName());
    private Connection conexion;

    public GestorUsuarios(Connection conexion) {
        this.conexion = conexion;
    }

/**
 * El método registrarUsuario inserta un nuevo usuario en una tabla
 *  de la base de datos y devuelve true si la operación es exitosa.
 * 
 * @param usuario El objeto Usuario que se pasa como parámetro contiene la información del usuario,
 *  como nombre_usuario (nombre de usuario),
 *  contrasena (contraseña), email, etc.
 * @return El método registrarUsuario devuelve un valor booleano.
 *  Devuelve true si el registro del usuario fue exitoso
 *  y false si hubo un error durante el proceso de registro.
 */
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre_usuario, contrasena, email, es_administrador) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setBoolean(4, usuario.esAdministrador());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar usuario");
        }
        return false;
    }

 /**
     * El método autenticarUsuario autentica a un usuario consultando la tabla de la base de datos
     *  `usuarios` según el nombre de usuario y contraseña proporcionados.
     */
    public  Usuario autenticarUsuario(String nombreUsuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, contrasena);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre_usuario"),
                            rs.getString("contrasena"),
                            rs.getString("email"),
                            sql, rs.getBoolean("es_administrador"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al autenticar usuario");
        }
        return null;
    }
}
