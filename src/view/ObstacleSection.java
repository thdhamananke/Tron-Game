/****package view;

import javax.swing.*;
import java.awt.*;

public class ObstacleSection extends JPanel {

    private boolean obstacleMode = false;

    public ObstacleSection(GameBoardPanel board) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Obstacles"));

        JToggleButton toggleButton = new JToggleButton("Mode Placement");
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleButton.setMaximumSize(new Dimension(200, 30));

        toggleButton.addActionListener(e -> {

            obstacleMode = toggleButton.isSelected();

            if (obstacleMode) {
                toggleButton.setText("Mode Actif ✓");
                toggleButton.setBackground(new java.awt.Color(144, 238, 144));
            } else {
                toggleButton.setText("Mode Placement");
                toggleButton.setBackground(null);
            }
        });

        JButton clearButton = new JButton("Effacer obstacles");
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton.setMaximumSize(new Dimension(200, 30));

        clearButton.addActionListener(e -> {
            board.clearObstacles();
            board.repaint();
        });

        add(Box.createVerticalStrut(5));
        add(toggleButton);
        add(Box.createVerticalStrut(10));
        add(clearButton);
        add(Box.createVerticalStrut(10));
    }

 


    public boolean getobstacleMode(){
        return this.obstacleMode;
    }
}
**/