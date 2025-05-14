# Exercice : Gestion de Tâches avec Priorités

## Objectif
Créer une application qui gère une liste de tâches. Chaque tâche possède une description et une priorité. Vous utiliserez les concepts de génériques, d'énumérations et d'expressions lambda pour filtrer et traiter la liste.

## Instructions

1. Créez une énumération Priorite avec les valeurs BASSE, MOYENNE et HAUTE.
2. Définissez une classe Tache contenant :
    - Un attribut String description
    - Un attribut de type Priorite
    - Un constructeur pour initialiser ces attributs
    - Une méthode toString() pour afficher la tâche
3. Créez une classe TaskManager qui possède :
    - Un attribut privé de type List<Tache> pour stocker les tâches
    - Une méthode ajouterTache(Tache t) qui ajoute une tâche dans la liste
    - Une méthode filtrerParPriorite(Priorite p) qui retourne une nouvelle liste contenant uniquement les tâches de la priorité donnée, en utilisant une expression lambda
4. Créez une classe TestTaskManager pour :
    - Instancier TaskManager
    - Ajouter plusieurs tâches avec différentes priorités
    - Afficher la liste complète des tâches
    - Afficher la liste filtrée pour une priorité donnée

---

## Exercice Lambda-Expressions et Interfaces Fonctionnelles

### Énoncé
Créez une application Java qui :
1. Définit une interface fonctionnelle nommée Operation avec une méthode abstraite permettant d'effectuer une opération sur deux entiers.
2. Utilise des expressions lambda pour implémenter les opérations basiques : addition, soustraction, multiplication et division.
3. Demande à l'utilisateur de saisir deux nombres et le symbole de l'opération (+, -, *, /).
4. Affiche le résultat de l'opération choisie.

### Consignes
- Déclarez l'interface `Operation` en l'annotant avec `@FunctionalInterface`.
- Implémentez les lambda expressions pour chaque opération.
- Gérez l'entrée utilisateur via la console.
- Traitez le cas d'une division par zéro en affichant un message d'erreur.

---

## Exercice : Manipulation des Streams

Objectif : Appliquer les opérations intermédiaires et terminales sur une collection de produits.

1. Créez une classe Produit avec les attributs suivants :
    - nom (String)
    - prix (double)
    - quantite (int)

2. Dans la méthode main, initialisez une liste de produits (au moins 5) avec des valeurs variées.

3. À l'aide d'un Stream, réalisez les opérations suivantes :
    a. Filtrer et afficher les produits dont le prix est inférieur à 100 €.
    b. Calculer et afficher la valeur totale du stock (pour chaque produit : prix * quantite).
    c. Trier les produits par quantité décroissante et afficher leur nom.

4. Bonus : Utilisez un stream parallèle pour recalculer la somme totale des stocks.

Testez votre code et vérifiez que les résultats correspondent aux attentes.

---

# 🧠 Exercice Avancé — Détection intelligente d’anomalies de paiements

## 🎯 Objectif

Développer un **analyseur concurrent d’anomalies de paiements**, capable de traiter des données hétérogènes issues de diverses sources et d’en extraire les irrégularités en utilisant :

- des **Record Patterns complexes**,
- un `switch` évolué avec `when`,
- des **Virtual Threads**,
- et la **Structured Concurrency**.

---

## 📘 Spécifications

Vous devez :

1. Créer différents types d’événements financiers à analyser.
2. Implémenter une logique d’analyse basée sur un `switch` utilisant des `Record Patterns` combinés avec des conditions `when`.
3. Lancer ces analyses en **Virtual Threads**, regroupées par la **Structured Concurrency**.
4. Distinguer les événements « normaux », « suspects » et « frauduleux ».
5. Générer un rapport final avec un comptage de chaque type.

---

## 📦 Modélisation des données

```java
public sealed interface EvenementPaiement permits PaiementCarte, VirementBancaire, RejetPaiement {}

public record PaiementCarte(String id, String titulaire, double montant, String pays) implements EvenementPaiement {}
public record VirementBancaire(String id, String iban, double montant, String motif) implements EvenementPaiement {}
public record RejetPaiement(String id, String raison, boolean bloquant) implements EvenementPaiement {}
```

## 🔍 Analyse attendue via switch

Vous devez appliquer un switch avec when comme suit :

- PaiementCarte avec montant > 5000 et pays ≠ "FR" → fraude suspectée

- VirementBancaire avec motif contenant "donation" et montant > 10000 → fraude potentielle

- RejetPaiement bloquant avec raison = "compte clôturé" → anomalie critique

- Tout autre cas → événement normal

## 🧪 Exemple de jeu de test

```java
List<EvenementPaiement> events = List.of(
    new PaiementCarte("PC01", "Alice", 6000, "US"),
    new PaiementCarte("PC02", "Bob", 300, "FR"),
    new VirementBancaire("VB01", "FR761234...", 12000, "donation anniversaire"),
    new RejetPaiement("RJ01", "compte clôturé", true),
    new RejetPaiement("RJ02", "provision insuffisante", false)
);
```

## 🧩 Comportement du programme

1. Chaque événement est traité en Virtual Thread.

2. Les threads sont lancés et contrôlés par une Structured Concurrency (StructuredTaskScope.ShutdownOnFailure).

3. Un switch avancé est utilisé pour analyser chaque événement.

4. Les résultats sont comptabilisés par catégorie (normal, suspect, critique, frauduleux).

5. Le programme affiche un rapport final détaillé.


## ✅ Exemple de sortie attendue

```yaml
[ALERTE] PaiementCarte PC01 suspecté de fraude : montant élevé depuis US
[OK] PaiementCarte PC02 traité normalement
[ALERTE] VirementBancaire VB01 suspect : donation de plus de 10000 €
[CRITIQUE] Rejet RJ01 : compte clôturé (bloquant)
[OK] Rejet RJ02 sans impact

=== RAPPORT FINAL ===
- Événements normaux : 2
- Suspects : 2
- Critiques : 1

```

## 🛠 Contraintes

- Utiliser uniquement les switch sur sealed interfaces + record patterns + when

- Aucun if en dehors des when

- Le traitement doit utiliser StructuredTaskScope.ShutdownOnFailure

- Les Virtual Threads sont obligatoires

- Le résultat de chaque analyse doit être imprimé depuis le thread d'analyse
