package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class SpeedSection extends JPanel {

    public SpeedSection(GameController controller) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Vitesse"));

        JSlider slider = new JSlider(100,2000,500);
        slider.setMajorTickSpacing(500);
        slider.setMinorTickSpacing(100);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setInverted(true);

        slider.addChangeListener(e ->
                controller.setDelay(slider.getValue())
        );

        add(new JLabel("Délai (ms)"));
        add(slider);
    }
}
