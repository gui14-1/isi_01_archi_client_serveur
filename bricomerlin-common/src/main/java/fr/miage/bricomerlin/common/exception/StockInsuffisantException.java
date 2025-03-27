package fr.miage.bricomerlin.common.exception;

import java.io.Serializable;

/**
 * Exception levée lorsque le stock d'un article est insuffisant pour une vente
 */
public class StockInsuffisantException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reference;
    private int stockDisponible;
    private int stockDemande;

    public StockInsuffisantException(String reference, int stockDisponible, int stockDemande) {
        super("Stock insuffisant pour l'article '" + reference +
                "': disponible=" + stockDisponible + ", demandé=" + stockDemande);
        this.reference = reference;
        this.stockDisponible = stockDisponible;
        this.stockDemande = stockDemande;
    }

    public String getReference() {
        return reference;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public int getStockDemande() {
        return stockDemande;
    }
}