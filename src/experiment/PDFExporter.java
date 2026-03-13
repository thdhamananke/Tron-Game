package experiment;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import model.*;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

public class PDFExporter {

    public static void export(ExperimentConfig config, ExperimentResult result, List<Strategie> strategies, String filename) {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Titre du rapport
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Rapport d'Expérimentation IA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Saut de ligne

            // Détails de la configuration
            document.add(new Paragraph("Configuration :"));
            document.add(new Paragraph(" - Taille plateau : " + config.getNbLignes() + "x" + config.getNbColonnes()));
            document.add(new Paragraph(" - Profondeur : " + config.getDepth()));
            document.add(new Paragraph(" "));

            int numTeams = config.getNbEquipes();

            // début du tableau 1
            document.add(new Paragraph("Résumé des Sessions (Confiance Globale)", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(numTeams + 2);
            table.setWidthPercentage(100);
            
            addHeaderCell(table, "Parties");
            for (int i = 0; i < numTeams; i++) {
                addHeaderCell(table, "T" + (i + 1));
            }
            addHeaderCell(table, "Conf %");

            // Ligne des configurations (MnS|TOC...)
            addHeaderCell(table, "Config");
            for (int i = 0; i < numTeams; i++) {
                int indexTeam = i * config.getJoueurs(); 
                Strategie strat = strategies.get(indexTeam);
                
                // On récupère le nom court de la stratégie et de son heuristique
                String configName = nomCourt(strat) + "|" + nomCourt(strat.getHeuristic());
                addCell(table, configName, false);
            }
            addCell(table, "-", false);


            // les sessions
            for (ExperimentResult.ResumeSession session : result.getResumesSession()) {
                addCell(table, String.valueOf(session.nbParties), false);
                for (int i = 0; i < numTeams; i++) {
                    // On cherche l'équipe par son nom "Equipe_1", "Equipe_2" etc.
                    String teamName = "Equipe_" + (i + 1);
                    double rate = 0.0;
                    for (Team team : session.winRates.keySet()) {
                        if (team.getName().equals(teamName)) {
                            rate = session.winRates.get(team);
                            break;
                        }
                    }
                    addCell(table, String.format(Locale.US, "%.1f%%", rate), false);
                }
                addCell(table, String.format(Locale.US, "±%.2f%%", session.confidence), false);
            }

            
            addHeaderCell(table, "TOTAL");
            // addCell(table, "TOTAL", true);
            for (int i = 0; i < numTeams; i++) {
                String teamName = "Equipe_" + (i + 1);
                // On cherche l'équipe correspondante dans le résultat global
                Team targetTeam = result.getWinsPerTeam().keySet().stream()
                                        .filter(team -> team.getName().equals(teamName)).findFirst()
                                        .orElse(null);
                
                double totalRate = (targetTeam != null) ? result.getWinRate(targetTeam) : 0.0;
                addCell(table, String.format(Locale.US, "%.1f%%", totalRate), true);
            }

            // Remplace ton bloc de calcul globalConf par celui-ci :
            double globalConf = 0.0;
            if (!result.getWinsPerTeam().isEmpty()) {
                // On parcourt toutes les équipes présentes dans le résultat global
                for (Team team : result.getWinsPerTeam().keySet()) {
                    double confEquipe = result.getConfidence(team, 1.96);
                    // On garde la valeur la plus élevée (le "pire cas" statistique)
                    if (confEquipe > globalConf) {
                        globalConf = confEquipe;
                    }
                }
            }
            addCell(table, String.format(Locale.US, "±%.2f%%", globalConf), true);
            document.add(table);
            document.add(new Paragraph(" "));


            // début du tableau 2
            document.add(new Paragraph("Résumé des Sessions (Confiance Detaillé)", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            document.add(new Paragraph(" "));

            PdfPTable tableDetail = new PdfPTable(numTeams + 1);
            tableDetail.setWidthPercentage(100);

            // En-tête
            addHeaderCell(tableDetail, "Session");
            for (int i = 0; i < numTeams; i++) {
                addHeaderCell(tableDetail, "T" + (i + 1));
            }

            // les configurations
            addHeaderCell(tableDetail, "Nb Parties");
            for (int i = 0; i < numTeams; i++) {
                int indexTeam = i * config.getJoueurs(); 
                Strategie strat = strategies.get(indexTeam);
                addCell(tableDetail, nomCourt(strat) + "|" + nomCourt(strat.getHeuristic()), false);
            }

            // On boucle sur les sessions pour afficher le WinRate ± Confiance par équipe
            for (ExperimentResult.ResumeSession session : result.getResumesSession()) {
                addCell(tableDetail, session.nbParties + "", false);
                for (int i = 0; i < numTeams; i++) {
                    String teamName = "Equipe_" + (i + 1);
                    Team targetTeamInSession = null;
                    double rate = 0.0;
                    
                    // On cherche l'équipe pour cette session
                    for (Team team : session.winRates.keySet()) {
                        if (team.getName().equals(teamName)) {
                            targetTeamInSession = team;
                            rate = session.winRates.get(team);
                            break;
                        }
                    }
                    
                    // Calcul de la confiance spécifique à cette équipe pour cette session
                    double partie = rate / 100.0;
                    double confIndiv = 1.96 * Math.sqrt((partie * (1 - partie)) / session.nbParties) * 100.0;
                    
                    // WinRate ± Confiance
                    addCell(tableDetail, String.format(Locale.US, "%.1f%% ±%.2f%%", rate, confIndiv), false);
                }
            }

            // le Tableau 2
            addHeaderCell(tableDetail, "TOTAL GLOBAL");
            for (int i = 0; i < numTeams; i++) {
                String teamName = "Equipe_" + (i + 1);
                Team targetTeam = result.getWinsPerTeam().keySet().stream()
                                        .filter(team -> team.getName().equals(teamName)).findFirst()
                                        .orElse(null);
                
                if (targetTeam != null) {
                    double totalRate = result.getWinRate(targetTeam);
                    double totalConf = result.getConfidence(targetTeam, 1.96);
                    addCell(tableDetail, String.format(Locale.US, "%.1f%% ±%.2f%%", totalRate, totalConf), true);
                } else {
                    addCell(tableDetail, "0.0% ±0.00%", true);
                }
            }

            document.add(tableDetail);
            document.add(new Paragraph(" "));

            Paragraph conclusion = new Paragraph("On a : " + conclusion(result));
            document.add(conclusion);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addCell(PdfPTable table, String text, boolean isBold) {
        Font font = isBold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10) : FontFactory.getFont(FontFactory.HELVETICA, 10);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addHeaderCell(PdfPTable table, String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        // On précise explicitement
        cell.setBackgroundColor(new java.awt.Color(230, 230, 230)); 
        cell.setPadding(5);
        table.addCell(cell);
    }


    private static String nomCourt(Object obj) {
        String name = obj.getClass().getSimpleName();
        return switch (name) {
            // Stratégies
            case "MinMaxStrategie" -> "MnS";
            case "AlphaBetaStrategie" -> "ABS";
            case "MaxNStrategie" -> "MaxN";
            case "ParanoidStrategie" -> "PaS";
            case "SOSStrategie" -> "SOS";
            // Heuristiques
            case "FreeSpaceHeuristic" -> "FSH";
            case "VoronoiHeuristic" -> "VoH";
            case "TreeOfChambersHeuristic" -> "TOC";
            default -> name.length() > 3 ? name.substring(0, 3) : name;
        };
    }


    /**
     * Un petit resumé 
     * @param result
     * @return
     */
    private static String conclusion(ExperimentResult result) {
        Team best = null;
        double maxRate = -1;
        for (Team team : result.getWinsPerTeam().keySet()) {
            double rate = result.getWinRate(team);
            if (rate > maxRate) {
                maxRate = rate;
                best = team;
            }
        }
        return (best != null) ? "L'équipe dominante est " + best.getName() + " avec " + String.format("%.1f%%", maxRate) + " de victoires." : "Match nul global.";
    }
}