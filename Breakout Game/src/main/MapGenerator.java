package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

class MapGenerator {
    public int map[][];
    private int rows;
    private int cols;
    
    public MapGenerator(int row, int col) {
        this.rows = row;
        this.cols = col;
        map = new int[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                map[i][j] = 1;
            }
        }
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getColumns() {
        return cols;
    }
    
    /**
     * Retorna el rectángulo que representa la posición y tamaño del ladrillo,
     * calculado de forma responsive según el tamaño actual del panel.
     */
    public Rectangle getBrickRect(int row, int col, int panelWidth, int panelHeight) {
        int leftMargin = panelWidth / 10;
        int topMargin = panelHeight / 10;
        int availableWidth = panelWidth - 2 * leftMargin;
        int availableHeight = panelHeight / 3;
        int brickWidth = availableWidth / cols;
        int brickHeight = availableHeight / rows;
        int brickX = col * brickWidth + leftMargin;
        int brickY = row * brickHeight + topMargin;
        return new Rectangle(brickX, brickY, brickWidth, brickHeight);
    }
    
    /**
     * Dibuja los ladrillos. Cada fila se pinta con un color distinto (se reciclan si hay más filas que colores).
     */
    public void draw(Graphics2D g, int panelWidth, int panelHeight) {
        int leftMargin = panelWidth / 10;
        int topMargin = panelHeight / 10;
        int availableWidth = panelWidth - 2 * leftMargin;
        int availableHeight = panelHeight / 3;
        int brickWidth = availableWidth / cols;
        int brickHeight = availableHeight / rows;
        
        Color[] colors = {Color.red, Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue, Color.magenta};
        
        for (int i = 0; i < rows; i++) {
            g.setColor(colors[i % colors.length]);
            for (int j = 0; j < cols; j++) {
                if (map[i][j] > 0) {
                    int brickX = j * brickWidth + leftMargin;
                    int brickY = i * brickHeight + topMargin;
                    g.fillRect(brickX, brickY, brickWidth, brickHeight);
                    
                    g.setStroke(new BasicStroke(2));
                    g.setColor(Color.black);
                    g.drawRect(brickX, brickY, brickWidth, brickHeight);
                    g.setColor(colors[i % colors.length]);
                }
            }
        }
    }
    
    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}
