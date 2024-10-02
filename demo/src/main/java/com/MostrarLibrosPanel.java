package com;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class MostrarLibrosPanel extends JPanel {
    private LinkedList<Libro> biblioteca;
    private JTextArea areaTexto;

    public MostrarLibrosPanel(LinkedList<Libro> biblioteca) {
        this.biblioteca = biblioteca;
        setLayout(new BorderLayout());

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaTexto);

        JButton btnActualizar = new JButton("Actualizar lista");
        btnActualizar.addActionListener(e -> actualizarListaLibros());

        add(scrollPane, BorderLayout.CENTER);
        add(btnActualizar, BorderLayout.SOUTH);
    }

    private void actualizarListaLibros() {
        areaTexto.setText("");
        for (Libro libro : biblioteca) {
            if (libro.isDisponible()) {
                areaTexto.append(libro.toString() + "\n\n");
            }
        }
        if (areaTexto.getText().isEmpty()) {
            areaTexto.setText("No hay libros disponibles");
        }
    }
}