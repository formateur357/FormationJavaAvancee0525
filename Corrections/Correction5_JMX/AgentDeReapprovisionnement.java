import java.util.concurrent.ThreadLocalRandom;

public class AgentDeReapprovisionnement implements Runnable {
    private StockManager stockManager;
    private String[] products;

    public AgentDeReapprovisionnement(StockManager stockManager, String[] products) {
        this.stockManager = stockManager;
        this.products = products;
    }

    @Override
    public void run() {
        while (true) {
            String product = products[ThreadLocalRandom.current().nextInt(products.length)];
            int quantity = ThreadLocalRandom.current().nextInt(1, 10);
            stockManager.replenishStock(product, quantity);
            System.out.println("Réapprovisionnement : " + quantity + " " + product + " ajouté au stock.");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}