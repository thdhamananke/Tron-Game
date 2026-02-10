package model;

/**
 * Thread pour calculer le coup d'un joueur sans bloquer le jeu.
 */
public class PlayerThread extends Thread {

    private final Player player;
    private final Plateau plateau;
    private Direction direction = Direction.NONE;

    public PlayerThread(Player player, Plateau plateau) {
        this.player = player;
        this.plateau = plateau;
    }

    @Override
    public void run() {
        if (!player.isAlive()) {
            direction = Direction.NONE;
            return;
        }

        try {
            Direction d = player.getStrategie()
                                .calculerMouvement(player, plateau);
            if (d != null) {
                direction = d;
            }
        } catch (Exception e) {
            e.printStackTrace();
            direction = Direction.NONE;
        }
    }

    public Direction getChosenDirection() {
        return direction;
    }
}
