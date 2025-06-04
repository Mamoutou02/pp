package com.notification.interfaces;

import java.util.List;

/**
 * Interface définissant le contrat pour le service de notification
 */
public interface IServiceNotification {
    /**
     * Ajouter un abonné au service
     * @param observateur L'observateur à ajouter
     */
    void ajouterAbonne(IObservateur observateur);
    
    /**
     * Retirer un abonné du service
     * @param observateur L'observateur à retirer
     */
    void retirerAbonne(IObservateur observateur);
    
    /**
     * Envoyer une notification à tous les abonnés
     * @param message Le message à envoyer
     * @param expediteur L'expéditeur du message
     */
    void notifierTous(String message, String expediteur);
    
    /**
     * Vérifier si un employé est abonné
     * @param id L'identifiant de l'employé
     * @return true si l'employé est abonné, false sinon
     */
    boolean estAbonne(String id);
    
    /**
     * Obtenir la liste des abonnés
     * @return La liste des abonnés
     */
    List<IObservateur> getAbonnes();
} 