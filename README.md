# Système de Notifications d'Entreprise

Ce projet est un système de notifications d'entreprise développé en Java avec une base de données MySQL.

## Prérequis

- Java 17 ou supérieur
- MySQL 8.0 ou supérieur
- Maven

## Configuration de la base de données

1. Assurez-vous que MySQL est installé et en cours d'exécution sur votre machine
2. Connectez-vous à MySQL en tant qu'utilisateur root :
   ```bash
   mysql -u root
   ```
   Si vous avez un mot de passe :
   ```bash
   mysql -u root -p
   ```

3. Exécutez le script d'initialisation de la base de données :
   ```bash
   mysql -u root < src/main/resources/init_database.sql
   ```
   Ou si vous avez un mot de passe :
   ```bash
   mysql -u root -p < src/main/resources/init_database.sql
   ```

## Configuration de l'application

Si votre configuration MySQL est différente, modifiez les informations de connexion dans le fichier `src/main/java/com/notification/db/DatabaseConnection.java` :

```java
private static final String URL = "jdbc:mysql://localhost:3306/notification_system";
private static final String USER = "root";
private static final String PASSWORD = ""; // Mettez votre mot de passe ici si nécessaire
```

## Compilation et exécution

1. Compiler le projet :
   ```bash
   mvn clean compile
   ```

2. Exécuter l'application :
   ```bash
   mvn exec:java
   ```

## Utilisation

### Connexion administrateur
- Email : admin@entreprise.com
- Mot de passe : admin123

### Fonctionnalités

1. Espace Administrateur :
   - Gestion des abonnés
   - Envoi de notifications
   - Consultation de la liste des abonnés

2. Espace Abonné :
   - Consultation des notifications
   - Gestion de l'abonnement

## Structure de la base de données

### Table `employes`
- `id` : Identifiant unique de l'employé
- `nom` : Nom de l'employé
- `email` : Email unique de l'employé
- `mot_de_passe` : Mot de passe de l'employé
- `is_admin` : Indique si l'employé est administrateur

### Table `notifications`
- `id` : Identifiant unique de la notification
- `message` : Contenu de la notification
- `expediteur_id` : ID de l'expéditeur
- `destinataire_id` : ID du destinataire
- `date_envoi` : Date et heure d'envoi

### Table `abonnements`
- `employe_id` : ID de l'employé abonné
- `date_abonnement` : Date et heure de l'abonnement 