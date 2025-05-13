package mbeans;

import java.util.concurrent.ConcurrentHashMap;

public class StockManagerStandard implements StockManagerStandardMBean {
    private final ConcurrentHashMap<String, Integer> stock = new ConcurrentHashMap<>();

    @Override
    public int getStock(String produit) {
        return stock.getOrDefault(produit, 0);
    }

    @Override
    public void addProduct(String produit, int quantite) {
        stock.put(produit, quantite);
    }

    @Override
    public void replenishStock(String produit, int quantite) {
        stock.merge(produit, quantite, Integer::sum);
    }
}