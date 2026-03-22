#!/bin/bash

BASE_DIR="$(pwd)"
RESULTS_DIR="$BASE_DIR/csv"
PDF_DIR="$BASE_DIR/pdf"
LIB_DIR="$BASE_DIR/lib"
JAVA_CP="$BASE_DIR/build/classes:$LIB_DIR/*"

mkdir -p "$RESULTS_DIR" "$PDF_DIR" "$BASE_DIR/build/classes"

#compilation
echo "Compilation en cours..."
find "$BASE_DIR/src" -name "*.java" > sources.txt

javac -cp "$LIB_DIR/*" -d "$BASE_DIR/build/classes" @sources.txt
if [ $? -ne 0 ]; then
    echo "Erreur compilation"
    rm sources.txt
    exit 1
fi

rm sources.txt
echo "Compilation réussie"
echo ""

#config
plateau_sizes=(12 15)
depths=(3 4)

# DUEL FIXE
equipes=2
joueurs=1
total_players=2

strategies=("MinMaxStrategie" "AlphaBetaStrategie" "MaxNStrategie" "ParanoidStrategie")
heuristics=("FreeSpaceHeuristic" "VoronoiHeuristic")

PARTIES_PAR_CONFIG=50

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
MASTER_CSV="$RESULTS_DIR/duel_$TIMESTAMP.csv"
MASTER_PDF="$PDF_DIR/duel_$TIMESTAMP.pdf"

TMP_CSV="/tmp/temp_$TIMESTAMP.csv"
TMP_PDF="/tmp/temp_$TIMESTAMP.pdf"
TMP_PDF_LIST=()

# Créer en-tete du CSV
echo "# EXPÉRIENCES DUEL - $(date)" > "$MASTER_CSV"
echo "# Plateau: ${plateau_sizes[*]}, Profondeurs: ${depths[*]}" >> "$MASTER_CSV"
echo "# Stratégies: ${strategies[*]}" >> "$MASTER_CSV"
echo "# Heuristiques: ${heuristics[*]}" >> "$MASTER_CSV"
echo "# Parties par config: $PARTIES_PAR_CONFIG" >> "$MASTER_CSV"
echo "" >> "$MASTER_CSV"

current=0
start_time=$(date +%s)

# calcul de nbr total de comibaisons
total=$(( ${#plateau_sizes[@]} * ${#depths[@]} * ${#strategies[@]} * ${#strategies[@]} * ${#heuristics[@]} * ${#heuristics[@]} ))

# EXPÉRIMENTATIONS
for plateau in "${plateau_sizes[@]}"; do
for profondeur in "${depths[@]}"; do

for strat1 in "${strategies[@]}"; do
for strat2 in "${strategies[@]}"; do

# Évite meme conf inutile (A vs A)
[ "$strat1" == "$strat2" ] && continue

for heur1 in "${heuristics[@]}"; do
for heur2 in "${heuristics[@]}"; do

current=$((current + 1))
echo -n "[$current/$total] P${plateau} D${profondeur} ${strat1}/${heur1} vs ${strat2}/${heur2} ... "

# Config string
config="P${plateau}_D${profondeur}_${strat1}_${heur1}_VS_${strat2}_${heur2}"

# Listes pour le batch
strategies_list="$strat1,$strat2"
heuristics_list="$heur1,$heur2"

# Exécution
java -cp "$JAVA_CP" experiment.ExperimentMain \
    "$plateau" "$equipes" "$joueurs" "$profondeur" \
    "$strategies_list" "$heuristics_list" "$PARTIES_PAR_CONFIG" "false" \
    "$TMP_CSV" "$TMP_PDF" > /dev/null 2>&1

# TRAITEMENT CSV

if [ -f "$TMP_CSV" ]; then
    echo "" >> "$MASTER_CSV"
    echo "# CONFIG: $config" >> "$MASTER_CSV"
    echo "# Plateau: ${plateau}x${plateau}, Profondeur: $profondeur" >> "$MASTER_CSV"
    echo "# Stratégies: $strat1 ($heur1) vs $strat2 ($heur2)" >> "$MASTER_CSV"
    echo "# Parties: $PARTIES_PAR_CONFIG" >> "$MASTER_CSV"
    
    cat "$TMP_CSV" >> "$MASTER_CSV"
    rm -f "$TMP_CSV"
    echo -n "CSV "
else
    echo -n " CSV "
fi

# TRAITEMENT PDF

if [ -f "$TMP_PDF" ]; then
    NAMED_PDF="/tmp/pdf_${current}_$TIMESTAMP.pdf"
    mv "$TMP_PDF" "$NAMED_PDF"
    TMP_PDF_LIST+=("$NAMED_PDF")
    echo " PDF"
else
    echo " PDF"
fi

done
done
done
done

done
done

echo ""


# FUSION PDF 

if [ ${#TMP_PDF_LIST[@]} -gt 0 ]; then
    echo "Fusion de ${#TMP_PDF_LIST[@]} PDFs..."
    
    # Vérifier si pdfunite est disponible
    if command -v pdfunite &> /dev/null; then
        echo "Utilisation de pdfunite..."
        pdfunite "${TMP_PDF_LIST[@]}" "$MASTER_PDF"
        if [ $? -eq 0 ]; then
            echo " PDF fusionné: $MASTER_PDF"
        else
            echo " Erreur fusion avec pdfunite"
            # Fallback: garder le premier
            cp "${TMP_PDF_LIST[0]}" "$MASTER_PDF"
        fi
    
    # Sinon
    elif command -v gs &> /dev/null; then
        echo "Utilisation de Ghostscript..."
        gs -dBATCH -dNOPAUSE -q -sDEVICE=pdfwrite -sOutputFile="$MASTER_PDF" "${TMP_PDF_LIST[@]}"
        if [ $? -eq 0 ]; then
            echo " PDF fusionné: $MASTER_PDF"
        else
            echo " Erreur fusion avec Ghostscript"
            cp "${TMP_PDF_LIST[0]}" "$MASTER_PDF"
        fi
  
    elif [ -f "$BASE_DIR/build/classes/experiment/PdfMerger.class" ]; then
        echo "Utilisation de PdfMerger Java..."
        java -cp "$JAVA_CP" experiment.PdfMerger "$MASTER_PDF" "${TMP_PDF_LIST[@]}"
        if [ $? -eq 0 ]; then
            echo " PDF fusionné: $MASTER_PDF"
        else
            echo " Erreur fusion PDF"
            cp "${TMP_PDF_LIST[0]}" "$MASTER_PDF"
        fi
    else
        echo " Aucun outil de fusion disponible"
        echo "   Installez pdfunite (poppler-utils) ou Ghostscript"
        cp "${TMP_PDF_LIST[0]}" "$MASTER_PDF"
        echo "  Premier PDF conservé"
    fi
    
    # Nettoyage
    for f in "${TMP_PDF_LIST[@]}"; do
        rm -f "$f"
    done
else
    echo " Aucun PDF à fusionner"
fi


# STATISTIQUES FINALES
end_time=$(date +%s)
duration=$((end_time - start_time))

# Compter les lignes de résultats
total_lines=$(grep -c "^[0-9]" "$MASTER_CSV" 2>/dev/null || echo 0)

echo ""
echo "══════════════════════════════════════════"
echo "EXPÉRIENCES TERMINÉES"
echo "══════════════════════════════════════════"
echo "Temps      : $((duration / 3600))h $(((duration % 3600) / 60))m $((duration % 60))s"
echo "Configs    : $current / $total"
echo "Parties    : $total_lines"
echo "CSV        : $MASTER_CSV"
echo "PDF        : $MASTER_PDF"
echo "══════════════════════════════════════════"

# Aperçu rapide des résultats
if [ $total_lines -gt 0 ]; then
    echo ""
    echo "Aperçu des résultats (premières lignes):"
    head -5 "$MASTER_CSV"
fi

# LANCEMENT DE L'ANALYSEUR DE GRAPHIQUES
echo ""
echo "══════════════════════════════════════════"
echo "Lancement de l'analyseur de graphiques..."
echo "══════════════════════════════════════════"

# Vérifier que le fichier CSV existe
if [ -f "$MASTER_CSV" ]; then
    echo "Fichier CSV: $MASTER_CSV"
    
     # Lancer l'analyseur en arrière-plan
    #java -cp "$JAVA_CP" experiment.ExperimentConfigwithChartsmain "$MASTER_CSV" &
    #CHART_PID=$!
    java -cp "$JAVA_CP" experiment.RunAnalyzer "$MASTER_CSV" &
    CHART_PID=$!
    
    echo "Analyseur lancé (PID: $CHART_PID)"


else
    echo " Fichier CSV non trouvé: $MASTER_CSV"
    echo "   Impossible de lancer l'analyseur"
fi

echo ""
echo "══════════════════════════════════════════"
echo "Script terminé"
echo "══════════════════════════════════════════"