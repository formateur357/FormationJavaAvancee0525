# 🧪 Exercice pratique – Gestion d’un entrepôt multithreadé

## 🎯 Objectif

Développer une **application de simulation d’entrepôt** où plusieurs agents (threads) effectuent des actions concurrentes sur des stocks de produits.

Cette simulation mettra en œuvre :
- Création et gestion de `Thread`
- Synchronisation (`synchronized`, `ReentrantLock`)
- `Callable` et `ExecutorService`
- Utilisation de `BlockingQueue` pour les commandes clients
- Traitement parallèle avec `ForkJoinPool`
- Traitement asynchrone avec `CompletableFuture`

---

## 🏗️ Description du système

L'entrepôt gère un stock de produits. Plusieurs types d'agents interagissent avec ce stock :

- `AgentDeCommande` : traite les commandes client (réservation produit).
- `AgentDeReapprovisionnement` : ajoute périodiquement des produits.
- `ControleurDeStock` : calcule périodiquement le stock total via Fork/Join.
- `JournalisationAsynchrone` : écrit les événements dans un fichier de log de manière asynchrone.

Chaque opération devra être **thread-safe**.

---

## 🧩 Contraintes techniques

- Les agents s’exécutent en parallèle.
- Les ressources (produits) sont **protégées par des verrous explicites** (`ReentrantLock`).
- Les tâches des agents sont gérées par un **`ExecutorService`**.
- Le stock total est **calculé avec Fork/Join**.
- Les logs sont envoyés en **asynchrone** via `CompletableFuture`.

---

## 🛠️ Étapes proposées

1. **Créer une classe `Stock`** :
   - Attributs : `Map<String, Integer> produits`
   - Verrou : `ReentrantLock lock`
   - Méthodes thread-safe :
     - `ajouterProduit(String, int)`
     - `retirerProduit(String, int)`
     - `getQuantite(String)`

2. **Créer la classe `AgentDeCommande` (implements Callable)** :
   - Retire un ou plusieurs produits du stock.
   - Simule un délai de traitement.
   - Retourne un `String` indiquant le succès ou l’échec.

3. **Créer la classe `AgentDeReapprovisionnement` (Runnable)** :
   - Ajoute aléatoirement des quantités de produits.
   - Boucle infinie avec `Thread.sleep()`.

4. **Créer `ControleurDeStock` avec ForkJoin** :
   - Calcule la somme totale du stock via `RecursiveTask<Integer>`.
   - Exécuté périodiquement.

5. **Créer `JournalisationAsynchrone` avec `CompletableFuture`** :
   - Enregistre chaque action dans un fichier (ou console).
   - Les appels sont non bloquants.

6. **Créer une classe `Main` pour orchestrer le tout** :
   - Démarre les threads.
   - Soumet les tâches au pool.
   - Arrête l'exécution au bout de 30 secondes.

---

## 💡 Exemple de sortie console attendue

```
[LOG] Commande: -5 unités de "Clavier" -> SUCCÈS
[LOG] Reapprovisionnement: +10 unités de "Souris"
[LOG] Stock total: 225 produits
[LOG] Commande: -20 unités de "Écran" -> ÉCHEC (stock insuffisant)
[LOG] Stock total: 215 produits
```