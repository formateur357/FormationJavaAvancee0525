public interface StockManagerMBean {
    void addProduct(String name, int quantity);
    void removeProduct(String name, int quantity);
    int getStock(String name);
    void replenishStock(String name, int quantity);
}