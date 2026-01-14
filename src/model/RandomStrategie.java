package model;
import java.util.Random;
import java.util.random.*;

public class RandomStrategie  implements  Strategie{
    Random random = new Random();

    @Override
    public Direction calculerMouvement(Player player, Plateau plateau) {
        int val = random.nextInt(4);
        return Direction.values()[val];
       
    }

    @Override
    public String getNom() {
        return " Stratégie Aléatoire";
    }
    
}
/*
function minimax(node, depth, maximizingPlayer) is
    if depth = 0 or node is a terminal node then
        return the heuristic value of node
    if maximizingPlayer then
        value := −∞
        for each child of node do
            value := max(value, minimax(child, depth − 1, FALSE))
    else (* minimizing player *)
        value := +∞
        for each child of node do
            value := min(value, minimax(child, depth − 1, TRUE))
    return value */


    /*#define PROFMAX 5 // Marche pour tous les niveaux
#define INIFNI MAXINT
#define odd(a) ((a)&1)
int MiniMax(char *Position,int profondeur)
{
int valeur,Best,i,N;
char *PositionSuivante[100];
if (profondeur==PROFMAX)
return Evaluation(Position);
N=TrouveCoupsPossibles(Position,PositionSuivante);
Best=-INFINI;
for (i=0; i<N; i++)
{
valeur=MiniMax(PositionSuivante[i],profondeur+1)
if (odd(profondeur)) // niveaux impairs, on minimise
{
if (valeur<Best)
Best=valeur;
}
else if (valeur>Best) // niveaux pairs, on maximise
Best=valeur;
}
return Best;
} */