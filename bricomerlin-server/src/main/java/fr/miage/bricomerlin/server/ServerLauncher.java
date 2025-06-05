package fr.miage.bricomerlin.server;

/**
 * Classe principale pour lancer le serveur
 */
public class ServerLauncher {
    public static void main(String[] args) {
        try {
            // Désactiver le SecurityManager
            System.setSecurityManager(null);

            // Créer et démarrer le serveur
            BricoMerlinServer server = new BricoMerlinServer();
            server.start();

            // Ajouter un hook d'arrêt pour nettoyer les ressources
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Arrêt du serveur...");
                server.stop();
            }));

            System.out.println("Serveur démarré avec succès.");

        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}