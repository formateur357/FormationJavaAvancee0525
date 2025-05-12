import java.util.concurrent.ThreadLocalRandom;

public class AgentDeReapprovisionnement implements Runnable {
    private final Stock stock;
    private final String[] produits;

    public AgentDeReapprovisionnement(Stock stock, String[] produits) {
        this.stock = stock;
        this.produits = produits;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String produit = produits[ThreadLocalRandom.current().nextInt(produits.length)];
                int quantite = ThreadLocalRandom.current().nextInt(1, 10);
                stock.ajouterProduit(produit, quantite);
                System.out.println("[LOG] Reapprovisionnement: +" + quantite + " unités de \"" + produit + "\"");
                Thread.sleep(1500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}