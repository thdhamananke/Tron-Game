package observer;

/**
 * Interface représentant un écouteur du modèle.
 *
 * Elle fait partie de l'implémentation du patron Observer.
 * Toute classe souhaitant être informée des changements
 * du modèle (vue graphique, vue console, contrôleur, etc.)
 * doit implémenter cette interface.
 */
public interface EcouteurModele {
    /**
     * Methode qui met a jour l'objet.
     * @param source La source de l'obejt.
    */
    public void modeleMisAJour(Object source);
}
