package fr.miage.bricomerlin.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe modèle représentant une facture
 */
public class Facture {
    private int idFacture;
    private Date dateFacturation;
    private double total;
    private String modePaiement;
    private List<LigneFacture> lignes;

    public Facture() {
        this.lignes = new ArrayList<>();
    }

    public Facture(int idFacture, Date dateFacturation, double total, String modePaiement) {
        this.idFacture = idFacture;
        this.dateFacturation = dateFacturation;
        this.total = total;
        this.modePaiement = modePaiement;
        this.lignes = new ArrayList<>();
    }

    public int getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }

    public Date getDateFacturation() {
        return dateFacturation;
    }

    public void setDateFacturation(Date dateFacturation) {
        this.dateFacturation = dateFacturation;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public List<LigneFacture> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneFacture> lignes) {
        this.lignes = lignes;
    }

    public void addLigne(LigneFacture ligne) {
        this.lignes.add(ligne);
    }

    public void recalculerTotal() {
        this.total = this.lignes.stream()
                .mapToDouble(LigneFacture::getSousTotal)
                .sum();
    }

    @Override
    public String toString() {
        return "Facture [idFacture=" + idFacture + ", dateFacturation=" + dateFacturation +
                ", total=" + total + ", modePaiement=" + modePaiement +
                ", lignes=" + lignes.size() + "]";
    }
}