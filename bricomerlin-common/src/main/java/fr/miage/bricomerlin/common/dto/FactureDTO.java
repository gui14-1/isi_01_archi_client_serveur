package fr.miage.bricomerlin.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe de transfert de données pour une facture
 */
public class FactureDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idFacture;
    private Date dateFacturation;
    private double total;
    private String modePaiement;
    private List<LigneFactureDTO> lignes;

    public FactureDTO() {
        this.lignes = new ArrayList<>();
    }

    public FactureDTO(int idFacture, Date dateFacturation, double total, String modePaiement) {
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

    public List<LigneFactureDTO> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneFactureDTO> lignes) {
        this.lignes = lignes;
    }

    public void addLigne(LigneFactureDTO ligne) {
        this.lignes.add(ligne);
    }

    public void recalculerTotal() {
        this.total = this.lignes.stream()
                .mapToDouble(LigneFactureDTO::getSousTotal)
                .sum();
    }

    @Override
    public String toString() {
        return "Facture [idFacture=" + idFacture + ", dateFacturation=" + dateFacturation +
                ", total=" + total + ", modePaiement=" + modePaiement + ", lignes=" + lignes.size() + "]";
    }
}