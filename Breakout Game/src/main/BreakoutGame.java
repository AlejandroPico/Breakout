package main;

import javax.swing.*;
import java.awt.*;

public class BreakoutGame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Breakout Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            // Crear el panel de juego con dificultad por defecto (0: Fácil) y 5 filas para ladrillos
            GamePanel gamePanel = new GamePanel(0, 5);
            frame.add(gamePanel, BorderLayout.CENTER);
            
            // Panel de control: desplegable para seleccionar filas (1 a 15) y dificultad
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            
            JLabel filasLabel = new JLabel("Filas:");
            Integer[] filasOptions = new Integer[20];
            for (int i = 0; i < 20; i++) {
                filasOptions[i] = i + 1;
            }
            JComboBox<Integer> comboFilas = new JComboBox<>(filasOptions);
            comboFilas.setSelectedIndex(4); // valor por defecto: 5 filas
            
            JLabel dificultadLabel = new JLabel("Dificultad:");
            String[] opciones = {"Fácil", "Medio", "Difícil"};
            JComboBox<String> comboDificultad = new JComboBox<>(opciones);
            
            JButton btnNuevoJuego = new JButton("Nuevo Juego");
            btnNuevoJuego.addActionListener(e -> {
                int dificultad = comboDificultad.getSelectedIndex();
                int filas = (Integer) comboFilas.getSelectedItem();
                gamePanel.restartGame(dificultad, filas);
            });
            
            controlPanel.add(filasLabel);
            controlPanel.add(comboFilas);
            controlPanel.add(dificultadLabel);
            controlPanel.add(comboDificultad);
            controlPanel.add(btnNuevoJuego);
            
            frame.add(controlPanel, BorderLayout.NORTH);
            
            frame.setSize(700, 600);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true); // La ventana es redimensionable (responsive)
            frame.setVisible(true);
        });
    }
}
