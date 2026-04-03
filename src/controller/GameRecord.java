package controller;

import model.Team;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameRecord {

    public final LocalDateTime date;
    public final int nombreTours;
    public final Team gagnant;
    public final String strategieRouge;
    public final String strategieBleu;
    public final int rows;
    public final int cols;

    public GameRecord(int nombreTours, Team gagnant,
                      String stratRouge, String stratBleu,
                      int rows, int cols) {
        this.date = LocalDateTime.now();
        this.nombreTours = nombreTours;
        this.gagnant = gagnant;
        this.strategieRouge = stratRouge;
        this.strategieBleu = stratBleu;
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String gagnantStr =
                (gagnant == null) ? "Match nul" : gagnant.getName();

        return String.format(
                "[%s] %dx%d - %d tours - Gagnant: %s (Rouge:%s vs Bleu:%s)",
                date.format(formatter),
                rows, cols,
                nombreTours,
                gagnantStr,
                strategieRouge,
                strategieBleu
        );
    }
}
