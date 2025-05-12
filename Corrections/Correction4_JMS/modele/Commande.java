package modele;

import java.io.Serializable;

public class Commande implements Serializable {
    private String id;
    private String produit;
    private int quantite;

    public Commande(String id, String produit, int quantite) {
        this.id = id;
        this.produit = produit;
        this.quantite = quantite;
    }

    public String getId() { return id; }
    public String getProduit() { return produit; }
    public int getQuantite() { return quantite; }

    @Override
    public String toString() {
        return "Commande[id=" + id + ", produit=" + produit + ", quantite=" + quantite + "]";
    }
}