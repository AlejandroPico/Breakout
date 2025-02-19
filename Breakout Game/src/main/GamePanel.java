package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
class GamePanel extends JPanel implements KeyListener, ActionListener {

    // Estado y puntuación
    private boolean play = false;
    private int score = 0;
    
    // Mapa de ladrillos
    public MapGenerator map;
    private int totalBricks;
    
    // Timer del juego
    private Timer timer;
    private int delay;
    
    // Posición y velocidad de la bola
    private int ballPosX, ballPosY;
    private int ballXdir, ballYdir;
    private final int ballDiameter = 20;
    
    // Posición de la paleta (barra)
    private int playerX;
    
    // Configuración del mapa: columnas fijas (10) y filas variable (elegible)
    private int blockCols = 10;
    private int blockRows;
    
    public GamePanel(int dificultad, int filas) {
        this.blockRows = filas;
        restartGame(dificultad, filas);
        
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        // Permite mover la paleta con el ratón
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();
                playerX = mouseX - getPaddleWidth() / 2;
                if (playerX < 0)
                    playerX = 0;
                if (playerX > getWidth() - getPaddleWidth())
                    playerX = getWidth() - getPaddleWidth();
                repaint();
            }
        });
        
        timer = new Timer(delay, this);
        timer.start();
        requestFocusInWindow();
    }
    
    /**
     * Calcula el ancho de la paleta de forma dinámica según el ancho actual del panel.
     */
    private int getPaddleWidth() {
        return Math.max(50, getWidth() / 7);
    }
    
    /**
     * Configura la velocidad inicial de la bola según la dificultad.
     */
    private void setDifficulty(int dificultad) {
        delay = 10; // en milisegundos
        switch (dificultad) {
            case 0: // Fácil
                ballXdir = -2;
                ballYdir = -3;
                break;
            case 1: // Medio
                ballXdir = -3;
                ballYdir = -4;
                break;
            case 2: // Difícil
                ballXdir = -4;
                ballYdir = -5;
                break;
            default:
                ballXdir = -2;
                ballYdir = -3;
        }
    }
    
    /**
     * Reinicia el juego con la dificultad y cantidad de filas de ladrillos indicadas.
     */
    public void restartGame(int dificultad, int filas) {
        play = true;
        score = 0;
        blockRows = filas;
        totalBricks = blockRows * blockCols;
        // Se ubica la paleta y la bola en posiciones centrales (si el tamaño aun no está definido, se actualizará en el primer repaint)
        playerX = getWidth() / 2 - getPaddleWidth() / 2;
        ballPosX = getWidth() / 2;
        ballPosY = getHeight() / 2;
        setDifficulty(dificultad);
        map = new MapGenerator(blockRows, blockCols);
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        // Fondo negro
        g.setColor(Color.black);
        g.fillRect(0, 0, panelWidth, panelHeight);
        
        // Dibujar ladrillos (layout responsive)
        map.draw((Graphics2D) g, panelWidth, panelHeight);
        
        // Dibujar bordes
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, panelHeight);
        g.fillRect(0, 0, panelWidth, 3);
        g.fillRect(panelWidth - 3, 0, 3, panelHeight);
        
        // Puntuación
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Puntuación: " + score, panelWidth - 150, 30);
        
        // Dibujar la paleta (barra) en la parte inferior
        g.setColor(Color.green);
        g.fillRect(playerX, panelHeight - 50, getPaddleWidth(), 8);
        
        // Dibujar la bola
        g.setColor(Color.yellow);
        g.fillOval(ballPosX, ballPosY, ballDiameter, ballDiameter);
        
        // Mensaje de victoria
        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("¡Ganaste!", panelWidth / 2 - 75, panelHeight / 2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Presiona 'Nuevo Juego' para reiniciar.", panelWidth / 2 - 150, panelHeight / 2 + 50);
        }
        
        // Mensaje de Game Over
        if (ballPosY > panelHeight) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over", panelWidth / 2 - 100, panelHeight / 2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Puntuación: " + score, panelWidth / 2 - 70, panelHeight / 2 + 40);
            g.drawString("Presiona 'Nuevo Juego' para reiniciar.", panelWidth / 2 - 150, panelHeight / 2 + 70);
        }
        
        g.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // Mover la bola
            ballPosX += ballXdir;
            ballPosY += ballYdir;
            
            // Colisión con la paleta con rebote variable
            Rectangle ballRect = new Rectangle(ballPosX, ballPosY, ballDiameter, ballDiameter);
            Rectangle paddleRect = new Rectangle(playerX, panelHeight - 50, getPaddleWidth(), 8);
            if (ballRect.intersects(paddleRect)) {
                // Se calcula el centro de la paleta y de la bola
                int paddleCenter = playerX + getPaddleWidth() / 2;
                int ballCenter = ballPosX + ballDiameter / 2;
                // Valor entre -1 y 1 según dónde impacta la bola en la paleta
                double relativeIntersect = (double)(ballCenter - paddleCenter) / (getPaddleWidth() / 2);
                // Ángulo máximo de rebote (60°)
                double maxBounceAngle = Math.toRadians(60);
                double bounceAngle = relativeIntersect * maxBounceAngle;
                
                // Calcular la velocidad actual (magnitud)
                double speed = Math.sqrt(ballXdir * ballXdir + ballYdir * ballYdir);
                ballXdir = (int)Math.round(speed * Math.sin(bounceAngle));
                ballYdir = (int)Math.round(-speed * Math.cos(bounceAngle));
                
                // Asegurarse de que la bola se mueva hacia arriba
                if (ballYdir >= 0) {
                    ballYdir = -Math.abs(ballYdir);
                }
            }
            
            // Colisiones con los ladrillos
            for (int i = 0; i < map.getRows(); i++) {
                for (int j = 0; j < map.getColumns(); j++) {
                    if (map.map[i][j] > 0) {
                        Rectangle brickRect = map.getBrickRect(i, j, panelWidth, panelHeight);
                        if (new Rectangle(ballPosX, ballPosY, ballDiameter, ballDiameter).intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;
                            
                            if (ballPosX + ballDiameter - 1 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width)
                                ballXdir = -ballXdir;
                            else
                                ballYdir = -ballYdir;
                            
                            break;
                        }
                    }
                }
            }
            
            // Colisiones con las paredes
            if (ballPosX < 0)
                ballXdir = -ballXdir;
            if (ballPosY < 0)
                ballYdir = -ballYdir;
            if (ballPosX > panelWidth - ballDiameter)
                ballXdir = -ballXdir;
        }
        repaint();
    }
    
    // Métodos para el manejo del teclado
    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyReleased(KeyEvent e) { }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        int panelWidth = getWidth();
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            if (playerX >= panelWidth - getPaddleWidth())
                playerX = panelWidth - getPaddleWidth();
            else
                moveRight();
        }
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            if (playerX <= 0)
                playerX = 0;
            else
                moveLeft();
        }
    }
    
    public void moveRight() {
        playerX += 20;
        if (playerX > getWidth() - getPaddleWidth())
            playerX = getWidth() - getPaddleWidth();
        play = true;
    }
    
    public void moveLeft() {
        playerX -= 20;
        if (playerX < 0)
            playerX = 0;
        play = true;
    }
}
