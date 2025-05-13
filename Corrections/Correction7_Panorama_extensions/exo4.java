
public class Produit {
    private String nom;
    private double prix;
    private int quantite;

    public Produit(String nom, double prix, int quantite) {
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
    }
    
    public String getNom() {
        return nom;
    }
    
    public double getPrix() {
        return prix;
    }
    
    public int getQuantite() {
        return quantite;
    }
    
    public double valeurStock() {
        return prix * quantite;
    }
    
    @Override
    public String toString() {
        return nom + " - " + prix + "€ - Quantité : " + quantite;
    }
}

### Classe TestProduits

import java.util.Arrays;
import java.util.List;

public class TestProduits {
    public static void main(String[] args) {
        List<Produit> produits = Arrays.asList(
            new Produit("Produit A", 50, 10),
            new Produit("Produit B", 150, 5),
            new Produit("Produit C", 30, 20),
            new Produit("Produit D", 80, 15),
            new Produit("Produit E", 200, 3)
        );

        // a. Filtrer les produits dont le prix est inférieur à 100 €
        System.out.println("Produits à moins de 100€ :");
        produits.stream()
                .filter(p -> p.getPrix() < 100)
                .forEach(System.out::println);

        // b. Calculer la valeur totale du stock
        double totalStock = produits.stream()
                                    .mapToDouble(Produit::valeurStock)
                                    .sum();
        System.out.println("\nValeur totale du stock : " + totalStock + "€");

        // c. Trier les produits par quantité décroissante et afficher leur nom
        System.out.println("\nProduits triés par quantité décroissante :");
        produits.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getQuantite(), p1.getQuantite()))
                .forEach(p -> System.out.println(p.getNom()));

        // Bonus : Calcul en parallèle de la somme totale des stocks
        double totalParallel = produits.parallelStream()
                                       .mapToDouble(Produit::valeurStock)
                                       .sum();
        System.out.println("\nValeur totale (calcul en parallèle) : " + totalParallel + "€");
    }
}