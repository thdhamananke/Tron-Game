package model;

import java.util.*;

/**
 * Classe principale pour tester les strategies (MinMax et AlphaBeta)
 * avec affichage graphique de la grille
*/
public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        //  configuration unique
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║         BIENVENUE AU JEU DE TRON           ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        int nbLignes  = entier(sc, "Nombre de lignes du plateau : ");
        int nbColonnes  = entier(sc, "Nombre de colonnes du plateau : ");
        int nbEquipes  = entier(sc, "Nombre d'équipes : ");
        int joueursParEquipe = entier(sc, "Nombre de joueurs par équipe : ");
        int profondeur = entier(sc, "Profondeur de la recherche : ");

        Plateau plateau = new Plateau(nbLignes, nbColonnes);

        //  strategies demander une seule fois 
        List<Player> joueurs = new ArrayList<>();
        List<Team> equipes = new ArrayList<>();

        Color[] colors = couleursAleatoires(nbEquipes);

        for (int i = 0; i < nbEquipes; i++) {
            Color color = colors[i];
            Team team = new Team("Equipe_" + (i + 1), new ArrayList<>(), color);

            List<Player> teamPlayers = playersForTeam(team.getName(), joueursParEquipe, plateau, team);
            team.getMembers().addAll(teamPlayers);
            equipes.add(team);
            joueurs.addAll(teamPlayers);
        }

        for (Player player : joueurs) {
            System.out.println("\nConfiguration pour " + player.getName());
            System.out.println("Stratégie à utiliser ?");
            System.out.println("1 - MinMax");
            System.out.println("2 - AlphaBeta");
            System.out.println("3 - MaxN");
            System.out.println("4 - Paranoid");
            System.out.println("5 - SOS");
            int choixAlgo = entier(sc, "Votre Choix : ");

            System.out.println("Heuristique à utiliser ? ");
            System.out.println("1 - FreeSpaceHeuristic");
            System.out.println("2 - VoronoiHeuristic");
            System.out.println("3 - TreeOfChambersHeuristic");
            int choixHeuristique = entier(sc, "Votre choix : ");

            Heuristic heuristic ;
            switch (choixHeuristique) {
                case 1 -> heuristic = new FreeSpaceHeuristic();
                case 2 -> heuristic = new VoronoiHeuristic();
                case 3 -> heuristic = new TreeOfChambersHeuristic();
                default -> heuristic = new FreeSpaceHeuristic();
            }
   
            Strategie strat;
            switch (choixAlgo) {
                case 1 -> strat = new MinMaxStrategie(heuristic, profondeur);
                case 2 -> strat = new AlphaBetaStrategie(heuristic, profondeur);
                case 3 -> strat = new MaxNStrategie(heuristic, profondeur);
                case 4 -> strat = new ParanoidStrategie(heuristic, profondeur);
                case 5 -> strat = new SOSStrategie(heuristic , profondeur, joueurs );
                default -> strat = new MinMaxStrategie(heuristic, profondeur);
            }

            // On stocke la stratégie directement dans le joueur
            player.setStrategie(strat);
        }

        // la boucle des parties
        boolean continuer = true;
        while (continuer) {
            // créer un plateau et réinitialiser les joueurs pour la nouvelle partie
            plateau = new Plateau(nbLignes, nbColonnes);
            ModeleJeu modele = new ModeleJeu(nbLignes, nbColonnes, joueurs);
            ModeleJeuThread modeleThread = new ModeleJeuThread(modele, 700);
            
            
            modele.demarrer();

            // placer les joueurs sur le plateau
            for (Team team : equipes) {
                for (Player player : team.getMembers()) {
                    Position pos = randomEmptyPosition(plateau);
                    player.setAlive(true);
                    plateau.placerJoueur(pos, player);
                }
            }

            // jouer une partie
            int maxTours = calculerMaxTours(nbLignes, nbColonnes, joueurs.size());
            jouerPartieGenerique(modeleThread, joueurs, maxTours);

            // demander si l’utilisateur veut rejouer
            System.out.println("Voulez-vous rejouer une partie ? (O/N) : ");
            String reponse = sc.nextLine().trim().toUpperCase();
            if (!reponse.equals("O")) {
                continuer = false;
                System.out.println("Merci d'avoir joué ! À bientôt !");
            }
        }
    }


    /**
     * Permet de joueur une partie.
     * 
     * @param modeleThread      le modele du jeu
     * @param joueurs           list des joueurs
     * @param strategies        list des strategies
     * @param maxTours          le nombre de tour maximale
    */
    public static void jouerPartieGenerique(ModeleJeuThread modeleThread, List<Player> joueurs, int maxTours) {
        int tour = 0;

        while (!modeleThread.estTermine() && tour < maxTours) {
            tour++;
            clearScreen();

            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║               TOUR " + String.format("%3d", tour) + "                     ║");
            System.out.println("╚════════════════════════════════════════════╝\n");

            // Faire réfléchir les IA et applique le mouvement
            modeleThread.avancerTour();

            // Affichage des résultats du tour
            for (Player player : joueurs) {
                if (player.isAlive()) {
                    Direction dir = player.getDernierCoupCal();
                    String directionTexte = (dir != null) ? formatDirection(dir) : "TIMEOUT/ATTENTE";
                    System.out.println(player.getColor().paint("█") + " " + player.getName() + " : " + directionTexte);
                } else {
                    System.out.println(player.getColor().paint(" ") + " " + player.getName() + " : MORT");
                }
            }

            System.out.println();
            afficherPlateauColore(modeleThread.getPlateau(), joueurs);
            
            // Petite pause visuelle pour l'utilisateur
            pause(200);
        }

        afficherResultatsFinaux(modeleThread.getModele(), tour);
    }


    /**
     * Affiche le plateau avec cellules carrées (console)
    */
    private static void afficherPlateauColore(Plateau plateau, List<Player> joueurs) {

        int lignes = plateau.getNbLignes();
        int colonnes = plateau.getNbColonnes();

        // En-tête colonnes
        System.out.print("    ");
        for (int j = 0; j < colonnes; j++) {
            System.out.print(String.format("%2d ", j));
        }
        System.out.println();

        // Bordure supérieure
        System.out.print("   ┌");
        for (int j = 0; j < colonnes; j++) {
            System.out.print("──");
            if (j < colonnes - 1) System.out.print("┬");
        }
        System.out.println("┐");

        // Contenu
        for (int i = 0; i < lignes; i++) {
            System.out.print(String.format("%2d │", i));

            for (int j = 0; j < colonnes; j++) {
                Position pos = new Position(i, j);
                Cellule cellule = plateau.getCellule(pos);

                boolean headPrinted = false;

                for (Player p : joueurs) {
                    if (p.isAlive() && p.getPosition().equals(pos)) {
                        System.out.print(p.getColor().paintHead("●●"));
                        headPrinted = true;
                        break;
                    }
                }

                if (!headPrinted) {
                    if (cellule.isEmpty()) {
                        System.out.print("  "); 
                    } else {
                        System.out.print(cellule.getOwner().getColor().paint("██"));
                    }
                }

                if (j < colonnes - 1) System.out.print("│");
            }

            System.out.println("│");

            // Séparateur horizontal
            if (i < lignes - 1) {
                System.out.print("   ├");
                for (int j = 0; j < colonnes; j++) {
                    System.out.print("──");
                    if (j < colonnes - 1) System.out.print("┼");
                }
                System.out.println("┤");
            }
        }

        // Bordure inférieure
        System.out.print("   └");
        for (int j = 0; j < colonnes; j++) {
            System.out.print("──");
            if (j < colonnes - 1) System.out.print("┴");
        }
        System.out.println("┘\n");
    }

    /**
     * Calcule un maxTours adapté à la taille du plateau et au nombre de joueurs.
     * Facteur dépend du nombre de joueurs : plus il y a de joueurs, plus le plateau se remplit vite
    */
    private static int calculerMaxTours(int nbLignes, int nbColonnes, int nbJoueurs) {
        int taillePlateau = nbLignes * nbColonnes;
        double facteur = Math.max(0.5, 1.0 - (nbJoueurs - 1) * 0.1);
        return (int) (taillePlateau * facteur);
    }

    /**
     * Determine une coleur aleatroire pour les equipes
     * @param nbEquipes nombre d'equipe
     * @return  la couleur
    */
    private static Color[] couleursAleatoires(int nbEquipes) {
        Color[] result = new Color[nbEquipes];
        List<Color> available = new ArrayList<>(Arrays.asList(Color.values()));
        Random rand = new Random();

        for (int i = 0; i < nbEquipes; i++) {
            int index = rand.nextInt(available.size());
            result[i] = available.get(index);
            available.remove(index);
        }
        return result;
    }

    /**
     * Affiche les résultats finaux  (utilise le modèle pur)
     * @param modele le modele du jeu
     * @param tour  le tour.
    */
    private static void afficherResultatsFinaux(ModeleJeu modele, int tour) {
        clearScreen();
        System.out.println("\n" + "═".repeat(60));
        System.out.println("🏆          PARTIE TERMINÉE          🏆");
        System.out.println("═".repeat(60) + "\n");
        
        System.out.println("📊 Nombre de tours : " + tour);
        
        List<Player> joueurs = modele.getJoueurs();
        for (Player p : joueurs) {
            String statut = p.isAlive() ? "✓ VIVANT" : "✗ MORT";
            System.out.println("   " + p.getColor().paint("█") + " " + p.getName() + 
                             " : " + statut);
        }
        
        System.out.println();
        
        if (modele.estTermine()) {
            Team gagnant = modele.getEquipeGagnante();
            if (gagnant != null) {
                System.out.println("🎉 GAGNANT : " + gagnant.getColor().paintHead(gagnant.getName()) + " 🎉");
            } else {
                System.out.println("🤝 MATCH NUL 🤝");
            }
        }
        
        System.out.println("\n" + "═".repeat(60) + "\n");
    }
    
    /** Formate une direction avec une flèche */
    private static String formatDirection(Direction dir) {
        if (dir == null) return "TIMEOUT (STAY)";
        switch (dir) {
            case HAUT: return "↑ HAUT";
            case BAS: return "↓ BAS";
            case GAUCHE: return "← GAUCHE";
            case DROITE: return "→ DROITE";
            default: return dir.toString();
        }
    }
    
    /** Efface l'écran (compatible multi-plateformes) */
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Si ça ne marche pas, on fait juste des lignes vides
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }
    
    /** Met en pause l'exécution */
    private static void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Lecture sécurisée d'un entier positif
     * @param scanner   le scanner
     * @param prompt    l'entrer donnée
     * @return un entier valide.
    */
    public static int entier(Scanner scanner, String prompt) {
        int value;
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                scanner.nextLine();
                if (value > 0) break; 
                else System.out.println("Merci d'entrer un entier positif.");
            } else {
                System.out.println("Erreur : veuillez entrer un entier.");
                scanner.nextLine();
            }
        }
        return value;
    }

    /**
     * Pour composer les membres de l'équipe.
     * @param teamName  nom de l'equipe
     * @param teamSize  la taille de l'equipe
     * @param plateau   le plateau du jeu
     * @param team l'équipe concerné
     * @return la liste des joueurs d'une équipe donnée
    */
    public static List<Player> playersForTeam(String teamName, int teamSize, Plateau plateau, Team team) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < teamSize; i++) {
            Position pos = randomEmptyPosition(plateau);
            Player player = new Player(teamName + "_" + (i+1), team, pos);
            players.add(player);
            plateau.placerJoueur(pos, player);
        }
        return players;
    }

    /**
     * Permet de trouver une position vide sur le plateau pour pouvoir placer un joueur
     * @param plateau le plateau concerné
     * @return une position choisie aléatoirement.
    */
    private static Position randomEmptyPosition(Plateau plateau) {
        Random random = new Random();
        Position position;
        int essais = 0;
        do {
            position = new Position(
                random.nextInt(plateau.getNbLignes()),
                random.nextInt(plateau.getNbColonnes())
            );
            essais++;
            if (essais > plateau.getNbLignes() * plateau.getNbColonnes()) {
                throw new IllegalStateException("Impossible de trouver une case libre");
            }
        } while (!plateau.estLibre(position));
        return position;
    }

}