package fr.miage.bricomerlin.service;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Implémentation du service de gestion des prix
 */
public class PrixServiceImpl extends UnicastRemoteObject implements PrixService {
    private static final String PRICES_FILE = "prices.yml";
    private Map<String, Double> prixArticles;

    public PrixServiceImpl() throws RemoteException {
        super();
        loadPrixArticles();
    }

    @Override
    public Map<String, Double> getPrixArticles() throws RemoteException {
        return new HashMap<>(prixArticles);
    }

    @Override
    public void updatePrixArticle(String reference, double prix) throws RemoteException {
        prixArticles.put(reference, prix);
        savePrixArticles();
    }

    /**
     * Charge les prix depuis le fichier YAML
     */
    @SuppressWarnings("unchecked")
    private void loadPrixArticles() {
        File file = new File(PRICES_FILE);
        if (!file.exists()) {
            prixArticles = new HashMap<>();
            savePrixArticles();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Yaml yaml = new Yaml();
            prixArticles = yaml.load(reader);
            if (prixArticles == null) {
                prixArticles = new HashMap<>();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des prix: " + e.getMessage());
            prixArticles = new HashMap<>();
        }
    }

    /**
     * Sauvegarde les prix dans le fichier YAML
     */
    private void savePrixArticles() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(PRICES_FILE)) {
            yaml.dump(prixArticles, writer);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des prix: " + e.getMessage());
        }
    }
}