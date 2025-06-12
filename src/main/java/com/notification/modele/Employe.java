package com.notification.modele;

import com.notification.interfaces.INotificationReceiver;

/**
 * Classe représentant un employé qui peut recevoir des notifications
 */
public class Employe implements INotificationReceiver {
    private String id;
    private String nom;
    private String email;
    private String motDePasse;

    public Employe(String id, String nom, String email, String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    @Override
    public void recevoirNotification(String message, String expediteur) {
        // Affichage console uniquement (la persistance est gérée par le DAO)
        System.out.println(String.format("[%s] a reçu un message de %s : %s", this.nom, expediteur, message));
        // Simulation d'envoi d'email
        System.out.println(String.format("Email envoyé à %s (%s)", this.nom, this.email));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    /**
     * Vérifier les identifiants de connexion
     * @param email Email de l'employé
     * @param motDePasse Mot de passe de l'employé
     * @return true si les identifiants sont corrects, false sinon
     */
    public boolean verifierIdentifiants(String email, String motDePasse) {
        return this.email.equals(email) && this.motDePasse.equals(motDePasse);
    }
} 