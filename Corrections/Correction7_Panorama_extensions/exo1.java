import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// 1. Énumération pour définir la priorité des tâches
enum Priorite {
     BASSE, MOYENNE, HAUTE
}

// 2. Classe représentant une tâche
class Tache {
     private String description;
     private Priorite priorite;

     public Tache(String description, Priorite priorite) {
          this.description = description;
          this.priorite = priorite;
     }

     public Priorite getPriorite() {
          return priorite;
     }

     @Override
     public String toString() {
          return "Tache{description='" + description + "', priorite=" + priorite + "}";
     }
}

// 3. Gestionnaire de tâches
class TaskManager {
     private List<Tache> taches = new ArrayList<>();

     // Ajoute une tâche dans la liste
     public void ajouterTache(Tache t) {
          taches.add(t);
     }

     // Retourne la liste des tâches filtrées par une priorité donnée
     public List<Tache> filtrerParPriorite(Priorite p) {
          return taches.stream()
                            .filter(t -> t.getPriorite() == p)
                            .collect(Collectors.toList());
     }

     // Affiche toutes les tâches
     public void afficherTaches() {
          taches.forEach(System.out::println);
     }
}

// 4. Classe de test pour démontrer l'utilisation du TaskManager
public class TestTaskManager {
     public static void main(String[] args) {
          TaskManager manager = new TaskManager();
          
          // Ajout de tâches avec différentes priorités
          manager.ajouterTache(new Tache("Finir le rapport", Priorite.HAUTE));
          manager.ajouterTache(new Tache("Nettoyer le bureau", Priorite.BASSE));
          manager.ajouterTache(new Tache("Préparer la réunion", Priorite.MOYENNE));
          manager.ajouterTache(new Tache("Répondre aux emails", Priorite.BASSE));
          manager.ajouterTache(new Tache("Analyser les données", Priorite.HAUTE));
          
          // Affichage de toutes les tâches
          System.out.println("Toutes les tâches :");
          manager.afficherTaches();
          
          // Affichage des tâches de priorité HAUTE
          System.out.println("\nTâches de priorité HAUTE :");
          List<Tache> tachesHaute = manager.filtrerParPriorite(Priorite.HAUTE);
          tachesHaute.forEach(System.out::println);
     }
}