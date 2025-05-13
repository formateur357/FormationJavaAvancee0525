package mbeans;

public interface StockManagerStandardMBean {
    int getStock(String produit);
    void addProduct(String produit, int quantite);
    void replenishStock(String produit, int quantite);
}