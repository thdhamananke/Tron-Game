import view.GUI;
import view.VueJeu;

import javax.swing.*;

public class Main {

    public static void main(String[] args)
    {
       // VueJeu gui = new GUI();
        SwingUtilities.invokeLater(() -> {
            new GUI();
        });

    }

}
