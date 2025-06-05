package fr.miage.bricomerlin.model;

/**
 * Classe modèle représentant une ligne de facture
 */
public class LigneFacture {
    private int idLigne;
    private int idFacture;
    private String referenceArticle;
    private int quantite;
    private double prixUnitaire;

    public LigneFacture() {
    }

    public LigneFacture(int idLigne, int idFacture, String referenceArticle, int quantite, double prixUnitaire) {
        this.idLigne = idLigne;
        this.idFacture = idFacture;
        this.referenceArticle = referenceArticle;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public int getIdLigne() {
        return idLigne;
    }

    public void setIdLigne(int idLigne) {
        this.idLigne = idLigne;
    }

    public int getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }

    public String getReferenceArticle() {
        return referenceArticle;
    }

    public void setReferenceArticle(String referenceArticle) {
        this.referenceArticle = referenceArticle;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public double getSousTotal() {
        return quantite * prixUnitaire;
    }

    @Override
    public String toString() {
        return "LigneFacture [idLigne=" + idLigne + ", idFacture=" + idFacture +
                ", referenceArticle=" + referenceArticle + ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire + "]";
    }
}
