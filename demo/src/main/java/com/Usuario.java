package com;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String contrasena;
    private String email;
    private String documento;
    private boolean esAdministrador;

    public Usuario(int id, String nombreUsuario, String contrasena, String email, String documento, boolean esAdministrador) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.email = email;
        this.documento = documento;
        this.esAdministrador = esAdministrador;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public boolean esAdministrador() {
        return this.esAdministrador;
    }

    public void setEsAdministrador(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }
   

  /**
     * El método `toString` en Java sobrescribe la implementación predeterminada para devolver una representación de cadena
     * formateada de los atributos del objeto `Prestamo`.
     * 
     * @return El método `toString()` se sobrescribe para devolver una representación de cadena del objeto `Prestamo`,
     * que incluye los valores de sus atributos como id, usuario, documento, libro y fecha de préstamo.
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", email='" + email + '\'' +
                ", esAdministrador=" + esAdministrador +
                '}';
    }
}