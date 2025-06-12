package com.notification.service;

import com.notification.interfaces.INotificationReceiver;
import com.notification.interfaces.IServiceNotification;
import com.notification.modele.Employe;
import com.notification.dao.Employes;
import com.notification.dao.Notifications;
import com.notification.dao.Abonnements;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe implémentant le service de notification avec persistance MySQL
 */
public class ServiceNotification implements IServiceNotification {
    private final Employes employes;
    private final Notifications notifications;
    private final Abonnements abonnements;

    public ServiceNotification() {
        this.employes = new Employes();
        this.notifications = new Notifications();
        this.abonnements = new Abonnements();
    }

    public boolean ajouterNouvelEmploye(String id, String nom, String email, String motDePasse) {
        try {
            // Vérifier si l'employé existe déjà
            if (employes.existeParId(id) || employes.existeParEmail(email)) {
                return false;
            }

            // Créer et sauvegarder l'employé
            Employe nouvelEmploye = new Employe(id, nom, email, motDePasse);
            if (employes.sauvegarder(nouvelEmploye)) {
                // Ajouter automatiquement aux abonnements
                abonnements.abonner(id);
                System.out.println(String.format("%s a été ajouté et abonné au service de notifications", nom));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'employé: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void ajouterAbonne(INotificationReceiver administrateur) {
        try {
            if (administrateur instanceof Employe employe) {
                // Vérifier si l'employé n'est pas un administrateur
                if (employes.verifierAdmin(employe.getEmail(), employe.getMotDePasse())) {
                    System.out.println("Un administrateur ne peut pas être abonné au service");
                    return;
                }
                
                // Vérifier si l'employé existe dans la base de données
                if (!employes.existeParId(employe.getId())) {
                    System.out.println("L'employé n'existe pas dans la base de données");
                    return;
                }

                if (!abonnements.estAbonne(employe.getId())) {
                    abonnements.abonner(employe.getId());
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
    public void retirerAbonne(INotificationReceiver administrateur) {
        try {
            if (administrateur instanceof Employe employe) {
                if (abonnements.estAbonne(employe.getId())) {
                    abonnements.desabonner(employe.getId());
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
            List<String> abonnesIds = abonnements.getAbonnesIds();
            
            // Si aucun abonné, afficher un message et sortir
            if (abonnesIds.isEmpty()) {
                System.out.println("Aucun abonné à notifier");
                return;
            }

            // Récupérer le nom de l'expéditeur
            String nomExpediteur = employes.trouverParId(expediteur)
                .map(Employe::getNom)
                .orElse("Système");

            System.out.println(String.format("\nEnvoi du message à %d abonné(s)...", abonnesIds.size()));
            
            // Envoyer la notification à chaque abonné
            for (String abonneId : abonnesIds) {
                // Ne pas envoyer à l'expéditeur
                if (!abonneId.equals(expediteur)) {
                    notifications.sauvegarderNotification(message, expediteur, abonneId);
                    Optional<Employe> employe = employes.trouverParId(abonneId);
                    if (employe.isPresent()) {
                        Employe destinataire = employe.get();
                        destinataire.recevoirNotification(message, nomExpediteur);
                    }
                }
            }
            
            System.out.println("Notifications envoyées avec succès!");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'envoi des notifications: " + e.getMessage());
        }
    }

    @Override
    public boolean estAbonne(String id) {
        try {
            return abonnements.estAbonne(id);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'abonnement: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<INotificationReceiver> getAbonnes() {
        List<INotificationReceiver> abonnes = new ArrayList<>();
        try {
            List<String> abonnesIds = abonnements.getAbonnesIds();
            for (String id : abonnesIds) {
                employes.trouverParId(id).ifPresent(abonnes::add);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des abonnés: " + e.getMessage());
        }
        return abonnes;
    }

    public Optional<Employe> connecterAbonne(String email, String motDePasse) {
        try {
            if (employes.verifierIdentifiants(email, motDePasse)) {
                return employes.trouverParEmail(email);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean verifierAdmin(String email, String motDePasse) {
        try {
            return employes.verifierAdmin(email, motDePasse);
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
        List<INotificationReceiver> abonnes = getAbonnes();
        if (abonnes.isEmpty()) {
            System.out.println("Aucun abonné");
        } else {
            for (INotificationReceiver abonne : abonnes) {
                if (abonne instanceof Employe employe) {
                    System.out.println("- ID: " + employe.getId() + " | Nom: " + employe.getNom());
                }
            }
        }
    }

    public void afficherNotificationsUtilisateur(String employeId) {
        try {
            List<String> notifications = this.notifications.getNotificationsUtilisateur(employeId);
            Optional<Employe> employe = employes.trouverParId(employeId);
            
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

    public void afficherHistoriqueNotifications() {
        try {
            List<String> notificationsList = this.notifications.getHistoriqueNotifications();
            System.out.println("\nHistorique des notifications :");
            if (notificationsList.isEmpty()) {
                System.out.println("Aucune notification envoyée");
            } else {
                for (String notification : notificationsList) {
                    System.out.println("- " + notification);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'historique des notifications: " + e.getMessage());
        }
    }
} 