#!/bin/bash


BASE_DIR="$(pwd)"
RESULTS_DIR="$BASE_DIR/csv"
PDF_DIR="$BASE_DIR/pdf"
LIB_DIR="$BASE_DIR/lib"
JAVA_CP="$BASE_DIR/build/classes:$LIB_DIR/*"




# COMPILATION

echo "Compilation en cours..."
find "$BASE_DIR/src" -name "*.java" > sources.txt
javac -cp "$LIB_DIR/*" -d "$BASE_DIR/build/classes" @sources.txt
if [ $? -ne 0 ]; then
    echo "Erreur compilation"
    rm sources.txt
    exit 1
fi
rm sources.txt
echo " Compilation réussie"



# PARAMÈTRES COMPLETS

plateau_sizes=(15 20 25 30)
team_sizes=(2 3 4 5)
players_per_team=(1 2 3)
depths=(3 4 5 6)
strategies=("MinMaxStrategie" "AlphaBetaStrategie" "MaxNStrategie" "ParanoidStrategie")
heuristics=("FreeSpaceHeuristic" "VoronoiHeuristic" "TreeOfChambersHeuristic")
PARTIES_PAR_CONFIG=4 

# ====================================================
# CALCUL DU NOMBRE DE CONFIGURATIONS
# ====================================================
total_configs=$(( ${#plateau_sizes[@]} * ${#team_sizes[@]} * ${#players_per_team[@]} * 
                  ${#depths[@]} * ${#strategies[@]} * ${#heuristics[@]} ))


echo "        EXPÉRIENCES MASSIVES         "

echo "Configurations: $total_configs"
echo "Parties totales: $((total_configs * PARTIES_PAR_CONFIG))"
echo ""

# Demander confirmation
echo "1) Lancer toutes les expériences"
echo "2) Quitter"
read -p "Choix: " choice

case $choice in
    1)
        PARTIES_PAR_CONFIG=1000
        ;;
    2)
        exit 0
        ;;
esac


# FICHIER RÉSUMÉ

SUMMARY_FILE="$RESULTS_DIR/summary_$(date +%Y%m%d_%H%M%S).csv"
echo "Date,Plateau,Equipes,Joueurs,Profondeur,Strategie,Heuristique,Victoires_Equipe1,Victoires_Equipe2,Victoires_Equipe3,Victoires_Equipe4,MatchsNuls,TempsMoyen,ToursMoyens" > "$SUMMARY_FILE"


# BOUCLE PRINCIPALE

current=0
start_time=$(date +%s)

for plateau in "${plateau_sizes[@]}"; do
    for equipes in "${team_sizes[@]}"; do
        for joueurs in "${players_per_team[@]}"; do
            # Éviter trop de joueurs
            total_players=$((equipes * joueurs))
            if [ $total_players -gt 16 ]; then
                echo "  Ignoré: ${equipes}eq x ${joueurs}j = $total_players joueurs"
                continue
            fi
            
            for profondeur in "${depths[@]}"; do
                for strategie in "${strategies[@]}"; do
                    for heuristique in "${heuristics[@]}"; do
                        current=$((current + 1))
                        
                        config_name="P${plateau}_E${equipes}_J${joueurs}_D${profondeur}_${strategie}_${heuristique}"
                        csv_file="$RESULTS_DIR/${config_name}.csv"
                        pdf_file="$PDF_DIR/${config_name}.pdf"

                        
                        echo "[$current/$total_configs] $config_name"
                        
                        # Lancer l'expérience
                        java -cp "$JAVA_CP" experiment.ExperimentMain \
                            "$plateau" "$equipes" "$joueurs" "$profondeur" \
                            "$strategie" "$heuristique" "$PARTIES_PAR_CONFIG" "false" \

                        
                        if [ $? -eq 0 ] && [ -f "$csv_file" ]; then
                            echo "  Terminé"
                            
                            # Compter les victoires (approximation - à adapter)
                            eq1=$(grep -c "Equipe_1" "$csv_file" 2>/dev/null || echo 0)
                            eq2=$(grep -c "Equipe_2" "$csv_file" 2>/dev/null || echo 0)
                            eq3=$(grep -c "Equipe_3" "$csv_file" 2>/dev/null || echo 0)
                            eq4=$(grep -c "Equipe_4" "$csv_file" 2>/dev/null || echo 0)
                            nuls=$(grep -c "Match Nul" "$csv_file" 2>/dev/null || echo 0)
                            
                            # Ajouter au résumé
                            echo "$(date +%Y-%m-%d),$plateau,$equipes,$joueurs,$profondeur,$strategie,$heuristique,$eq1,$eq2,$eq3,$eq4,$nuls,0,0" >> "$SUMMARY_FILE"


                        fi
                        
                        # Petite pause
                        sleep 2
                    done
                done
            done
        done
    done
done


# RAPPORT FINAL

end_time=$(date +%s)
duration=$((end_time - start_time))



echo "EXPÉRIENCES TERMINÉES"

echo "Temps: $((duration / 3600))h $(((duration % 3600) / 60))m $((duration % 60))s"
echo "Résumé: $SUMMARY_FILE"

# Afficher le top 10
echo ""
echo "TOP 10 MEILLEURES CONFIGURATIONS:"
tail -n +2 "$SUMMARY_FILE" | sort -t',' -k8 -nr | head -10 | column -t -s','
