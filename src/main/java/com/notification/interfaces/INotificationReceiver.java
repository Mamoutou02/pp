package com.notification.interfaces;

/**
 * Interface définissant le contrat pour les entités pouvant recevoir des notifications
 */
public interface INotificationReceiver {
    /**
     * Méthode appelée pour recevoir une notification
     * @param message Le message de la notification
     * @param expediteur L'expéditeur du message
     */
    void recevoirNotification(String message, String expediteur);
    
    /**
     * Obtenir l'identifiant du receveur
     * @return L'identifiant unique du receveur
     */
    String getId();
    
    /**
     * Obtenir le nom du receveur
     * @return Le nom du receveur
     */
    String getNom();
} 