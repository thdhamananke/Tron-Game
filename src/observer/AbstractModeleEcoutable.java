package observer;

import java.util.*;

// Une classe abstrait qui implement l'interface ModeleEcoutable
public abstract class AbstractModeleEcoutable implements ModeleEcoutable{
    private List<EcouteurModele> ecouteurs;

    // cree une nouvelle instance et initialise une liste d'ecouteurs
    public AbstractModeleEcoutable() {
        this.ecouteurs = new ArrayList<>();
    }
    
    /**
     * Ajoute un écouteur à la liste des écouteurs.
     * @param ecouteur L'écouteur à ajouter.
    */
    @Override
    public void ajoutEcouteur(EcouteurModele ecouteur) {
        ecouteurs.add(ecouteur);
    }

    /**
     * Retire l'écouteur de la liste des écouteurs.
     * @param ecouteur L'écouteur à retirer.
    */
    @Override
    public void retraitEcouteur(EcouteurModele ecouteur) {
        ecouteurs.remove(ecouteur);
    }

    /**
     * Envoi des notifications à tous les écouteurs enregistrés que le modèle a été mis à jour.
     * En supposant que 'this' est une instance de...
    */
    protected void notifier() {
        for(EcouteurModele ecouteur : ecouteurs) {
            ecouteur.modeleMisAJour(this);
        }
    }

}
