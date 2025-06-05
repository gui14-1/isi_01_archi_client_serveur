package fr.miage.bricomerlin.dao;

import fr.miage.bricomerlin.model.Article;
import fr.miage.bricomerlin.unit.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour les opérations sur les articles
 */
public class ArticleDAO {
    private Connection connection;

    public ArticleDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Récupère un article par sa référence
     * @param reference La référence de l'article
     * @return L'article trouvé ou null si aucun n'est trouvé
     */
    public Article getArticleByReference(String reference) {
        try {
            String query = "SELECT * FROM Article WHERE reference = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, reference);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Article article = new Article();
                article.setReference(resultSet.getString("reference"));
                article.setFamille(resultSet.getString("famille"));
                article.setPrixUnitaire(resultSet.getDouble("prix_unitaire"));
                article.setStock(resultSet.getInt("stock"));
                return article;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'article: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Récupère tous les articles d'une famille donnée avec un stock non nul
     * @param famille La famille d'articles
     * @return La liste des références des articles
     */
    public List<String> getArticlesByFamilleWithStock(String famille) {
        List<String> references = new ArrayList<>();
        try {
            String query = "SELECT reference FROM Article WHERE famille = ? AND stock > 0";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, famille);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                references.add(resultSet.getString("reference"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles par famille: " + e.getMessage());
            e.printStackTrace();
        }
        return references;
    }

    /**
     * Met à jour le stock d'un article
     * @param reference La référence de l'article
     * @param newStock La nouvelle valeur du stock
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateStock(String reference, int newStock) {
        try {
            String query = "UPDATE Article SET stock = ? WHERE reference = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, newStock);
            statement.setString(2, reference);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}