import re
import pandas as pd
from pathlib import Path
INPUT_CSV = "csv/duel_20260326_193013.csv"  
OUTPUT_XLSX = "analyse_experiences.xlsx"

def parse_duel_file(file_path: str) -> pd.DataFrame:
    file_path = Path(file_path)

    with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
        lines = [line.rstrip("\n") for line in f]

    rows = []
    i = 0

    config_pattern = re.compile(
        r"# CONFIG:\s*P(?P<plateau>\d+)_D(?P<profondeur>\d+)_"
        r"(?P<strategie1>.*?)_(?P<heuristique1>.*?)_VS_"
        r"(?P<strategie2>.*?)_(?P<heuristique2>.*)"
    )

    while i < len(lines):
        line = lines[i].strip()

        if line.startswith("# CONFIG:"):
            match = config_pattern.match(line)
            if not match:
                i += 1
                continue

            meta = match.groupdict()
            meta["plateau"] = int(meta["plateau"])
            meta["profondeur"] = int(meta["profondeur"])

            config_name = line.replace("# CONFIG:", "").strip()

            # Avance jusqu'a entete
            while i < len(lines) and "TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours" not in lines[i]:
                i += 1

            # pas de entete, on passe
            if i >= len(lines):
                break

            i += 1

            partie_num = 1
            while i < len(lines):
                current = lines[i].strip()

                if not current:
                    i += 1
                    continue

                if current.startswith("# CONFIG:"):
                    break

                if current.startswith("#"):
                    i += 1
                    continue

                # sortie: TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours
                parts = [p.strip() for p in current.split(",")]
                if len(parts) == 4:
                    try:
                        taille_grille = int(parts[0])
                        gagnant = parts[1]
                        temps = float(parts[2])
                        tours = float(parts[3])

                        row = {
                            "config": config_name,
                            "partie": partie_num,
                            "plateau": meta["plateau"],
                            "profondeur": meta["profondeur"],
                            "strategie1": meta["strategie1"],
                            "heuristique1": meta["heuristique1"],
                            "strategie2": meta["strategie2"],
                            "heuristique2": meta["heuristique2"],
                            "taille_grille": taille_grille,
                            "equipe_gagnante": gagnant,
                            "temps_moyen": temps,
                            "nb_moyen_tours": tours,
                        }

                        # Colonnes utiles pour les stats
                        row["victoire_equipe1"] = 1 if gagnant == "Equipe_1" else 0
                        row["victoire_equipe2"] = 1 if gagnant == "Equipe_2" else 0
                        row["match_nul"] = 1 if gagnant.lower() == "match nul" else 0

                        rows.append(row)
                        partie_num += 1
                    except ValueError:
                        pass

                i += 1
            continue

        i += 1

    df = pd.DataFrame(rows)
    return df


# TABLEAUX DE SYNTHÈSE

def build_summary(df: pd.DataFrame, group_cols):
    summary = (
        df.groupby(group_cols, dropna=False)
        .agg(
            parties=("partie", "count"),
            victoires_e1=("victoire_equipe1", "sum"),
            victoires_e2=("victoire_equipe2", "sum"),
            matchs_nuls=("match_nul", "sum"),
            temps_moyen=("temps_moyen", "mean"),
            tours_moyens=("nb_moyen_tours", "mean"),
        )
        .reset_index()
    )

    summary["winrate_e1_pct"] = (summary["victoires_e1"] / summary["parties"] * 100).round(2)
    summary["winrate_e2_pct"] = (summary["victoires_e2"] / summary["parties"] * 100).round(2)
    summary["nul_pct"] = (summary["matchs_nuls"] / summary["parties"] * 100).round(2)

    return summary.sort_values(group_cols).reset_index(drop=True)


# EXPORT EXCEL
def export_to_excel(df: pd.DataFrame, output_path: str):
    resume_config = build_summary(
        df,
        ["plateau", "profondeur", "strategie1", "heuristique1", "strategie2", "heuristique2"]
    )

    resume_strategie1 = build_summary(df, ["strategie1"])
    resume_heuristique1 = build_summary(df, ["heuristique1"])
    resume_profondeur = build_summary(df, ["profondeur"])
    resume_plateau = build_summary(df, ["plateau"])

    # Tableau croisé 
    pivot_strat_heur = pd.pivot_table(
        df,
        index=["strategie1"],
        columns=["heuristique1"],
        values="victoire_equipe1",
        aggfunc="mean",
        fill_value=0
    )
    pivot_strat_heur = (pivot_strat_heur * 100).round(2)

    with pd.ExcelWriter(output_path, engine="openpyxl") as writer:
        df.to_excel(writer, sheet_name="donnees_brutes_normalisees", index=False)
        resume_config.to_excel(writer, sheet_name="resume_configuration", index=False)
        resume_strategie1.to_excel(writer, sheet_name="resume_strategie1", index=False)
        resume_heuristique1.to_excel(writer, sheet_name="resume_heuristique1", index=False)
        resume_profondeur.to_excel(writer, sheet_name="resume_profondeur", index=False)
        resume_plateau.to_excel(writer, sheet_name="resume_plateau", index=False)
        pivot_strat_heur.to_excel(writer, sheet_name="pivot_strat_heuristique")



#
# MAIN
if __name__ == "__main__":
    df = parse_duel_file(INPUT_CSV)

        print("Nombre de parties trouvées :", len(df))
        print(df.head())
        export_to_excel(df, OUTPUT_XLSX)