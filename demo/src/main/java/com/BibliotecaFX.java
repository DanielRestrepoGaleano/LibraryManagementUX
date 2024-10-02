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

        Button[] botones = new Button[10];
        String[] opciones = {
                "Mostrar libros", "Agregar libro", "Eliminar libro", "Editar libro",
                "Cambiar estado de libro", "Cerrar sesión", "Realizar préstamo",
                "Buscar libros", "Devolver Libro", "Buscar usuario y libros prestados"
        };

        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i] = new Button(opciones[i]);
            botones[i].setOnAction(e -> {
                try {
                    manejarOpcion(index);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            });
            botones[i].setMaxWidth(Double.MAX_VALUE);
        }

        ImageView imageView = null;
        try {
            Image image = new Image(new FileInputStream("C:/xampp/htdocs/BibliotecaGUI/Thumbsup1.png"));
            imageView = new ImageView(image);
            imageView.setFitWidth(100); // Ajusta el tamaño de la imagen
            imageView.setPreserveRatio(true);
        } catch (FileNotFoundException e) {
            mostrarError("Error de imagen", "No se pudo cargar la imagen de Duke.");
        }

        root.getChildren().addAll(botones);

        if (imageView != null) {
            root.getChildren().add(imageView); // Añadir la imagen debajo de los botones
        }
        Scene scene = new Scene(root, 300, 500);
        primaryStage.setTitle("Biblioteca");
        primaryStage.setScene(scene);
        primaryStage.show();

        mostrarLoginDialog();
    }

    private void manejarOpcion(int opcion) throws SQLException {

        switch (opcion) {
            case 0:
                mostrarLibros();
                break;
            case 1:
                agregarLibro();
                break;
            case 2:
                eliminarLibro();
                break;
            case 3:
                editarLibro();
                break;
            case 4:
                cambiarEstadoLibro();
                break;
            case 5:
                cerrarSesion();
                break;
            case 6:
                realizarPrestamo();
                break;
            case 7:
                buscarLibros();
                break;
            case 8:
                devolverLibro();
                break;
            case 9:
                buscarUsuarioYLibrosPrestados();
                break;
        }

    }

    private void mostrarLibros() {
        try {
            List<Libro> libros = ConexionBD.buscarLibros("");
            StringBuilder sb = new StringBuilder();
            for (Libro libro : libros) {
                boolean disponible = ConexionBD.isLibroDisponible(libro.getId());
                sb.append(String.format("ID: %d, Título: %s, ISBN: %s, Disponible: %s\n",
                    libro.getId(), libro.getTitulo(), libro.getIsbn(), disponible ? "Sí" : "No"));
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Libros");
            alert.setHeaderText(null);
            alert.setContentText(sb.toString());
            alert.showAndWait();
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

        dialog.getDialogPane().setContent(new VBox(8,
                new Label("Título:"), tituloField,
                new Label("Autor:"), autorField,
                new Label("ISBN:"), isbnField,
                new Label("Fecha de Publicación:"), fechaPublicacionField,
                new Label("Número de Páginas:"), numPaginasField,
                new Label("Descripción:"), descripcionArea));

        ButtonType agregarButtonType = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(agregarButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == agregarButtonType) {
                try {
                    int fechaPublicacion = Integer.parseInt(fechaPublicacionField.getText());
                    int numPaginas = Integer.parseInt(numPaginasField.getText());
                    return new Libro(0, tituloField.getText(), autorField.getText(), fechaPublicacion,
                            numPaginas, true, isbnField.getText(), descripcionArea.getText());
                } catch (NumberFormatException e) {
                    mostrarError("Error",
                            "Por favor, ingrese números válidos para la fecha de publicación y el número de páginas.");
                    return null;
                }
            }
            return null;
        });

        Optional<Libro> result = dialog.showAndWait();
        result.ifPresent(libro -> {
            try {
                ConexionBD.crearLibro(libro);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("Libro agregado correctamente.");
                alert.showAndWait();
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo agregar el libro: " + e.getMessage());
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
                                        descripcionArea.getText());
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

        TextField nombreUsuarioField = new TextField(usuarioActual.getNombreUsuario());
        TextField documentoField = new TextField(usuarioActual.getDocumento());
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
                StringBuilder sb = new StringBuilder();
                sb.append("Libros prestados a ").append(userDoc.getKey()).append(":\n\n");
                for (Map<String, Object> libro : librosPrestados) {
                    sb.append(String.format("ID del préstamo: %d, ID del libro: %d, Título: %s, ISBN: %s\n",
                        libro.get("id_prestamo"), libro.get("id_libro"), libro.get("titulo"), libro.get("isbn")));
                }
                mostrarInformacion("Resultados de la búsqueda", sb.toString());
            }
        } catch (SQLException e) {
            mostrarError("Error", "No se pudo realizar la búsqueda: " + e.getMessage());
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
