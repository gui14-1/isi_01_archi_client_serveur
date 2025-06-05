package fr.miage.bricomerlin.server;

import fr.miage.bricomerlin.service.BricoMerlinService;
import fr.miage.bricomerlin.service.BricoMerlinServiceImpl;
import fr.miage.bricomerlin.unit.DatabaseConnection;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

/**
 * Classe principale du serveur Brico-Merlin
 */
public class BricoMerlinServer {
    private int port;
    private String serviceName;
    private BricoMerlinServiceImpl serviceImpl;
    private Registry registry;

    /**
     * Constructeur du serveur
     */
    public BricoMerlinServer() {
        try {
            loadConfig();
            serviceImpl = new BricoMerlinServiceImpl();

            // Création ou récupération du registry RMI
            try {
                registry = LocateRegistry.createRegistry(port);
                System.out.println("Registry RMI créé sur le port " + port);
            } catch (RemoteException e) {
                System.out.println("Registry RMI existe déjà sur le port " + port);
                registry = LocateRegistry.getRegistry(port);
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de l'initialisation du serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge la configuration depuis le fichier properties
     */
    private void loadConfig() throws IOException {
        Properties prop = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

        if (input == null) {
            throw new IOException("Fichier config.properties introuvable");
        }

        prop.load(input);

        port = Integer.parseInt(prop.getProperty("server.port", "1099"));
        serviceName = prop.getProperty("server.name", "BricoMerlinService");

        input.close();

        System.out.println("Configuration chargée: port=" + port + ", nom du service=" + serviceName);
    }

    /**
     * Démarre le serveur
     */
    public void start() {
        try {
            // Export de l'objet distant
            BricoMerlinService serviceStub = (BricoMerlinService) UnicastRemoteObject.exportObject(serviceImpl, 0);

            // Enregistrement dans le registry
            registry.rebind(serviceName, serviceStub);

            System.out.println("Serveur Brico-Merlin démarré");
            System.out.println("Service enregistré sous le nom: " + serviceName);
            System.out.println("En attente des connexions clients...");

        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Arrête le serveur
     */
    public void stop() {
        try {
            // Désenregistrement du service
            registry.unbind(serviceName);
            System.out.println("Service désenregistré");

            // Fermer la connexion à la base de données
            DatabaseConnection.getInstance().close();

            System.out.println("Serveur arrêté");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'arrêt du serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}