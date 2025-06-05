package fr.miage.bricomerlin.client;

import fr.miage.bricomerlin.service.BricoMerlinService;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

/**
 * Classe client pour se connecter au service RMI BricoMerlin
 */
public class BricoMerlinClient {
    private String host;
    private int port;
    private String serviceName;
    private BricoMerlinService service;

    /**
     * Constructeur du client
     * @throws IOException si erreur lors du chargement de la configuration
     * @throws NotBoundException si le service RMI n'est pas disponible
     * @throws RemoteException si erreur RMI
     */
    public BricoMerlinClient() throws IOException, NotBoundException, RemoteException {
        loadConfig();
        connectToService();
    }

    /**
     * Charge la configuration depuis le fichier properties
     * @throws IOException si erreur lors du chargement
     */
    private void loadConfig() throws IOException {
        Properties prop = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

        if (input == null) {
            throw new IOException("Fichier config.properties introuvable");
        }

        prop.load(input);

        host = prop.getProperty("server.host", "localhost");
        port = Integer.parseInt(prop.getProperty("server.port", "1099"));
        serviceName = prop.getProperty("server.name", "BricoMerlinService");

        input.close();
    }

    /**
     * Se connecte au service RMI
     * @throws RemoteException si erreur RMI
     * @throws NotBoundException si le service n'est pas disponible
     */
    private void connectToService() throws RemoteException, NotBoundException {
        // Connexion au registry RMI
        Registry registry = LocateRegistry.getRegistry(host, port);

        // Récupération du service
        service = (BricoMerlinService) registry.lookup(serviceName);

        System.out.println("Connexion établie avec le service " + serviceName);
    }

    /**
     * Retourne le service RMI
     * @return le service BricoMerlin
     */
    public BricoMerlinService getService() {
        return service;
    }
}
