// Importation des bibliothèques nécessaires
import java.util.*;                    // Collections (List, Map, etc.)
import java.util.concurrent.*;         // Threads, synchronisation
import java.util.stream.*;             // API Stream pour le traitement fonctionnel
import java.io.*;                      // Gestion des entrées/sorties
import java.nio.file.*;                // Gestion des chemins de fichiers

// Classe principale
public class EvenementProcessorAvance {

    // Définition d'un record Java pour représenter un événement (immuable)
    public record Evenement(String type, int priorite, String description) {}

    // Verrou pour la synchronisation des accès concurrents
    private static final Object lock = new Object();
    // Compteurs pour les différentes catégories d'événements
    private static int normal = 0;
    private static int suspect = 0;
    private static int critique = 0;

    public static void main(String[] args) throws IOException {
        // Liste d'événements à traiter (immuable)
        List<Evenement> evenements = List.of(
            new Evenement("ALERTE", 5, "Température élevée"),
            new Evenement("INFO", 1, "Routine"),
            new Evenement("ERREUR", 10, "Défaillance critique"),
            new Evenement("INFO", 2, "Routine 2"),
            new Evenement("ALERTE", 8, "Surcharge électrique"),
            new Evenement("ERREUR", 3, "Erreur mineure"),
            new Evenement("INFO", 5, "Vérification système"),
            new Evenement("ERREUR", 9, "Crash système")
        );

        // Liste des threads créés pour le traitement parallèle
        List<Thread> threads = new ArrayList<>();
        // Liste synchronisée des résultats de traitement
        List<String> traitements = Collections.synchronizedList(new ArrayList<>());

        // Traitement de chaque événement dans un thread virtuel
        for (Evenement evt : evenements) {
            Thread t = Thread.startVirtualThread(() -> {
                // Switch pattern matching avec condition (Java 21)
                String result = switch (evt) {
                    // Cas critique : ERREUR avec priorité ≥ 8
                    case Evenement(String type, int priorite, String desc)
                        when type.equals("ERREUR") && priorite >= 8 ->
                            {
                                increment("critique"); // Incrément du compteur
                                yield "[CRITIQUE] " + desc;
                            }
                    // Cas suspect : ALERTE avec priorité ≥ 5
                    case Evenement(String type, int priorite, String desc)
                        when type.equals("ALERTE") && priorite >= 5 ->
                            {
                                increment("suspect");
                                yield "[SUSPECT] " + desc;
                            }
                    // Cas normal : tous les autres
                    default -> {
                        increment("normal");
                        yield "[NORMAL] " + evt.description();
                    }
                };

                // Ajout du résultat dans la liste synchronisée
                traitements.add(result);
                // Affichage immédiat du résultat
                System.out.println(result);
            });

            // Stockage du thread pour le suivi
            threads.add(t);
        }

        // Attente de la fin de l'exécution de tous les threads
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Bonne pratique : restaurer l'état d'interruption
            }
        });

        // Affichage du résumé global
        System.out.println("\nRésumé du traitement :");
        System.out.println(" - Normaux  : " + normal);
        System.out.println(" - Suspects : " + suspect);
        System.out.println(" - Critiques: " + critique);

        // === Traitements avancés avec l'API Stream ===
        System.out.println("\nAnalyse avancée :");

        // Moyenne de priorité par type d’événement
        Map<String, Double> moyenneParType = evenements.stream()
            .collect(Collectors.groupingBy(
                Evenement::type, // Clé de regroupement
                Collectors.averagingInt(Evenement::priorite) // Valeur : moyenne
            ));

        // Affichage des moyennes
        moyenneParType.forEach((type, moyenne) ->
            System.out.printf(" - Moyenne de priorité pour %s : %.2f%n", type, moyenne)
        );

        // Regroupement des descriptions d'événements par type
        Map<String, List<String>> descriptionsParType = evenements.stream()
            .collect(Collectors.groupingBy(
                Evenement::type,
                Collectors.mapping(Evenement::description, Collectors.toList())
            ));

        // Affichage des descriptions par type
        descriptionsParType.forEach((type, descs) -> {
            System.out.println(" - Descriptions pour " + type + " : " + descs);
        });

        // === Sauvegarde des résultats dans un fichier ===
        Path path = Path.of("resultats_traitement.txt"); // Chemin du fichier de sortie
        Files.write(path, traitements, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("\nRésultats écrits dans le fichier : " + path.toAbsolutePath());
    }

    // Méthode utilitaire pour incrémenter les compteurs en toute sécurité
    private static void increment(String type) {
        synchronized (lock) {
            switch (type) {
                case "normal" -> normal++;
                case "suspect" -> suspect++;
                case "critique" -> critique++;
            }
        }
    }
}
