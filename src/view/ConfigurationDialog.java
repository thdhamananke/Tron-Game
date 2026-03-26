package view;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.util.*;
import java.util.List;

public class ConfigurationDialog extends JDialog {

    private JSpinner nbEquipesSpinner;
    private JSpinner nbJoueursParEquipeSpinner;
    private JSpinner rowsSpinner;
    private JSpinner colsSpinner;
    private JPanel configPanel;
    private List<JComboBox<String>> strategieBoxes = new ArrayList<>();
    private List<JComboBox<String>> heuristiqueBoxes = new ArrayList<>();
    private List<JSpinner> depthSpinners = new ArrayList<>();
    private boolean confirmed = false;

    private List<Player> joueurs;
    private int nbLignes, nbColonnes;

    public ConfigurationDialog(JFrame parent) {
        super(parent, "Configuration de la partie", true);
        setLayout(new BorderLayout(10, 10));
        setSize(800, 600); // Augmenté légèrement pour accueillir le message d'aide
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. PARAMÈTRES GÉNÉRAUX (NORTH) ---
        JPanel topPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Paramètres généraux"));

        topPanel.add(new JLabel("Nombre de lignes :"));
        rowsSpinner = new JSpinner(new SpinnerNumberModel(30, 10, 50, 1));
        topPanel.add(rowsSpinner);

        topPanel.add(new JLabel("Nombre de colonnes :"));
        colsSpinner = new JSpinner(new SpinnerNumberModel(30, 10, 50, 1));
        topPanel.add(colsSpinner);

        topPanel.add(new JLabel("Nombre d'équipes :"));
        nbEquipesSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 6, 1));
        nbEquipesSpinner.addChangeListener(e -> regenererConfig());
        topPanel.add(nbEquipesSpinner);

        topPanel.add(new JLabel("Joueurs par équipe :"));
        nbJoueursParEquipeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));
        nbJoueursParEquipeSpinner.addChangeListener(e -> regenererConfig());
        topPanel.add(nbJoueursParEquipeSpinner);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- 2. CONFIGURATION DES JOUEURS (CENTER) ---
        configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(configPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 450));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- 3. ZONE BASSE (SOUTH) : MESSAGE D'AIDE + BOUTONS ---
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        // Bloc du message d'aide
        JLabel helpMsg = new JLabel("<html><div style='text-align: center; color: #555555;'>" +
                "<b>💡 Astuce :</b> Une fois la partie configurée, utilisez le <b>Mode Dessin</b><br>" +
                "dans le menu de droite pour placer des obstacles gris sur le plateau !</div></html>", SwingConstants.CENTER);
        helpMsg.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        helpMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("Lancer la partie");
        JButton cancelButton = new JButton("Annuler");

        okButton.addActionListener(e -> {
            if (validerConfiguration()) {
                confirmed = true;
                construireJoueurs();
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // On assemble le message et les boutons dans le panel Sud
        southPanel.add(helpMsg);
        southPanel.add(buttonPanel);
        
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
        regenererConfig(); 
    }

    private void regenererConfig() {
        configPanel.removeAll();
        strategieBoxes.clear();
        heuristiqueBoxes.clear();
        depthSpinners.clear();

        int nbEquipes = (int) nbEquipesSpinner.getValue();
        int nbParEquipe = (int) nbJoueursParEquipeSpinner.getValue();

        String[] strategies = {"Random", "MinMax", "AlphaBeta", "MaxN", "Paranoid", "SOS"};
        String[] heuristiques = {"FreeSpace", "Voronoi", "TreeOfChambers"};

        for (int eq = 0; eq < nbEquipes; eq++) {
            JPanel equipePanel = new JPanel(new BorderLayout());
            java.awt.Color borderColor = getAwtColorForTeam(eq).darker();
            equipePanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(borderColor, 2),
                    "Équipe " + (eq + 1) + " (" + getColorForTeam(eq).getEmoji() + ")",
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 12)
            ));
            equipePanel.setBackground(new java.awt.Color(245, 245, 245));

            
            JPanel joueursPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2, 5, 2, 5); // Espacement entre les éléments
            gbc.fill = GridBagConstraints.HORIZONTAL;

            for (int j = 0; j < nbParEquipe; j++) {
                gbc.gridy = j; // Ligne actuelle du joueur
                
                // Colonne 0 : Label
                gbc.gridx = 0; gbc.weightx = 0.1;
                joueursPanel.add(new JLabel("Joueur " + (j + 1) + " :"), gbc);

                // Colonne 1 : Stratégie
                gbc.gridx = 1; gbc.weightx = 0.4;
                JComboBox<String> stratBox = new JComboBox<>(strategies);
                stratBox.setSelectedItem("AlphaBeta");
                // Supprimez setPreferredSize ou réduisez-le
                stratBox.setMinimumSize(new Dimension(100, 20)); 
                joueursPanel.add(stratBox, gbc);
                strategieBoxes.add(stratBox);

                // Colonne 2 : Heuristique
                gbc.gridx = 2; gbc.weightx = 0.4;
                JComboBox<String> heurBox = new JComboBox<>(heuristiques);
                heurBox.setSelectedItem("FreeSpace");
                heurBox.setMinimumSize(new Dimension(100, 20));
                joueursPanel.add(heurBox, gbc);
                heuristiqueBoxes.add(heurBox);

                // Colonne 3 : Profondeur
                gbc.gridx = 3; gbc.weightx = 0.2;
                JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 15, 1));
                JPanel spinnerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                spinnerWrapper.setOpaque(false);
                depthSpinner.setPreferredSize(new Dimension(70, 40));
                spinnerWrapper.add(depthSpinner);

                joueursPanel.add(spinnerWrapper, gbc);
                depthSpinners.add(depthSpinner);
            }

            equipePanel.add(joueursPanel, BorderLayout.CENTER);
            configPanel.add(equipePanel);
            configPanel.add(Box.createVerticalStrut(10));
        }

        configPanel.revalidate();
        configPanel.repaint();
    }

    private model.Color getColorForTeam(int index) {
        model.Color[] colors = model.Color.values();
        return colors[index % colors.length];
    }

    private java.awt.Color getAwtColorForTeam(int index) {
        return getColorForTeam(index).toAWT();
    }

    private boolean validerConfiguration() { return true; }

    private void construireJoueurs() {
        nbLignes = (int) rowsSpinner.getValue();
        nbColonnes = (int) colsSpinner.getValue();
        int nbEquipes = (int) nbEquipesSpinner.getValue();
        int nbParEquipe = (int) nbJoueursParEquipeSpinner.getValue();

        joueurs = new ArrayList<>();
        List<Team> teams = new ArrayList<>();

        for (int eq = 0; eq < nbEquipes; eq++) {
            model.Color color = getColorForTeam(eq);
            Team team = new Team("Équipe " + (eq + 1), new ArrayList<>(), color);
            teams.add(team);
        }

        Random rand = new Random();
        Set<Position> prises = new HashSet<>();

        for (int eq = 0; eq < nbEquipes; eq++) {
            Team team = teams.get(eq);
            for (int j = 0; j < nbParEquipe; j++) {
                Position pos;
                int tries = 0;
                do {
                    int row = rand.nextInt(nbLignes);
                    int col = rand.nextInt(nbColonnes);
                    pos = new Position(row, col);
                    tries++;
                    if (tries > 1000) break;
                } while (prises.contains(pos) || tropProche(pos, prises, 3));

                prises.add(pos);
                String name = team.getName() + " - J" + (j + 1);
                Player player = new Player(name, team, pos);

                int idx = eq * nbParEquipe + j;
                String stratName = (String) strategieBoxes.get(idx).getSelectedItem();
                String heurName = (String) heuristiqueBoxes.get(idx).getSelectedItem();
                int depth = (int) depthSpinners.get(idx).getValue();

                Heuristic heuristic = creerHeuristique(heurName);
                Strategie strategie = creerStrategie(stratName, heuristic, depth, joueurs);

                player.setStrategie(strategie);
                player.setHeuristic(heuristic);
                joueurs.add(player);
                team.getMembers().add(player);
            }
        }
    }

    private Heuristic creerHeuristique(String nom) {
        return switch (nom) {
            case "Voronoi" -> new VoronoiHeuristic();
            case "TreeOfChambers" -> new TreeOfChambersHeuristic();
            default -> new FreeSpaceHeuristic();
        };
    }

    private Strategie creerStrategie(String nom, Heuristic heur, int depth, List<Player> joueurs) {
        return switch (nom) {
            case "Random" -> new RandomStrategie(heur, depth);
            case "MinMax" -> new MinMaxStrategie(heur, depth);
            case "AlphaBeta" -> new AlphaBetaStrategie(heur, depth);
            case "MaxN" -> new MaxNStrategie(heur, depth);
            case "Paranoid" -> new ParanoidStrategie(heur, depth);
            case "SOS" -> new SOSStrategie(heur, depth, joueurs);
            default -> new AlphaBetaStrategie(heur, depth);
        };
    }

    private boolean tropProche(Position pos, Set<Position> prises, int minDist) {
        for (Position p : prises) {
            if (p.distanceManhattan(pos) < minDist) return true;
        }
        return false;
    }

    public boolean isConfirmed() { return confirmed; }
    public List<Player> getJoueurs() { return joueurs; }
    public int getNbLignes() { return nbLignes; }
    public int getNbColonnes() { return nbColonnes; }
}