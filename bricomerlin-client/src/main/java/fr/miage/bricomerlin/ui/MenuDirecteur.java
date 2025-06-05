package fr.miage.bricomerlin.ui;

import fr.miage.bricomerlin.service.BricoMerlinService;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Interface utilisateur pour le directeur
 */
public class MenuDirecteur {
    private BricoMerlinService service;
    private Scanner scanner;

    public MenuDirecteur(BricoMerlinService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    /**
     * Affiche le menu du directeur
     */
    public void afficher() {
        boolean retourMenuPrincipal = false;

        while (!retourMenuPrincipal) {
            System.out.println("\n" + ConsoleColors.PURPLE_BOLD + "===== MODE DIRECTEUR =====" + ConsoleColors.RESET);
            System.out.println("1. Consulter le chiffre d'affaires d'une journée");
            System.out.println("2. Consulter une facture");
            System.out.println("3. Retour au menu principal");
            System.out.print(ConsoleColors.PURPLE + "\nVotre choix : " + ConsoleColors.RESET);

            int choix = lireChoixUtilisateur();

            try {
                switch (choix) {
                    case 1:
                        consulterChiffreAffaires();
                        break;
                    case 2:
                        consulterFacture();
                        break;
                    case 3:
                        retourMenuPrincipal = true;
                        break;
                    default:
                        System.out.println(ConsoleColors.RED + "Choix invalide ! Veuillez réessayer." + ConsoleColors.RESET);
                }
            } catch (RemoteException e) {
                System.err.println(ConsoleColors.RED + "Erreur de communication avec le serveur: " + e.getMessage() + ConsoleColors.RESET);
            } catch (Exception e) {
                System.err.println(ConsoleColors.RED + "Erreur: " + e.getMessage() + ConsoleColors.RESET);
            }
        }
    }

    /**
     * Consulte le chiffre d'affaires d'une journée
     */
    private void consulterChiffreAffaires() throws RemoteException {
        System.out.print("Entrez la date (format JJ/MM/AAAA) : ");
        String dateStr = scanner.nextLine();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(dateStr);

            double ca = service.calculerChiffreAffaire(date);

            System.out.println("\n" + ConsoleColors.GREEN_BOLD + "---- Chiffre d'affaires du " + dateStr + " ----" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW_BOLD + "Total : " + ca + " €" + ConsoleColors.RESET);
        } catch (ParseException e) {
            System.out.println(ConsoleColors.RED + "Format de date invalide. Utilisez le format JJ/MM/AAAA." + ConsoleColors.RESET);
        }
    }

    /**
     * Consulte une facture spécifique
     */
    private void consulterFacture() throws RemoteException {
        System.out.print("Entrez le numéro de facture : ");
        try {
            int idFacture = Integer.parseInt(scanner.nextLine());

            // Réutilisation du code du caissier pour afficher la facture
            MenuCaissier menuCaissier = new MenuCaissier(service, scanner);
            java.lang.reflect.Method method = MenuCaissier.class.getDeclaredMethod("consulterFactureEnCours");
            method.setAccessible(true);

            // On modifie le champ factureEnCours temporairement
            java.lang.reflect.Field field = MenuCaissier.class.getDeclaredField("factureEnCours");
            field.setAccessible(true);
            field.set(menuCaissier, idFacture);

            // On appelle la méthode
            method.invoke(menuCaissier);

            // On remet à zéro
            field.set(menuCaissier, 0);
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED + "Numéro de facture invalide. Veuillez entrer un nombre entier." + ConsoleColors.RESET);
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Erreur: " + e.getMessage() + ConsoleColors.RESET);
        }
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