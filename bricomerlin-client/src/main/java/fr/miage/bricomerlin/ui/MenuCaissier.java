package fr.miage.bricomerlin.ui;

import fr.miage.bricomerlin.common.dto.ArticleDTO;
import fr.miage.bricomerlin.common.dto.FactureDTO;
import fr.miage.bricomerlin.common.dto.LigneFactureDTO;
import fr.miage.bricomerlin.common.exception.ArticleInexistantException;
import fr.miage.bricomerlin.common.exception.StockInsuffisantException;
import fr.miage.bricomerlin.service.BricoMerlinService;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

/**
 * Interface utilisateur pour le caissier
 */
public class MenuCaissier {
    private BricoMerlinService service;
    private Scanner scanner;
    private int factureEnCours = 0;

    public MenuCaissier(BricoMerlinService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    /**
     * Affiche le menu du caissier
     */
    public void afficher() {
        boolean retourMenuPrincipal = false;

        while (!retourMenuPrincipal) {
            System.out.println("\n" + ConsoleColors.CYAN_BOLD + "===== MODE CAISSIER =====" + ConsoleColors.RESET);
            if (factureEnCours > 0) {
                System.out.println(ConsoleColors.YELLOW + "Facture en cours : #" + factureEnCours + ConsoleColors.RESET);
            }
            System.out.println("1. Consulter un article");
            System.out.println("2. Rechercher des articles par famille");
            System.out.println("3. Ajouter un article à la facture");
            System.out.println("4. Consulter la facture en cours");
            System.out.println("5. Payer la facture");
            System.out.println("6. Charger une facture non payée");
            System.out.println("7. Nouvelle facture");
            System.out.println("8. Retour au menu principal");
            System.out.print(ConsoleColors.CYAN + "\nVotre choix : " + ConsoleColors.RESET);

            int choix = lireChoixUtilisateur();

            try {
                switch (choix) {
                    case 1:
                        consulterArticle();
                        break;
                    case 2:
                        rechercherArticleParFamille();
                        break;
                    case 3:
                        ajouterArticleFacture();
                        break;
                    case 4:
                        consulterFactureEnCours();
                        break;
                    case 5:
                        payerFacture();
                        break;
                    case 6:
                        chargerFactureNonPayee();
                        break;
                    case 7:
                        nouvelleFacture();
                        break;
                    case 8:
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
     * Consulte les informations d'un article
     */
    private void consulterArticle() throws RemoteException {
        System.out.print("Entrez la référence de l'article : ");
        String reference = scanner.nextLine();

        try {
            ArticleDTO article = service.consulterStock(reference);
            System.out.println("\n" + ConsoleColors.GREEN_BOLD + "---- Détails de l'article ----" + ConsoleColors.RESET);
            System.out.println("Référence : " + article.getReference());
            System.out.println("Famille : " + article.getFamille());
            System.out.println("Prix unitaire : " + article.getPrixUnitaire() + " €");
            System.out.println("Stock disponible : " + article.getStock());
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
        for (String ref : references) {
            try {
                ArticleDTO article = service.consulterStock(ref);
                System.out.println("- " + ref + " : " + article.getPrixUnitaire() + " € (Stock: " + article.getStock() + ")");
            } catch (ArticleInexistantException e) {
                // Cela ne devrait pas arriver car on a récupéré la liste des références existantes
                System.out.println("- " + ref + " : [Erreur de récupération]");
            }
        }
    }

    /**
     * Ajoute un article à la facture
     */
    private void ajouterArticleFacture() throws RemoteException {
        System.out.print("Entrez la référence de l'article : ");
        String reference = scanner.nextLine();

        System.out.print("Entrez la quantité : ");
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
            factureEnCours = service.acheterArticle(reference, quantite, factureEnCours);
            System.out.println(ConsoleColors.GREEN + "Article ajouté à la facture avec succès !" + ConsoleColors.RESET);
        } catch (ArticleInexistantException e) {
            System.out.println(ConsoleColors.RED + "Article inexistant: " + e.getMessage() + ConsoleColors.RESET);
        } catch (StockInsuffisantException e) {
            System.out.println(ConsoleColors.RED + "Stock insuffisant: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    /**
     * Consulte la facture en cours
     */
    private void consulterFactureEnCours() throws RemoteException {
        if (factureEnCours <= 0) {
            System.out.println(ConsoleColors.YELLOW + "Aucune facture en cours. Veuillez créer une nouvelle facture." + ConsoleColors.RESET);
            return;
        }

        FactureDTO facture = service.consulterFacture(factureEnCours);

        if (facture == null) {
            System.out.println(ConsoleColors.RED + "Impossible de récupérer la facture #" + factureEnCours + ConsoleColors.RESET);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("\n" + ConsoleColors.GREEN_BOLD + "===== FACTURE #" + facture.getIdFacture() + " =====" + ConsoleColors.RESET);
        System.out.println("Date : " + dateFormat.format(facture.getDateFacturation()));
        System.out.println("Statut : " + (facture.getModePaiement() == null ? "Non payée" : "Payée (" + facture.getModePaiement() + ")"));
        System.out.println(ConsoleColors.GREEN_BOLD + "\n--- Détail des articles ---" + ConsoleColors.RESET);
        System.out.printf("%-15s %-10s %-10s %-10s\n", "Référence", "Quantité", "Prix Unit.", "Sous-total");
        System.out.println("----------------------------------------------");

        for (LigneFactureDTO ligne : facture.getLignes()) {
            System.out.printf("%-15s %-10d %-10.2f %-10.2f\n",
                    ligne.getReferenceArticle(),
                    ligne.getQuantite(),
                    ligne.getPrixUnitaire(),
                    ligne.getSousTotal());
        }

        System.out.println("----------------------------------------------");
        System.out.println(ConsoleColors.YELLOW_BOLD + "TOTAL : " + facture.getTotal() + " €" + ConsoleColors.RESET);
    }

    /**
     * Procède au paiement de la facture courante
     */
    private void payerFacture() throws RemoteException {
        if (factureEnCours <= 0) {
            System.out.println(ConsoleColors.YELLOW + "Aucune facture en cours. Veuillez créer une nouvelle facture." + ConsoleColors.RESET);
            return;
        }

        FactureDTO facture = service.consulterFacture(factureEnCours);

        if (facture == null) {
            System.out.println(ConsoleColors.RED + "Impossible de récupérer la facture #" + factureEnCours + ConsoleColors.RESET);
            return;
        }

        if (facture.getModePaiement() != null) {
            System.out.println(ConsoleColors.YELLOW + "Cette facture a déjà été payée (" + facture.getModePaiement() + ")." + ConsoleColors.RESET);
            factureEnCours = 0; // Réinitialisation
            return;
        }

        System.out.println(ConsoleColors.YELLOW_BOLD + "Montant à payer : " + facture.getTotal() + " €" + ConsoleColors.RESET);

        System.out.print("Choisissez le mode de paiement (CB, Espèces) : ");
        String modePaiement = scanner.nextLine();

        if (modePaiement.isEmpty()) {
            System.out.println(ConsoleColors.RED + "Mode de paiement invalide." + ConsoleColors.RESET);
            return;
        }

        boolean paiementOk = service.payerFacture(factureEnCours, modePaiement);

        if (paiementOk) {
            System.out.println(ConsoleColors.GREEN_BOLD + "Paiement effectué avec succès !" + ConsoleColors.RESET);
            factureEnCours = 0; // Réinitialisation après paiement
        } else {
            System.out.println(ConsoleColors.RED + "Échec du paiement. Veuillez réessayer." + ConsoleColors.RESET);
        }
    }

    /**
     * Crée une nouvelle facture
     */
    private void nouvelleFacture() {
        factureEnCours = 0;
        System.out.println(ConsoleColors.GREEN + "Nouvelle facture initialisée." + ConsoleColors.RESET);
    }

    /**
     * Charge une facture non payée existante
     */
    private void chargerFactureNonPayee() throws RemoteException {
        List<FactureDTO> facturesNonPayees = service.getFacturesNonPayees();
        
        if (facturesNonPayees.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "Aucune facture non payée trouvée." + ConsoleColors.RESET);
            return;
        }

        System.out.println("\n" + ConsoleColors.GREEN_BOLD + "===== FACTURES NON PAYÉES =====" + ConsoleColors.RESET);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        for (FactureDTO facture : facturesNonPayees) {
            System.out.printf("%d. Facture #%d - %s - %.2f€ (%d articles)\n",
                facturesNonPayees.indexOf(facture) + 1,
                facture.getIdFacture(),
                dateFormat.format(facture.getDateFacturation()),
                facture.getTotal(),
                facture.getLignes().size());
        }

        System.out.print("\nSélectionnez le numéro de la facture à charger (0 pour annuler) : ");
        int choix;
        try {
            choix = Integer.parseInt(scanner.nextLine());
            if (choix == 0) {
                return;
            }
            if (choix < 1 || choix > facturesNonPayees.size()) {
                System.out.println(ConsoleColors.RED + "Choix invalide." + ConsoleColors.RESET);
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED + "Choix invalide." + ConsoleColors.RESET);
            return;
        }

        FactureDTO factureChoisie = facturesNonPayees.get(choix - 1);
        factureEnCours = factureChoisie.getIdFacture();
        
        System.out.println(ConsoleColors.GREEN + "Facture #" + factureEnCours + " chargée avec succès !" + ConsoleColors.RESET);
        
        // Affichage des détails de la facture chargée
        consulterFactureEnCours();
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