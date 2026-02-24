package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {

    private ControlSection controlSection;
    private StrategySection strategySection;
    private ConfigSection configSection;
    private ObstacleSection obstacleSection;
    private SpeedSection speedSection;

    public SidePanel(GameController controller,
                     GUI gui,
                     GameBoardPanel board) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 🔥 IMPORTANT : hauteur forcée pour activer le scroll
        setPreferredSize(new Dimension(260, 1000));

        setBackground(new java.awt.Color(245,245,245));

        controlSection = new ControlSection(controller);
        strategySection = new StrategySection(controller, gui);
        configSection = new ConfigSection(controller, board);
        obstacleSection = new ObstacleSection(board);
        speedSection = new SpeedSection(controller);

        add(controlSection);
        add(Box.createVerticalStrut(10));

        add(strategySection);
        add(Box.createVerticalStrut(10));

        add(configSection);
        add(Box.createVerticalStrut(10));

        add(obstacleSection);
        add(Box.createVerticalStrut(10));

        add(speedSection);
        add(Box.createVerticalStrut(20));
    }

    public ControlSection getControlSection() {
        return controlSection;
    }
}
