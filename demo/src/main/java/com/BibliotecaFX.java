package com;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BibliotecaFX extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection conexion;
    private GestorUsuarios gestorUsuarios;
    private Usuario usuarioActual;

    @Override
    public void start(Stage primaryStage) {
        try {
            conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            gestorUsuarios = new GestorUsuarios(conexion);
        } catch (SQLException e) {
            mostrarError("Error de conexión", "No se pudo conectar a la base de datos.");
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        
        String[] opciones = {
            "Registrar usuario","Mostrar libros", "Agregar libro", "Eliminar libro", "Editar libro",
            "Cambiar estado de libro", "Cerrar sesión", "Realizar préstamo", "Buscar libros",
            "Devolver libro", "Buscar prestamo por usuario" , "Exportar a Excel"
        };
        Button[] botones = new Button[opciones.length];
        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i] = new Button(opciones[i]);
            botones[i].setOnAction(e -> {
                try {
                    manejarOpcion(index);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });
            botones[i].setMaxWidth(Double.MAX_VALUE);
        }
        
        /* Cargar imagen */
        ImageView imageView = null;
        try {
            Image image = new Image(new FileInputStream("C:/xampp/htdocs/BibliotecaGUI/demo/target/classes/Thumbsup1.png"));
            imageView = new ImageView(image);
            imageView.setFitWidth(70); // Ajusta el tamaño de la imagen
            imageView.setPreserveRatio(true);
        } catch (FileNotFoundException e) {
            mostrarError("Error de imagen", "No se pudo cargar la imagen de Duke.");
        }
        
        if (imageView != null) {
            root.getChildren().add(imageView); // Añadir la imagen debajo de los botones
        }
        root.getChildren().addAll(botones);
        Scene scene = new Scene(root, 300, 550);
        primaryStage.setTitle("Biblioteca");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        mostrarLoginDialog();
    }
    private void manejarOpcion(int opcion) throws SQLException {

        switch (opcion) {
            
            case 0:
            registrarUsuario();
                break;
            case 1:
                mostrarLibros();
                break;
            case 2:
                agregarLibro();
                break;
            case 3:
                eliminarLibro();
                break;
            case 4:
                editarLibro();
                break;
            case 5:
                cambiarEstadoLibro();
                break;
            case 6:
                cerrarSesion();
                break;
            case 7:
                realizarPrestamo();
                break;
            case 8:
                buscarLibros();
                break;
            case 9:
                devolverLibro();
                break;
            case 10:
                buscarUsuarioYLibrosPrestados();
                break;
            case 11:
                exportarExcel();
                break;
        }

    }
    private void registrarUsuario() {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Registrar Usuario");
        dialog.setHeaderText("Ingrese los detalles del usuario");
    
        TextField nombreUsuarioField = new TextField();
        PasswordField contrasenaField = new PasswordField();
        TextField emailField = new TextField();
        TextField documentoField = new TextField();
        CheckBox esAdministradorCheckBox = new CheckBox("Es administrador");
    
        dialog.getDialogPane().setContent(new VBox(8,
                new Label("Nombre de usuario:"), nombreUsuarioField,
                new Label("Contraseña:"), contrasenaField,
                new Label("Email:"), emailField,
                new Label("Documento:"), documentoField,
                esAdministradorCheckBox));
    
        ButtonType registrarButtonType = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registrarButtonType, ButtonType.CANCEL);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registrarButtonType) {
                return new Usuario(0, nombreUsuarioField.getText(), contrasenaField.getText(), emailField.getText(), documentoField.getText(), esAdministradorCheckBox.isSelected());
            }
            return null;
        });
    
        Optional<Usuario> result = dialog.showAndWait();
        result.ifPresent(usuario -> {
            try {
                if (ConexionBD.crearUsuario(usuario)) {
                    mostrarInformacion("Éxito", "Usuario registrado correctamente.");
                } else {
                    mostrarError("Error", "No se pudo registrar el usuario.");
                }
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo registrar el usuario: " + e.getMessage());
            }
        });
    }

    private void exportarExcel() {
        try {
            // Crear un archivo Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Usuarios y Libros");
    
            // Escribir los encabezados en el archivo Excel
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Documento");
            headerRow.createCell(1).setCellValue("Nombre");
            headerRow.createCell(2).setCellValue("Libros Prestados");
            headerRow.createCell(3).setCellValue("Libros Devueltos");
            headerRow.createCell(4).setCellValue("Total Libros");
    
            // Obtener todos los usuarios
            List<Usuario> usuarios = ConexionBD.obtenerTodosLosUsuarios();
    
            int rowNum = 1;
            for (Usuario usuario : usuarios) {
                String documento = usuario.getDocumento();
                String nombre = usuario.getNombreUsuario();
    
                // Obtener los libros prestados y devueltos
                List<Libro> librosPrestados = ConexionBD.buscarLibrosPrestados(documento);
                List<Libro> librosDevueltos = ConexionBD.buscarLibrosDevolvidos(documento);
    
                // Escribir los datos del usuario
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(documento);
                row.createCell(1).setCellValue(nombre);
                row.createCell(2).setCellValue(librosPrestados.size());
                row.createCell(3).setCellValue(librosDevueltos.size());
                row.createCell(4).setCellValue(librosPrestados.size() + librosDevueltos.size());
    
                // Escribir los detalles de los libros prestados
                for (Libro libro : librosPrestados) {
                    row = sheet.createRow(rowNum++);
                    row.createCell(1).setCellValue("Prestado: " + libro.getTitulo());
                    row.createCell(2).setCellValue(libro.getAutor());
                    row.createCell(3).setCellValue(libro.getIsbn());
    
                    Cell fechaPrestamo = row.createCell(4);
                    fechaPrestamo.setCellValue(libro.getFechaPrestamo());
                    fechaPrestamo.setCellStyle(crearEstiloFecha(workbook));
                }
    
                // Escribir los detalles de los libros devueltos
                for (Libro libro : librosDevueltos) {
                    row = sheet.createRow(rowNum++);
                    row.createCell(1).setCellValue("Devuelto: " + libro.getTitulo());
                    row.createCell(2).setCellValue(libro.getAutor());
                    row.createCell(3).setCellValue(libro.getIsbn());
    
                    Cell fechaDevolucion = row.createCell(4);
                    fechaDevolucion.setCellValue(libro.getFechaDevolucion());
                    fechaDevolucion.setCellStyle(crearEstiloFecha(workbook));
                }
    
                // Agregar una fila vacía entre usuarios
                rowNum++;
            }
    
            // Ajustar el ancho de las columnas
            for (int i = 0; i <= 4; i++) {
                sheet.autoSizeColumn(i);
            }
    
            // Guardar el archivo Excel
            FileOutputStream fos = new FileOutputStream("C:/xampp/htdocs/BibliotecaGUI/demo/target/usuarios_y_libros.xlsx");
            workbook.write(fos);
            fos.close();
            workbook.close();
    
            // Mostrar mensaje de confirmación
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportar a Excel");
            alert.setHeaderText(null);
            alert.setContentText("El archivo Excel ha sido generado correctamente.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar mensaje de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exportar a Excel");
            alert.setHeaderText(null);
            alert.setContentText("Ha ocurrido un error al generar el archivo Excel: " + e.getMessage());
            alert.showAndWait();
        }
    }
 
private CellStyle crearEstiloFecha(Workbook workbook) {
    CellStyle estiloFecha = workbook.createCellStyle();
    CreationHelper createHelper = workbook.getCreationHelper();
    estiloFecha.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
    return estiloFecha;
}
private void mostrarLibros() {
    try {
        List<Libro> libros = ConexionBD.buscarLibros("");
        ListView<String> listView = new ListView<>();

        // Agregar los libros a la lista
        for (Libro libro : libros) {
            boolean disponible = ConexionBD.isLibroDisponible(libro.getId());
            listView.getItems().add(String.format("ID: %d, Título: %s, ISBN: %s, Disponible: %s",
                libro.getId(), libro.getTitulo(), libro.getIsbn(), disponible ? "Sí" : "No"));
        }

        // Crear un diálogo con la lista
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Libros");
        dialog.setHeaderText(null);

        // Establecer un ancho prefijado para el diálogo
        dialog.getDialogPane().setPrefWidth(800);

        // Agregar la lista al diálogo
        dialog.getDialogPane().setContent(listView);

        // Agregar un botón de cerrar
        ButtonType closeButton = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButton);

        // Establecer un evento para que se cierre el diálogo cuando se hace clic en el botón de cerrar
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == closeButton) {
                return "Cerrar";
            }
            return null;
        });

        // Mostrar el diálogo
        dialog.showAndWait();
    } catch (SQLException e) {
        mostrarError("Error", "No se pudieron obtener los libros: " + e.getMessage());
    }
}

    private void agregarLibro() {
        Dialog<Libro> dialog = new Dialog<>();
        dialog.setTitle("Agregar Libro");
        dialog.setHeaderText("Ingrese los detalles del libro");

        TextField tituloField = new TextField();
        TextField autorField = new TextField();
        TextField isbnField = new TextField();
        TextField fechaPublicacionField = new TextField();
        TextField numPaginasField = new TextField();
        TextArea descripcionArea = new TextArea();
        TextField cantidadField = new TextField();
        cantidadField.setPromptText("Cantidad de copias");

        dialog.getDialogPane().setContent(new VBox(8,
                new Label("Título:"), tituloField,
                new Label("Autor:"), autorField,
                new Label("ISBN:"), isbnField,
                new Label("Fecha de Publicación:"), fechaPublicacionField,
                new Label("Número de Páginas:"), numPaginasField,
                new Label("Descripción:"), descripcionArea,
                new Label("Cantidad de copias:"), cantidadField));

        ButtonType agregarButtonType = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(agregarButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == agregarButtonType) {
                try {
                    int fechaPublicacion = Integer.parseInt(fechaPublicacionField.getText());
                    int numPaginas = Integer.parseInt(numPaginasField.getText());
                    int cantidad = Integer.parseInt(cantidadField.getText());
                    return new Libro(0, tituloField.getText(), autorField.getText(), fechaPublicacion,
                            numPaginas, true, isbnField.getText(), descripcionArea.getText(), cantidad);
                } catch (NumberFormatException e) {
                    mostrarError("Error",
                            "Por favor, ingrese números válidos para la fecha de publicación, el número de páginas y la cantidad.");
                    return null;
                }
            }
            return null;
        });

        Optional<Libro> result = dialog.showAndWait();
        result.ifPresent(libro -> {
            try {
                ConexionBD.crearLibrosMultiples(libro);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("Libros agregados correctamente.");
                alert.showAndWait();
            } catch (SQLException e) {
                mostrarError("Error", "No se pudieron agregar los libros: " + e.getMessage());
            }
        });
    }
    

    private void eliminarLibro() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Eliminar Libro");
        dialog.setHeaderText("Ingrese el ID del libro a eliminar");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(id -> {
            try {
                int libroId = Integer.parseInt(id);
                ConexionBD.eliminarLibro(libroId);
                mostrarInformacion("Éxito", "Libro eliminado correctamente.");
            } catch (NumberFormatException e) {
                mostrarError("Error", "Por favor, ingrese un ID válido.");
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo eliminar el libro: " + e.getMessage());
            }
        });
    }

    private void editarLibro() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Editar Libro");
        idDialog.setHeaderText("Ingrese el ID del libro a editar");
        idDialog.setContentText("ID:");

        Optional<String> idResult = idDialog.showAndWait();
        idResult.ifPresent(id -> {
            try {
                int libroId = Integer.parseInt(id);
                Libro libro = ConexionBD.leerLibro(libroId);
                if (libro != null) {
                    Dialog<Libro> editDialog = new Dialog<>();
                    editDialog.setTitle("Editar Libro");
                    editDialog.setHeaderText("Edite los detalles del libro");

                    TextField tituloField = new TextField(libro.getTitulo());
                    TextField autorField = new TextField(libro.getAutor());
                    TextField isbnField = new TextField(libro.getIsbn());
                    TextField fechaPublicacionField = new TextField(String.valueOf(libro.getfechaPublicacion()));
                    TextField numPaginasField = new TextField(String.valueOf(libro.getNumPaginas()));
                    TextArea descripcionArea = new TextArea(libro.getDescripcion());

                    editDialog.getDialogPane().setContent(new VBox(8,
                            new Label("Título:"), tituloField,
                            new Label("Autor:"), autorField,
                            new Label("ISBN:"), isbnField,
                            new Label("Fecha de Publicación:"), fechaPublicacionField,
                            new Label("Número de Páginas:"), numPaginasField,
                            new Label("Descripción:"), descripcionArea));

                    ButtonType editarButtonType = new ButtonType("Editar", ButtonBar.ButtonData.OK_DONE);
                    editDialog.getDialogPane().getButtonTypes().addAll(editarButtonType, ButtonType.CANCEL);

                    editDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == editarButtonType) {
                            try {
                                int fechaPublicacion = Integer.parseInt(fechaPublicacionField.getText());
                                int numPaginas = Integer.parseInt(numPaginasField.getText());
                                return new Libro(libroId, tituloField.getText(), autorField.getText(), fechaPublicacion,
                                        numPaginas, libro.isDisponible(), isbnField.getText(),
                                        descripcionArea.getText(), numPaginas);
                            } catch (NumberFormatException e) {
                                mostrarError("Error",
                                        "Por favor, ingrese números válidos para la fecha de publicación y el número de páginas.");
                                return null;
                            }
                        }
                        return null;
                    });

                    Optional<Libro> editResult = editDialog.showAndWait();
                    editResult.ifPresent(libroEditado -> {
                        try {
                            ConexionBD.actualizarLibro(libroId, libroEditado);
                            mostrarInformacion("Éxito", "Libro editado correctamente.");
                        } catch (SQLException e) {
                            mostrarError("Error", "No se pudo editar el libro: " + e.getMessage());
                        }
                    });
                } else {
                    mostrarError("Error", "No se encontró el libro con el ID especificado.");
                }
            } catch (NumberFormatException e) {
                mostrarError("Error", "Por favor, ingrese un ID válido.");
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo obtener el libro: " + e.getMessage());
            }
        });
    }

    private void cambiarEstadoLibro() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cambiar Estado de Libro");
        dialog.setHeaderText("Ingrese el ID del libro para cambiar su estado");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(id -> {
            try {
                int libroId = Integer.parseInt(id);
                Libro libro = ConexionBD.leerLibro(libroId);
                if (libro != null) {
                    boolean nuevoEstado = !libro.isDisponible();
                    ConexionBD.actualizarDisponibilidadLibro(libroId, nuevoEstado);
                    mostrarInformacion("Éxito", "Estado del libro cambiado a: " + (nuevoEstado ? "Disponible" : "No disponible"));
                } else {
                    mostrarError("Error", "No se encontró el libro con el ID especificado.");
                }
            } catch (NumberFormatException e) {
                mostrarError("Error", "Por favor, ingrese un ID válido.");
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo cambiar el estado del libro: " + e.getMessage());
            }
        });
    }

    private void cerrarSesion() {
        usuarioActual = null;
        mostrarLoginDialog();
        Platform.exit();

    }

    private void realizarPrestamo() {
        Dialog<Prestamo> dialog = new Dialog<>();
        dialog.setTitle("Realizar Préstamo");
        dialog.setHeaderText("Ingrese los detalles del préstamo");

        TextField nombreUsuarioField = new TextField();
        TextField documentoField = new TextField();
        TextField idLibroField = new TextField();

        dialog.getDialogPane().setContent(new VBox(8,
            new Label("Nombre de Usuario:"), nombreUsuarioField,
            new Label("Documento:"), documentoField,
            new Label("ID del Libro:"), idLibroField
        ));

        ButtonType prestarButtonType = new ButtonType("Prestar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(prestarButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == prestarButtonType) {
                try {
                    int idLibro = Integer.parseInt(idLibroField.getText());
                    Libro libro = ConexionBD.leerLibro(idLibro);
                    if (libro != null && libro.isDisponible()) {
                        return new Prestamo(0, nombreUsuarioField.getText(), documentoField.getText(), idLibro, libro.getIsbn(), libro.getTitulo(), libro.getAutor(), new Timestamp(System.currentTimeMillis()), false);
                    } else {
                        mostrarError("Error", "El libro no está disponible o no existe.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    mostrarError("Error", "Por favor, ingrese un ID de libro válido.");
                    return null;
                } catch (SQLException e) {
                    mostrarError("Error", "No se pudo obtener la información del libro: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Prestamo> result = dialog.showAndWait();
result.ifPresent(prestamo -> {
    try {
        ConexionBD.crearPrestamo(prestamo);
        ConexionBD.actualizarDisponibilidadLibro(prestamo.getIdLibro(), false);
        mostrarInformacion("Éxito", "Préstamo realizado correctamente.");
    } catch (SQLException e) {
        mostrarError("Error", "No se pudo realizar el préstamo: " + e.getMessage());
    }
});
    }

    private void buscarLibros() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Libros");
        dialog.setHeaderText("Ingrese el criterio de búsqueda (título o ISBN)");
        dialog.setContentText("Criterio:");
    
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(criterio -> {
            try {
                List<Libro> librosEncontrados = ConexionBD.buscarLibros(criterio);
                if (librosEncontrados.isEmpty()) {
                    mostrarInformacion("Búsqueda", "No se encontraron libros que coincidan con el criterio.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Libro libro : librosEncontrados) {
                        boolean disponible = ConexionBD.isLibroDisponible(libro.getId());
                        sb.append(String.format("ID: %d, Título: %s, ISBN: %s, Disponible: %s\n",
                            libro.getId(), libro.getTitulo(), libro.getIsbn(), disponible ? "Sí" : "No"));
                    }
                    mostrarInformacion("Resultados de la búsqueda", sb.toString());
                }
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo realizar la búsqueda: " + e.getMessage());
            }
        });
    }
    

    private void devolverLibro() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Devolver Libro");
        dialog.setHeaderText("Ingrese el ID del préstamo a devolver");
        dialog.setContentText("ID del préstamo:");
    
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(idPrestamoStr -> {
            try {
                int idPrestamo = Integer.parseInt(idPrestamoStr);
                System.out.println("Intentando devolver el préstamo con ID: " + idPrestamo);
                boolean devolucionExitosa = ConexionBD.registrarDevolucion(idPrestamo);
                if (devolucionExitosa) {
                    mostrarInformacion("Éxito", "El libro ha sido devuelto correctamente. ID del préstamo: " + idPrestamo);
                } else {
                    mostrarError("Error", "No se pudo realizar la devolución. No se encontró un préstamo activo con el ID: " + idPrestamo);
                }
            } catch (NumberFormatException e) {
                mostrarError("Error", "Por favor, ingrese un ID de préstamo válido.");
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo realizar la devolución: " + e.getMessage());
            }
        });
    }

    private void buscarUsuarioYLibrosPrestados() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Buscar Usuario y Libros Prestados");
        dialog.setHeaderText("Ingrese el nombre de usuario y documento");
    
        ButtonType buscarButtonType = new ButtonType("Buscar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buscarButtonType, ButtonType.CANCEL);
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        TextField nombreUsuario = new TextField();
        TextField documento = new TextField();
    
        grid.add(new Label("Nombre de Usuario:"), 0, 0);
        grid.add(nombreUsuario, 1, 0);
        grid.add(new Label("Documento:"), 0, 1);
        grid.add(documento, 1, 1);
    
        dialog.getDialogPane().setContent(grid);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buscarButtonType) {
                return new Pair<>(nombreUsuario.getText(), documento.getText());
            }
            return null;
        });
    
        Optional<Pair<String, String>> result = dialog.showAndWait();
    
        result.ifPresent(userDoc -> {
            try {
                List<Map<String, Object>> librosPrestados = ConexionBD.buscarUsuarioYLibrosPrestados(userDoc.getKey(), userDoc.getValue());
                if (librosPrestados.isEmpty()) {
                    mostrarInformacion("Búsqueda", "El usuario no tiene libros prestados actualmente.");
                } else {
                    mostrarLibrosPrestadosConOpcionAplazamiento(librosPrestados, userDoc.getKey());
                }
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo realizar la búsqueda: " + e.getMessage());
            }
        });
    }
    
    private void mostrarLibrosPrestadosConOpcionAplazamiento(List<Map<String, Object>> librosPrestados, String nombreUsuario) {
        Dialog<Integer> resultDialog = new Dialog<>();
        resultDialog.setTitle("Resultados de la búsqueda");
        resultDialog.setHeaderText("Libros prestados a " + nombreUsuario);
    
        ButtonType aplazarButtonType = new ButtonType("Aplazar", ButtonBar.ButtonData.OK_DONE);
        resultDialog.getDialogPane().getButtonTypes().addAll(aplazarButtonType, ButtonType.CANCEL);
    
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(20, 150, 10, 10));
    
        ListView<String> listView = new ListView<>();
        for (Map<String, Object> libro : librosPrestados) {
            listView.getItems().add(String.format("ID del préstamo: %d, ID del libro: %d, Título: %s, ISBN: %s",
                    libro.get("id_prestamo"), libro.get("id_libro"), libro.get("titulo"), libro.get("isbn")));
        }
    
        contentBox.getChildren().add(listView);
    
        Label idPrestamoLabel = new Label("ID del préstamo a aplazar:");
        TextField idPrestamoField = new TextField();
        contentBox.getChildren().addAll(idPrestamoLabel, idPrestamoField);
    
        resultDialog.getDialogPane().setContent(contentBox);
    
        resultDialog.setResultConverter(dialogButton -> {
            if (dialogButton == aplazarButtonType) {
                try {
                    return Integer.parseInt(idPrestamoField.getText());
                } catch (NumberFormatException e) {
                     mostrarError("Error", "Por favor, ingrese un ID de préstamo válido.");
                    return null;
                }
            }
            return null;
        });
    
        Optional<Integer> result = resultDialog.showAndWait();
    
        result.ifPresent(idPrestamo -> {
            try {
                if (ConexionBD.aplazarPrestamo(idPrestamo)) {
                    mostrarInformacion("Aplazamiento", "El préstamo se ha aplazado correctamente.");
                } else {
                    mostrarError("Error", "No se pudo aplazar el préstamo.");
                }
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo aplazar el préstamo: " + e.getMessage());
            }
        });
    }

    private void mostrarLoginDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Iniciar sesión");
        dialog.setHeaderText("Por favor, ingrese sus credenciales");

        ButtonType loginButtonType = new ButtonType("Iniciar sesión", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        TextField username = new TextField();
        username.setPromptText("Nombre de usuario");
        PasswordField password = new PasswordField();
        password.setPromptText("Contraseña");

        dialog.getDialogPane().setContent(new VBox(8, username, password));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            String nombreUsuario = usernamePassword.getKey();
            String contrasena = usernamePassword.getValue();

            usuarioActual = gestorUsuarios.autenticarUsuario(nombreUsuario, contrasena);
            if (usuarioActual == null) {
                mostrarError("Error de autenticación", "Credenciales incorrectas");
                mostrarLoginDialog();
            }
        });
    }

    // Mostrar informacion
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

  

    public static void main(String[] args) {
        launch(args);
    }
}
