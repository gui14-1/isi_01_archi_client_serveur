package fr.miage.bricomerlin;

import fr.miage.bricomerlin.service.PrixService;
import fr.miage.bricomerlin.service.PrixServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe principale pour lancer le serveur central
 */
public class CentralServerLauncher {
    private static final int PORT = 1098;
    private static final String SERVICE_NAME = "PrixService";

    public static void main(String[] args) {
        try {
            // Désactiver le SecurityManager
            System.setSecurityManager(null);

            // Créer le service
            PrixService prixService = new PrixServiceImpl();

            // Créer ou récupérer le registry RMI
            Registry registry = LocateRegistry.createRegistry(PORT);
            System.out.println("Registry RMI créé sur le port " + PORT);

            // Enregistrer le service
            registry.rebind(SERVICE_NAME, prixService);
            System.out.println("Service " + SERVICE_NAME + " enregistré");

            System.out.println("Serveur central démarré et prêt à recevoir des requêtes...");

        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du serveur central: " + e.getMessage());
            e.printStackTrace();
        }
    }
}