package controller;

import model.*;
import view.GUI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameThread extends GameController {

    private final ModeleJeu modele;

    public GameThread(ModeleJeu modele, GUI vue) {
        super(modele, vue);
        this.modele = modele;
    }

    @Override
    public void run() {

        modele.demarrer();

        while (modele.isJeuEnCours()) {

    List<Player> tousLesJoueurs = modele.getJoueurs();
    List<PlayerThread> threads = new ArrayList<>();
    Map<Player, PlayerThread> map = new HashMap<>();

    // thread pour CHAQUE joueur
    for (Player joueur : tousLesJoueurs) {
        PlayerThread pt = new PlayerThread(joueur, modele.getPlateau());
        threads.add(pt);
        map.put(joueur, pt);
        pt.start();
    }

    // attendre les res
    for (PlayerThread pt : threads) {
        try {
            pt.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    
    List<Direction> coups = new ArrayList<>();

    for (Player joueur : tousLesJoueurs) {
        PlayerThread pt = map.get(joueur);
        Direction d = pt.getChosenDirection();
        if (d == null) d = Direction.NONE;
        coups.add(d);
    }

    modele.tourSuivant(coups);

    javax.swing.SwingUtilities.invokeLater(() ->
            getVue().mettreAjourAffichage()
    );
}


        System.out.println(
                "Partie terminée. Gagnant : " + modele.getEquipeGagnante()
        );

        javax.swing.SwingUtilities.invokeLater(() ->
                getVue().mettreAjourAffichage()
        );
    }
}
