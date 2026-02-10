package model;

import java.util.*;

/**
 * Heuristique "Tree of Chambers" (Lukas Kang).
 *
 * Principe :
 * - Partition Voronoï joueur / adversaire / battlefront
 * - Analyse des points d'articulation dans la région Voronoï
 * - Construction d'une chambre principale et de chambres adjacentes
 * - Le score correspond à l'espace contrôlable maximal
 * Valeur finale score(joueur) - score(meilleur adversaire)
 */
public class TreeOfChambersHeuristic implements Heuristic {
    @Override
    public String getName() {
        return "Tree of Chambers";
    }

    /**
     * Évalue le plateau pour un joueur donné en utilisant l'heuristique "Tree of Chambers".
     *
     * @param grid le plateau de jeu
     * @param player le joueur pour lequel calculer le score
     * @return un score double représentant la valeur stratégique de la position
    */
    @Override
    public double evaluate(Plateau grid, Player player) {
        if (player == null || !player.isAlive()) {
            return -1000000.0;
        }

        List<Player> opponents = getOpponents(grid, player);
        if (opponents.isEmpty()) {
            return countAccessibleCells(grid, player.getPosition());
        }

        int myBest = 0;
        int oppBest = 0;

        for (Player opp : opponents) {
            VoronoiResult vr = computeVoronoi2P(grid, player.getPosition(), opp.getPosition() );

            myBest = Math.max(myBest, tocValueForPlayer(grid, vr, true));
            oppBest = Math.max(oppBest, tocValueForPlayer(grid, vr, false));
        }

        return myBest - oppBest;
    }

    /**
     * Récupère la liste des joueurs adverses encore en vie.
     * @param grid le plateau de jeu
     * @param me le joueur courant
     * @return liste des adversaires vivants
    */
    private List<Player> getOpponents(Plateau grid, Player me) {
        List<Player> res = new ArrayList<>();

        for (int r = 0; r < grid.getNbLignes(); r++) {
            for (int c = 0; c < grid.getNbColonnes(); c++) {
                Position pos = new Position(r, c);
                Player p = grid.getJoueurAt(pos);

                if (p != null && p != me && p.isAlive() && !res.contains(p)) {
                    res.add(p);
                }
            }
        }

        return res;
    }

    /* ===================== VORONOI ===================== */

    /** Résultat d'une partition Voronoï entre deux joueurs */
    private static class VoronoiResult {
        int[][] owner;              // 1 joueur, 2 adversaire, 0 battlefront, -1 bloqué
        boolean[][] regionP1;
        boolean[][] regionP2;
        boolean[][] battlefront;
    }

    /**
     * Calcule la partition Voronoï à deux joueurs.
     * @param grid le plateau de jeu
     * @param p1 position du joueur 1
     * @param p2 position du joueur 2
     * @return un VoronoiResult contenant régions et battlefront
    */
    private VoronoiResult computeVoronoi2P(Plateau grid, Position p1, Position p2) {
        int R = grid.getNbLignes();
        int C = grid.getNbColonnes();
        int INF = 1000000;

        int[][] d1 = new int[R][C];
        int[][] d2 = new int[R][C];

        for (int i = 0; i < R; i++) {
            Arrays.fill(d1[i], INF);
            Arrays.fill(d2[i], INF);
        }

        bfsDistances(grid, p1, d1);
        bfsDistances(grid, p2, d2);

        VoronoiResult vr = new VoronoiResult();
        vr.owner = new int[R][C];
        vr.regionP1 = new boolean[R][C];
        vr.regionP2 = new boolean[R][C];
        vr.battlefront = new boolean[R][C];

        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                Position pos = new Position(r, c);

                if (!grid.estLibre(pos) && !pos.equals(p1) && !pos.equals(p2)) {
                    vr.owner[r][c] = -1;
                    continue;
                }

                int a = d1[r][c];
                int b = d2[r][c];

                if (a < b) {
                    vr.owner[r][c] = 1;
                    vr.regionP1[r][c] = true;
                } else if (b < a) {
                    vr.owner[r][c] = 2;
                    vr.regionP2[r][c] = true;
                } else if (a != INF) {
                    vr.owner[r][c] = 0;
                    vr.battlefront[r][c] = true;
                } else {
                    vr.owner[r][c] = -1;
                }
            }
        }

        vr.regionP1[p1.getRow()][p1.getCol()] = true;
        vr.regionP2[p2.getRow()][p2.getCol()] = true;

        return vr;
    }

    /**
     * Calcule les distances minimales depuis une position vers toutes 
     * les cases accessibles du plateau via BFS.
     *
     * @param grid le plateau du jeu
     * @param start position de départ
     * @param dist tableau des distances à remplir
    */
    private void bfsDistances(Plateau grid, Position start, int[][] dist) {
        int R = grid.getNbLignes();
        int C = grid.getNbColonnes();

        boolean[][] visited = new boolean[R][C];
        ArrayDeque<Position> q = new ArrayDeque<>();

        visited[start.getRow()][start.getCol()] = true;
        dist[start.getRow()][start.getCol()] = 0;
        q.add(start);

        while (!q.isEmpty()) {
            Position cur = q.poll();
            int cd = dist[cur.getRow()][cur.getCol()];

            for (Direction dir : Direction.values()) {
                Position nxt = cur.move(dir);
                if (!grid.estDansPlateau(nxt)) continue;
                if (!grid.estLibre(nxt) && !nxt.equals(start)) continue;

                if (!visited[nxt.getRow()][nxt.getCol()]) {
                    visited[nxt.getRow()][nxt.getCol()] = true;
                    dist[nxt.getRow()][nxt.getCol()] = cd + 1;
                    q.add(nxt);
                }
            }
        }
    }

    /* ===================== TREE OF CHAMBERS ===================== */

    /**
     * Calcule la valeur de la chambre principale + meilleure chambre 
     * adjacente pour un joueur donné.
     * @param grid le plateau
     * @param vr résultat Voronoï
     * @param isP1 true si joueur = P1
     * @return score numérique
    */
    private int tocValueForPlayer(Plateau grid, VoronoiResult vr, boolean isP1) {
        boolean[][] region = isP1 ? vr.regionP1 : vr.regionP2;
        Set<Integer> nodes = cellsToNodeSet(grid, region);
        if (nodes.isEmpty()) return 0;

        Set<Integer> articulations = findArticulationPoints(grid, nodes);
        if (articulations.isEmpty()) {
            return nodes.size();
        }

        Position start = findAnyCell(region);
        if (start == null) return nodes.size();

        Chamber main = exploreChamber(grid, region, articulations, vr.battlefront, start);

        int bestAdj = 0;
        for (int art : main.borderArticulations) {
            Position p = new Position(art / grid.getNbColonnes(), art % grid.getNbColonnes());

            Chamber ch = exploreChamber(grid, region, articulations, vr.battlefront, p);

            if (!ch.touchesBattlefront) {
                bestAdj = Math.max(bestAdj, ch.size);
            }
        }

        return main.size + bestAdj;
    }

    /**
     * Retourne n'importe quelle cellule présente dans la région.
     * @param region matrice booléenne représentant la région
     * @return position d'une cellule, ou null si aucune
    */
    private Position findAnyCell(boolean[][] region) {
        for (int r = 0; r < region.length; r++) {
            for (int c = 0; c < region[0].length; c++) {
                if (region[r][c]) return new Position(r, c);
            }
        }
        return null;
    }

    /** Représente une chambre explorée dans la grille */
    private static class Chamber {
        int size = 0;
        boolean touchesBattlefront = false;
        Set<Integer> borderArticulations = new HashSet<>();
    }

    /**
     * Explore une chambre à partir d'une position initiale.
     * @param grid le plateau
     * @param region matrice de cellules accessibles
     * @param articulations ensemble de points d'articulation
     * @param battlefront matrice des battlefronts
     * @param start position de départ
     * @return une instance Chamber avec taille et borderArticulations
     */
    private Chamber exploreChamber( Plateau grid, boolean[][] region, Set<Integer> 
                                    articulations, boolean[][] battlefront, Position start) {
        int R = grid.getNbLignes();
        int C = grid.getNbColonnes();

        Chamber ch = new Chamber();
        boolean[][] visited = new boolean[R][C];
        ArrayDeque<Position> q = new ArrayDeque<>();

        int startId = start.getRow() * C + start.getCol();
        if (!region[start.getRow()][start.getCol()]) return ch;

        visited[start.getRow()][start.getCol()] = true;
        q.add(start);

        while (!q.isEmpty()) {
            Position cur = q.poll();
            int r = cur.getRow(), c = cur.getCol();
            int id = r * C + c;

            ch.size++;
            if (battlefront[r][c]) ch.touchesBattlefront = true;

            for (Direction dir : Direction.values()) {
                Position nxt = cur.move(dir);
                if (!grid.estDansPlateau(nxt)) continue;
                if (!region[nxt.getRow()][nxt.getCol()]) continue;

                int nid = nxt.getRow() * C + nxt.getCol();
                if (articulations.contains(nid) && nid != startId) {
                    ch.borderArticulations.add(nid);
                    continue;
                }

                if (!visited[nxt.getRow()][nxt.getCol()]) {
                    visited[nxt.getRow()][nxt.getCol()] = true;
                    q.add(nxt);
                }
            }
        }

        return ch;
    }

    /* ===================== ARTICULATIONS ===================== */

    /**
     * Transforme les cellules accessibles en ensemble d'IDs.
     * @param grid plateau
     * @param region région booléenne
     * @return ensemble d'entiers représentant les cellules
    */
    private Set<Integer> cellsToNodeSet(Plateau grid, boolean[][] region) {
        Set<Integer> res = new HashSet<>();
        int C = grid.getNbColonnes();
        for (int r = 0; r < region.length; r++) {
            for (int c = 0; c < region[0].length; c++) {
                if (region[r][c]) res.add(r * C + c);
            }
        }
        return res;
    }

    /**
     * Identifie les points d'articulation dans un graphe 4-voisins.
     * @param grid plateau
     * @param nodes ensemble de cellules
     * @return ensemble de points d'articulation
    */
    private Set<Integer> findArticulationPoints(Plateau grid, Set<Integer> nodes) {
        Map<Integer, List<Integer>> adj = buildAdj(grid, nodes);
        Map<Integer, Integer> disc = new HashMap<>();
        Map<Integer, Integer> low = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> ap = new HashSet<>();
        int[] time = {0};

        for (int u : nodes) {
            if (!disc.containsKey(u)) {
                dfsAP(u, adj, disc, low, parent, ap, time);
            }
        }
        return ap;
    }

    private void dfsAP(int u, Map<Integer, List<Integer>> adj, Map<Integer, Integer> disc, Map<Integer, 
                        Integer> low, Map<Integer, Integer> parent, Set<Integer> ap, int[] time ) {
        disc.put(u, ++time[0]);
        low.put(u, disc.get(u));
        int children = 0;

        for (int v : adj.getOrDefault(u, List.of())) {
            if (!disc.containsKey(v)) {
                children++;
                parent.put(v, u);
                dfsAP(v, adj, disc, low, parent, ap, time);
                low.put(u, Math.min(low.get(u), low.get(v)));

                if (parent.get(u) == null && children > 1) ap.add(u);
                if (parent.get(u) != null && low.get(v) >= disc.get(u)) ap.add(u);
            } else if (v != parent.getOrDefault(u, -1)) {
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }
    }

    /**
     * Construit la liste d'adjacence pour le graphe 4-voisins.
     * @param grid plateau
     * @param nodes ensemble de cellules
     * @return map id -> liste des voisins
    */
    private Map<Integer, List<Integer>> buildAdj(Plateau grid, Set<Integer> nodes ) {
        int C = grid.getNbColonnes();
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (int id : nodes) adj.put(id, new ArrayList<>());

        for (int id : nodes) {
            int r = id / C;
            int c = id % C;
            Position cur = new Position(r, c);

            for (Direction d : Direction.values()) {
                Position nxt = cur.move(d);
                if (!grid.estDansPlateau(nxt)) continue;
                int nid = nxt.getRow() * C + nxt.getCol();
                if (nodes.contains(nid)) {
                    adj.get(id).add(nid);
                }
            }
        }
        return adj;
    }

    /* ===================== UTILS ===================== */

    /**
     * Compte le nombre de cellules accessibles depuis une position donnée (BFS simple).
     * @param grid plateau
     * @param start position de départ
     * @return nombre de cellules accessibles
    */
    private int countAccessibleCells(Plateau grid, Position start) {
        boolean[][] visited = new boolean[grid.getNbLignes()][grid.getNbColonnes()];
        Queue<Position> q = new ArrayDeque<>();

        visited[start.getRow()][start.getCol()] = true;
        q.add(start);

        int count = 1;
        while (!q.isEmpty()) {
            Position cur = q.poll();
            for (Direction dir : Direction.values()) {
                Position nxt = cur.move(dir);
                if (grid.estDansPlateau(nxt)
                        && grid.estLibre(nxt)
                        && !visited[nxt.getRow()][nxt.getCol()]) {
                    visited[nxt.getRow()][nxt.getCol()] = true;
                    q.add(nxt);
                    count++;
                }
            }
        }
        
        return count;
    }
}
