# Brico-Merlin - Système de Gestion de Stock et Facturation

Ce projet est une application client-serveur pour la gestion de stock et de facturation de l'entreprise Brico-Merlin. Il permet de gérer les articles en stock, les ventes et la facturation dans différents magasins.

## Architecture du Système

Le système est composé de trois parties principales :

1. **Serveur Central** (`bricomerlin-central`)
   - Gère les prix des articles
   - Centralise les données de facturation
   - Met à jour les prix quotidiennement

2. **Serveur Magasin** (`bricomerlin-server`)
   - Gère le stock local
   - Traite les ventes
   - Communique avec le serveur central pour les prix

3. **Client** (`bricomerlin-client`)
   - Interface utilisateur pour les caisses
   - Permet la consultation du stock
   - Gère les ventes et factures

## Prérequis

- Java 8
- MySQL 8.0 ou supérieur
- Maven 3.6 ou supérieur

## Configuration de la Base de Données

1. Créer une base de données MySQL :
```sql
CREATE DATABASE bricomerlin;
```

2. Créer un utilisateur :
```sql
CREATE USER 'bricomerlin'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON bricomerlin.* TO 'bricomerlin'@'localhost';
FLUSH PRIVILEGES;
```

## Installation et Lancement

### 1. Compilation des Modules

```bash
# Compiler tous les modules
mvn clean install
```

### 2. Lancement du Serveur Central

```bash
# Se placer dans le dossier du serveur central
cd bricomerlin-central
mvn exec:java -Dexec.mainClass="fr.miage.bricomerlin.CentralServerLauncher"
```

Le serveur central démarre sur le port 1098.

### 3. Lancement du Serveur Magasin

```bash
# Se placer dans le dossier du serveur
cd bricomerlin-server
mvn exec:java -Dexec.mainClass="fr.miage.bricomerlin.server.BricoMerlinServer"
```

Le serveur magasin démarre sur le port 1099.

### 4. Lancement du Client

```bash
# Se placer dans le dossier du client
cd bricomerlin-client
mvn exec:java -Dexec.mainClass="fr.miage.bricomerlin.client.BricoMerlinClient"
```

## Fonctionnalités

### Gestion du Stock
- Consultation du stock d'un article
- Recherche d'articles par famille
- Ajout de produits au stock

### Gestion des Ventes
- Achat d'articles
- Génération de factures
- Paiement des factures
- Consultation des factures

### Administration
- Mise à jour des prix (serveur central)
- Calcul du chiffre d'affaires
- Sauvegarde des données

## Structure des Données

### Articles
- Référence (unique)
- Famille
- Prix unitaire
- Stock disponible

### Factures
- ID facture
- Date de facturation
- Total
- Mode de paiement
- Lignes de facture (articles achetés)

## Communication

Le système utilise RMI (Remote Method Invocation) pour la communication entre les composants :
- Client → Serveur Magasin : Port 1099
- Serveur Magasin → Serveur Central : Port 1098

## Fichiers de Configuration

- `config.properties` : Configuration des serveurs
- `articles.yml` : Liste des prix des articles
- `database.sql` : Script d'initialisation de la base de données 