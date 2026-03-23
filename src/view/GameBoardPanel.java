package view;

import model.*;
import observer.EcouteurModele;

import javax.swing.*;
import java.awt.*; // pour java.awt.Color, Graphics, etc.
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class GameBoardPanel extends JPanel implements EcouteurModele {

    private int columns = 30;
    private int rows = 30;

    private static final double CELL_SIZE_SCALE = 0.95;
    private static final int MIN_CELL_SIZE = 10;
    private static final int MAX_CELL_SIZE = 50;

    private CellState[][] cellStates;
    private ModeleJeu game;

    private int obstacleButton = 0;
    
    private int lastToggledRow = -1;
private int lastToggledCol = -1;

    public GameBoardPanel(ModeleJeu game) {
        this.game = game;
        System.out.println("in constructer");
        cellStates = new CellState[rows][columns];
        setBackground(java.awt.Color.WHITE);
        setPreferredSize(new Dimension(600, 600));
        initializeCellStates();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (game == null) return;
                toggleObstacle(e.getX(), e.getY());
            }
        });
        addMouseMotionListener(new MouseAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            int cellWidth = getPreferredSize().width / columns;
            int cellHeight = getPreferredSize().height / rows;
             if (game == null) return;
                 int row = e.getY() / cellHeight; 
                int col = e.getX() / cellWidth;  
       
        if (row != lastToggledRow || col != lastToggledCol) {
            toggleObstacle(e.getX(), e.getY());
            lastToggledRow = row;
            lastToggledCol = col;
        }
    }
    });
    }

    private void initializeCellStates() {
        System.out.println("initialqin");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cellStates[i][j] = CellState.EMPTY;
            }
        }
    }

    public void setGridSize(int rows, int cols) {
        this.rows = rows;
        this.columns = cols;
        this.cellStates = new CellState[rows][columns];
        initializeCellStates();
        repaint();
    }

    /* ================= OBSTACLES ================= */

   private void toggleObstacle(int mouseX, int mouseY) {
    System.out.println("toggleObstacle called at: " + mouseX + ", " + mouseY); // DEBUG
    int cellSize = calculateCellSize();
    int offsetX = (getWidth() - columns * cellSize) / 2;
    int offsetY = (getHeight() - rows * cellSize) / 2;

    int col = (mouseX - offsetX) / cellSize;
    int row = (mouseY - offsetY) / cellSize;

    System.out.println("Calculated row: " + row + ", col: " + col); // DEBUG

    if (row < 0 || row >= rows || col < 0 || col >= columns) {
        System.out.println("Out of bounds"); // DEBUG
        return;
    }

    Position pos = new Position(row, col);

    if ((row == 2 && col == 2) || (row == rows - 3 && col == columns - 3)) {
        JOptionPane.showMessageDialog(this,
                "Impossible de placer un obstacle sur une position de départ!",
                "Avertissement",
                JOptionPane.WARNING_MESSAGE);
        return;
    }
    System.out.println("Before toggle, cell at (" + row + ", " + col + "): " + cellStates[row][col]);
    
    if (game.getPlateau().getObstacles().contains(pos)) {
      
        System.out.println("Removing obstacle at: " + pos);
        this.game.retirerObstacle(pos);
                 
    } else {

        System.out.println("Adding obstacle at: " + pos);
        this.game.ajouterObstacle(pos);
        
    }
   
    updateFromModel(game.getPlateau().getEtatPourVue());
    revalidate();
    repaint();
}


    public void clearObstacles() {
        if (game != null) {
            game.clearObstacles();
            repaint();
        }
    }

    /* ================= OBSERVATEUR ================= */

    @Override
    public void modeleMisAJour(Object source) {
        if (game != null && game.getPlateau() != null) {
            updateFromModel(game.getPlateau().getEtatPourVue());
        }
    }

    public void updateFromModel(CellState[][] modelGrid) {
        if (modelGrid == null ) return;

        this.rows = modelGrid.length;
        this.columns = modelGrid[0].length;
        //this.cellStates = modelGrid;
         for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
            if(modelGrid[i][j] == CellState.WALL){
            }
            this.cellStates[i][j] = modelGrid[i][j];
        }
         }
        repaint();
    }

    /* ================= AFFICHAGE ================= */

    private int calculateCellSize() {
        int availableWidth = getWidth() - 20;
        int availableHeight = getHeight() - 20;

        int cellSize = Math.min(
                availableWidth / Math.max(1, columns),
                availableHeight / Math.max(1, rows)
        );

        cellSize = (int)(cellSize * CELL_SIZE_SCALE);

        return Math.max(
                MIN_CELL_SIZE,
                Math.min(MAX_CELL_SIZE, cellSize)
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (rows == 0 || columns == 0) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int cellSize = calculateCellSize();
        int totalWidth = columns * cellSize;
        int totalHeight = rows * cellSize;

        int offsetX = (getWidth() - totalWidth) / 2;
        int offsetY = (getHeight() - totalHeight) / 2;

        // Dessiner les cellules

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int x = offsetX + col * cellSize;
                int y = offsetY + row * cellSize;
                java.awt.Color fillColor = getCellColor(row, col);
               
                g2d.setColor(fillColor);
                g2d.fillRect(x, y, cellSize, cellSize);

                // Contour léger
                g2d.setColor(new java.awt.Color(200, 200, 200));
                g2d.drawRect(x, y, cellSize, cellSize);
            }
        }

        // Dessiner les têtes des joueurs par-dessus (priorité absolue)
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Position pos = new Position(row, col);
                Player player = game.getJoueurAt(pos);
                if (player != null && player.isAlive() && player.getPosition().equals(pos)) {
                    int x = offsetX + col * cellSize;
                    int y = offsetY + row * cellSize;
                    drawPlayerHead(g2d, player, x, y, cellSize);
                }
            }
        }

        // Bordure noire autour du plateau
        g2d.setColor(java.awt.Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(offsetX - 1, offsetY - 1, totalWidth + 2, totalHeight + 2);
    }

    private void drawPlayerHead(Graphics2D g2d, Player player, int x, int y, int cellSize) {
        // On utilise une couleur plus foncée pour la tête
        g2d.setColor(player.getAwtColor().darker());

        int headSize = cellSize - 4;
        int headX = x + 2;
        int headY = y + 2;

        Ellipse2D.Double head = new Ellipse2D.Double(headX, headY, headSize, headSize);
        g2d.fill(head);
    }

    private java.awt.Color getCellColor (int row, int col) {
        if (cellStates == null) return java.awt.Color.WHITE;
      
        switch (cellStates[row][col]) {
            case EMPTY:
             
                return java.awt.Color.WHITE;
            case WALL:
                return java.awt.Color.DARK_GRAY;
            case PLAYER:
                // On retourne la couleur du joueur plus claire pour le fond
                Player p = game.getJoueurAt(new Position(row, col));
                if (p != null) {
                    return p.getAwtColor().brighter();
                }
                return java.awt.Color.WHITE;
            default:
                return java.awt.Color.WHITE;
        }
    }

    public int getColumns() { return columns; }
    public int getRows() { return rows; }
    public void setObstacleButton(int Int) { this.obstacleButton = Int ;  }
    public int getObstacleButton() { return obstacleButton; }

    public void setGame(ModeleJeu game) {
        this.game = game;
        if (game != null && game.getPlateau() != null) {
            System.out.println("in set game ");
            updateFromModel(game.getPlateau().getEtatPourVue());
        }
    }
}