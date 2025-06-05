package fr.miage.bricomerlin.unit;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe utilitaire pour la connexion à la base de données
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String user;
    private String password;
    private String driver;

    private DatabaseConnection() {
        try {
            loadConfig();
            Class.forName(driver);
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion à la base de données établie");
            
            // Initialiser la base de données si elle est vide
            DatabaseInitializer initializer = new DatabaseInitializer(connection);
            initializer.initializeIfEmpty();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            System.err.println("Erreur lors de la connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadConfig() throws IOException {
        Properties prop = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

        if (input == null) {
            System.err.println("Fichier de configuration introuvable !");
            throw new IOException("Fichier config.properties introuvable");
        }

        prop.load(input);

        url = prop.getProperty("db.url");
        user = prop.getProperty("db.user");
        password = prop.getProperty("db.password");
        driver = prop.getProperty("db.driver");

        input.close();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else if (instance.checkClosed()) {
            instance = new DatabaseConnection();
        }

        return instance;
    }

    private boolean checkClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
}
