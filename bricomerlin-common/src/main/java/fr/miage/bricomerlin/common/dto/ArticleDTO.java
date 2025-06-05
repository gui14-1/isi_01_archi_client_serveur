package fr.miage.bricomerlin.common.dto;

import java.io.Serializable;

/**
 * Classe de transfert de données pour un article
 */
public class ArticleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reference;
    private String famille;
    private double prixUnitaire;
    private int stock;

    public ArticleDTO() {
    }

    public ArticleDTO(String reference, String famille, double prixUnitaire, int stock) {
        this.reference = reference;
        this.famille = famille;
        this.prixUnitaire = prixUnitaire;
        this.stock = stock;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getFamille() {
        return famille;
    }

    public void setFamille(String famille) {
        this.famille = famille;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Article [reference=" + reference + ", famille=" + famille +
                ", prixUnitaire=" + prixUnitaire + ", stock=" + stock + "]";
    }
}