package view;

import controller.GameController;
import controller.GameRecord;
import model.ModeleJeu;
import observer.EcouteurModele;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GUI extends JFrame implements  EcouteurModele {

    private final GameController controller;

    private TopPanel topPanel;
    private SidePanel sidePanel;
    private BottomPanel bottomPanel;
    private GameBoardPanel gameBoard;

    public GUI(GameController controller) {

        this.controller = controller;

        setTitle("Jeu Tron - Combat de Bots Avancé");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5,5));

        initComponents();
        createMenuBar();

        setLocationRelativeTo(null);
        setVisible(true);

        mettreAjourAffichage();
    }

    private void initComponents() {

        ModeleJeu game = controller.getGame();

        topPanel = new TopPanel();
        gameBoard = new GameBoardPanel(game);
        sidePanel = new SidePanel(controller, this, gameBoard);
        bottomPanel = new BottomPanel();

        add(topPanel, BorderLayout.NORTH);
        add(gameBoard, BorderLayout.CENTER);

        // 🔥 ScrollPane correct
        JScrollPane scrollPane = new JScrollPane(sidePanel);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    public int getRows() {
        return gameBoard.getRows();
    }

    public int getColumns() {
        return gameBoard.getColumns();
    }

    /* ================= MENU ================= */

    private void createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu jeuMenu = new JMenu("Jeu");

        JMenuItem nouvellePartie = new JMenuItem("Nouvelle Partie");
        nouvellePartie.addActionListener(e ->
                sidePanel.getControlSection().restart());

        JMenuItem quitter = new JMenuItem("Quitter");
        quitter.addActionListener(e -> System.exit(0));

        jeuMenu.add(nouvellePartie);
        jeuMenu.addSeparator();
        jeuMenu.add(quitter);

        JMenu historiqueMenu = new JMenu("Historique");

        JMenuItem voirHistorique = new JMenuItem("Voir l'historique");
        voirHistorique.addActionListener(e -> afficherHistorique());

        JMenuItem statistiques = new JMenuItem("Statistiques");
        statistiques.addActionListener(e -> afficherStatistiques());

        historiqueMenu.add(voirHistorique);
        historiqueMenu.add(statistiques);

        menuBar.add(jeuMenu);
        menuBar.add(historiqueMenu);

        setJMenuBar(menuBar);
    }

    private void afficherHistorique() {

        List<GameRecord> parties =
                controller.getHistory().getParties();

        if (parties.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Aucune partie dans l'historique",
                    "Historique",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea textArea = new JTextArea(parties.toString());
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600,400));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "Historique",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void afficherStatistiques() {

        JOptionPane.showMessageDialog(this,
                controller.getHistory().getStatistiques(),
                "Statistiques",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void mettreAjourAffichage() {

        if (controller.getGame() == null) return;

        topPanel.update(
                controller.getTour(),
                controller.getGameState(),
                controller.getWinner()
        );

        gameBoard.setGame(controller.getGame());
        gameBoard.repaint();

        if (controller.getGame().estTermine()
                && controller.isRunning()) {

            controller.setRunning(false);
            afficherMessageVictoire();
            sidePanel.getControlSection().enableStart();
        }
    }

    private void afficherMessageVictoire() {

        String winner = controller.getWinner();

        String message;
        String title;

        if (winner.equals("Match nul")) {
            message = "Match nul !\nAucun joueur n'a survécu.";
            title = "Égalité";
        } else {
            message = "🏆 Victoire pour " + winner +
                      "\nNombre de tours : " + controller.getTour();
            title = "Partie Terminée";
        }

        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void modeleMisAJour(Object source) {
        SwingUtilities.invokeLater(this::mettreAjourAffichage);
    }
}
