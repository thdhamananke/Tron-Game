package view;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

import controller.GameController;
import model.ModeleJeu;
import observer.EcouteurModele;

public class GUI extends JFrame implements VueJeu ,EcouteurModele  {

    // private ModeleJeu game;
    private JPanel topPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;
    private GameBoardPanel gameBoard;
    private GameController controller;
    private JComboBox<String> strategieRougeBox;
    private JComboBox<String> strategieBleuBox;


    public GUI(GameController controller) {

        setTitle("Jeu Tron - Combat de Bots");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        this.controller = controller;
        createTopPanel();
        createGameBoard(this.controller.getGame());
        createRightPanel();
        createBottomPanel();

        add(topPanel, BorderLayout.NORTH);
        add(gameBoard, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
       // rightPanel.setMinimumSize(new Dimension(40,100));

        setLocationRelativeTo(null);
        setVisible(true);
    }


    /* ===================== TOP PANEL ===================== */
    private void createTopPanel() {
        topPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        topPanel.add(createInfoBox("TOUR", "14"));
        topPanel.add(createInfoBox("STATUT", "Terminé"));
        topPanel.add(createInfoBox("GAGNANT", "Bot Bleu"));

        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private JPanel createInfoBox(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JLabel(value, SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    /* ===================== GAME BOARD ===================== */
    private void createGameBoard(ModeleJeu game) {
        gameBoard = new GameBoardPanel(game); // rows, cols

        gameBoard.setBorder(new CompoundBorder(
                new BevelBorder(BevelBorder.LOWERED),
                new EmptyBorder(10, 10, 10, 10)
        ));
        gameBoard.setBackground(Color.BLACK);
    }


    /* ===================== RIGHT PANEL ===================== */
    private void createRightPanel() {
        rightPanel = new JPanel();

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new CompoundBorder(
                new BevelBorder(BevelBorder.RAISED),
                new EmptyBorder(10, 10, 10, 10)
        ));
        rightPanel.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Contrôles");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(title);
        rightPanel.add(Box.createVerticalStrut(10));
        JButton startButton = createButton("▶ Démarrer", new Color(0, 200, 0));
        rightPanel.add(startButton);
        rightPanel.add(Box.createVerticalStrut(8));

        startButton.addActionListener(e -> {
            gameBoard.setGame(controller.getGame());
            new Thread(() -> controller.lunchgame()).start();
           this.repaint();
           this.revalidate();

        });
        rightPanel.add(createButton("⏸ Pause", new Color(255, 200, 0)));
        rightPanel.add(Box.createVerticalStrut(8));

        rightPanel.add(createButton("⏭ Tour Suivant", new Color(0, 200, 200)));
        rightPanel.add(Box.createVerticalStrut(8));

        JButton restartButton = createButton("⟲ Redémarrer", new Color(220, 0, 0));
        rightPanel.add(restartButton);
        restartButton.addActionListener(e -> {
            controller.restart();
            gameBoard.setGame(controller.getGame());
            new Thread(() -> controller.lunchgame()).start();
            this.repaint();
            this.revalidate();

        });
        rightPanel.add(Box.createVerticalStrut(15));

        strategieRougeBox = createStrategyBox("Bot Rouge");
        rightPanel.add(Box.createVerticalStrut(10));
        strategieBleuBox = createStrategyBox("Bot Bleu");
        JButton applyStrategieButton = createButton("Appliquer stratégies", null);

        applyStrategieButton.addActionListener(e -> {
            String strategieRouge = getStrategieRouge();
            String strategieBleu  = getStrategieBleu();

            controller.appliquerStrategie(strategieRouge, strategieBleu);
        });


        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(applyStrategieButton);


        rightPanel.add(Box.createVerticalStrut(15));

        JSpinner rowsSpinner = createSpinner(30,15,100 );
        JSpinner colsSpinner = createSpinner(30,15,100 );
        JButton applyboardSizeButton = createButton("Applique",null);

        applyboardSizeButton.addActionListener(e -> {
            int rows = (int) rowsSpinner.getValue();
            int cols = (int) colsSpinner.getValue();
            this.controller.changeGridSize(rows, cols);

        });

        JLabel sizeLabel = new JLabel("board size");
        sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightPanel.add(sizeLabel);

        JPanel spinnerPanel = new JPanel();
        spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.Y_AXIS));
        spinnerPanel.add(rowsSpinner);
        spinnerPanel.add(colsSpinner);
        spinnerPanel.add(applyboardSizeButton);
        rightPanel.add(spinnerPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        JLabel paternLabel = new JLabel("Patterns");
        paternLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paternLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightPanel.add(paternLabel);

        JComboBox<String> patterncombobox = new JComboBox<>();
        patterncombobox.addItem( "pattern1");
        patterncombobox.addItem( "pattern2");
        patterncombobox.addItem( "pattern3");
        patterncombobox.addItem( "pattern4");
        rightPanel.add(patterncombobox);

        // JSlider slider = new JSlider(100, 1600, 600);
        //slider.setMajorTickSpacing(500);
       // slider.setPaintTicks(true);
       // slider.setPaintLabels(true);
        // rightPanel.add(slider);

    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorder(new BevelBorder(BevelBorder.RAISED));

        btn.getModel().addChangeListener(e -> {
            if (btn.getModel().isPressed()) {
                btn.setBorder(new BevelBorder(BevelBorder.LOWERED));
            } else {
                btn.setBorder(new BevelBorder(BevelBorder.RAISED));
            }
        });

        return btn;
    }


    private JComboBox<String> createStrategyBox(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new CompoundBorder(
                new BevelBorder(BevelBorder.LOWERED),
                new EmptyBorder(5, 5, 5, 5)
        ));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));

        JComboBox<String> combo = new JComboBox<>(new String[]{
                "Random",
                "Minimax",
                "AlphaBeta",
                "MaxN",
                "Paranoid",
                "SOS"
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);
        rightPanel.add(panel);

        return combo;
    }


    /* ================= BOTTOM PANEL ================= */
    private void createBottomPanel() {
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        bottomPanel.add(createLegend(Color.RED, "Bot Rouge"));
        bottomPanel.add(createLegend(Color.BLUE, "Bot Bleu"));
        bottomPanel.add(createLegend(Color.GRAY, "Mur"));
    }

    private JPanel createLegend(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel box = new JLabel("   ");
        box.setOpaque(true);
        box.setBackground(color);
        p.add(box);
        p.add(new JLabel(text));
        return p;
    }

    private JSpinner createSpinner(int value, int min, int max) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, 1));
        spinner.setFont(new Font("Arial", Font.PLAIN, 12));
        spinner.setPreferredSize(new Dimension(50, 35));
        return spinner;
    }

    /* ===================== UPDATE VIEW ===================== */
    @Override
    public void mettreAjourAffichage() {
        // modeleMisAJour(this);
        gameBoard.setGame(controller.getGame());
        this.gameBoard.updateFromModel(this.controller.getGame().getPlateau().getEtatPourVue());
        repaint();
        revalidate();
    }


    @Override
    public void modeleMisAJour(Object source) {
        this.gameBoard.updateFromModel(this.controller.getGame().getPlateau().getEtatPourVue());
        mettreAjourAffichage();
    }

    public void setController(GameController controller){
        this.controller = controller;
    }

    public int getColumns() {
        return this.gameBoard.getColumns() ;
    }

    public int getRows() {
        return this.gameBoard.getRows();
    }

    public String getStrategieRouge() {
        return (String) strategieRougeBox.getSelectedItem();
    }

    public String getStrategieBleu() {
        return (String) strategieBleuBox.getSelectedItem();
    }


}