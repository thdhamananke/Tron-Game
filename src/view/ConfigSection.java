package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class ConfigSection extends JPanel {

    public ConfigSection(GameController controller, GameBoardPanel board) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Configuration"));

        JSpinner rowsSpinner = new JSpinner(
                new SpinnerNumberModel(30,15,100,1)
        );

        JSpinner colsSpinner = new JSpinner(
                new SpinnerNumberModel(30,15,100,1)
        );

        JButton applyButton = new JButton("Appliquer taille");

        applyButton.addActionListener(e -> {

            int rows = (int) rowsSpinner.getValue();
            int cols = (int) colsSpinner.getValue();

            controller.changeGridSize(rows, cols);
            board.setGridSize(rows, cols);
        });

        add(new JLabel("Lignes"));
        add(rowsSpinner);
        add(new JLabel("Colonnes"));
        add(colsSpinner);
        add(Box.createVerticalStrut(10));
        add(applyButton);
    }
}
