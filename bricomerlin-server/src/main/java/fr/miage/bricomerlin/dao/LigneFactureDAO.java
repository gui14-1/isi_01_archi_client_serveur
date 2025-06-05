package fr.miage.bricomerlin.dao;

import fr.miage.bricomerlin.model.LigneFacture;
import fr.miage.bricomerlin.unit.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour les opérations sur les lignes de facture
 */
public class LigneFactureDAO {
    private final Connection connection;

    public LigneFactureDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Récupère toutes les lignes d'une facture
     * @param idFacture L'ID de la facture
     * @return La liste des lignes de la facture
     */
    public List<LigneFacture> getLignesFactureByIdFacture(int idFacture) {
        List<LigneFacture> lignes = new ArrayList<>();
        try {
            String query = "SELECT * FROM LigneFacture WHERE id_facture = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idFacture);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                LigneFacture ligne = new LigneFacture();
                ligne.setIdLigne(resultSet.getInt("id_ligne"));
                ligne.setIdFacture(resultSet.getInt("id_facture"));
                ligne.setReferenceArticle(resultSet.getString("reference_article"));
                ligne.setQuantite(resultSet.getInt("quantite"));
                ligne.setPrixUnitaire(resultSet.getDouble("prix_unitaire"));
                lignes.add(ligne);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des lignes de facture: " + e.getMessage());
            e.printStackTrace();
        }
        return lignes;
    }

    /**
     * Ajoute une ligne à une facture
     * @param ligne La ligne de facture à ajouter
     * @return L'ID de la ligne créée ou -1 en cas d'erreur
     */
    public int createLigneFacture(LigneFacture ligne) {
        try {
            String query = "INSERT INTO LigneFacture (id_facture, reference_article, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, ligne.getIdFacture());
            statement.setString(2, ligne.getReferenceArticle());
            statement.setInt(3, ligne.getQuantite());
            statement.setDouble(4, ligne.getPrixUnitaire());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la ligne de facture: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
}
