package view;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;


import javax.swing.border.*;

public class GameBoardPanel extends JPanel {

    public GameBoardPanel(){

        this.setBackground(Color.BLACK);
        setVisible(true);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //g.setColor(new Color(245, 245, 245));
        g.fillRect(0, 0, getWidth(), getHeight());

    }
}
