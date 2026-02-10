package model;

import java.util.*;

/**
 * Heuristique "Tree of Chambers" (Lukas Kang, section 4.3) :
 * 1) Calcule une partition Voronoi (région du joueur / région adverse / battlefront)
 * 2) Si la région du joueur contient des points d'articulation :
 *    - Construire la "chambre" du joueur en n'itérant pas à travers les articulations
 *    - Explorer les chambres adjacentes via articulations
 *    - Ne garder que les chambres adjacentes qui NE contiennent PAS la battlefront
 *    - Score joueur = taille(chambre) + max(taille(chambreAdjacenteValide))
 * 3) Sinon score joueur = taille(région Voronoi)
 * 4) Valeur retournée = score(joueur) - meilleurScore(adversaire)
 *
 * Référence: Kang BSc paper, p.5 (Tree of Chambers). :contentReference[oaicite:3]{index=3}
 */
public class TreeOfChambersHeuristic implements Heuristic {

    @Override
    public String getName() {
        return "Tree of Chambers";
    }

    @Override
    public double evaluate(Plateau grid, Player player) {
        if (player == null || !player.isAlive()) return -1_000_000.0;

        List<Player> opponents = getOpponents(grid, player);
        if (opponents.isEmpty()) {
            // fallback : sans adversaire connu, on renvoie juste l'espace accessible
            return countAccessibleCells(grid, player.getPosition());
        }

        // On compare au "meilleur adversaire" (le plus dangereux)
        double myBest = Double.NEGATIVE_INFINITY;
        double oppBest = Double.NEGATIVE_INFINITY;

        for (Player opp : opponents) {
            if (opp == null || !opp.isAlive()) continue;

            VoronoiResult vr = computeVoronoi2P(grid, player.getPosition(), opp.getPosition());

            int myVal = tocValueForPlayer(grid, vr, /*isP1=*/true);
            int oppVal = tocValueForPlayer(grid, vr, /*isP1=*/false);

            myBest = Math.max(myBest, myVal);
            oppBest = Math.max(oppBest, oppVal);
        }

        if (myBest == Double.NEGATIVE_INFINITY) myBest = 0;
        if (oppBest == Double.NEGATIVE_INFINITY) oppBest = 0;

        return myBest - oppBest;
    }

    /**
     * Idée: retourner tous les joueurs vivants != player (ou juste le "principal adversaire").
     */
    @SuppressWarnings("unchecked")
    private List<Player> getOpponents(Plateau grid, Player player) {
        List<Player> res = new ArrayList<>();
        try {
            // Si tu as Plateau.getPlayers(): List<Player>
            // Remplace si ton projet utilise un autre nom: getJoueurs(), getAllPlayers(), etc.
            List<Player> all = (List<Player>) grid.getClass().getMethod("getPlayers").invoke(grid);
            for (Player p : all) {
                if (p != null && p != player && p.isAlive()) res.add(p);
            }
        } catch (Exception e) {
            // Si pas de getPlayers(), adapte ici manuellement
            // (ex: grid.getJoueurs(), grid.getListeJoueurs(), etc.)
        }
        return res;
    }

    // -------------------- VORONOI 2 JOUEURS --------------------

    private static class VoronoiResult {
        // owner[r][c] : 1 -> P1, 2 -> P2, 0 -> battlefront, -1 -> wall/occupied/unreachable
        int[][] owner;
        boolean[][] battlefront;
        boolean[][] regionP1;
        boolean[][] regionP2;
    }

    /**
     * Voronoi 2 joueurs via BFS multi-source.
     * On calcule distP1 et distP2, puis:
     * - si distP1 < distP2 -> P1
     * - si distP2 < distP1 -> P2
     * - si égal et atteignable -> battlefront (0)
     */
    private VoronoiResult computeVoronoi2P(Plateau grid, Position p1, Position p2) {
        int R = grid.getNbLignes();
        int C = grid.getNbColonnes();

        int INF = 1_000_000;
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
        vr.battlefront = new boolean[R][C];
        vr.regionP1 = new boolean[R][C];
        vr.regionP2 = new boolean[R][C];

        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                Position pos = new Position(r, c);

                // Si c'est un mur/case occupée, on marque inaccessible
                if (!grid.estLibre(pos) && !(r == p1.getRow() && c == p1.getCol()) && !(r == p2.getRow() && c == p2.getCol())) {
                    vr.owner[r][c] = -1;
                    continue;
                }

                int a = d1[r][c];
                int b = d2[r][c];

                if (a == INF && b == INF) {
                    vr.owner[r][c] = -1;
                } else if (a < b) {
                    vr.owner[r][c] = 1;
                    vr.regionP1[r][c] = true;
                } else if (b < a) {
                    vr.owner[r][c] = 2;
                    vr.regionP2[r][c] = true;
                } else {
                    // égal et atteignable -> battlefront
                    vr.owner[r][c] = 0;
                    vr.battlefront[r][c] = true;
                }
            }
        }

        // Assure que la position de départ est dans sa région
        vr.regionP1[p1.getRow()][p1.getCol()] = true;
        vr.regionP2[p2.getRow()][p2.getCol()] = true;
        vr.owner[p1.getRow()][p1.getCol()] = 1;
        vr.owner[p2.getRow()][p2.getCol()] = 2;

        return vr;
    }

    private void bfsDistances(Plateau grid, Position start, int[][] dist) {
        int R = grid.getNbLignes();
        int C = grid.getNbColonnes();

        boolean[][] vis = new boolean[R][C];
        ArrayDeque<Position> q = new ArrayDeque<>();

        dist[start.getRow()][start.getCol()] = 0;
        vis[start.getRow()][start.getCol()] = true;
        q.add(start);

        while (!q.isEmpty()) {
            Position cur = q.poll();
            int cd = dist[cur.getRow()][cur.getCol()];

            for (Direction dir : Direction.values()) {
                Position nxt = cur.move(dir);
                if (!grid.estDansPlateau(nxt)) continue;

                // Autorise la case start même si estLibre() est false (selon ton moteur)
                boolean free = grid.estLibre(nxt) || (nxt.getRow() == start.getRow() && nxt.getCol() == start.getCol());
                if (!free) continue;

                if (!vis[nxt.getRow()][nxt.getCol()]) {
                    vis[nxt.getRow()][nxt.getCol()] = true;
                    dist[nxt.getRow()][nxt.getCol()] = cd + 1;
                    q.add(nxt);
                }
            }
        }
    }

    // -------------------- TREE OF CHAMBERS --------------------

    private int tocValueForPlayer(Plateau grid, VoronoiResult vr, boolean isP1) {
        boolean[][] region = isP1 ? vr.regionP1 : vr.regionP2;

        // 1) Si pas de points d’articulation -> taille région Voronoi
        Set<Integer> regionNodes = cellsToNodeSet(grid, region);
        if (regionNodes.isEmpty()) return 0;

        Set<Integer> articulations = findArticulationPoints(grid, regionNodes);

        if (articulations.isEmpty()) {
            return regionNodes.size();
        }

        // 2) Sinon : chambre depuis la position du joueur (dans owner==1 ou 2)
        Position start = findStartInVoronoi(vr, isP1);
        if (start == null) return regionNodes.size(); // fallback

        // Chambre principale: BFS dans la région en interdisant de TRAVERSER les articulations
        Chamber main = exploreChamber(grid, region, articulations, vr.battlefront, start);

        // 3) Chambres adjacentes via articulations touchées par la chambre principale
        int bestNeighbor = 0;
        for (int artNode : main.borderArticulations) {
            int ar = artNode / grid.getNbColonnes();
            int ac = artNode % grid.getNbColonnes();
            Position artPos = new Position(ar, ac);

            Chamber neigh = exploreChamber(grid, region, articulations, vr.battlefront, artPos);

            // Si la chambre contient la battlefront -> discard (papier)
            if (neigh.touchesBattlefront) continue;

            bestNeighbor = Math.max(bestNeighbor, neigh.size);
        }

        return main.size + bestNeighbor;
    }

    private Position findStartInVoronoi(VoronoiResult vr, boolean isP1) {
        int target = isP1 ? 1 : 2;
        for (int r = 0; r < vr.owner.length; r++) {
            for (int c = 0; c < vr.owner[0].length; c++) {
                // On prend la case "joueur" (normalement unique = position actuelle)
                // Comme on ne l'a pas explicitement, on privilégie les cases marquées owner==target
                // et adjacentes à des murs (souvent la trace).
                if (vr.owner[r][c] == target) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    private static class Chamber {
        int size;
        boolean touchesBattlefront;
        Set<Integer> borderArticulations = new HashSet<>();
    }

    /**
     * Explore une chambre à partir d'un start:
     * - On reste dans "region"
     * - On ne traverse PAS les articulation points (sauf si start est articulation: on l'autorise comme entrée)
     * - On collecte les articulations rencontrées en bordure
     */
    private Chamber exploreChamber(Plateau grid, boolean[][] region, Set<Integer> articulations,
                                   boolean[][] battlefront, Position start) {

        int R = grid.getNbLignes();
        int C = grid.getNbColonnes();

        Chamber ch = new Chamber();

        boolean[][] vis = new boolean[R][C];
        ArrayDeque<Position> q = new ArrayDeque<>();

        int startId = start.getRow() * C + start.getCol();

        // Si start n'est même pas dans la région, chambre vide
        if (!region[start.getRow()][start.getCol()]) return ch;

        vis[start.getRow()][start.getCol()] = true;
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

                int nr = nxt.getRow(), nc = nxt.getCol();
                if (!region[nr][nc]) continue;

                int nid = nr * C + nc;

                // Si c'est une articulation : on ne traverse pas, on la note comme bordure
                // Exception: si la case actuelle est start articulation, on autorise l'entrée uniquement au départ
                if (articulations.contains(nid) && nid != startId) {
                    ch.borderArticulations.add(nid);
                    continue;
                }

                if (!vis[nr][nc]) {
                    vis[nr][nc] = true;
                    q.add(nxt);
                }
            }
        }

        return ch;
    }

    private Set<Integer> cellsToNodeSet(Plateau grid, boolean[][] region) {
        Set<Integer> nodes = new HashSet<>();
        int R = grid.getNbLignes();
        int C = grid.getNbColonnes();
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                if (region[r][c]) nodes.add(r * C + c);
            }
        }
        return nodes;
    }

    /**
     * Points d'articulation dans le graphe 4-voisins induit par "regionNodes".
     * Tarjan DFS (disc/low).
     */
    private Set<Integer> findArticulationPoints(Plateau grid, Set<Integer> regionNodes) {
        int C = grid.getNbColonnes();

        Map<Integer, List<Integer>> adj = buildAdj(grid, regionNodes);

        Map<Integer, Integer> disc = new HashMap<>();
        Map<Integer, Integer> low = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> ap = new HashSet<>();
        int[] time = {0};

        for (int u : regionNodes) {
            if (!disc.containsKey(u)) {
                dfsAP(u, adj, disc, low, parent, ap, time);
            }
        }
        return ap;
    }

    private void dfsAP(int u,
                       Map<Integer, List<Integer>> adj,
                       Map<Integer, Integer> disc,
                       Map<Integer, Integer> low,
                       Map<Integer, Integer> parent,
                       Set<Integer> ap,
                       int[] time) {

        disc.put(u, ++time[0]);
        low.put(u, disc.get(u));

        int children = 0;

        for (int v : adj.getOrDefault(u, Collections.emptyList())) {
            if (!disc.containsKey(v)) {
                children++;
                parent.put(v, u);
                dfsAP(v, adj, disc, low, parent, ap, time);

                low.put(u, Math.min(low.get(u), low.get(v)));

                Integer pu = parent.get(u);

                // u racine
                if (pu == null && children > 1) ap.add(u);

                // u non-racine
                if (pu != null && low.get(v) >= disc.get(u)) ap.add(u);

            } else if (!Objects.equals(parent.get(u), v)) {
                // back edge
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }
    }

    private Map<Integer, List<Integer>> buildAdj(Plateau grid, Set<Integer> nodes) {
        int R = grid.getNbLignes();
        int C = grid.getNbColonnes();

        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (int id : nodes) adj.put(id, new ArrayList<>());

        for (int id : nodes) {
            int r = id / C;
            int c = id % C;
            Position cur = new Position(r, c);

            for (Direction dir : Direction.values()) {
                Position nxt = cur.move(dir);
                if (!grid.estDansPlateau(nxt)) continue;
                int nid = nxt.getRow() * C + nxt.getCol();
                if (nodes.contains(nid)) {
                    adj.get(id).add(nid);
                }
            }
        }
        return adj;
    }

    // -------------------- UTILS --------------------

    private int countAccessibleCells(Plateau grid, Position start) {
        boolean[][] visited = new boolean[grid.getNbLignes()][grid.getNbColonnes()];
        Queue<Position> q = new LinkedList<>();
        visited[start.getRow()][start.getCol()] = true;
        q.add(start);

        int count = 1;
        while (!q.isEmpty()) {
            Position cur = q.poll();
            for (Direction dir : Direction.values()) {
                Position nxt = cur.move(dir);
                if (grid.estDansPlateau(nxt)
                        && !visited[nxt.getRow()][nxt.getCol()]
                        && grid.estLibre(nxt)) {
                    visited[nxt.getRow()][nxt.getCol()] = true;
                    q.add(nxt);
                    count++;
                }
            }
        }
        return count;
    }
}
