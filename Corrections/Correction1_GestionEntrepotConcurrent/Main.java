import java.util.Map;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Stock stock = new Stock();
        String[] produits = {"Clavier", "Souris", "Écran", "Casque"};

        // Initialisation du stock
        for (String p : produits) {
            stock.ajouterProduit(p, 20);
        }

        // Création d’un pool fixe pour traiter les commandes simultanées
        // Le pool fixe de threads permet de contrôler le nombre de tâches simultanées
        // et d’éviter une surcharge du système avec trop de threads concurrents.
        ExecutorService poolCommande = Executors.newFixedThreadPool(4);

        // Pool ForkJoin pour le calcul parallèle du stock total
        ForkJoinPool poolForkJoin = new ForkJoinPool();

        // Lancement du thread de réapprovisionnement continu
        Thread reapproThread = new Thread(new AgentDeReapprovisionnement(stock, produits));
        reapproThread.start();

        // Planification périodique de la vérification du stock total
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            Map<String, Integer> snapshot = stock.getProduitsSnapshot();
            String[] keys = snapshot.keySet().toArray(new String[0]);
            int total = poolForkJoin.invoke(new ControleurDeStock(snapshot, keys, 0, keys.length));
            JournalisationAsynchrone.log("Stock total: " + total + " produits");
        }, 2, 5, TimeUnit.SECONDS);

        // Simulation de 20 commandes générées aléatoirement
        for (int i = 0; i < 20; i++) {
            String produit = produits[ThreadLocalRandom.current().nextInt(produits.length)];
            int quantite = ThreadLocalRandom.current().nextInt(1, 15);
            Future<String> resultat = poolCommande.submit(new AgentDeCommande(stock, produit, quantite));

            // Traitement du résultat de façon asynchrone
            CompletableFuture.supplyAsync(() -> {
                try {
                    return resultat.get();
                } catch (Exception e) {
                    return "Erreur lors de la commande.";
                }
            }).thenAccept(JournalisationAsynchrone::log);

            Thread.sleep(800);
        }

        // Attente avant arrêt des threads
        Thread.sleep(15000);
        reapproThread.interrupt();
        poolCommande.shutdown();
        scheduler.shutdown();
        poolForkJoin.shutdown();
    }
}