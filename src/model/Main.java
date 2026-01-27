package model;

import java.util.*;

/**
 * Classe Main pour tester la stratégie MinMaxStrategy
 * avec affichage graphique de la grille
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║   TEST STRATEGIE MINMAX - JEU DE TRON     ║");
        System.out.println("╚════════════════════════════════════════════╝\n");
        
        // Configuration du plateau
        int nbLignes = 10;
        int nbColonnes = 10;
        
        // Création des équipes
        Team equipeRouge = new Team("Equipe Rouge", new ArrayList<>(), Color.RED);
        Team equipeBleu = new Team("Equipe Bleue", new ArrayList<>(), Color.BLUE);
        
        // Création des joueurs avec positions initiales bien séparées
        Player joueur1 = new Player("Alice", equipeRouge, new Position(2 ,4));
        Player joueur2 = new Player("Bob", equipeBleu, new Position(5, 5));
        
        // Ajout des joueurs aux équipes
        equipeRouge.getMembers().add(joueur1);
        equipeBleu.getMembers().add(joueur2);
        
        // Liste des joueurs
        List<Player> joueurs = Arrays.asList(joueur1, joueur2);
        
        // Création du modèle de jeu
        ModeleJeu modele = new ModeleJeu(nbLignes, nbColonnes, joueurs);
        modele.demarrer();
        
        System.out.println("📊 Configuration du jeu :");
        System.out.println("   Plateau : " + nbLignes + "×" + nbColonnes);
        System.out.println("   " + joueur1.getColor().paint("█") + " " + joueur1.getName() + " (" + equipeRouge.getName() + ") départ: " + joueur1.getPosition());
        System.out.println("   " + joueur2.getColor().paint("█") + " " + joueur2.getName() + " (" + equipeBleu.getName() + ") départ: " + joueur2.getPosition());
        System.out.println();
        
        // Création des heuristiques
        Heuristic heuristiqueSimple = new FreeSpaceHeuristic();
        Heuristic heuristiqueAvancee = new AdvancedHeuristic();
        
        // Création des stratégies
        Strategie strategieMinMax1 = new MinMaxStrategie(heuristiqueSimple, 4);
        Strategie strategieMinMax2 = new MinMaxStrategie(heuristiqueSimple, 4);
        
        System.out.println("🤖 Stratégies :");
        System.out.println("   " + joueur1.getName() + " : " + strategieMinMax1.getNom() + " (Advanced)");
        System.out.println("   " + joueur2.getName() + " : " + strategieMinMax2.getNom() + " (Simple)");
        System.out.println("\n" + "═".repeat(60) + "\n");
        
        // Affichage initial
        System.out.println("🎮 ÉTAT INITIAL :\n");
        afficherPlateauColore(modele.getPlateau(), joueurs);
        
        pause(2000);
        
        // Jouer la partie avec affichage
        jouerPartieAvecAffichage(modele, joueur1, joueur2, strategieMinMax1, strategieMinMax2, 100);
    }
    
    /**
     * Joue une partie complète avec affichage graphique
     */
    private static void jouerPartieAvecAffichage(ModeleJeu modele, Player joueur2, Player joueur1,
                                                   Strategie strat1, Strategie strat2, int maxTours) {
        
        int tour = 0;
        
        while (!modele.estTermine() && tour < maxTours) {
            
            tour++;
            
            // Calcul des mouvements
            Direction dir1 = null;
            Direction dir2 = null;
            
            long debut1 = 0, temps1 = 0;
            long debut2 = 0, temps2 = 0;
            
            if (joueur1.isAlive()) {
                debut1 = System.currentTimeMillis();
                dir1 = strat1.calculerMouvement(joueur1, modele.getPlateau());
                temps1 = System.currentTimeMillis() - debut1;
            } else {
                dir1 = Direction.HAUT;
            }
            
            if (joueur2.isAlive()) {
                debut2 = System.currentTimeMillis();
                dir2 = strat2.calculerMouvement(joueur2, modele.getPlateau());
                temps2 = System.currentTimeMillis() - debut2;
            } else {
                dir2 = Direction.HAUT;
            }
            
            // Affichage des informations du tour
            clearScreen();
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║          TOUR " + String.format("%3d", tour) + "                          ║");
            System.out.println("╚════════════════════════════════════════════╝\n");
            
            if (joueur1.isAlive()) {
                System.out.println(joueur1.getColor().paint("█") + " " + joueur1.getName() + 
                                 " : " + formatDirection(dir1) + 
                                 " (calculé en " + temps1 + "ms)");
            }
            
            if (joueur2.isAlive()) {
                System.out.println(joueur2.getColor().paint("█") + " " + joueur2.getName() + 
                                 " : " + formatDirection(dir2) + 
                                 " (calculé en " + temps2 + "ms)");
            }
            
            System.out.println();
            
            // Exécution du tour
            List<Direction> coups = Arrays.asList(dir1, dir2);
            modele.tourSuivant(coups);
            
            // Affichage du plateau
            afficherPlateauColore(modele.getPlateau(), modele.getJoueurs());
            
            // Statistiques
            afficherStatistiques(modele.getPlateau(), joueur1, joueur2);
            
            // Pause pour voir l'évolution (ajustez selon vos besoins)
            pause(500);
        }
        
        // Résultats finaux
        afficherResultatsFinaux(modele, tour);
    }
    
    /**
     * Affiche le plateau avec couleurs
     */
    private static void afficherPlateauColore(Plateau plateau, List<Player> joueurs) {
        int lignes = plateau.getNbLignes();
        int colonnes = plateau.getNbColonnes();
        
        // En-tête avec numéros de colonnes
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
        System.out.println("─┐");
        
        // Contenu du plateau
        for (int i = 0; i < lignes; i++) {
            System.out.print(String.format("%2d │", i));
            
            for (int j = 0; j < colonnes; j++) {
                Position pos = new Position(i, j);
                Cellule cellule = plateau.getCellule(pos);
                
                // Vérifier si c'est la tête d'un joueur
                boolean isJoueurHead = false;
                for (Player p : joueurs) {
                    if (p.isAlive() && p.getPosition().equals(pos)) {
                        System.out.print(p.getColor().paintHead("●●"));
                        isJoueurHead = true;
                        break;
                    }
                }
                
                if (!isJoueurHead) {
                    if (cellule.isEmpty()) {
                        System.out.print(" ·");
                    } else {
                        Player owner = cellule.getOwner();
                        System.out.print(owner.getColor().paint("██"));
                    }
                }
                
                if (j < colonnes - 1) System.out.print("│");
            }
            
            System.out.println("│");
            
            // Séparateur entre lignes
            if (i < lignes - 1) {
                System.out.print("   ├");
                for (int j = 0; j < colonnes; j++) {
                    System.out.print("──");
                    if (j < colonnes - 1) System.out.print("┼");
                }
                System.out.println("─┤");
            }
        }
        
        // Bordure inférieure
        System.out.print("   └");
        for (int j = 0; j < colonnes; j++) {
            System.out.print("──");
            if (j < colonnes - 1) System.out.print("┴");
        }
        System.out.println("─┘\n");
    }
    
    /**
     * Affiche les statistiques du jeu
     */
    private static void afficherStatistiques(Plateau plateau, Player joueur1, Player joueur2) {
        FreeSpaceHeuristic heuristic = new FreeSpaceHeuristic();
        
        double espace1 = joueur1.isAlive() ? heuristic.evaluate(plateau, joueur1) : 0;
        double espace2 = joueur2.isAlive() ? heuristic.evaluate(plateau, joueur2) : 0;
        
        System.out.println("📈 STATISTIQUES :");
        System.out.println("   " + joueur1.getColor().paint("█") + " " + joueur1.getName() + 
                         " : " + (int)espace1 + " cases libres | " + 
                         (joueur1.isAlive() ? "✓ VIVANT" : "✗ MORT"));
        System.out.println("   " + joueur2.getColor().paint("█") + " " + joueur2.getName() + 
                         " : " + (int)espace2 + " cases libres | " + 
                         (joueur2.isAlive() ? "✓ VIVANT" : "✗ MORT"));
        System.out.println();
    }
    
    /**
     * Affiche les résultats finaux
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
    
    /**
     * Formate une direction avec une flèche
     */
    private static String formatDirection(Direction dir) {
        switch (dir) {
            case HAUT: return "↑ HAUT";
            case BAS: return "↓ BAS";
            case GAUCHE: return "← GAUCHE";
            case DROITE: return "→ DROITE";
            default: return dir.toString();
        }
    }
    
    /**
     * Efface l'écran (compatible multi-plateformes)
     */
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
    
    /**
     * Met en pause l'exécution
     */
    private static void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}