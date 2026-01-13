package observer;

// L'interface ModeleEcoutable pour implementer les methodes pour ajouter et retirer un ecouteurs
public interface ModeleEcoutable {
    /**
     * Ajoute un écouteur au modèle.
     * @param ecouteur L'écouteur à ajouter. 
    */
    public void ajoutEcouteur(EcouteurModele ecouteur);

    /**
     * Retire un écouteur du modèle.
     * @param ecouteur L'écouteur à retirer.
    */
    public void retraitEcouteur(EcouteurModele ecouteur);
}
