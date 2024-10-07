package com;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * La clase `Prestamo` representa un préstamo con atributos como nombre de
 * usuario, documento, detalles del libro
 * y fecha de préstamo.
 */
public class Prestamo {
    private int id;
    private String nombreUsuario;
    private String documento;
    private int idLibro;
    private String isbnLibro;
    private String tituloLibro;
    private String autorLibro;
    private Timestamp fechaPrestamo;
    private Timestamp fechaDevolucion;
    private boolean devuelto;
    private Timestamp fechaVencimiento;
    private String estado;
    private int numeroAplazamientos;

    /**
     * El constructor `public Prestamo(int id, String nombreUsuario, String
     * documento, int idLibro,
     * String isbnLibro, String tituloLibro, String autorLibro, Date fechaPrestamo)`
     * en la clase `Prestamo`
     * inicializa una nueva instancia del objeto `Prestamo` con los valores
     * proporcionados para sus atributos.
     */
    public Prestamo(int id, String nombreUsuario, String documento, int idLibro, String isbnLibro, String tituloLibro,
            String autorLibro, Timestamp fechaPrestamo, boolean devuelto) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.documento = documento;
        this.idLibro = idLibro;
        this.isbnLibro = isbnLibro;
        this.tituloLibro = tituloLibro;
        this.autorLibro = autorLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = null;
        this.devuelto = devuelto;
    }

    // Getters y setters
    public Timestamp getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Timestamp date) {
        this.fechaVencimiento = date;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getNumeroAplazamientos() {
        return numeroAplazamientos;
    }

    public void setNumeroAplazamientos(int numeroAplazamientos) {
        this.numeroAplazamientos = numeroAplazamientos;
    }

    public Timestamp getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Timestamp fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    // Actualizar getter y setter para fechaPrestamo
    public Timestamp getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Timestamp fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getIsbnLibro() {
        return isbnLibro;
    }

    public void setIsbnLibro(String isbnLibro) {
        this.isbnLibro = isbnLibro;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    public String getAutorLibro() {
        return autorLibro;
    }

    public void setAutorLibro(String autorLibro) {
        this.autorLibro = autorLibro;
    }

    public void aplazar() {
        if (numeroAplazamientos < 2) { // Permitimos hasta 2 aplazamientos
            numeroAplazamientos++;
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaVencimiento);
            cal.add(Calendar.DAY_OF_MONTH, 15);
            fechaVencimiento = new Timestamp(cal.getTimeInMillis());
        }
    }

    public boolean estaVencido() {
        return new Timestamp(System.currentTimeMillis()).after(fechaVencimiento);
    }

    /**
     * El método `toString` en Java sobrescribe la implementación predeterminada
     * para devolver una representación de cadena
     * formateada de los atributos del objeto `Prestamo`.
     * 
     * @return El método `toString()` se sobrescribe para devolver una
     *         representación de cadena del objeto `Prestamo`,
     *         que incluye los valores de sus atributos como id, nombreUsuario,
     *         documento, idLibro, isbnLibro, tituloLibro,
     *         autorLibro y fechaPrestamo.
     */
    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", documento='" + documento + '\'' +
                ", idLibro=" + idLibro +
                ", isbnLibro='" + isbnLibro + '\'' +
                ", tituloLibro='" + tituloLibro + '\'' +
                ", autorLibro='" + autorLibro + '\'' +
                ", fechaPrestamo=" + fechaPrestamo +
                '}';
    }
}
