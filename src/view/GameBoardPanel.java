package view;

import javax.swing.*;
import java.awt.*;

public class GameBoardPanel extends JPanel {

    private int columns = 30;
    private int rows = 30;
    private static final double CELL_SIZE_SCALE = 1;
    private boolean[][] cellStates;

    public GameBoardPanel() {
        cellStates = new boolean[rows][columns];
        setBackground(Color.WHITE);
    }

    public void setGridSize(int rows, int cols) {
        this.rows = rows;
        this.columns = cols;
        this.cellStates = new boolean[rows][columns];
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int cellSize = (int) (Math.min(
                getWidth() / columns,
                getHeight() / rows
        ) * CELL_SIZE_SCALE);

        int offsetX = (getWidth() - columns * cellSize) / 2;
        int offsetY = (getHeight() - rows * cellSize) / 2;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                g2d.setColor(cellStates[row][col] ? Color.CYAN : Color.WHITE);
                g2d.fillRect(
                        offsetX + col * cellSize,
                        offsetY + row * cellSize,
                        cellSize,
                        cellSize
                );
                g2d.setColor(Color.BLACK);
                g2d.drawRect(
                        offsetX + col * cellSize,
                        offsetY + row * cellSize,
                        cellSize,
                        cellSize
                );
            }
        }
    }
}


