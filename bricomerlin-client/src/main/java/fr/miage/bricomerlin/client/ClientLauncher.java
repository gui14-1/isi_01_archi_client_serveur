package fr.miage.bricomerlin.client;

import fr.miage.bricomerlin.ui.BricoMerlinUI;

/**
 * Classe de lancement du client BricoMerlin
 */
public class ClientLauncher {
    public static void main(String[] args) {
        try {
            // Désactiver le SecurityManager
            System.setSecurityManager(null);

            BricoMerlinClient client = new BricoMerlinClient();
            BricoMerlinUI ui = new BricoMerlinUI(client);
            ui.start();
        } catch (Exception e) {
            System.err.println("Erreur lors du lancement du client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
