import javax.management.*;
import java.util.HashMap;
import java.util.Map;

public class StockManager implements StockManagerMBean {
    private Map<String, Integer> stock;

    public StockManager() {
        stock = new HashMap<>();
    }

    @Override
    public void addProduct(String name, int quantity) {
        stock.put(name, stock.getOrDefault(name, 0) + quantity);
    }

    @Override
    public void removeProduct(String name, int quantity) {
        int currentQuantity = stock.getOrDefault(name, 0);
        if (currentQuantity >= quantity) {
            stock.put(name, currentQuantity - quantity);
        }
    }

    @Override
    public int getStock(String name) {
        return stock.getOrDefault(name, 0);
    }

    @Override
    public void replenishStock(String name, int quantity) {
        stock.put(name, stock.getOrDefault(name, 0) + quantity);
    }
}