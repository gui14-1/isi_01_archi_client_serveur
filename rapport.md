# Rapport de Développement - Système BricoMerlin

## Table des matières
1. [Introduction](#introduction)
2. [Architecture du système](#architecture-du-système)
3. [Technologies utilisées](#technologies-utilisées)
4. [Structure du projet](#structure-du-projet)
5. [Fonctionnalités implémentées](#fonctionnalités-implémentées)
6. [Base de données](#base-de-données)
7. [Communication RMI](#communication-rmi)
8. [Interface utilisateur](#interface-utilisateur)
9. [Conclusion](#conclusion)

---

## Introduction

Ce rapport présente le développement d'un système informatique de gestion de stock et de facturation pour l'entreprise **BricoMerlin**, spécialisée dans la vente d'articles de bricolage. Le système suit une architecture client-serveur distribuée et utilise **Java RMI** (Remote Method Invocation) comme middleware de communication.

L'objectif principal est de fournir un système permettant la gestion du stock d'articles, la facturation des ventes, et la synchronisation des données entre plusieurs magasins via un serveur central.

---

## Architecture du système

### Modèle client-serveur distribué

Le système adopte une architecture **multi-tiers** avec les composants suivants :

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Serveur Central│    │ Serveur Magasin │    │   Clients POS   │
│    (Siège)      │◄──►│    (Local)      │◄──►│  (Caisses)     │
│                 │    │                 │    │                 │
│ - Gestion prix  │    │ - Stock local   │    │ - Interface UI  │
│ - Synchronisation│    │ - Facturation   │    │ - Opérations    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Composants principaux

1. **Serveur Central** (`bricomerlin-central`) : Gestion centralisée des prix
2. **Serveur Local** (`bricomerlin-server`) : Gestion du stock et facturation par magasin
3. **Clients** (`bricomerlin-client`) : Interfaces utilisateur (caisses, points info)
4. **Composants partagés** (`bricomerlin-common`) : DTOs, interfaces RMI, exceptions

---

## Technologies utilisées

| Technologie | Usage | Justification |
|------------|--------|---------------|
| **Java 8** | Langage principal | Portabilité, robustesse, support RMI natif |
| **Java RMI** | Middleware communication | Communication transparente objets distants |
| **MySQL** | SGBD | Persistance des données, transactions ACID |
| **JDBC** | Accès aux données | API standard Java pour BDD relationnelles |
| **Maven** | Gestion de projet | Build automation, gestion dépendances |

---

## Structure du projet

### Architecture modulaire Maven

```
isi_01_archi_client_serveur/
├── bricomerlin-common/         # Interfaces et DTOs partagés
│   └── src/main/java/fr/miage/bricomerlin/
│       ├── common/dto/         # Data Transfer Objects
│       ├── common/exception/   # Exceptions métier
│       └── service/           # Interfaces RMI
├── bricomerlin-server/        # Serveur magasin local
│   └── src/main/java/fr/miage/bricomerlin/
│       ├── dao/              # Data Access Objects
│       ├── model/            # Entités métier
│       ├── service/          # Implémentation services
│       └── unit/             # Utilitaires (DB connection)
├── bricomerlin-client/       # Applications clientes
│   └── src/main/java/fr/miage/bricomerlin/
│       ├── client/           # Client RMI
│       └── ui/               # Interfaces utilisateur
└── bricomerlin-central/      # Serveur central prix
    └── src/main/java/fr/miage/bricomerlin/
        └── service/          # Service prix centralisé
```

### Pattern DAO (Data Access Object)

Implementation du pattern DAO pour séparer la logique métier de l'accès aux données :

```java
public class ArticleDAO {
    // Opérations CRUD sur les articles
    public Article getArticleByReference(String reference) { ... }
    public boolean updateStock(String reference, int newStock) { ... }
    public List<String> getArticlesByFamilleWithStock(String famille) { ... }
}
```

---

## Fonctionnalités implémentées

### 1. Gestion du stock

- **Consultation d'articles** : Récupération des informations (stock, prix, famille)
- **Recherche par famille** : Listage des articles d'une famille avec stock > 0
- **Mise à jour stock** : Ajout/soustraction automatique lors des ventes

### 2. Facturation

- **Création de factures** : Génération automatique lors d'achats
- **Gestion ligne de facture** : Ajout d'articles avec quantités
- **Calcul totaux** : Recalcul automatique des montants
- **Paiement** : Enregistrement du mode de paiement

### 3. Synchronisation prix

Le serveur central met à jour les prix quotidiennement :

```java
private void updatePrixFromCentralServer() {
    Registry registry = LocateRegistry.getRegistry(CENTRAL_HOST, CENTRAL_PORT);
    PrixService prixService = (PrixService) registry.lookup(CENTRAL_SERVICE);
    Map<String, Double> prixArticles = prixService.getPrixArticles();
    // Mise à jour en base locale
}
```

### 4. Interfaces utilisateur spécialisées

- **MenuCaissier** : Gestion des ventes et paiements
- **MenuDirecteur** : Consultation chiffre d'affaires
- **MenuMagasinier** : Gestion des stocks

### 5. Génération de tickets de caisse

**Problématique** : Aucune matérialisation physique des factures payées.

**Solution implémentée** :

```java
private void genererTicketCaisse(Facture facture) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String nomFichier = "ticket_facture_" + facture.getIdFacture() + "_" + dateFormat.format(new Date()) + ".txt";
    
    FileWriter writer = new FileWriter(nomFichier);
    // Génération du contenu formaté du ticket
    writer.write("=======================================\n");
    writer.write("           BRICO-MERLIN\n");
    writer.write("         TICKET DE CAISSE\n");
    // ... détails facture et articles
}
```

**Avantages** :
- Simulation de l'impression physique
- Traçabilité des transactions
- Format lisible pour archivage

---

## Base de données

### Modèle relationnel

```sql
-- Table des articles
CREATE TABLE Article (
    reference VARCHAR(50) PRIMARY KEY,
    famille VARCHAR(100) NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL
);

-- Table des factures
CREATE TABLE Facture (
    id_facture INT AUTO_INCREMENT PRIMARY KEY,
    date_facturation DATE NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    mode_paiement VARCHAR(50)
);

-- Table des lignes de facture
CREATE TABLE LigneFacture (
    id_ligne INT AUTO_INCREMENT PRIMARY KEY,
    id_facture INT NOT NULL,
    reference_article VARCHAR(50) NOT NULL,
    quantite INT NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_facture) REFERENCES Facture(id_facture),
    FOREIGN KEY (reference_article) REFERENCES Article(reference)
);
```

### Singleton de connexion

```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
```

---

## Communication RMI

### Interface de service

```java
public interface BricoMerlinService extends Remote {
    ArticleDTO consulterStock(String reference) throws RemoteException, ArticleInexistantException;
    List<String> rechercherArticles(String famille) throws RemoteException;
    int acheterArticle(String reference, int quantite, int idFacture) throws RemoteException;
    boolean payerFacture(int idFacture, String modePaiement) throws RemoteException;
    // ... autres méthodes
}
```

### Gestion des exceptions métier

```java
public class ArticleInexistantException extends Exception {
    public ArticleInexistantException(String reference) {
        super("Article inexistant: " + reference);
    }
}

public class StockInsuffisantException extends Exception {
    public StockInsuffisantException(String reference, int stockDisponible, int quantiteDemandee) {
        super("Stock insuffisant pour " + reference + 
              ": " + stockDisponible + " disponible, " + quantiteDemandee + " demandé");
    }
}
```

---

## Interface utilisateur

### Menu contextuel par rôle

L'interface s'adapte selon le type d'utilisateur :

```java
public class MenuCaissier {
    public void afficher() {
        System.out.println("===== MODE CAISSIER =====");
        System.out.println("1. Consulter un article");
        System.out.println("2. Rechercher des articles par famille");
        System.out.println("3. Ajouter un article à la facture");
        System.out.println("4. Consulter la facture en cours");
        System.out.println("5. Payer la facture");
        System.out.println("6. Charger une facture non payée");
        System.out.println("7. Nouvelle facture");
        System.out.println("8. Retour au menu principal");
    }
}
```

### Gestion d'état des sessions

- **Facture en cours** : Maintien de l'ID de facture active
- **Validation des saisies** : Contrôle des formats et valeurs
- **Feedback utilisateur** : Messages colorés selon le contexte

---

## Conclusion

### Objectifs atteints

✅ **Architecture distribuée** : Système client-serveur avec RMI  
✅ **Gestion persistante** : Base de données MySQL avec transactions  
✅ **Fonctionnalités métier** : Toutes les opérations demandées implémentées  
✅ **Synchronisation prix** : Communication avec serveur central  
✅ **Interfaces spécialisées** : Menus adaptés selon les rôles utilisateur  
✅ **Tickets de caisse** : Génération automatique de fichiers .txt  
✅ **Expérience utilisateur** : Interface console améliorée avec couleurs  

### Perspectives d'évolution

- **Interface graphique** : Migration vers JavaFX ou Swing
- **Sécurité** : Authentification et autorisation par rôles
- **Monitoring** : Logs structurés et métriques de performance
- **Tests unitaires** : Couverture JUnit pour validation automatisée 