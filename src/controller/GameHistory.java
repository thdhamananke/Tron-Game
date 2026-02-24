package controller;

import model.Team;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Historique des parties jouées
 */
public class GameHistory {
    
    private List<GameRecord> parties;
    private static final int MAX_RECORDS = 100;

    public GameHistory() {
        this.parties = new ArrayList<>();
    }

    public void ajouterPartie(GameRecord record) {
        parties.add(0, record); // Ajouter au début
        
        // Limiter le nombre de parties stockées
        if (parties.size() > MAX_RECORDS) {
            parties.remove(parties.size() - 1);
        }
    }

    public List<GameRecord> getParties() {
        return new ArrayList<>(parties);
    }

    public int getNombreParties() {
        return parties.size();
    }

    public void effacer() {
        parties.clear();
    }

    public String getStatistiques() {
        if (parties.isEmpty()) {
            return "Aucune partie jouée";
        }

        int victoiresRouge = 0;
        int victoiresBleu = 0;
        int matchsNuls = 0;
        int totalTours = 0;

        for (GameRecord record : parties) {
            if (record.gagnant == null) {
                matchsNuls++;
            } else if (record.gagnant.getName().contains("Rouge")) {
                victoiresRouge++;
            } else {
                victoiresBleu++;
            }
            totalTours += record.nombreTours;
        }

        double moyenneTours = (double) totalTours / parties.size();

        return String.format(
            "Statistiques (%d parties):\n" +
            "Rouge: %d victoires (%.1f%%)\n" +
            "Bleu: %d victoires (%.1f%%)\n" +
            "Matchs nuls: %d (%.1f%%)\n" +
            "Moyenne tours: %.1f",
            parties.size(),
            victoiresRouge, (victoiresRouge * 100.0 / parties.size()),
            victoiresBleu, (victoiresBleu * 100.0 / parties.size()),
            matchsNuls, (matchsNuls * 100.0 / parties.size()),
            moyenneTours
        );
    }
}

