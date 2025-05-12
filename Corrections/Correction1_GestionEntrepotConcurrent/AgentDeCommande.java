import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class AgentDeCommande implements Callable<String> {
    private final Stock stock;
    private final String produit;
    private final int quantite;

    public AgentDeCommande(Stock stock, String produit, int quantite) {
        this.stock = stock;
        this.produit = produit;
        this.quantite = quantite;
    }

    @Override
    public String call() throws Exception {
        // Simulation d'un délai aléatoire pour représenter la variabilité des commandes
        Thread.sleep(ThreadLocalRandom.current().nextInt(200, 1000));
        boolean succes = stock.retirerProduit(produit, quantite);
        return "Commande: -" + quantite + " unités de \"" + produit + "\" -> " + (succes ? "SUCCÈS" : "ÉCHEC");
    }
}