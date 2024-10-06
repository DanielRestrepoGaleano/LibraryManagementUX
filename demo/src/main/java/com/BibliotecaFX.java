package com;
import javafx.application.HostServices;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.io.File;
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
    private Stage primaryStage;
    private HostServices hostServices;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection conexion;
    private GestorUsuarios gestorUsuarios;
    private Usuario usuarioActual;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.hostServices = getHostServices();
        mostrarLoginDialog();
        try {
            conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            gestorUsuarios = new GestorUsuarios(conexion);
        } catch (SQLException e) {
            mostrarError("Error de conexión", "No se pudo conectar a la base de datos.");
            return;
        }

        /* 
        Scene scene = new Scene(root, 300, 550);
    primaryStage.setTitle("Biblioteca");
    primaryStage.setScene(scene);
    primaryStage.show();

    mostrarLoginDialog();
    */
    }
        private void mostrarMenuPrincipal(){
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
            root.getChildren().addAll(botones);
            Scene scene = new Scene(root, 300, 550);
            primaryStage.setTitle("Biblioteca");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            
            
            /* Cargar imágenes */
    ImageView dukeImageView = null;
    ImageView fatimaImageView = null;
    try {
        InputStream dukeInputStream = getClass().getResourceAsStream("/Thumbsup1.png");
        InputStream fatimaInputStream = getClass().getResourceAsStream("/fatima.png");
        
        if (dukeInputStream != null && fatimaInputStream != null) {
            Image dukeImage = new Image(dukeInputStream);
            Image fatimaImage = new Image(fatimaInputStream);
            
            dukeImageView = new ImageView(dukeImage);
            fatimaImageView = new ImageView(fatimaImage);
            
            dukeImageView.setFitWidth(85); //tamaños de imagen
            fatimaImageView.setFitWidth(85);
            
            dukeImageView.setPreserveRatio(true);
            fatimaImageView.setPreserveRatio(true);
        } else {
            throw new FileNotFoundException("No se pudo encontrar uno o ambos recursos de imagen");
        }
    } catch (FileNotFoundException e) {
        mostrarError("Error de imagen", "No se pudieron cargar una o ambas imágenes.");
    }
    
    HBox imageContainer = new HBox(100); // 100 es el espacio entre las imágenes
    
    if (dukeImageView != null && fatimaImageView != null) {
        Hyperlink dukeLink = new Hyperlink();
        dukeLink.setGraphic(dukeImageView);
        dukeLink.setBorder(null);
        
        Hyperlink fatimaLink = new Hyperlink();
        fatimaLink.setGraphic(fatimaImageView);
        fatimaLink.setBorder(null);
        
        HostServices hostServices = getHostServices();
        dukeLink.setOnAction(e -> {
            hostServices.showDocument("https://github.com/DanielRestrepoGaleano");
        });
        
        fatimaLink.setOnAction(e -> {
            hostServices.showDocument("https://iefatimanutibara.edu.co/"); // Reemplaza con la URL de tu escuela
        });
        
        imageContainer.getChildren().addAll(dukeLink, fatimaLink);
        root.getChildren().add(imageContainer);
    } else {
        // Si no se pudieron cargar las imágenes, añadir enlaces de texto como alternativa
        Hyperlink githubLink = new Hyperlink("Mi GitHub");
        Hyperlink escuelaLink = new Hyperlink("Mi Escuela");
        
        HostServices hostServices = getHostServices();
        githubLink.setOnAction(e -> {
            hostServices.showDocument("https://github.com/DanielRestrepoGaleano");
        });
        escuelaLink.setOnAction(e -> {
            hostServices.showDocument("https://iefatimanutibara.edu.co/"); // Reemplaza con la URL de tu escuela
        });
        
        imageContainer.getChildren().addAll(githubLink, escuelaLink);
        root.getChildren().add(imageContainer);
    }
   
   
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
        contrasenaField.setPromptText("Contraseña (solo para administradores)");
        TextField emailField = new TextField();
        TextField documentoField = new TextField();
        CheckBox esAdministradorCheckBox = new CheckBox("Es administrador");
    
        VBox formLayout = new VBox(8,
                new Label("Nombre de usuario:"), nombreUsuarioField,
                new Label("Email:"), emailField,
                new Label("Documento:"), documentoField,
                esAdministradorCheckBox);
    
        // Inicialmente, no agregamos el campo de contraseña
        Label contrasenaLabel = new Label("Contraseña:");
        
        esAdministradorCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Si se selecciona "Es administrador", mostrar el campo de contraseña
                if (!formLayout.getChildren().contains(contrasenaLabel)) {
                    formLayout.getChildren().addAll(contrasenaLabel, contrasenaField);
                    // Ajustar el tamaño del diálogo
                    Platform.runLater(() -> {
                        dialog.getDialogPane().getScene().getWindow().sizeToScene();
                    });
                }
            } else {
                // Si se deselecciona, ocultar el campo de contraseña
                formLayout.getChildren().removeAll(contrasenaLabel, contrasenaField);
                // Ajustar el tamaño del diálogo
                Platform.runLater(() -> {
                    dialog.getDialogPane().getScene().getWindow().sizeToScene();
                });
            }
        });
        
         // Establecer un ancho preferido para los campos de texto
        nombreUsuarioField.setPrefWidth(250);
        emailField.setPrefWidth(250);
        documentoField.setPrefWidth(250);
        contrasenaField.setPrefWidth(250);

        dialog.getDialogPane().setContent(formLayout);
    
        ButtonType registrarButtonType = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registrarButtonType, ButtonType.CANCEL);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registrarButtonType) {
                boolean esAdmin = esAdministradorCheckBox.isSelected();
                String contrasena = contrasenaField.getText();
                
                if (esAdmin && contrasena.isEmpty()) {
                    mostrarError("Error de registro", "Los usuarios administradores deben proporcionar una contraseña.");
                    return null;
                }
                
                return new Usuario(0, nombreUsuarioField.getText(), contrasena, emailField.getText(), documentoField.getText(), esAdmin);
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
             // Permitir al usuario elegir dónde guardar el archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("usuarios_y_libros.xlsx");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            // Guardar el archivo Excel
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            workbook.close();

            // Mostrar mensaje de confirmación
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportar a Excel");
            alert.setHeaderText(null);
            alert.setContentText("El archivo Excel ha sido generado correctamente.");
            alert.showAndWait();
        } else {
            // El usuario canceló la operación
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportar a Excel");
            alert.setHeaderText(null);
            alert.setContentText("La exportación a Excel fue cancelada.");
            alert.showAndWait();
        }
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
            String usernameInput = usernamePassword.getKey();
            String passwordInput = usernamePassword.getValue();
    
            try {
                Usuario usuario = ConexionBD.autenticarUsuario(usernameInput, passwordInput);
                if (usuario != null) {
                    if (usuario.esAdministrador()) {
                        mostrarMenuPrincipal();
                    } else {
                        mostrarError("Acceso denegado", "Solo los administradores pueden acceder al menú principal.");
                        mostrarLoginDialog();
                    }
                } else {
                    mostrarError("Error de autenticación", "Usuario o contraseña incorrectos.");
                    mostrarLoginDialog();
                }
            } catch (SQLException e) {
                mostrarError("Error de base de datos", "No se pudo autenticar al usuario: " + e.getMessage());
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
