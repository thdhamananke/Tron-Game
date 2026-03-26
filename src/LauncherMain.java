import java.util.Scanner;

public class LauncherMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("      LANCEUR DU PROJET TRON      ");
            System.out.println("1. Lancer le mode graphique");
            System.out.println("2. Lancer le mode console");
            System.out.println("3. Lancer l'expérimentation manuelle");
            System.out.println("0. Quitter");
            System.out.print("Choix : ");

            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    Main.main(new String[]{});
                    break;

                case "2":
                    model.Main.main(new String[]{});
                    break;

                case "3":
                    experiment.ExperimentMain.main(new String[]{});
                    break;

                case "0":
                    scanner.close();
                    return;

                default:
                    System.out.println("Choix invalide.");
                    break;
            }

        }
    }
}