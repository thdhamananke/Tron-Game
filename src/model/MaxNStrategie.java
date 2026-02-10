package model;

import java.util.*;
import java.util.concurrent.*;

/**
 * Stratégie IA basée sur MaxN avec parallélisme à la racine.
 *
 * Chaque coup possible du joueur courant est évalué en parallèle.
 * Le parallélisme est limité au premier niveau de l'arbre == root parallelism,
 * ce qui permet d'accélérer fortement la recherche sans complexifier l'algorithme.
*/
public class MaxNStrategie extends AbstractStrategie {

    private long startTime;
    private static final long TIME_LIMIT_MS = 500;

    // ordre fixe des joueurs
    private List<Player> players;

    public MaxNStrategie(Heuristic heuristic, int depth) {
        super(heuristic, depth);
    }

    @Override
    public String getName() {
        return "Stratégie MaxN (Root Parallelism)";
    }

    /**
     * Calcule le meilleur mouvement pour le joueur courant en utilisant l'algorithme MaxN.
     *
     * @param me le joueur contrôlé par cette stratégie
     * @param plateau l'état actuel du jeu
     * @return la direction choisie.
    */
    @Override
    public Direction calculerMouvement(Player me, Plateau plateau) {

        startTime = System.currentTimeMillis();
        players = getAlivePlayers(plateau);
        int index = players.indexOf(me);

        List<Direction> coups = plateau.getCoupsPossibles(me.getPosition());
        if (coups.isEmpty()) return Direction.HAUT;
        Collections.shuffle(coups);
        
        // Pool de threads = nombre de coeurs CPU
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Result>> futures = new ArrayList<>();

        for (Direction dir : coups) {

            Callable<Result> task = () -> {

                Plateau grid = plateau.copierPlateau();
                Position oldPos = me.getPosition();
                Position newPos = oldPos.move(dir);

                grid.placerMur(oldPos, me);
                Player meVirtuel = new Player(me.getName(), me.getTeam(), newPos);
                grid.placerJoueur(newPos, meVirtuel);

                List<Player> nextPlayers = replacePlayer(players, me, meVirtuel);
                double[] values = maxN(grid, nextPlayers, depth - 1, (index + 1) % players.size());

                return new Result(dir, values[index]);
            };

            futures.add(executor.submit(task));
        }

        Direction bestDirection = coups.get(0);
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Future<Result> future : futures) {
            try {
                Result result = future.get(TIME_LIMIT_MS, TimeUnit.MILLISECONDS);

                if (result.value > bestValue) {
                    bestValue = result.value;
                    bestDirection = result.direction;
                }

            } catch (TimeoutException e) {
                break; // temps dépassé → on arrête proprement
            } catch (Exception ignored) {
            }
        }

        executor.shutdownNow();
        return bestDirection;
    }

    /**
     * Algorithme MaxN récursif pour un jeu multi-joueurs. Chaque état du jeu est évalué par 
     * un vecteur de valeurs, une par joueur. Le joueur courant choisit le coup qui maximise 
     * sa propre composante dans ce vecteur.
     *
     * @param plateau état courant du jeu
     * @param players liste ordonnée des joueurs encore en vie
     * @param depth profondeur restante de recherche
     * @param currentIndex index du joueur courant dans la liste
     * @return vecteur des valeurs pour tous les joueurs
    */
    private double[] maxN(Plateau plateau, List<Player> players, int depth, int currentIndex) {

        if (depth == 0 || timeExceeded()) {
            return evaluateAll(plateau, players);
        }

        Player current = players.get(currentIndex);
        if (!current.isAlive()) {
            return maxN(plateau, players, depth, (currentIndex + 1) % players.size());
        }

        List<Direction> coups = plateau.getCoupsPossibles(current.getPosition());
        Collections.shuffle(coups);

        if (coups.isEmpty()) {
            return evaluateAll(plateau, players);
        }

        double[] bestVector = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Direction dir : coups) {

            Plateau grid = plateau.copierPlateau();
            Position oldPos = current.getPosition();
            Position newPos = oldPos.move(dir);

            grid.placerMur(oldPos, current);
            Player virtuel = new Player(current.getName(), current.getTeam(), newPos);
            grid.placerJoueur(newPos, virtuel);

            List<Player> nextPlayers = replacePlayer(players, current, virtuel);
            double[] values = maxN(grid, nextPlayers, depth - 1, (currentIndex + 1) % players.size());

            if (values[currentIndex] > bestValue) {
                bestValue = values[currentIndex];
                bestVector = values;
            }
        }

        return bestVector;
    }

    /**
     * Évalue l'état courant du plateau pour tous les joueurs.
     * @param plateau état du jeu à évaluer
     * @param players liste des joueurs
     * @return vecteur contenant la valeur heuristique de chaque joueur
    */
    private double[] evaluateAll(Plateau plateau, List<Player> players) {
        double[] values = new double[players.size()];
        for (int i = 0; i < players.size(); i++) {
            values[i] = heuristic.evaluate(plateau, players.get(i));
        }
        return values;
    }

    /**
     * Vérifie si la limite de temps de calcul est dépassée.
     * @return true si le temps est dépassé, false sinon
    */
    private boolean timeExceeded() {
        return System.currentTimeMillis() - startTime > TIME_LIMIT_MS;
    }

    /**
     * Récupère la liste des joueurs encore en vie sur le plateau.
     * @param plateau plateau de jeu
     * @return liste des joueurs vivants sans doublons
    */
    private List<Player> getAlivePlayers(Plateau plateau) {
        List<Player> res = new ArrayList<>();
        for (int i = 0; i < plateau.getNbLignes(); i++) {
            for (int j = 0; j < plateau.getNbColonnes(); j++) {
                Player p = plateau.getCellule(new Position(i, j)).getOwner();
                if (p != null && p.isAlive() && !res.contains(p)) {
                    res.add(p);
                }
            }
        }
        return res;
    }

    /**
     * Remplace un joueur par sa version virtuelle dans une copie de la liste.
     * @param list liste originale des joueurs
     * @param oldP joueur à remplacer
     * @param newP joueur virtuel
     * @return nouvelle liste avec le joueur remplacé
    */
    private List<Player> replacePlayer(List<Player> list, Player oldP, Player newP) {
        List<Player> copy = new ArrayList<>(list);
        int idx = copy.indexOf(oldP);
        if (idx >= 0) copy.set(idx, newP);
        return copy;
    }

    /** 
     * Structure interne pour stocker un résultat de thread, 
     * calcul parallèle à la racine de l'arbre.
    */
    private static class Result {
        Direction direction;
        double value;

        Result(Direction direction, double value) {
            this.direction = direction;
            this.value = value;
        }
    }
}
