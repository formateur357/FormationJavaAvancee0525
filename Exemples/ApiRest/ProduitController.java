package com.example.restdemo;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private final Map<Long, Produit> stock = new HashMap<>();
    private long idCounter = 1;

    // GET : liste tous les produits
    @GetMapping
    public Collection<Produit> getAll() {
        return stock.values();
    }

    // GET : un seul produit par ID
    @GetMapping("/{id}")
    public Produit getById(@PathVariable Long id) {
        return stock.get(id);
    }

    // POST : ajoute un nouveau produit
    @PostMapping
    public Produit create(@RequestBody Produit produit) {
        produit.setId(idCounter++);
        stock.put(produit.getId(), produit);
        return produit;
    }

    // PUT : met à jour un produit existant
    @PutMapping("/{id}")
    public Produit update(@PathVariable Long id, @RequestBody Produit produit) {
        produit.setId(id);
        stock.put(id, produit);
        return produit;
    }

    // DELETE : supprime un produit par ID
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        stock.remove(id);
    }
}
