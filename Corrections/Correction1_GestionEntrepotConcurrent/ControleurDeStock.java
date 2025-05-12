import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class ControleurDeStock extends RecursiveTask<Integer> {
    private final Map<String, Integer> produits;
    private final String[] keys;
    private final int start, end;

    public ControleurDeStock(Map<String, Integer> produits, String[] keys, int start, int end) {
        this.produits = produits;
        this.keys = keys;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        // Le modèle Fork/Join est utilisé ici pour paralléliser le calcul du stock total en divisant
        // récursivement le travail : c'est l'une des principales améliorations apportées dans Java 7 pour le parallélisme.
        if (end - start <= 2) {
            int sum = 0;
            for (int i = start; i < end; i++) {
                sum += produits.getOrDefault(keys[i], 0);
            }
            return sum;
        } else {
            int mid = (start + end) / 2;
            ControleurDeStock left = new ControleurDeStock(produits, keys, start, mid);
            ControleurDeStock right = new ControleurDeStock(produits, keys, mid, end);
            left.fork(); // Exécution asynchrone du sous-calcul gauche
            return right.compute() + left.join(); // Agrégation des résultats une fois que les sous-tâches sont terminées
        }
    }
}