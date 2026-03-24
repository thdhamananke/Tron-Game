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
        setSize(800, 500); // un peu plus large
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Paramètres généraux
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

        // Panneau de configuration des joueurs (dynamique)
        configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(configPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Interdire le scroll horizontal
        configPanel.setMaximumSize(new Dimension(scrollPane.getWidth(), Integer.MAX_VALUE));
        scrollPane.setPreferredSize(new Dimension(600, 250));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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

        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        regenererConfig(); // initialisation
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

            JPanel joueursPanel = new JPanel(new GridLayout(nbParEquipe, 4, 8, 5)); // 4 colonnes
            joueursPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            for (int j = 0; j < nbParEquipe; j++) {
                joueursPanel.add(new JLabel("Joueur " + (j + 1) + " :", SwingConstants.RIGHT));

                // strategie
                JComboBox<String> stratBox = new JComboBox<>(strategies);
                stratBox.setSelectedItem("AlphaBeta");
                stratBox.setPreferredSize(new Dimension(120, 20));
                joueursPanel.add(stratBox);
                strategieBoxes.add(stratBox);

                // 2. Heuristique
                JComboBox<String> heurBox = new JComboBox<>(heuristiques);
                heurBox.setSelectedItem("FreeSpace");
                heurBox.setPreferredSize(new Dimension(120, 20));
                joueursPanel.add(heurBox);
                heuristiqueBoxes.add(heurBox);

                // 3. Profondeur
                JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
                JPanel depthWrapper = new JPanel(new BorderLayout());
                
                depthWrapper.setOpaque(false);
                depthWrapper.setPreferredSize(new Dimension(40, 20));
                depthWrapper.add(depthSpinner, BorderLayout.CENTER);

                joueursPanel.add(depthWrapper); 
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

    private boolean validerConfiguration() {
        return true;
    }

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
                    if (tries > 1000) {
                        pos = new Position(rand.nextInt(nbLignes), rand.nextInt(nbColonnes));
                        break;
                    }
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
            case "FreeSpace" -> new FreeSpaceHeuristic();
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<Player> getJoueurs() {
        return joueurs;
    }

    public int getNbLignes() {
        return nbLignes;
    }

    public int getNbColonnes() {
        return nbColonnes;
    }
}