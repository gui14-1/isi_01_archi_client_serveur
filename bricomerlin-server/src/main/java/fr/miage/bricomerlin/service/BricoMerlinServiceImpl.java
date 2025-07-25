package fr.miage.bricomerlin.service;

import fr.miage.bricomerlin.common.dto.ArticleDTO;
import fr.miage.bricomerlin.common.dto.FactureDTO;
import fr.miage.bricomerlin.common.dto.LigneFactureDTO;
import fr.miage.bricomerlin.common.exception.ArticleInexistantException;
import fr.miage.bricomerlin.common.exception.StockInsuffisantException;
import fr.miage.bricomerlin.dao.ArticleDAO;
import fr.miage.bricomerlin.dao.FactureDAO;
import fr.miage.bricomerlin.dao.LigneFactureDAO;
import fr.miage.bricomerlin.model.Article;
import fr.miage.bricomerlin.model.Facture;
import fr.miage.bricomerlin.model.LigneFacture;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implémentation du service RMI pour Brico-Merlin
 */
public class BricoMerlinServiceImpl implements BricoMerlinService {
    private ArticleDAO articleDAO;
    private FactureDAO factureDAO;
    private LigneFactureDAO ligneFactureDAO;
    private static final String CENTRAL_HOST = "localhost";
    private static final int CENTRAL_PORT = 1098;
    private static final String CENTRAL_SERVICE = "PrixService";

    public BricoMerlinServiceImpl() {
        this.articleDAO = new ArticleDAO();
        this.factureDAO = new FactureDAO();
        this.ligneFactureDAO = new LigneFactureDAO();

        // Mise à jour des prix au démarrage
        updatePrixFromCentralServer();

        System.out.println("Service BricoMerlin initialisé");
    }

    /**
     * Met à jour les prix depuis le serveur central
     */
    private void updatePrixFromCentralServer() {
        try {
            System.out.println("Tentative de connexion au serveur central sur " + CENTRAL_HOST + ":" + CENTRAL_PORT);
            // Connexion au serveur central
            Registry registry = LocateRegistry.getRegistry(CENTRAL_HOST, CENTRAL_PORT);
            PrixService prixService = (PrixService) registry.lookup(CENTRAL_SERVICE);
            System.out.println("Connexion au serveur central établie");

            // Récupération des prix
            System.out.println("Récupération des prix depuis le serveur central...");
            Map<String, Double> prixArticles = prixService.getPrixArticles();
            System.out.println("Nombre d'articles reçus : " + prixArticles.size());

            // Mise à jour des prix dans la base de données
            int updatedCount = 0;
            int failedCount = 0;
            for (Map.Entry<String, Double> entry : prixArticles.entrySet()) {
                Article article = articleDAO.getArticleByReference(entry.getKey());
                if (article != null) {
                    System.out.println("Mise à jour du prix de l'article " + entry.getKey() + 
                                     " : " + article.getPrixUnitaire() + "€ -> " + entry.getValue() + "€");
                    article.setPrixUnitaire(entry.getValue());
                    if (articleDAO.updatePrix(article)) {
                        updatedCount++;
                    } else {
                        failedCount++;
                        System.err.println("Échec de la mise à jour du prix pour l'article " + entry.getKey());
                    }
                } else {
                    System.err.println("Article non trouvé dans la base de données : " + entry.getKey());
                }
            }

            System.out.println("Mise à jour des prix terminée : " + updatedCount + " articles mis à jour, " + 
                             failedCount + " échecs");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour des prix: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ArticleDTO consulterStock(String reference) throws RemoteException, ArticleInexistantException {
        Article article = articleDAO.getArticleByReference(reference);

        if (article == null) {
            throw new ArticleInexistantException(reference);
        }

        System.out.println("Consultation du stock pour l'article " + reference);

        return convertToDTO(article);
    }

    @Override
    public List<String> rechercherArticles(String famille) throws RemoteException {
        System.out.println("Recherche des articles de la famille " + famille);
        return articleDAO.getArticlesByFamilleWithStock(famille);
    }

    @Override
    public int acheterArticle(String reference, int quantite, int idFacture)
            throws RemoteException, ArticleInexistantException, StockInsuffisantException {

        Article article = articleDAO.getArticleByReference(reference);

        if (article == null) {
            throw new ArticleInexistantException(reference);
        }

        if (article.getStock() < quantite) {
            throw new StockInsuffisantException(reference, article.getStock(), quantite);
        }

        // Mise à jour du stock
        int newStock = article.getStock() - quantite;
        articleDAO.updateStock(reference, newStock);

        // Si c'est une nouvelle facture (idFacture = 0), on la crée
        Facture facture;
        if (idFacture == 0) {
            facture = new Facture();
            facture.setDateFacturation(new Date());
            facture.setTotal(0);
            facture.setModePaiement(null); // Pas encore payé

            idFacture = factureDAO.createFacture(facture);
        } else {
            facture = factureDAO.getFactureById(idFacture);
        }

        // Ajout de la ligne de facture
        LigneFacture ligne = new LigneFacture();
        ligne.setIdFacture(idFacture);
        ligne.setReferenceArticle(reference);
        ligne.setQuantite(quantite);
        ligne.setPrixUnitaire(article.getPrixUnitaire());

        int idLigne = ligneFactureDAO.createLigneFacture(ligne);

        // Recalcule le total de la facture
        facture = factureDAO.getFactureById(idFacture);
        facture.recalculerTotal();
        factureDAO.updateFacture(facture);

        System.out.println("Achat de " + quantite + " " + reference + " sur la facture " + idFacture);

        return idFacture;
    }

    @Override
    public boolean payerFacture(int idFacture, String modePaiement) throws RemoteException {
        Facture facture = factureDAO.getFactureById(idFacture);

        if (facture == null) {
            System.err.println("Facture introuvable: " + idFacture);
            return false;
        }

        if (facture.getModePaiement() != null) {
            System.err.println("La facture " + idFacture + " a déjà été payée.");
            return false;
        }

        facture.setModePaiement(modePaiement);
        boolean success = factureDAO.updateFacture(facture);

        if (success) {
            // Génération du fichier .txt pour simuler l'impression du ticket
            genererTicketCaisse(facture);
        }

        System.out.println("Paiement de la facture " + idFacture + " par " + modePaiement);

        return success;
    }

    /**
     * Génère un fichier .txt pour simuler l'impression d'un ticket de caisse
     * @param facture La facture payée
     */
    private void genererTicketCaisse(Facture facture) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String nomFichier = "ticket_facture_" + facture.getIdFacture() + "_" + dateFormat.format(new Date()) + ".txt";
            
            FileWriter writer = new FileWriter(nomFichier);
            
            writer.write("=======================================\n");
            writer.write("           BRICO-MERLIN\n");
            writer.write("         TICKET DE CAISSE\n");
            writer.write("=======================================\n\n");
            
            writer.write("Facture n°: " + facture.getIdFacture() + "\n");
            writer.write("Date: " + displayDateFormat.format(facture.getDateFacturation()) + "\n");
            writer.write("Mode de paiement: " + facture.getModePaiement() + "\n\n");
            
            writer.write("---------------------------------------\n");
            writer.write(String.format("%-15s %3s %8s %10s\n", "Article", "Qté", "P.Unit", "Sous-total"));
            writer.write("---------------------------------------\n");
            
            for (LigneFacture ligne : facture.getLignes()) {
                writer.write(String.format("%-15s %3d %8.2f€ %9.2f€\n",
                    ligne.getReferenceArticle(),
                    ligne.getQuantite(),
                    ligne.getPrixUnitaire(),
                    ligne.getSousTotal()));
            }
            
            writer.write("---------------------------------------\n");
            writer.write(String.format("TOTAL:                    %9.2f€\n", facture.getTotal()));
            writer.write("=======================================\n");
            writer.write("     Merci de votre visite !\n");
            writer.write("=======================================\n");
            
            writer.close();
            System.out.println("Ticket de caisse généré: " + nomFichier);
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du ticket: " + e.getMessage());
        }
    }

    @Override
    public FactureDTO consulterFacture(int idFacture) throws RemoteException {
        Facture facture = factureDAO.getFactureById(idFacture);

        if (facture == null) {
            System.err.println("Facture introuvable: " + idFacture);
            return null;
        }

        System.out.println("Consultation de la facture " + idFacture);

        return convertToDTO(facture);
    }

    @Override
    public double calculerChiffreAffaire(Date date) throws RemoteException {
        System.out.println("Calcul du chiffre d'affaires pour la date: " + date);
        return factureDAO.getChiffreAffairesForDate(date);
    }

    @Override
    public boolean ajouterProduit(String reference, int quantite) throws RemoteException, ArticleInexistantException {
        Article article = articleDAO.getArticleByReference(reference);

        if (article == null) {
            throw new ArticleInexistantException(reference);
        }

        int newStock = article.getStock() + quantite;
        boolean success = articleDAO.updateStock(reference, newStock);

        System.out.println("Ajout de " + quantite + " à l'article " + reference + " (nouveau stock: " + newStock + ")");

        return success;
    }

    @Override
    public List<FactureDTO> getFacturesNonPayees() throws RemoteException {
        System.out.println("Récupération des factures non payées");
        List<Facture> factures = factureDAO.getFacturesNonPayees();
        return factures.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit un objet Article du modèle en ArticleDTO
     */
    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setReference(article.getReference());
        dto.setFamille(article.getFamille());
        dto.setPrixUnitaire(article.getPrixUnitaire());
        dto.setStock(article.getStock());
        return dto;
    }

    /**
     * Convertit un objet Facture du modèle en FactureDTO
     */
    private FactureDTO convertToDTO(Facture facture) {
        FactureDTO dto = new FactureDTO();
        dto.setIdFacture(facture.getIdFacture());
        dto.setDateFacturation(facture.getDateFacturation());
        dto.setTotal(facture.getTotal());
        dto.setModePaiement(facture.getModePaiement());

        // Conversion des lignes de facture
        List<LigneFactureDTO> lignesDTO = facture.getLignes().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        dto.setLignes(lignesDTO);

        return dto;
    }

    /**
     * Convertit un objet LigneFacture du modèle en LigneFactureDTO
     */
    private LigneFactureDTO convertToDTO(LigneFacture ligne) {
        LigneFactureDTO dto = new LigneFactureDTO();
        dto.setIdLigne(ligne.getIdLigne());
        dto.setIdFacture(ligne.getIdFacture());
        dto.setReferenceArticle(ligne.getReferenceArticle());
        dto.setQuantite(ligne.getQuantite());
        dto.setPrixUnitaire(ligne.getPrixUnitaire());
        return dto;
    }
}