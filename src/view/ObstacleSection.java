package view;

import javax.swing.*;
import java.awt.*;

/**
 * Section de l'interface permettant de gérer le dessin des obstacles.
 */
public class ObstacleSection extends JPanel {

    private GameBoardPanel board;
    private JToggleButton drawButton;

    public ObstacleSection(GameBoardPanel board) {
        this.board = board;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "🧱 GESTION DES OBSTACLES"
        ));
        setBackground(new Color(245, 245, 245));

        // Bouton d'activation
        drawButton = new JToggleButton("✏️ Activer le Dessin");
        drawButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        drawButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        drawButton.setMaximumSize(new Dimension(180, 40));

        // Message explicatif (Le message que vous avez demandé)
        JLabel instructionLabel = new JLabel("<html><body style='width: 150px; text-align: center;'>"
            + "<br><font color='#666666'><i>Cliquez ou maintenez le clic pour dessiner des murs gris infranchissables.</i></font></body></html>");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));

        // Action du bouton
        drawButton.addActionListener(e -> {
            boolean active = drawButton.isSelected();
            // On informe le board qu'on passe en mode dessin
            // Note: Assurez-vous d'avoir setObstacleButton(int) ou setDrawingMode dans board
            board.setObstacleButton(active ? 1 : 0); 
            
            if (active) {
                drawButton.setText("🛑 Arrêter le Dessin");
                drawButton.setBackground(new Color(255, 200, 200)); // Rouge clair
            } else {
                drawButton.setText("✏️ Activer le Dessin");
                drawButton.setBackground(null);
            }
        });


        // Ajout des composants avec des espaces
        add(Box.createVerticalStrut(10));
        add(drawButton);
        add(Box.createVerticalStrut(5));
        add(instructionLabel);
        add(Box.createVerticalStrut(15));
        add(Box.createVerticalStrut(10));
    }
}