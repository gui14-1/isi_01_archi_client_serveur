package fr.miage.bricomerlin.dao;

import fr.miage.bricomerlin.model.Facture;
import fr.miage.bricomerlin.model.LigneFacture;
import fr.miage.bricomerlin.unit.DatabaseConnection;

import java.sql.*;
import java.util.Date;
import java.util.List;

/**
 * Classe DAO pour les opérations sur les factures
 */
public class FactureDAO {
    private Connection connection;
    private LigneFactureDAO ligneFactureDAO;

    public FactureDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.ligneFactureDAO = new LigneFactureDAO();
    }

    /**
     * Crée une nouvelle facture
     * @param facture La facture à créer
     * @return L'ID de la facture créée ou -1 en cas d'erreur
     */
    public int createFacture(Facture facture) {
        try {
            String query = "INSERT INTO Facture (date_facturation, total, mode_paiement) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setDate(1, new java.sql.Date(facture.getDateFacturation().getTime()));
            statement.setDouble(2, facture.getTotal());
            statement.setString(3, facture.getModePaiement());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la facture: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Récupère une facture par son ID
     * @param idFacture L'ID de la facture
     * @return La facture trouvée ou null si aucune n'est trouvée
     */
    public Facture getFactureById(int idFacture) {
        try {
            String query = "SELECT * FROM Facture WHERE id_facture = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idFacture);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Facture facture = new Facture();
                facture.setIdFacture(resultSet.getInt("id_facture"));
                facture.setDateFacturation(resultSet.getDate("date_facturation"));
                facture.setTotal(resultSet.getDouble("total"));
                facture.setModePaiement(resultSet.getString("mode_paiement"));

                // Récupération des lignes de facture associées
                List<LigneFacture> lignes = ligneFactureDAO.getLignesFactureByIdFacture(idFacture);
                facture.setLignes(lignes);

                return facture;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la facture: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Met à jour une facture existante
     * @param facture La facture à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateFacture(Facture facture) {
        try {
            String query = "UPDATE Facture SET total = ?, mode_paiement = ? WHERE id_facture = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDouble(1, facture.getTotal());
            statement.setString(2, facture.getModePaiement());
            statement.setInt(3, facture.getIdFacture());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la facture: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calcule le chiffre d'affaires pour une date donnée
     * @param date La date pour laquelle calculer le chiffre d'affaires
     * @return Le chiffre d'affaires total
     */
    public double getChiffreAffairesForDate(Date date) {
        double total = 0;
        try {
            String query = "SELECT SUM(total) as chiffre_affaires FROM Facture WHERE date_facturation = ? AND mode_paiement IS NOT NULL";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next() && resultSet.getObject("chiffre_affaires") != null) {
                total = resultSet.getDouble("chiffre_affaires");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du chiffre d'affaires: " + e.getMessage());
            e.printStackTrace();
        }
        return total;
    }
}