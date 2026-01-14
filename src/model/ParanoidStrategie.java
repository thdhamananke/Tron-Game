package model;

public class ParanoidStrategie implements Strategie {

    @Override
    public Direction calculerMouvement(Player player, Plateau plateau) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculerMouvement'");
    }

    @Override
    public String getNom() {
        return "Stratégie paranoid ";
    }
    
}
