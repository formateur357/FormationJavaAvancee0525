//version 1
// Importation des classes nécessaires pour gérer les listes
import java.util.List;
import java.util.ArrayList;

// Déclaration de la classe EvenementProcessor qui traite une liste d'événements
public class EvenementProcessor {

    // Définition d'un record Evenement immuable qui contient les informations d'un événement :
    // type, priorité et une description.
    public record Evenement(String type, int priorite, String description) {}

    // Variables de comptage des événements traités, initialisées à 0
    // NOTA: Ces compteurs ne sont pas atomiques, d'où l'utilisation d'une synchronisation explicite.
    private static int normal = 0;
    private static int suspect = 0;
    private static int critique = 0;

    // Objet lock utilisé pour synchroniser l'incrémentation des compteurs
    private static final Object lock = new Object();

    // Point d'entrée de l'application
    public static void main(String[] args) {
        // Création d'une liste immuable contenant plusieurs événements
        List<Evenement> evenements = List.of(
            new Evenement("ALERTE", 5, "Température élevée"),
            new Evenement("INFO", 1, "Routine"),
            new Evenement("ERREUR", 10, "Défaillance critique"),
            new Evenement("INFO", 2, "Routine 2"),
            new Evenement("ALERTE", 8, "Surcharge électrique"),
            new Evenement("ERREUR", 3, "Erreur mineure")
        );

        // Création d'une liste qui stockera les threads lancés pour le traitement en parallèle
        List<Thread> threads = new ArrayList<>();

        // Parcours de la liste des événements pour les traiter chacun dans un thread virtuel
        for (Evenement evt : evenements) {
            // Création et démarrage d'un thread virtuel pour traiter l'événement
            Thread t = Thread.startVirtualThread(() -> {
                // Utilisation d'un switch basé sur le pattern matching pour traiter différemment les types d'événements
                String result = switch (evt) {
                    // Si l'événement est de type "ERREUR" avec une priorité >= 8,
                    // il est considéré comme critique
                    case Evenement(String type, int priorite, String desc)
                        when type.equals("ERREUR") && priorite >= 8 ->
                            {
                                increment("critique");
                                yield "[CRITIQUE] " + desc;
                            }
                    // Si l'événement est de type "ALERTE" avec une priorité >= 5,
                    // il est considéré comme suspect
                    case Evenement(String type, int priorite, String desc)
                        when type.equals("ALERTE") && priorite >= 5 ->
                            {
                                increment("suspect");
                                yield "[SUSPECT] " + desc;
                            }
                    // Pour tous les autres cas, l'événement est traité normalement
                    default -> {
                        increment("normal");
                        yield "[NORMAL] " + evt.description();
                    }
                };

                // Affichage du résultat du traitement de l'événement
                System.out.println(result);
            });

            // Ajout du thread créé à la liste pour pouvoir le gérer ensuite
            threads.add(t);
        }

        // Boucle pour attendre la fin de l'exécution de tous les threads lancés
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                // En cas d'interruption, on restaure le statut d'interruption du thread courant
                Thread.currentThread().interrupt();
            }
        });

        // Affichage d'un résumé du traitement indiquant le nombre d'événements pour chaque catégorie
        System.out.println("\nRésumé du traitement :");
        System.out.println(" - Événements normaux  : " + normal);
        System.out.println(" - Événements suspects : " + suspect);
        System.out.println(" - Événements critiques: " + critique);
    }

    // Méthode synchronisée pour incrémenter les compteurs en fonction du type d'événement
    // Utilisation d'un bloc synchronized pour éviter les conditions de concurrence
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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//version 2 (avec AtomicInteger et StructuredTaskScope)

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AnalyseurPaiement {

    // Définition d'une hiérarchie d'événements de paiement.
    // Sealed interface pour limiter les types qui l'implémentent.
    public sealed interface EvenementPaiement permits PaiementCarte, VirementBancaire, RejetPaiement {}

    // Type d'événement pour un paiement par carte.
    // Record immuable contenant l'id, le titulaire, le montant et le pays.
    public record PaiementCarte(String id, String titulaire, double montant, String pays) implements EvenementPaiement {}

    // Type d'événement pour un virement bancaire.
    // Record contenant l'id, l'IBAN, le montant et le motif du virement.
    public record VirementBancaire(String id, String iban, double montant, String motif) implements EvenementPaiement {}

    // Type d'événement pour un rejet de paiement.
    // Record décrivant l'id, la raison du rejet et si celui-ci est bloquant.
    public record RejetPaiement(String id, String raison, boolean bloquant) implements EvenementPaiement {}

    // Enumération pour catégoriser l'analyse des paiements.
    enum TypeAnalyse { NORMAL, SUSPECT, CRITIQUE }

    // Méthode qui analyse un événement de paiement à l'aide d'un switch basé sur des patterns de record.
    public static TypeAnalyse analyser(EvenementPaiement evenement) {
        return switch (evenement) {
            // Cas d'un paiement par carte suspect si le montant est > 5000 et que le pays n'est pas "FR".
            case PaiementCarte pc when pc.montant() > 5000 && !pc.pays().equals("FR") -> {
                System.out.printf("[ALERTE] PaiementCarte %s suspecté de fraude : montant élevé depuis %s%n", pc.id(), pc.pays());
                yield TypeAnalyse.SUSPECT;
            }
            // Cas classique pour un paiement par carte traité normalement.
            case PaiementCarte pc -> {
                System.out.printf("[OK] PaiementCarte %s traité normalement%n", pc.id());
                yield TypeAnalyse.NORMAL;
            }
            // Cas d'un virement bancaire suspect si le montant est > 10000 et que le motif contient "donation".
            case VirementBancaire vb when vb.montant() > 10000 && vb.motif().toLowerCase().contains("donation") -> {
                System.out.printf("[ALERTE] Virement %s suspect : %s de %.2f €%n", vb.id(), vb.motif(), vb.montant());
                yield TypeAnalyse.SUSPECT;
            }
            // Cas classique pour un virement bancaire traité normalement.
            case VirementBancaire vb -> {
                System.out.printf("[OK] VirementBancaire %s traité normalement%n", vb.id());
                yield TypeAnalyse.NORMAL;
            }
            // Cas d'un rejet de paiement critique si le rejet est bloquant et que la raison est "compte clôturé".
            case RejetPaiement rj when rj.bloquant() && rj.raison().equalsIgnoreCase("compte clôturé") -> {
                System.out.printf("[CRITIQUE] Rejet %s : %s (bloquant)%n", rj.id(), rj.raison());
                yield TypeAnalyse.CRITIQUE;
            }
            // Autre cas de rejet de paiement qui est traité normalement.
            case RejetPaiement rj -> {
                System.out.printf("[OK] Rejet %s sans impact : %s%n", rj.id(), rj.raison());
                yield TypeAnalyse.NORMAL;
            }
        };
    }

    // Méthode principale : point d'entrée de l'application.
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Création d'une liste d'événements de paiement à analyser.
        List<EvenementPaiement> events = List.of(
            new PaiementCarte("PC01", "Alice", 6000, "US"),
            new PaiementCarte("PC02", "Bob", 300, "FR"),
            new VirementBancaire("VB01", "FR761234...", 12000, "donation anniversaire"),
            new RejetPaiement("RJ01", "compte clôturé", true),
            new RejetPaiement("RJ02", "provision insuffisante", false)
        );

        // Initialisation de compteurs pour chaque type d'analyse.
        AtomicInteger normal = new AtomicInteger();
        AtomicInteger suspect = new AtomicInteger();
        AtomicInteger critique = new AtomicInteger();

        // Utilisation d'un StructuredTaskScope pour traiter les événements en parallèle.
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            for (EvenementPaiement evt : events) {
                scope.fork(() -> {
                    // Analyse de l'événement et mise à jour du compteur correspondant.
                    TypeAnalyse res = analyser(evt);
                    switch (res) {
                        case NORMAL -> normal.incrementAndGet();
                        case SUSPECT -> suspect.incrementAndGet();
                        case CRITIQUE -> critique.incrementAndGet();
                    }
                    return null;
                });
            }
            // Attente de la fin de toutes les tâches.
            scope.join();
            // Propagation de l'éventuelle exception d'une tâche.
            scope.throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("La tâche a été interrompue.");
        }

        // Affichage du rapport final de l'analyse.
        System.out.println("\n=== RAPPORT FINAL ===");
        System.out.printf("- Événements normaux : %d%n", normal.get());
        System.out.printf("- Suspects : %d%n", suspect.get());
        System.out.printf("- Critiques : %d%n", critique.get());
    }
}