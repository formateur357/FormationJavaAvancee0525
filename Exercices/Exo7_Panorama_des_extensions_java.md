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