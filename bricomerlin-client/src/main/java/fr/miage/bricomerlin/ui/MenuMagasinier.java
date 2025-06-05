package fr.miage.bricomerlin.ui;

import fr.miage.bricomerlin.common.dto.ArticleDTO;
import fr.miage.bricomerlin.common.exception.ArticleInexistantException;
import fr.miage.bricomerlin.service.BricoMerlinService;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

/**
 * Interface utilisateur pour le magasinier
 */
public class MenuMagasinier {
    private BricoMerlinService service;
    private Scanner scanner;

    public MenuMagasinier(BricoMerlinService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    /**
     * Affiche le menu du magasinier
     */
    public void afficher() {
        boolean retourMenuPrincipal = false;

        while (!retourMenuPrincipal) {
            System.out.println("\n" + ConsoleColors.YELLOW_BOLD + "===== MODE MAGASINIER =====" + ConsoleColors.RESET);
            System.out.println("1. Consulter le stock d'un article");
            System.out.println("2. Ajouter des produits au stock");
            System.out.println("3. Rechercher des articles par famille");
            System.out.println("4. Retour au menu principal");
            System.out.print(ConsoleColors.YELLOW + "\nVotre choix : " + ConsoleColors.RESET);

            int choix = lireChoixUtilisateur();

            try {
                switch (choix) {
                    case 1:
                        consulterStock();
                        break;
                    case 2:
                        ajouterAuStock();
                        break;
                    case 3:
                        rechercherArticleParFamille();
                        break;
                    case 4:
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
     * Consulte le stock d'un article
     */
    private void consulterStock() throws RemoteException {
        System.out.print("Entrez la référence de l'article : ");
        String reference = scanner.nextLine();

        try {
            ArticleDTO article = service.consulterStock(reference);
            System.out.println("\n" + ConsoleColors.GREEN_BOLD + "---- Stock de l'article ----" + ConsoleColors.RESET);
            System.out.println("Référence : " + article.getReference());
            System.out.println("Famille : " + article.getFamille());
            System.out.println("Prix unitaire : " + article.getPrixUnitaire() + " €");
            System.out.println("Stock disponible : " + article.getStock());

            if (article.getStock() < 10) {
                System.out.println(ConsoleColors.RED_BOLD + "ATTENTION: Stock faible!" + ConsoleColors.RESET);
            }
        } catch (ArticleInexistantException e) {
            System.out.println(ConsoleColors.RED + "Article inexistant: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    /**
     * Ajoute des produits au stock
     */
    private void ajouterAuStock() throws RemoteException {
        System.out.print("Entrez la référence de l'article : ");
        String reference = scanner.nextLine();

        System.out.print("Entrez la quantité à ajouter : ");
        int quantite;
        try {
            quantite = Integer.parseInt(scanner.nextLine());
            if (quantite <= 0) {
                System.out.println(ConsoleColors.RED + "La quantité doit être supérieure à 0." + ConsoleColors.RESET);
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED + "Quantité invalide. Veuillez entrer un nombre entier." + ConsoleColors.RESET);
            return;
        }

        try {
            boolean success = service.ajouterProduit(reference, quantite);

            if (success) {
                System.out.println(ConsoleColors.GREEN + "Stock mis à jour avec succès !" + ConsoleColors.RESET);
                ArticleDTO article = service.consulterStock(reference);
                System.out.println("Nouveau stock disponible : " + article.getStock());
            } else {
                System.out.println(ConsoleColors.RED + "Erreur lors de la mise à jour du stock." + ConsoleColors.RESET);
            }
        } catch (ArticleInexistantException e) {
            System.out.println(ConsoleColors.RED + "Article inexistant: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    /**
     * Recherche des articles par famille
     */
    private void rechercherArticleParFamille() throws RemoteException {
        System.out.print("Entrez le nom de la famille d'articles : ");
        String famille = scanner.nextLine();

        List<String> references = service.rechercherArticles(famille);

        if (references.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "Aucun article trouvé dans la famille '" + famille + "'" + ConsoleColors.RESET);
            return;
        }

        System.out.println("\n" + ConsoleColors.GREEN_BOLD + "---- Articles de la famille " + famille + " ----" + ConsoleColors.RESET);
        System.out.printf("%-15s %-15s %-10s %-10s\n", "Référence", "Famille", "Prix", "Stock");
        System.out.println("----------------------------------------------");

        for (String ref : references) {
            try {
                ArticleDTO article = service.consulterStock(ref);
                System.out.printf("%-15s %-15s %-10.2f %-10d\n",
                        article.getReference(),
                        article.getFamille(),
                        article.getPrixUnitaire(),
                        article.getStock());

                if (article.getStock() < 10) {
                    System.out.println(ConsoleColors.RED + "  ⚠ Stock faible!" + ConsoleColors.RESET);
                }
            } catch (ArticleInexistantException e) {
                // Cela ne devrait pas arriver car on a récupéré la liste des références existantes
                System.out.println("- " + ref + " : [Erreur de récupération]");
            }
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