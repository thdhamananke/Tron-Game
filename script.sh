#!/bin/bash

export LANG=fr_FR.UTF-8
export LC_ALL=fr_FR.UTF-8

BASE_DIR="$(pwd)"
RESULTS_DIR="$BASE_DIR/csv"
PDF_DIR="$BASE_DIR/pdf"
LIB_DIR="$BASE_DIR/lib"
JAVA_CP="$BASE_DIR/build/classes:$LIB_DIR/*"

mkdir -p "$RESULTS_DIR" "$PDF_DIR" "$BASE_DIR/build/classes"

echo "Compilation en cours..."
find "$BASE_DIR/src" -name "*.java" > sources.txt

javac -cp "$LIB_DIR/*" -d "$BASE_DIR/build/classes" @sources.txt
if [ $? -ne 0 ]; then
    echo "Erreur de compilation"
    rm -f sources.txt
    exit 1
fi

rm -f sources.txt
echo "Compilation réussie"
echo ""

# =========================================================
# PARAMÈTRES
# =========================================================
plateau_sizes=(20 30 50)
depths=(3 6 9)

equipes=4
joueurs=2
total_players=$((equipes * joueurs))

strategies=("AlphaBetaStrategie" "MaxNStrategie" "ParanoidStrategie" "SOSStrategie")
heuristics=("FreeSpaceHeuristic" "VoronoiHeuristic" "TreeOfChambersHeuristic")

PARTIES_PAR_CONFIG=50

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
MASTER_CSV="$RESULTS_DIR/experiences_flat_${TIMESTAMP}.csv"

TMP_CSV="/tmp/temp_${TIMESTAMP}.csv"
TMP_PDF="/tmp/temp_${TIMESTAMP}.pdf"

# =========================================================
# CONSTRUIRE TOUTES LES CONFIGS D'ÉQUIPE
# Une config équipe = stratégie + heuristique
# =========================================================
TEAM_CONFIGS=()

for strat in "${strategies[@]}"; do
    for heur in "${heuristics[@]}"; do
        TEAM_CONFIGS+=("${strat}|${heur}")
    done
done

NB_TEAM_CONFIGS=${#TEAM_CONFIGS[@]}



# =========================================================
# EN-TÊTE CSV PLAT
# =========================================================
echo "config_id,plateau,profondeur,equipes,joueurs_par_equipe,total_joueurs,equipe1_strategie,equipe1_heuristique,equipe2_strategie,equipe2_heuristique,equipe3_strategie,equipe3_heuristique,equipe4_strategie,equipe4_heuristique,partie,taille_grille,equipe_gagnante,temps_moyen,nb_moyen_tours" > "$MASTER_CSV"

# =========================================================
# CALCUL DU NOMBRE TOTAL DE CONFIGS
# On prend des combinaisons de 4 configs différentes :
# i < j < k < l
# =========================================================
combinaisons_4=0
for ((i=0; i<NB_TEAM_CONFIGS; i++)); do
    for ((j=i+1; j<NB_TEAM_CONFIGS; j++)); do
        for ((k=j+1; k<NB_TEAM_CONFIGS; k++)); do
            for ((l=k+1; l<NB_TEAM_CONFIGS; l++)); do
                combinaisons_4=$((combinaisons_4 + 1))
            done
        done
    done
done

total=$(( ${#plateau_sizes[@]} * ${#depths[@]} * combinaisons_4 ))

echo "Nombre de configurations d'équipe possibles : $NB_TEAM_CONFIGS"
echo "Nombre de groupes de 4 équipes différentes : $combinaisons_4"
echo "Nombre total de campagnes à lancer : $total"
echo ""

current=0
start_time=$(date +%s)

# =========================================================
# BOUCLES PRINCIPALES
# =========================================================
for plateau in "${plateau_sizes[@]}"; do
for profondeur in "${depths[@]}"; do

    for ((i=0; i<NB_TEAM_CONFIGS; i++)); do
    for ((j=i+1; j<NB_TEAM_CONFIGS; j++)); do
    for ((k=j+1; k<NB_TEAM_CONFIGS; k++)); do
    for ((l=k+1; l<NB_TEAM_CONFIGS; l++)); do

        current=$((current + 1))

        IFS='|' read -r strat1 heur1 <<< "${TEAM_CONFIGS[$i]}"
        IFS='|' read -r strat2 heur2 <<< "${TEAM_CONFIGS[$j]}"
        IFS='|' read -r strat3 heur3 <<< "${TEAM_CONFIGS[$k]}"
        IFS='|' read -r strat4 heur4 <<< "${TEAM_CONFIGS[$l]}"

        config_id="P${plateau}_D${profondeur}_T1_${strat1}_${heur1}_T2_${strat2}_${heur2}_T3_${strat3}_${heur3}_T4_${strat4}_${heur4}"

   

        strategies_list="${strat1},${strat2},${strat3},${strat4}"
        heuristics_list="${heur1},${heur2},${heur3},${heur4}"

        rm -f "$TMP_CSV" "$TMP_PDF"

        java -cp "$JAVA_CP" experiment.ExperimentMain \
            "$plateau" "$equipes" "$joueurs" "$profondeur" \
            "$strategies_list" "$heuristics_list" "$PARTIES_PAR_CONFIG" "false" \
            "$TMP_CSV" "$TMP_PDF"

        if [ ! -f "$TMP_CSV" ]; then
        
            continue
        fi

        partie_num=0

        while IFS= read -r line; do
            [ -z "$line" ] && continue
            [[ "$line" =~ ^# ]] && continue

            if [[ "$line" == "TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours" ]]; then
                continue
            fi

            IFS=',' read -r taille_grille equipe_gagnante temps_moyen nb_moyen_tours <<< "$line"

            [ -z "$taille_grille" ] && continue
            [ -z "$equipe_gagnante" ] && continue

            partie_num=$((partie_num + 1))

            echo "${config_id},${plateau},${profondeur},${equipes},${joueurs},${total_players},${strat1},${heur1},${strat2},${heur2},${strat3},${heur3},${strat4},${heur4},${partie_num},${taille_grille},${equipe_gagnante},${temps_moyen},${nb_moyen_tours}" >> "$MASTER_CSV"

        done < "$TMP_CSV"


    done
    done
    done
    done

done
done

# =========================================================
# FIN
# =========================================================
end_time=$(date +%s)
duration=$((end_time - start_time))
total_lines=$(( $(wc -l < "$MASTER_CSV") - 1 ))

echo "=================================================="
echo "EXPÉRIENCES TERMINÉES"
echo "=================================================="
echo "Temps      : $((duration / 3600))h $(((duration % 3600) / 60))m $((duration % 60))s"
echo "Configs    : $current / $total"
echo "Parties    : $total_lines"
echo "CSV plat   : $MASTER_CSV"
echo "=================================================="