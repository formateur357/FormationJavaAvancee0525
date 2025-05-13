public class AgentDeCommande implements Runnable {
    private StockManager stockManager;
    private String product;
    private int quantity;

    public AgentDeCommande(StockManager stockManager, String product, int quantity) {
        this.stockManager = stockManager;
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public void run() {
        stockManager.removeProduct(product, quantity);
        System.out.println("Commande : " + quantity + " " + product + " retiré du stock.");
    }
}