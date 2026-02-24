package view;

import javax.swing.*;
import java.awt.*;

public class TopPanel extends JPanel 
{

    private JLabel tourLabel;
    private JLabel statutLabel;
    private JLabel gagnantLabel;

    public TopPanel() {

        setLayout(new GridLayout(1,3,10,10));

        tourLabel = new JLabel("0", SwingConstants.CENTER);
        statutLabel = new JLabel("Prêt", SwingConstants.CENTER);
        gagnantLabel = new JLabel("-", SwingConstants.CENTER);

        add(createBox("TOUR", tourLabel));
        add(createBox("STATUT", statutLabel));
        add(createBox("GAGNANT", gagnantLabel));
    }

    private JPanel createBox(String title, JLabel label) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public void update(int tour, String statut, String gagnant) {
        tourLabel.setText(String.valueOf(tour));
        statutLabel.setText(statut);
        gagnantLabel.setText(gagnant);
    }
}
