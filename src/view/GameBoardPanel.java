package view;

import model.*;
import observer.EcouteurModele;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;

public class GameBoardPanel extends JPanel implements EcouteurModele {


    private int columns = 30;
    private int rows = 30;
    private static final double CELL_SIZE_SCALE = 1;
    private CellState[][] cellStates;
    private ModeleJeu game;

    public GameBoardPanel(ModeleJeu game) {
        this.game = game;
        cellStates = new CellState[rows][columns];
        setBackground(Color.WHITE);
    }

    public void setGridSize(int rows, int cols) {
        this.rows = rows;
        this.columns = cols;
        this.cellStates = new CellState[rows][columns];
        repaint();
    }

    @Override
    public void modeleMisAJour(Object source) {
        if (!(source instanceof Plateau plateau)) return;
        CellState[][] etat = plateau.getEtatPourVue();
        updateFromModel(etat);
    }

    public void updateFromModel(CellState[][] modelGrid) {
        this.rows = modelGrid.length;
        this.columns = modelGrid[0].length;
        this.cellStates = modelGrid;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (rows == 0 || columns == 0) return;

        Graphics2D g2d = (Graphics2D) g;
        int cellSize = (int)(Math.min(getWidth() / columns, getHeight() / rows) * CELL_SIZE_SCALE);

        int offsetX = (getWidth() - columns * cellSize) / 2;
        int offsetY = (getHeight() - rows * cellSize) / 2;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                if (cellStates[row][col] == null) cellStates[row][col] = CellState.EMPTY;

                java.awt.Color fillColor = Color.WHITE; // default

                Position pos = new Position(row, col);
                Player player = game.getJoueurAt(pos);

                if (player != null) {
                    // Player takes priority
                    fillColor = player.getAwtColor();
                } else if (cellStates[row][col] == CellState.WALL) {
                    fillColor = Color.GRAY;
                } else {
                    fillColor = Color.WHITE;
                }

                int x = offsetX + col * cellSize;
                int y = offsetY + row * cellSize;

                g2d.setColor(fillColor);
                g2d.fillRect(x, y, cellSize, cellSize);

                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, cellSize, cellSize);
            }
        }
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public void setGame(ModeleJeu game) {
        this.game = game;
    }


}


