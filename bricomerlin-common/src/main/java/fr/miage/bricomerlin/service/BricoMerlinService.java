package fr.miage.bricomerlin.service;

import fr.miage.bricomerlin.common.dto.ArticleDTO;
import fr.miage.bricomerlin.common.dto.FactureDTO;
import fr.miage.bricomerlin.common.exception.ArticleInexistantException;
import fr.miage.bricomerlin.common.exception.StockInsuffisantException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

/**
 * Interface RMI définissant les services proposés par le système Brico-Merlin
 */
public interface BricoMerlinService extends Remote {

    /**
     * Consulte les informations d'un article à partir de sa référence
     *
     * @param reference la référence de l'article
     * @return les informations de l'article
     * @throws RemoteException en cas d'erreur RMI
     * @throws ArticleInexistantException si l'article n'existe pas
     */
    ArticleDTO consulterStock(String reference) throws RemoteException, ArticleInexistantException;

    /**
     * Recherche tous les articles d'une famille donnée dont le stock n'est pas nul
     *
     * @param famille la famille d'articles à rechercher
     * @return la liste des références des articles trouvés
     * @throws RemoteException en cas d'erreur RMI
     */
    List<String> rechercherArticles(String famille) throws RemoteException;

    /**
     * Permet à un client d'acheter un article en stock
     *
     * @param reference la référence de l'article à acheter
     * @param quantite la quantité à acheter
     * @param idFacture l'identifiant de la facture (0 pour une nouvelle facture)
     * @return l'identifiant de la facture (nouvelle ou existante)
     * @throws RemoteException en cas d'erreur RMI
     * @throws ArticleInexistantException si l'article n'existe pas
     * @throws StockInsuffisantException si le stock est insuffisant
     */
    int acheterArticle(String reference, int quantite, int idFacture)
            throws RemoteException, ArticleInexistantException, StockInsuffisantException;

    /**
     * Permet à un client de payer sa facture
     *
     * @param idFacture l'identifiant de la facture à payer
     * @param modePaiement le mode de paiement utilisé
     * @return true si le paiement a réussi, false sinon
     * @throws RemoteException en cas d'erreur RMI
     */
    boolean payerFacture(int idFacture, String modePaiement) throws RemoteException;

    /**
     * Consulte les détails d'une facture
     *
     * @param idFacture l'identifiant de la facture à consulter
     * @return les informations détaillées de la facture
     * @throws RemoteException en cas d'erreur RMI
     */
    FactureDTO consulterFacture(int idFacture) throws RemoteException;

    /**
     * Calcule le chiffre d'affaires à une date donnée
     *
     * @param date la date pour laquelle calculer le chiffre d'affaires
     * @return le montant total des ventes à cette date
     * @throws RemoteException en cas d'erreur RMI
     */
    double calculerChiffreAffaire(Date date) throws RemoteException;

    /**
     * Ajoute une quantité d'un produit existant au stock
     *
     * @param reference la référence de l'article à ajouter
     * @param quantite la quantité à ajouter
     * @return true si l'ajout a réussi, false sinon
     * @throws RemoteException en cas d'erreur RMI
     * @throws ArticleInexistantException si l'article n'existe pas
     */
    boolean ajouterProduit(String reference, int quantite)
            throws RemoteException, ArticleInexistantException;
}
