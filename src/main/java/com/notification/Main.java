package com.notification;

import com.notification.modele.Employe;
import com.notification.service.ServiceNotification;
import com.notification.db.DatabaseConnection;
import com.notification.dao.Employes;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static ServiceNotification serviceNotification;
    private static Employes employes;
    private static Scanner scanner;
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        // Initialiser la base de données
        DatabaseConnection.initializeDatabase();
        
        serviceNotification = new ServiceNotification();
        employes = new Employes();
        scanner = new Scanner(System.in);

        // L'administrateur est déjà créé dans la base de données lors de l'initialisation
        Employe admin = new Employe("1", "Admin", ADMIN_EMAIL, ADMIN_PASSWORD);

        boolean continuer = true;
        while (continuer) {
            afficherMenuPrincipal();
            String choix = scanner.nextLine();

            traiterChoixMenuPrincipal(choix);
        }
        
        // Fermer la connexion à la base de données
        DatabaseConnection.closeConnection();
        scanner.close();
    }

    private static void traiterChoixMenuPrincipal(String choix) {
        switch (choix) {
            case "1":
                menuConnexionAdmin();
                break;
            case "2":
                menuConnexionEmploye();
                break;
            case "0":
                System.out.println("Au revoir!");
                System.exit(0);
                break;
            default:
                System.out.println("Choix invalide!");
                break;
        }
    }

    private static void menuConnexionAdmin() {
        System.out.println("\n=== Connexion Administrateur ===");
        System.out.println("Email:");
        String email = scanner.nextLine();
        System.out.println("Mot de passe:");
        String motDePasse = scanner.nextLine();

        if (serviceNotification.verifierAdmin(email, motDePasse)) {
            System.out.println("Connexion administrateur réussie!");
            menuAdmin(email);
        } else {
            System.out.println("Email ou mot de passe incorrect!");
        }
    }

    private static void menuConnexionEmploye() {
        System.out.println("\n=== Connexion Employé ===");
        System.out.println("Email:");
        String email = scanner.nextLine();
        System.out.println("Mot de passe:");
        String motDePasse = scanner.nextLine();

        Optional<Employe> employe = serviceNotification.connecterAbonne(email, motDePasse);
        if (employe.isPresent()) {
            System.out.println("Connexion réussie!");
            menuEmploye(employe.get());
        } else {
            System.out.println("Email ou mot de passe incorrect!");
        }
    }

    private static void menuEmploye(Employe employe) {
        String choix;
        do {
            afficherMenuEmploye();
            choix = scanner.nextLine();
            traiterChoixMenuEmploye(choix, employe);
        } while (!choix.equals("0"));
    }

    private static void menuAdmin(String email) {
        boolean continuer = true;
        while (continuer) {
            afficherMenuAdmin();
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
                case 1:
                    serviceNotification.afficherAbonnes();
                    break;
                case 2:
                    ajouterNouvelAbonne();
                    break;
                case 3:
                    retirerAbonne();
                    break;
                case 4:
                    System.out.println("Entrez votre message:");
                    String message = scanner.nextLine();
                    serviceNotification.notifierTous(message, email);
                    break;
                case 5:
                    verifierAbonnement();
                    break;
                case 6:
                    serviceNotification.afficherHistoriqueNotifications();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Option invalide");
            }
        }
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n=== Système de Notifications ===");
        System.out.println("1. Connexion Administrateur");
        System.out.println("2. Connexion Employé");
        System.out.println("0. Quitter");
        System.out.print("Votre choix: ");
    }

    private static void afficherMenuAdmin() {
        System.out.println("\n=== Espace Administrateur ===");
        System.out.println("1. Afficher la liste des employés");
        System.out.println("2. Ajouter un nouvel employé");
        System.out.println("3. Désabonner un employé");
        System.out.println("4. Envoyer un message");
        System.out.println("5. Vérifier si un employé est abonné");
        System.out.println("6. Afficher l'historique des notifications");
        System.out.println("0. Retour au menu principal");
        System.out.print("Votre choix: ");
    }

    private static void afficherMenuEmploye() {
        System.out.println("\n=== Menu Employé ===");
        System.out.println("1. Voir mes notifications");
        System.out.println("2. S'abonner aux notifications");
        System.out.println("3. Se désabonner des notifications");
        System.out.println("0. Déconnexion");
        System.out.print("Votre choix: ");
    }

    private static void ajouterNouvelAbonne() {
        System.out.println("\n=== Création d'un Nouvel Abonné ===");
        
        System.out.println("Entrez l'ID de l'employé:");
        String id = scanner.nextLine();
        
        System.out.println("Entrez le nom de l'employé:");
        String nom = scanner.nextLine();
        
        System.out.println("Entrez l'email de l'employé:");
        String email = scanner.nextLine();
        
        // Vérifier que l'email n'est pas celui de l'admin
        if (email.equals(ADMIN_EMAIL)) {
            System.out.println("Erreur : Cet email est réservé à l'administrateur.");
            return;
        }

        System.out.println("Entrez le mot de passe de l'employé:");
        String motDePasse = scanner.nextLine();
        
        if (serviceNotification.ajouterNouvelEmploye(id, nom, email, motDePasse)) {
            System.out.println("\nCompte créé et abonné avec succès!");
            System.out.println("L'employé peut maintenant se connecter avec son email et mot de passe.");
        } else {
            System.out.println("\nErreur lors de la création du compte. Vérifiez que l'ID et l'email sont uniques.");
        }
    }

    private static void retirerAbonne() {
        System.out.println("Entrez l'ID de l'employé à désabonner:");
        String id = scanner.nextLine();
        for (Employe emp : serviceNotification.getAbonnes().stream()
                .filter(e -> e.getId().equals(id))
                .map(e -> (Employe) e)
                .toList()) {
            serviceNotification.retirerAbonne(emp);
            break;
        }
    }

    private static void verifierAbonnement() {
        System.out.println("Entrez l'ID de l'employé:");
        String id = scanner.nextLine();
        System.out.println(serviceNotification.estAbonne(id) ? "L'employé est abonné" : "L'employé n'est pas abonné");
    }

    private static void traiterChoixMenuEmploye(String choix, Employe employe) {
        switch (choix) {
            case "1":
                serviceNotification.afficherNotificationsUtilisateur(employe.getId());
                break;
            case "2":
                if (serviceNotification.estAbonne(employe.getId())) {
                    System.out.println("Vous êtes déjà abonné.");
                } else {
                    serviceNotification.ajouterAbonne(employe);
                }
                break;
            case "3":
                if (serviceNotification.estAbonne(employe.getId())) {
                    serviceNotification.retirerAbonne(employe);
                } else {
                    System.out.println("Vous n'êtes pas abonné.");
                }
                break;
            case "0":
                System.out.println("Déconnexion...");
                break;
            default:
                System.out.println("Option invalide");
                break;
        }
    }
} 