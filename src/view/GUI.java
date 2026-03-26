package view;

import controller.GameController;
import model.*;
import observer.EcouteurModele;

import javax.swing.*;
import java.awt.*;

/**
 * Interface Graphique - TOUTES LES CORRECTIONS
 */
public class GUI extends JFrame implements EcouteurModele {

    private final GameController controller;
    private TopPanel topPanel;
    private GameBoardPanel gameBoard;
    private JScrollPane scrollPane;
    private SidePanel sidePanel;
    private BottomPanel bottomPanel;

    private int rows = 30;
    private int columns = 30;

    public GUI(GameController controller) {
        this.controller = controller;

        setTitle("🎮 Jeu de Tron - Version Graphique Complète");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        
        getContentPane().setBackground(new java.awt.Color(230, 240, 250));

        initComponents();
        
        // FIX: Taille fixe pour éviter le scroll
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setVisible(true);

        System.out.println("=== Démarrage du Jeu Tron ===");
        System.out.println("✓ Interface initialisée");
        System.out.println("🎮 Prêt à jouer !");
    }

    private void initComponents() {
        
        // TopPanel
        topPanel = new TopPanel();
        add(topPanel, BorderLayout.NORTH);

        // GameBoard avec taille appropriée
        gameBoard = new GameBoardPanel(controller.getGame());
        gameBoard.setPreferredSize(new Dimension(750, 750));
        
        scrollPane = new JScrollPane(gameBoard);
        scrollPane.setBorder(BorderFactory.createLineBorder(new java.awt.Color(70, 130, 180), 3));
        scrollPane.setPreferredSize(new Dimension(800, 750));
        add(scrollPane, BorderLayout.CENTER);

        // SidePanel avec scroll interne
        sidePanel = new SidePanel(controller, this, gameBoard);
        
        // FIX: SidePanel avec son propre scroll
        JScrollPane sidePanelScroll = new JScrollPane(sidePanel);
        sidePanelScroll.setPreferredSize(new Dimension(360, 750));
        sidePanelScroll.setBorder(BorderFactory.createEmptyBorder());
        sidePanelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidePanelScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(sidePanelScroll, BorderLayout.EAST);

        // BottomPanel
        bottomPanel = new BottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Mise à jour complète de l'affichage
     */
    public void mettreAjourAffichage() {
        
        ModeleJeu game = controller.getGame();
        
        if (game == null) return;

        int tour = controller.getTour();
        String statut = controller.getGameState();
        String gagnant = controller.getWinner();

        // Mettre à jour tous les panels
        topPanel.update(tour, statut, gagnant, controller);
        
        // FIX: Conversion correcte avec les têtes des joueurs
        CellState[][] cellStates = convertToCellStates(game);
        gameBoard.updateFromModel(cellStates);
       // gameBoard.updateFromModel(this.controller.getGame().getPlateau().getEtatPourVue());
        bottomPanel.updateLegend(game.getJoueurs());

        // Message de victoire
        if (game.estTermine() && !controller.isRunning()) {
            SwingUtilities.invokeLater(this::afficherMessageVictoire);
        }
    }

    /**
     *  Afficher les têtes des joueurs correctement
     */
    private CellState[][] convertToCellStates(ModeleJeu game) {
        Plateau plateau = game.getPlateau();
        int rows = plateau.getNbLignes();
        int cols = plateau.getNbColonnes();
        
        CellState[][] states = new CellState[rows][cols];
        
        // D'abord remplir avec les murs et cellules vides
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Position pos = new Position(i, j);
                Cellule cell = game.getPlateau().getCellule(pos); 
                if (cell.isEmpty()) {
                    states[i][j] = CellState.EMPTY;
                } else {
                    states[i][j] = CellState.WALL;
                }

            }
        }
        
        //  FIX: Ensuite marquer les TÊTES des joueurs vivants
        // (ça écrase les murs aux positions actuelles des joueurs)
        for (Player p : game.getJoueurs()) {
            if (p.isAlive() && p.getPosition() != null) {
                Position pos = p.getPosition();
                if (plateau.estDansPlateau(pos)) {
                     // if (states[pos.getRow()][pos.getCol()] != CellState.WALL) {
                    
                    states[pos.getRow()][pos.getCol()] = CellState.PLAYER;
                     // }
                }
            }
        }
        
        return states;
    }

    /**
     * Message de victoire
     */
    private void afficherMessageVictoire() {
        
        ModeleJeu game = controller.getGame();
        Team gagnant = game.getEquipeGagnante();
        
        String message;
        String titre;
        
        if (gagnant == null) {
            titre = "🤝 MATCH NUL 🤝";
            message = String.format(
                "═══════════════════════════════\n" +
                "        PARTIE TERMINÉE\n" +
                "═══════════════════════════════\n\n" +
                "📊 Nombre de tours : %d\n\n" +
                "Match nul !\n\n" +
                "═══════════════════════════════",
                controller.getTour()
            );
        } else {
            titre = "🏆 VICTOIRE DE " + gagnant.getName().toUpperCase() + " 🏆";
            
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════\n");
            sb.append("        PARTIE TERMINÉE\n");
            sb.append("═══════════════════════════════\n\n");
            sb.append(String.format("📊 Nombre de tours : %d\n\n", controller.getTour()));
            
            sb.append("Résumé:\n");
            for (Player p : game.getJoueurs()) {
                String statut = p.isAlive() ? "✓ VIVANT" : "✗ MORT";
                sb.append(String.format("   %s : %s\n", p.getName(), statut));
            }
            
            sb.append("\n🎉 GAGNANT : ").append(gagnant.getName()).append(" 🎉\n\n");
            sb.append("═══════════════════════════════");
            message = sb.toString();
        }

        JTextArea textArea = new JTextArea(message);
        textArea.setFont(new Font("Monospaced", Font.BOLD, 12));
        textArea.setEditable(false);
        textArea.setOpaque(false);

        JOptionPane.showMessageDialog(
            this,
            textArea,
            titre,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Observer pattern
     */
    @Override
    public void modeleMisAJour(Object source) {
        SwingUtilities.invokeLater(this::mettreAjourAffichage);
    }

    // Getters
    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public GameBoardPanel getGameBoard() { return gameBoard; }
    
    public void setGridSize(int r, int c) {
        this.rows = r;
        this.columns = c;
        gameBoard.setGridSize(r, c);
        gameBoard.setGame(controller.getGame());
    }
}