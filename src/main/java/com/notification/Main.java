package com.notification;

import com.notification.modele.Employe;
import com.notification.service.ServiceNotification;
import com.notification.db.DatabaseConnection;
import com.notification.dao.EmployeDAO;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static ServiceNotification service;
    private static EmployeDAO employeDAO;
    private static Scanner scanner;
    private static final String ADMIN_EMAIL = "admin@entreprise.com";
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        // Initialiser la base de données
        DatabaseConnection.initializeDatabase();
        
        service = new ServiceNotification();
        employeDAO = new EmployeDAO();
        scanner = new Scanner(System.in);

        // L'administrateur est déjà créé dans la base de données lors de l'initialisation
        Employe admin = new Employe("1", "Admin", ADMIN_EMAIL, ADMIN_PASSWORD);

        boolean continuer = true;
        while (continuer) {
            afficherMenuPrincipal();
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
                case 1:
                    menuConnexionAdmin(admin);
                    break;
                case 2:
                    menuConnexionAbonne();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Option invalide");
            }
        }
        
        // Fermer la connexion à la base de données
        DatabaseConnection.closeConnection();
        scanner.close();
    }

    private static void menuConnexionAdmin(Employe admin) {
        System.out.println("\n=== Connexion Administrateur ===");
        System.out.println("Email:");
        String email = scanner.nextLine();
        System.out.println("Mot de passe:");
        String motDePasse = scanner.nextLine();

        if (service.verifierAdmin(email, motDePasse)) {
            System.out.println("Connexion administrateur réussie!");
            menuAdmin(admin);
        } else {
            System.out.println("Email ou mot de passe administrateur incorrect");
        }
    }

    private static void menuAdmin(Employe admin) {
        boolean continuer = true;
        while (continuer) {
            afficherMenuAdmin();
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
                case 1:
                    service.afficherAbonnes();
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
                    service.notifierTous(message, admin.getId());
                    break;
                case 5:
                    verifierAbonnement();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Option invalide");
            }
        }
    }

    private static void menuConnexionAbonne() {
        System.out.println("\n=== Connexion Abonné ===");
        System.out.println("Email:");
        String email = scanner.nextLine();
        System.out.println("Mot de passe:");
        String motDePasse = scanner.nextLine();

        Optional<Employe> employe = service.connecterAbonne(email, motDePasse);
        if (employe.isPresent()) {
            System.out.println("Connexion réussie!");
            menuAbonne(employe.get());
        } else {
            System.out.println("Email ou mot de passe incorrect");
            System.out.println("Veuillez contacter l'administrateur pour créer un compte.");
        }
    }

    private static void menuAbonne(Employe employe) {
        boolean continuer = true;
        while (continuer) {
            afficherMenuAbonne();
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
                case 1:
                    service.afficherNotificationsUtilisateur(employe.getId());
                    break;
                case 2:
                    if (service.estAbonne(employe.getId())) {
                        System.out.println("Vous êtes déjà abonné.");
                    } else {
                        service.ajouterAbonne(employe);
                    }
                    break;
                case 3:
                    if (service.estAbonne(employe.getId())) {
                        service.retirerAbonne(employe);
                    } else {
                        System.out.println("Vous n'êtes pas abonné.");
                    }
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Option invalide");
            }
        }
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
        
        if (service.ajouterNouvelEmploye(id, nom, email, motDePasse)) {
            System.out.println("\nCompte créé et abonné avec succès!");
            System.out.println("L'employé peut maintenant se connecter avec son email et mot de passe.");
        } else {
            System.out.println("\nErreur lors de la création du compte. Vérifiez que l'ID et l'email sont uniques.");
        }
    }

    private static void retirerAbonne() {
        System.out.println("Entrez l'ID de l'employé à désabonner:");
        String id = scanner.nextLine();
        for (Employe emp : service.getAbonnes().stream()
                .filter(e -> e.getId().equals(id))
                .map(e -> (Employe) e)
                .toList()) {
            service.retirerAbonne(emp);
            break;
        }
    }

    private static void verifierAbonnement() {
        System.out.println("Entrez l'ID de l'employé:");
        String id = scanner.nextLine();
        System.out.println(service.estAbonne(id) ? "L'employé est abonné" : "L'employé n'est pas abonné");
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n=== Système de Notifications ===");
        System.out.println("1. Connexion Administrateur");
        System.out.println("2. Connexion Abonné");
        System.out.println("0. Quitter");
        System.out.print("Votre choix: ");
    }

    private static void afficherMenuAdmin() {
        System.out.println("\n=== Espace Administrateur ===");
        System.out.println("1. Afficher la liste des abonnés");
        System.out.println("2. Ajouter un nouvel abonné");
        System.out.println("3. Désabonner un employé");
        System.out.println("4. Envoyer un message");
        System.out.println("5. Vérifier si un employé est abonné");
        System.out.println("0. Retour au menu principal");
        System.out.print("Votre choix: ");
    }

    private static void afficherMenuAbonne() {
        System.out.println("\n=== Espace Abonné ===");
        System.out.println("1. Voir mes notifications");
        System.out.println("2. S'abonner au service");
        System.out.println("3. Se désabonner du service");
        System.out.println("0. Déconnexion");
        System.out.print("Votre choix: ");
    }
} 