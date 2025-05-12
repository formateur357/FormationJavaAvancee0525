import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Stock {
    private final Map<String, Integer> produits = new HashMap<>();

    // Utilisation d'un verrou explicite (ReentrantLock) au lieu de 'synchronized'
    // pour un contrôle plus fin sur la synchronisation : timeout, interruption, essais conditionnels, etc.
    private final ReentrantLock lock = new ReentrantLock();

    public void ajouterProduit(String nom, int quantite) {
        lock.lock();
        try {
            produits.put(nom, produits.getOrDefault(nom, 0) + quantite);
        } finally {
            lock.unlock();
        }
    }

    public boolean retirerProduit(String nom, int quantite) {
        lock.lock();
        try {
            int dispo = produits.getOrDefault(nom, 0);
            if (dispo >= quantite) {
                produits.put(nom, dispo - quantite);
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public Map<String, Integer> getProduitsSnapshot() {
        lock.lock();
        try {
            return new HashMap<>(produits);
        } finally {
            lock.unlock();
        }
    }
}