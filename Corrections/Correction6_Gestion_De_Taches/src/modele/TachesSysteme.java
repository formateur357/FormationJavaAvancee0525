package modele;

public class TachesSysteme {

    @Tache(priorite = 1, description = "Réinitialisation de la base de données.")
    public void reinitialiserBaseDeDonnees() {
        System.out.println("Réinitialisation de la base de données...");
    }

    @Tache(priorite = 2, description = "Nettoyage du cache système.")
    public void nettoyerCache() {
        System.out.println("Nettoyage du cache système...");
    }

    @Tache(priorite = 3, description = "Envoi du rapport quotidien.")
    public void envoyerRapport() {
        System.out.println("Envoi du rapport quotidien...");
    }
}