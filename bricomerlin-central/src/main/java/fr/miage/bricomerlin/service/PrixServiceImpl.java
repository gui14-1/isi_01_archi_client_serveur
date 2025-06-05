package fr.miage.bricomerlin.service;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Implémentation du service de gestion des prix
 */
public class PrixServiceImpl extends UnicastRemoteObject implements PrixService {
    private Map<String, Double> prixArticles;
    private final String pricesFile;

    public PrixServiceImpl() throws RemoteException {
        super();
        System.out.println("Initialisation du service de prix...");
        this.pricesFile = loadConfig();
        loadPrixArticles();
        System.out.println("Service de prix initialisé avec " + prixArticles.size() + " articles");
    }

    private String loadConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Fichier config.properties non trouvé");
                return "articles.yml";
            }
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("prices.file", "articles.yml");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la configuration: " + e.getMessage());
            return "articles.yml";
        }
    }

    @Override
    public Map<String, Double> getPrixArticles() throws RemoteException {
        System.out.println("Réception d'une demande de récupération des prix");
        System.out.println("Nombre d'articles disponibles : " + prixArticles.size());
        return new HashMap<>(prixArticles);
    }

    @Override
    public void updatePrixArticle(String reference, double prix) throws RemoteException {
        System.out.println("Mise à jour du prix pour l'article " + reference + " : " + prix + "€");
        prixArticles.put(reference, prix);
        savePrixArticles();
        System.out.println("Prix mis à jour avec succès");
    }

    /**
     * Charge les prix depuis le fichier YAML
     */
    @SuppressWarnings("unchecked")
    private void loadPrixArticles() {
        System.out.println("Tentative de chargement des prix depuis les ressources...");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(pricesFile)) {
            if (inputStream != null) {
                System.out.println("Fichier trouvé dans les ressources");
                loadFromInputStream(inputStream);
                return;
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture depuis les ressources: " + e.getMessage());
        }

        System.out.println("Tentative de chargement depuis le système de fichiers...");
        File file = new File(pricesFile);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                loadFromReader(reader);
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement des prix: " + e.getMessage());
                e.printStackTrace();
                prixArticles = new HashMap<>();
            }
        } else {
            System.out.println("Le fichier n'existe pas, création d'une nouvelle liste de prix");
            prixArticles = new HashMap<>();
            savePrixArticles();
        }
    }

    private void loadFromInputStream(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            loadFromReader(reader);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement depuis l'InputStream: " + e.getMessage());
            e.printStackTrace();
            prixArticles = new HashMap<>();
        }
    }

    private void loadFromReader(Reader reader) {
        Yaml yaml = new Yaml();
        Object data = yaml.load(reader);
        System.out.println("Données YAML brutes : " + data);
        
        if (data instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) data;
            if (map.containsKey("prix_articles")) {
                Object prixArticlesObj = map.get("prix_articles");
                if (prixArticlesObj instanceof Map) {
                    Map<String, Object> prixMap = (Map<String, Object>) prixArticlesObj;
                    prixArticles = new HashMap<>();
                    for (Map.Entry<String, Object> entry : prixMap.entrySet()) {
                        if (entry.getValue() instanceof Number) {
                            prixArticles.put(entry.getKey(), ((Number) entry.getValue()).doubleValue());
                        }
                    }
                    System.out.println("Chargement réussi de " + prixArticles.size() + " articles");
                    prixArticles.forEach((ref, prix) -> 
                        System.out.println("Article " + ref + " : " + prix + "€")
                    );
                } else {
                    System.err.println("La structure 'prix_articles' n'est pas une map");
                    prixArticles = new HashMap<>();
                }
            } else {
                System.err.println("Clé 'prix_articles' non trouvée dans le fichier YAML");
                prixArticles = new HashMap<>();
            }
        } else {
            System.err.println("Le fichier YAML ne contient pas une structure de type Map");
            prixArticles = new HashMap<>();
        }
    }

    /**
     * Sauvegarde les prix dans le fichier YAML
     */
    private void savePrixArticles() {
        System.out.println("Sauvegarde des prix dans le fichier : " + pricesFile);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(pricesFile)) {
            Map<String, Object> data = new HashMap<>();
            data.put("prix_articles", prixArticles);
            yaml.dump(data, writer);
            System.out.println("Sauvegarde réussie de " + prixArticles.size() + " articles");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des prix: " + e.getMessage());
            e.printStackTrace();
        }
    }
}