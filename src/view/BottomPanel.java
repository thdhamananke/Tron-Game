package view;

import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JPanel {

    public BottomPanel() {

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(createLegend(java.awt.Color.RED, "Bot Rouge"));
        add(createLegend(java.awt.Color.BLUE, "Bot Bleu"));
        add(createLegend(java.awt.Color.GRAY, "Mur"));
        add(createLegend(java.awt.Color.WHITE, "Libre"));
    }

    private JPanel createLegend(java.awt.Color color, String text) {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel box = new JLabel("   ");
        box.setOpaque(true);
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK));

        p.add(box);
        p.add(new JLabel(text));

        return p;
    }
}
