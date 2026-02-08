# 1. Le contexte général du projet

Ce projet se situe à l’intersection de :
l’intelligence artificielle pour les jeux,
la théorie des jeux multi-joueurs,
et l’étude des coalitions (équipes, alliances implicites).

👉 L’idée centrale est d’étudier comment des joueurs contrôlés par des IA se comportent quand ils sont en équipe, et comment la profondeur de raisonnement influence leurs performances, selon :

la taille des équipes,
la taille de la grille,
la profondeur de recherche de l’algorithme.


# 2. Le jeu de Tron :
Le jeu de Tron est une version multi-joueurs du jeu du serpent :


## Règles principales :
Le jeu se déroule sur une grille 2D de taille fixe ou dynamique.
Chaque joueur contrôle un point.

### À chaque tour, un joueur se déplace :
en haut, bas, gauche ou droite.
À chaque déplacement, il laisse un mur derrière lui.
Ces murs sont infranchissables.

### Conditions de défaite
Un joueur perd s’il :
percute un mur (le sien ou celui d’un autre),
percute un autre joueur,
sort de la grille.

👉 Objectif : c'est d'être le dernier joueur (ou la dernière équipe) en vie.

# 3. L’approche classique : Minmax

Dans les versions simples du jeu :
On utilise un algorithme Minmax (ou Alpha-Beta).

Chaque joueur :
anticipe les coups adverses,
maximise sa chance de survie.
Heuristique classique

L’évaluation d’un état du jeu repose souvent sur :
la proportion de terrain contrôlé,
la liberté de mouvement,
l’accessibilité de zones futures.


⚠️ Problème :
Minmax est surtout adapté aux jeux à 2 joueurs, pas aux jeux multi-joueurs en équipes.

# 4. Le vrai sujet du projet : les coalitions

Ici, on ne veut pas seulement jouer à Tron :
👉 on veut analyser des affrontements entre équipes.

Exemples :

Équipe A : 2 joueurs peu profonds
Équipe B : 1 joueur très intelligent
Ou encore : 3 vs 3 sur une grande grille


Les questions clés :
Une grande équipe est-elle avantagée ?
Une équipe avec une plus grande profondeur de recherche gagne-t-elle toujours ?
À partir de quelle taille de grille la profondeur devient cruciale ?


# 5. L’algorithme SOS (Socially Oriented Search)
Qu’est-ce que SOS ?
SOS est une extension multi-joueur du Minmax, adaptée :
aux équipes,
aux comportements coopératifs ou méfiants.

Il généralise plusieurs comportements connus.
a) MAXN
Chaque joueur maximise son propre score.
Il ne suppose ni alliance, ni complot.

Tous les joueurs sont « égoïstes ».

👉 SOS peut reproduire MAXN quand chaque joueur optimise uniquement son heuristique.

b) Paranoid
Chaque joueur considère que tous les autres sont contre lui.
Même ses alliés potentiels sont vus comme des ennemis.

👉 SOS peut reproduire Paranoid en considérant une coalition adverse unique.

c) SOS : le juste milieu
SOS permet :
de définir des équipes,
de partager (ou non) les gains au sein d’une équipe,
de moduler le comportement :
    coopération forte,
    coopération faible,
    méfiance.

👉 C’est l’outil central du projet.


# 6. Ce qui est à implémenter :
Le jeu de Tron

1- Simulation complète du jeu (sans joueur humain)
Gestion :
de la grille,
des murs,
des collisions,
de plusieurs joueurs.


2- L’algorithme SOS

Adaptation de Minmax au multi-joueurs en équipes
Gestion :
de plusieurs fonctions d’évaluation,
du partage de l’utilité entre alliés.

3- Une heuristique pertinente

Par exemple :
surface accessible (flood fill),
nombre de coups possibles,
séparation du plateau,
survie à long terme.

👉 Elle doit être rapide et discriminante.

4- Un élagage superficiel
Objectif : réduire le temps de calcul.
Inspiré de :
coupes dans MAXN,
suppression des branches peu prometteuses.

⚠️ Ce n’est pas un alpha-beta classique, mais une optimisation pragmatique.

# 7. La question finale

Une fois tout implémenté, on dois répondre à :

Quelle influence la profondeur de recherche a-t-elle sur le jeu en fonction de la taille des équipes et de la taille de la grille ?

Cela implique :
des expérimentations automatiques,
des comparaisons statistiques,
des graphiques / tableaux de résultats.

# 8. Un petit resumé
Ce projet consiste à implémenter un jeu de Tron multi-joueurs, un algorithme de recherche orienté socialement (SOS), puis à analyser expérimentalement l’impact de la profondeur de raisonnement sur les performances d’équipes de tailles différentes, selon la taille du plateau.








# ================= MODELISATION / ARCHITECTURE ======================

src/
 ├── model/          ==>  logique du jeu (aucune UI)
 ├── ai/             ==>  décisions des joueurs (SOS, heuristiques)
 ├── controller/     ==>  orchestre le jeu
 ├── view/           ==>  interface graphique
 ├── experiment/     ==>  lancement massif de parties

# model
GameState {
    Grid grid
    List<Player>
    int turn
}

Grid {
    width;
    height;
    Cellule[][] cells;
}

enum CellType {
    EMPTY, WALL, PLAYER
}

class Cellule {
    CellType type;
    int playerId;
}


Position {
    row, col
}

Move {
    Direction direction
    String toString()
}

enum Direction {
    UP, DOWN, LEFT, RIGHT
}

Team {
    int id;
    List<Player> members;
}


Player {
    int id;
    String name;
    Position position;
    boolean alive;
    int teamId;
    PlayerStrategy strategy;
}


# ia (strategie)
interface PlayerStrategy {
    Move chooseMove(GameState state, Player self);
    String getName();
}

## ils implement l'interface
RandomStrategy {

}

MinimaxStrategy {
    int depth;
    boolean useAlphaBeta; // true = alpha-beta, false = classique
    Heuristic heuristic;
}

SOSStrategy {
    SOSMode mode; // MAXN ou PARANOID
    int depth;
    Heuristic heuristic;
}

enum SOSMode {
    PARANOID,
    MAXN
}



Heuristic <<interface>> (
    evaluate()   
)

L'Interface
Toute heuristique doit implémenter evaluate()
Sert de base pour toutes les stratégies (Random, SOS, Minimax…).



## ils implement l'heuristique
# AdvancedHeuristic
Heuristique avancée
Combine plusieurs critères :
Surface libre personnelle
Surface libre des autres joueurs (pour 3 joueurs ou plus)
Distance aux adversaires (pour enfermer ou éviter collisions)
Plus stratégique, permet d’observer des comportements plus intelligents de l’IA.

# FreeSpaceHeuristic 
Heuristique de base : compte simplement les cases accessibles pour le joueur.
Simple, rapide, fonctionne pour 2 joueurs ou plusieurs.



# controller 
GameController
GameRunner

# GameController :
Gère la logique d’une partie / d’un tour
Vérifie collisions, applique les Move, met à jour le GameState
Ne lance pas la partie complète, juste le fonctionnement interne d’un tour

# GameRunner :
Lancer une partie complète jusqu’à la fin ou nombre max de tours
Appelle GameController.playTurn() en boucle
Retourne GameResult pour analyses ou statistiques


# experiment
ExperimentConfig (
    gridSize, 
    teamSizes, 
    depth(profondeur), 
    nbGames...
);

ExperimentRunner (
    run()
);

ExperimentResult (
    winRate(taux de victoire)
    avgTime(temps moyenne)
    ....
)


CSVExporter {

}

ExperimentConfig :	Paramètres pour tests automatisés
ExperimentRunner :	Boucle sur toutes les configurations, lance les parties
ExperimentResult :	Stocke métriques des parties (victoire, tours, temps)
Statistics / CSVExporter :	Analyse et export des résultats

# view
GameView
GameGUI
