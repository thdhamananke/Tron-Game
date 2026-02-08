README – Module Stratégies (AI)

Aperçu
------

Le module 'model.strategy' contient toutes les stratégies d’intelligence artificielle utilisées pour le jeu Tron.
Chaque stratégie implémente l’interface 'Strategie', garantissant la méthode principale :

    Direction calculerMouvement(Player player, Plateau plateau);

Les stratégies utilisent une heuristique pour évaluer l’état du plateau et choisir le meilleur coup.

Architecture
------------

1. Interface 'Strategie'
   - Contrat commun à toutes les IA.
   - Méthodes :
       * Direction calculerMouvement(Player player, Plateau plateau) : retourne le meilleur coup à jouer.
       * String getNom() : nom de la stratégie.

2. Classe abstraite 'AbstractStrategie'
   - Contient le code commun aux stratégies :
       * heuristic pour évaluer le plateau
       * méthodes Do / Undo : applyMove et undoMove
   - Réduit le code dupliqué dans toutes les stratégies.

3. Stratégies implémentées

   Stratégie                 | Description
   ---------------------------|---------------------------------------------------------------
   MinMaxStrategie            | Algorithme Minimax classique, profondeur fixe, sans Alpha-Beta.
   MinMaxStrategy             | Minimax avec élagage Alpha-Beta pour optimiser la recherche.
   ParanoidStrategie          | Minimax pour plusieurs joueurs : le joueur courant maximise, tous les adversaires minimisent (coalition).

Chaque stratégie simule les coups possibles sur une copie du plateau, en appliquant et annulant les mouvements.

Fonctionnement général
---------------------

1. La stratégie reçoit :
   - le joueur courant
   - le plateau actuel

2. Pour chaque coup possible :
   - Simule le mouvement (applyMove)
   - Évalue la position (heuristique ou Minimax récursif)
   - Annule le mouvement (undoMove) pour tester les autres coups

3. Retourne la direction optimale selon la stratégie choisie.

Notes sur l’implémentation
--------------------------

- Do / Undo :
    - Permet de simuler un coup sans modifier le plateau réel.
    - MoveBackup sauvegarde l’ancienne position du joueur (et le mur créé si nécessaire).

- Heuristique :
    - Chaque stratégie utilise un objet Heuristic pour évaluer les positions.
    - L’heuristique doit être cohérente avec la stratégie (ex : contrôle des zones, distance aux adversaires…).

- Profondeur maximale :
    - MinMaxStrategie : DEPTH_MAX = 10
    - MinMaxStrategy : profondeur configurable via le constructeur
    - ParanoidStrategie : DEPTH_MAX = 5

Extension
---------

