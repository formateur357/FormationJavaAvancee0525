import com.sun.net.httpserver.*; // Utilisation du serveur HTTP léger fourni par le JDK
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;

public class Main {
    // Simule une base de données en mémoire pour stocker des produits
    private static final Map<Integer, Produit> stock = new HashMap<>();
    private static int idCounter = 1; // Compteur pour générer des IDs uniques

    public static void main(String[] args) throws IOException {
        // Crée un serveur HTTP écoutant sur le port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Associe l'URL "/produits" à un gestionnaire personnalisé
        server.createContext("/produits", new ProduitHandler());

        // Définit un pool de threads pour gérer les requêtes en parallèle
        server.setExecutor(Executors.newFixedThreadPool(4));

        System.out.println("Serveur REST démarré sur http://localhost:8080");
        server.start(); // Démarre le serveur
    }

    // === Classe interne pour gérer les requêtes HTTP vers /produits ===
    static class ProduitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod(); // Méthode HTTP (GET, POST, etc.)
            String path = exchange.getRequestURI().getPath(); // Chemin de la requête
            String[] segments = path.split("/"); // Segmente l’URL

            // Routage en fonction de la méthode et du chemin
            if (method.equals("GET") && segments.length == 2) {
                listerProduits(exchange); // GET /produits
            } else if (method.equals("GET") && segments.length == 3) {
                lireProduit(exchange, segments[2]); // GET /produits/{id}
            } else if (method.equals("POST")) {
                ajouterProduit(exchange); // POST /produits
            } else if (method.equals("PUT") && segments.length == 3) {
                modifierProduit(exchange, segments[2]); // PUT /produits/{id}
            } else if (method.equals("DELETE") && segments.length == 3) {
                supprimerProduit(exchange, segments[2]); // DELETE /produits/{id}
            } else {
                sendResponse(exchange, 404, "Not Found"); // Route non reconnue
            }
        }

        // Envoie la liste complète des produits
        private void listerProduits(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            for (Produit p : stock.values()) {
                json.append(p.toJson()).append(",");
            }
            if (json.length() > 1) json.setLength(json.length() - 1); // Supprime la dernière virgule
            json.append("]");
            sendResponse(exchange, 200, json.toString());
        }

        // Récupère un produit spécifique par ID
        private void lireProduit(HttpExchange exchange, String idStr) throws IOException {
            try {
                int id = Integer.parseInt(idStr);
                Produit p = stock.get(id);
                if (p == null) {
                    sendResponse(exchange, 404, "Produit introuvable");
                } else {
                    sendResponse(exchange, 200, p.toJson());
                }
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "ID invalide");
            }
        }

        // Ajoute un nouveau produit à la collection
        private void ajouterProduit(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Produit p = Produit.fromJson(body); // Parsing manuel
            p.setId(idCounter++); // Assigne un ID unique
            stock.put(p.getId(), p); // Stocke le produit
            sendResponse(exchange, 201, p.toJson()); // Retourne le produit créé
        }

        // Met à jour un produit existant
        private void modifierProduit(HttpExchange exchange, String idStr) throws IOException {
            try {
                int id = Integer.parseInt(idStr);
                if (!stock.containsKey(id)) {
                    sendResponse(exchange, 404, "Produit introuvable");
                    return;
                }
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Produit p = Produit.fromJson(body);
                p.setId(id); // Garde l’ID existant
                stock.put(id, p); // Remplace le produit
                sendResponse(exchange, 200, p.toJson());
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "ID invalide");
            }
        }

        // Supprime un produit par ID
        private void supprimerProduit(HttpExchange exchange, String idStr) throws IOException {
            try {
                int id = Integer.parseInt(idStr);
                if (stock.remove(id) != null) {
                    sendResponse(exchange, 204, ""); // Succès sans contenu
                } else {
                    sendResponse(exchange, 404, "Produit introuvable");
                }
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "ID invalide");
            }
        }

        // Méthode utilitaire pour envoyer une réponse HTTP
        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        }
    }

    // === Classe modèle représentant un produit ===
    static class Produit {
        private int id;
        private String nom;
        private double prix;

        public Produit() {} // Constructeur vide requis pour l’instanciation

        public Produit(int id, String nom, double prix) {
            this.id = id;
            this.nom = nom;
            this.prix = prix;
        }

        public void setId(int id) { this.id = id; }

        // Sérialise l’objet en JSON (simplifié)
        public String toJson() {
            return String.format("{\"id\":%d,\"nom\":\"%s\",\"prix\":%.2f}", id, nom, prix);
        }

        // Désérialise un JSON en objet Produit (très simpliste)
        public static Produit fromJson(String json) {
            String[] parts = json.replace("{", "").replace("}", "").replace("\"", "").split(",");
            String nom = parts[0].split(":")[1];
            double prix = Double.parseDouble(parts[1].split(":")[1]);
            return new Produit(0, nom, prix); // L’ID sera assigné ensuite
        }
    }
}
