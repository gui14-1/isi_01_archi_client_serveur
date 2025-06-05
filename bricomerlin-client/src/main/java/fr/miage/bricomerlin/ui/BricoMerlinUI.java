package fr.miage.bricomerlin.ui;

import fr.miage.bricomerlin.client.BricoMerlinClient;

import java.util.Scanner;

/**
 * Interface utilisateur principale
 */
public class BricoMerlinUI {
    private BricoMerlinClient client;
    private Scanner scanner;
    private MenuCaissier menuCaissier;
    private MenuDirecteur menuDirecteur;
    private MenuMagasinier menuMagasinier;

    /**
     * Constructeur de l'interface utilisateur
     * @param client le client RMI
     */
    public BricoMerlinUI(BricoMerlinClient client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.menuCaissier = new MenuCaissier(client.getService(), scanner);
        this.menuDirecteur = new MenuDirecteur(client.getService(), scanner);
        this.menuMagasinier = new MenuMagasinier(client.getService(), scanner);
    }

    /**
     * Démarre l'interface utilisateur
     */
    public void start() {
        afficherBanner();
        boolean continuer = true;

        while (continuer) {
            afficherMenuPrincipal();
            int choix = lireChoixUtilisateur();

            switch (choix) {
                case 1:
                    menuCaissier.afficher();
                    break;
                case 2:
                    menuDirecteur.afficher();
                    break;
                case 3:
                    menuMagasinier.afficher();
                    break;
                case 4:
                    System.out.println(ConsoleColors.YELLOW + "Au revoir !" + ConsoleColors.RESET);
                    continuer = false;
                    break;
                default:
                    System.out.println(ConsoleColors.RED + "Choix invalide ! Veuillez réessayer." + ConsoleColors.RESET);
            }
        }

        scanner.close();
    }

    /**
     * Affiche la bannière du programme
     */
    private void afficherBanner() {
        System.out.println(ConsoleColors.GREEN_BOLD + "╔═════════════════════════════════════════╗");
        System.out.println(ConsoleColors.GREEN_BOLD + "║             BRICO MERLIN                ║");
        System.out.println(ConsoleColors.GREEN_BOLD + "║   « Vos projets sont nos chantiers ! »  ║");
        System.out.println(ConsoleColors.GREEN_BOLD + "╚═════════════════════════════════════════╝" + ConsoleColors.RESET);
    }

    /**
     * Affiche le menu principal
     */
    private void afficherMenuPrincipal() {
        System.out.println("\n" + ConsoleColors.BLUE_BOLD + "===== MENU PRINCIPAL =====" + ConsoleColors.RESET);
        System.out.println("1. Mode Caissier");
        System.out.println("2. Mode Directeur");
        System.out.println("3. Mode Magasinier");
        System.out.println("4. Quitter");
        System.out.print(ConsoleColors.BLUE + "\nVotre choix : " + ConsoleColors.RESET);
    }

    /**
     * Lit le choix de l'utilisateur
     * @return le choix de l'utilisateur
     */
    private int lireChoixUtilisateur() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
