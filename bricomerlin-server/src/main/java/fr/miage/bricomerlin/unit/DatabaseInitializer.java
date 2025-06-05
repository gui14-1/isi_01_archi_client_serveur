package fr.miage.bricomerlin.unit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour initialiser la base de données si elle est vide
 */
public class DatabaseInitializer {
    private final Connection connection;

    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    /**
     * Vérifie si la base de données est vide et l'initialise si nécessaire
     */
    public void initializeIfEmpty() {
        try {
            if (!tablesExist()) {
                System.out.println("Tables non trouvées. Initialisation de la base de données en cours...");
                executeInitializationScript();
                System.out.println("Initialisation de la base de données terminée avec succès.");
            } else if (isDatabaseEmpty()) {
                System.out.println("Base de données vide détectée. Insertion des données de test...");
                insertTestData();
                System.out.println("Insertion des données de test terminée avec succès.");
            } else {
                System.out.println("Base de données déjà initialisée.");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si les tables nécessaires existent
     */
    private boolean tablesExist() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "Article", new String[]{"TABLE"});
        return tables.next();
    }

    /**
     * Vérifie si la base de données est vide en comptant les articles
     */
    private boolean isDatabaseEmpty() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Article");
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
        }
    }

    /**
     * Exécute le script d'initialisation de la base de données
     */
    private void executeInitializationScript() throws SQLException, IOException {
        String script = loadScript();
        try (Statement stmt = connection.createStatement()) {
            // Diviser le script en instructions individuelles
            String[] instructions = script.split(";");
            for (String instruction : instructions) {
                instruction = instruction.trim();
                if (!instruction.isEmpty() && !instruction.startsWith("CREATE USER") && !instruction.startsWith("GRANT") && !instruction.startsWith("FLUSH")) {
                    stmt.execute(instruction);
                }
            }
        }
    }

    /**
     * Insère les données de test dans la base de données
     */
    private void insertTestData() throws SQLException {
        String insertScript = "INSERT INTO Article (reference, famille, prix_unitaire, stock) VALUES " +
                "('REF001', 'Outillage', 12.99, 50), " +
                "('REF002', 'Outillage', 24.50, 30), " +
                "('REF003', 'Jardinage', 9.99, 100), " +
                "('REF004', 'Quincaillerie', 2.50, 200), " +
                "('REF005', 'Peinture', 19.99, 40), " +
                "('REF006', 'Peinture', 29.99, 25), " +
                "('REF007', 'Outillage', 54.99, 15), " +
                "('REF008', 'Jardinage', 15.50, 70), " +
                "('REF009', 'Quincaillerie', 1.75, 300), " +
                "('REF010', 'Outillage', 89.99, 10)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(insertScript);
        }
    }

    /**
     * Charge le script SQL depuis le fichier de ressources
     */
    private String loadScript() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("database.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
} 