package fr.miage.bricomerlin.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Interface RMI définissant les services de gestion des prix
 */
public interface PrixService extends Remote {
    /**
     * Récupère tous les prix des articles
     * @return une Map avec les références des articles comme clés et leurs prix comme valeurs
     * @throws RemoteException en cas d'erreur RMI
     */
    Map<String, Double> getPrixArticles() throws RemoteException;

    /**
     * Met à jour le prix d'un article
     * @param reference la référence de l'article
     * @param prix le nouveau prix
     * @throws RemoteException en cas d'erreur RMI
     */
    void updatePrixArticle(String reference, double prix) throws RemoteException;
} 