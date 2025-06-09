package com.notification.interfaces;

/**
 * Interface définissant le contrat pour les observateurs (employés)
 */
public interface NotificationReceiver {
    /**
     * Méthode appelée pour recevoir une notification
     * @param message Le message de la notification
     * @param expediteur L'expéditeur du message
     */
    void recevoirNotification(String message, String expediteur);
    
    /**
     * Obtenir l'identifiant de l'observateur
     * @return L'identifiant unique de l'observateur
     */
    String getId();
    
    /**
     * Obtenir le nom de l'observateur
     * @return Le nom de l'observateur
     */
    String getNom();
} 