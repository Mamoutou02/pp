package com.notification.service;

import com.notification.interfaces.IObservateur;
import com.notification.interfaces.IServiceNotification;
import com.notification.modele.Employe;
import com.notification.dao.EmployeDAO;
import com.notification.dao.NotificationDAO;
import com.notification.dao.AbonnementDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe implémentant le service de notification avec persistance MySQL
 */
public class ServiceNotification implements IServiceNotification {
    private final EmployeDAO employeDAO;
    private final NotificationDAO notificationDAO;
    private final AbonnementDAO abonnementDAO;

    public ServiceNotification() {
        this.employeDAO = new EmployeDAO();
        this.notificationDAO = new NotificationDAO();
        this.abonnementDAO = new AbonnementDAO();
    }

    public boolean ajouterNouvelEmploye(String id, String nom, String email, String motDePasse) {
        try {
            // Vérifier si l'employé existe déjà
            if (employeDAO.existeParId(id) || employeDAO.existeParEmail(email)) {
                return false;
            }

            // Créer et sauvegarder l'employé
            Employe nouvelEmploye = new Employe(id, nom, email, motDePasse);
            if (employeDAO.sauvegarder(nouvelEmploye)) {
                // Ajouter automatiquement aux abonnements
                abonnementDAO.abonner(id);
                System.out.println(String.format("%s a été ajouté et abonné au service de notifications", nom));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'employé: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void ajouterAbonne(IObservateur observateur) {
        try {
            if (observateur instanceof Employe employe) {
                // Vérifier si l'employé n'est pas un administrateur
                if (employeDAO.verifierAdmin(employe.getEmail(), employe.getMotDePasse())) {
                    System.out.println("Un administrateur ne peut pas être abonné au service");
                    return;
                }
                
                // Vérifier si l'employé existe dans la base de données
                if (!employeDAO.existeParId(employe.getId())) {
                    System.out.println("L'employé n'existe pas dans la base de données");
                    return;
                }

                if (!abonnementDAO.estAbonne(employe.getId())) {
                    abonnementDAO.abonner(employe.getId());
                    System.out.println(String.format("%s s'est abonné au service de notifications", employe.getNom()));
                } else {
                    System.out.println(String.format("%s est déjà abonné au service", employe.getNom()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'abonnement: " + e.getMessage());
        }
    }

    @Override
    public void retirerAbonne(IObservateur observateur) {
        try {
            if (observateur instanceof Employe employe) {
                if (abonnementDAO.estAbonne(employe.getId())) {
                    abonnementDAO.desabonner(employe.getId());
                    System.out.println(String.format("%s s'est désabonné du service de notifications", employe.getNom()));
                } else {
                    System.out.println(String.format("%s n'est pas abonné au service", employe.getNom()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du désabonnement: " + e.getMessage());
        }
    }

    @Override
    public void notifierTous(String message, String expediteur) {
        try {
            // Récupérer la liste des abonnés une seule fois
            List<String> abonnesIds = abonnementDAO.getAbonnesIds();
            
            // Si aucun abonné, afficher un message et sortir
            if (abonnesIds.isEmpty()) {
                System.out.println("Aucun abonné ne recevra ce message.");
                return;
            }

            // Récupérer le nom de l'expéditeur
            String nomExpediteur = employeDAO.trouverParId(expediteur)
                .map(Employe::getNom)
                .orElse("Système");

            // Compter combien d'abonnés vont recevoir le message
            int nombreDestinataires = 0;
            
            // Envoyer la notification à chaque abonné
            for (String abonneId : abonnesIds) {
                // Ne pas envoyer à l'expéditeur
                if (!abonneId.equals(expediteur)) {
                    notificationDAO.sauvegarderNotification(message, expediteur, abonneId);
                    Optional<Employe> employe = employeDAO.trouverParId(abonneId);
                    if (employe.isPresent()) {
                        Employe destinataire = employe.get();
                        destinataire.recevoirNotification(message, expediteur);
                        
                        // Envoyer l'email
                        String sujet = "Nouvelle notification de " + nomExpediteur;
                        String contenuEmail = String.format("""
                            Bonjour %s,
                            
                            Vous avez reçu une nouvelle notification de %s :
                            
                            %s
                            
                            Cordialement,
                            Le système de notifications
                            """, destinataire.getNom(), nomExpediteur, message);
                        
                        EmailService.envoyerEmail(destinataire.getEmail(), sujet, contenuEmail);
                        nombreDestinataires++;
                    }
                }
            }

            // Afficher un résumé de l'envoi
            System.out.println("\nRésumé de l'envoi :");
            System.out.println("- Message : " + message);
            System.out.println("- Nombre de destinataires : " + nombreDestinataires);
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'envoi des notifications: " + e.getMessage());
        }
    }

    @Override
    public boolean estAbonne(String id) {
        try {
            return abonnementDAO.estAbonne(id);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'abonnement: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<IObservateur> getAbonnes() {
        List<IObservateur> abonnes = new ArrayList<>();
        try {
            List<String> abonnesIds = abonnementDAO.getAbonnesIds();
            for (String id : abonnesIds) {
                employeDAO.trouverParId(id).ifPresent(abonnes::add);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des abonnés: " + e.getMessage());
        }
        return abonnes;
    }

    public Optional<Employe> connecterAbonne(String email, String motDePasse) {
        try {
            if (employeDAO.verifierIdentifiants(email, motDePasse)) {
                return employeDAO.trouverParEmail(email);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean verifierAdmin(String email, String motDePasse) {
        try {
            return employeDAO.verifierAdmin(email, motDePasse);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification admin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Afficher la liste des abonnés
     */
    public void afficherAbonnes() {
        System.out.println("\nListe des abonnés:");
        List<IObservateur> abonnes = getAbonnes();
        if (abonnes.isEmpty()) {
            System.out.println("Aucun abonné");
        } else {
            for (IObservateur abonne : abonnes) {
                System.out.println("- " + abonne.getNom());
            }
        }
    }

    public void afficherNotificationsUtilisateur(String employeId) {
        try {
            List<String> notifications = notificationDAO.getNotificationsUtilisateur(employeId);
            Optional<Employe> employe = employeDAO.trouverParId(employeId);
            
            if (employe.isPresent()) {
                System.out.println("\nNotifications reçues par " + employe.get().getNom() + ":");
                if (notifications.isEmpty()) {
                    System.out.println("Aucune notification reçue");
                } else {
                    for (String notification : notifications) {
                        System.out.println("- " + notification);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des notifications: " + e.getMessage());
        }
    }
} 