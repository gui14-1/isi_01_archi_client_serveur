CREATE DATABASE IF NOT EXISTS bricomerlin;
USE bricomerlin;

-- Création des tables
CREATE TABLE IF NOT EXISTS Article (
                                       reference VARCHAR(20) PRIMARY KEY,
    famille VARCHAR(50) NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL
    );

CREATE TABLE IF NOT EXISTS Facture (
                                       id_facture INT AUTO_INCREMENT PRIMARY KEY,
                                       date_facturation DATE NOT NULL,
                                       total DECIMAL(10,2) NOT NULL,
    mode_paiement VARCHAR(20)
    );

CREATE TABLE IF NOT EXISTS LigneFacture (
                                            id_ligne INT AUTO_INCREMENT PRIMARY KEY,
                                            id_facture INT,
                                            reference_article VARCHAR(20),
    quantite INT NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_facture) REFERENCES Facture(id_facture) ON DELETE CASCADE,
    FOREIGN KEY (reference_article) REFERENCES Article(reference) ON DELETE CASCADE
    );

-- Insertion de données de test
INSERT INTO Article (reference, famille, prix_unitaire, stock) VALUES
                                                                   ('REF001', 'Outillage', 12.99, 50),
                                                                   ('REF002', 'Outillage', 24.50, 30),
                                                                   ('REF003', 'Jardinage', 9.99, 100),
                                                                   ('REF004', 'Quincaillerie', 2.50, 200),
                                                                   ('REF005', 'Peinture', 19.99, 40),
                                                                   ('REF006', 'Peinture', 29.99, 25),
                                                                   ('REF007', 'Outillage', 54.99, 15),
                                                                   ('REF008', 'Jardinage', 15.50, 70),
                                                                   ('REF009', 'Quincaillerie', 1.75, 300),
                                                                   ('REF010', 'Outillage', 89.99, 10);

-- Création d'un utilisateur pour l'application
CREATE USER IF NOT EXISTS 'bricomerlin'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON bricomerlin.* TO 'bricomerlin'@'localhost';
FLUSH PRIVILEGES;