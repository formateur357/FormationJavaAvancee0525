# 7.1 Les Types en Java (Java 5 et au-delà)

Java 5 a introduit plusieurs évolutions majeures du langage liées aux types, destinées à **renforcer la sécurité du typage** et à **réduire le code boilerplate**. Voici les principales nouveautés.

---

## **1. Les Génériques**

### **But :** fournir un **typage sûr** des collections et d'autres types de conteneurs à la compilation.

### **Avant (Java 1.4) :**
```java
List liste = new ArrayList();
liste.add("Bonjour");
String val = (String) liste.get(0); // cast manuel
```

### Avec les génériques (Java 5+) :

```java
List<String> liste = new ArrayList<>();
liste.add("Bonjour");
String val = liste.get(0); // pas besoin de cast
```

### Cas d’usage concret : un dépôt typé

```java
public class Depot<T> {
    private final List<T> elements = new ArrayList<>();

    public void ajouter(T element) {
        elements.add(element);
    }

    public T retirer() {
        return elements.remove(0);
    }
}
```
```java
Depot<String> d1 = new Depot<>();
d1.ajouter("message");

Depot<Integer> d2 = new Depot<>();
d2.ajouter(42);
```

---

## 2. Les Énumérations (Enums)

### But : remplacer les constantes public static final par un type sûr et orienté objet.

### Exemple simple :

```java
public enum Jour {
    LUNDI, MARDI, MERCREDI, JEUDI, VENDREDI, SAMEDI, DIMANCHE
}
```

### Avec méthode et attributs :

```java
public enum Priorite {
    HAUTE(3), MOYENNE(2), BASSE(1);

    private final int niveau;

    Priorite(int niveau) {
        this.niveau = niveau;
    }

    public int getNiveau() {
        return niveau;
    }
}
```

## 3. Autoboxing / Unboxing

### But : simplifier la conversion entre types primitifs (int, double, etc.) et leurs objets wrapper (Integer, Double, etc.)

### Sans autoboxing (avant Java 5) :

```java
Integer i = new Integer(5);
int x = i.intValue();
```

### Avec autoboxing :

```java
Integer i = 5;   // autoboxing
int x = i;       // unboxing
```

---

## 4. Les Records (Java 14+)

Bien que postérieurs à Java 5, les records prolongent l’idée de typage compact et sécurisé.

### But : créer rapidement une classe immuable avec des données et des accesseurs.

### Syntaxe :

```java
public record Utilisateur(String nom, int age) {}
```

### Utilisation :

```java
Utilisateur u = new Utilisateur("Alice", 30);
System.out.println(u.nom() + " a " + u.age() + " ans.");
```

### Résumé visuel :

```text
|-------------------|-------|---------------------------------------|
| Fonctionnalité    | Java	| Objectif principal                    |
|-------------------|-------|---------------------------------------|
| Génériques	    | 5	    | Typage sécurisé des collections       |
|-------------------|-------|---------------------------------------|
| Enums	            | 5	    | Constantes typées et orientées objet  |
|-------------------|-------|---------------------------------------|
| Autoboxing	    | 5	    | Simplification des conversions        |
|-------------------|-------|---------------------------------------|
| Records	        | 14    | Définition simple de POJOs immuables  |
|-------------------|-------|---------------------------------------|
```
---

# 7.2 Les Lambda-Expressions et Interfaces Fonctionnelles (Java 8)

Java 8 a introduit une avancée majeure avec la **programmation fonctionnelle** via les **expressions lambda**. Cette fonctionnalité permet d'écrire du code plus concis et plus lisible, en particulier dans les API orientées fonctions comme les **Streams**.

---

## **1. Interface Fonctionnelle**

### Définition :
Une **interface fonctionnelle** est une interface avec **une seule méthode abstraite**. Annotée avec `@FunctionalInterface` pour la lisibilité (facultatif mais recommandé).

### Exemple :

```java
@FunctionalInterface
public interface Operation {
    int appliquer(int a, int b);
}
```

---

## 2. Expression Lambda

### Syntaxe générale :

```java
(paramètres) -> expression
```

### Exemple simple avec l’interface Operation :

```java
Operation addition = (a, b) -> a + b;
System.out.println(addition.appliquer(5, 3)); // Affiche 8
```

## 3. Utilisation avec les API Java

### Ex : avec Runnable :

```java
Runnable tache = () -> System.out.println("Exécution dans un thread !");
new Thread(tache).start();
```

### Ex : avec Comparator

```java
List<String> noms = Arrays.asList("Alice", "Bob", "Claire");
noms.sort((a, b) -> b.compareTo(a)); // tri décroissant
```

---

## 4. Référence de méthode

Permet de réutiliser une méthode existante de façon concise.

### Exemple :

```java
List<String> noms = Arrays.asList("Alice", "Bob", "Claire");
noms.forEach(System.out::println); // équivalent à (s) -> System.out.println(s)
```

## 5. Cas d’usage concret : filtre de produits

```java
import java.util.*;
import java.util.stream.*;

public class Produit {
    String nom;
    double prix;

    public Produit(String nom, double prix) {
        this.nom = nom;
        this.prix = prix;
    }

    public String toString() {
        return nom + " - " + prix + " €";
    }

    public static void main(String[] args) {
        List<Produit> produits = List.of(
            new Produit("Clavier", 49.99),
            new Produit("Souris", 19.99),
            new Produit("Écran", 149.99)
        );

        // Filtrage avec lambda
        produits.stream()
                .filter(p -> p.prix < 50)
                .forEach(System.out::println);
    }
}
```

---

## 6. Interfaces fonctionnelles standard

```text
|---------------|-------------------|---------------------------|
| Interface	    | Signature	        | Utilisation typique       |
|---------------|-------------------|---------------------------|
| Runnable	    | void run()	    | Tâche sans retour         |
|---------------|-------------------|---------------------------|
| Callable<T>	| T call()	        | Tâche avec retour         |
|---------------|-------------------|---------------------------|
| Consumer<T>	| void accept(T t)	| Traitement d’un objet     |
|---------------|-------------------|---------------------------|
| Function<T,R>	| R apply(T t)	    | Transformation            |
|---------------|-------------------|---------------------------|
| Predicate<T>	| boolean test(T t)	| Filtrage, test condition  |
|---------------|-------------------|---------------------------|
| Supplier<T>	| T get()	        | Fourniture d’objet        |
|---------------|-------------------|---------------------------|
```

---

# 7.3 Les Streams de Java 8

Les **Streams** introduits en Java 8 permettent de traiter des collections de manière déclarative et fonctionnelle. Ils facilitent les opérations comme le filtrage, le tri, la transformation, l’agrégation, de façon concise et parallèle si besoin.

---

## **1. Définition d’un Stream**

Un `Stream` est une **séquence d’éléments** provenant d’une source (collection, tableau, etc.) sur laquelle on applique des **opérations intermédiaires** et **terminales**.

```java
List<String> noms = List.of("Alice", "Bob", "Claire");
noms.stream()
    .filter(n -> n.length() > 3)
    .sorted()
    .forEach(System.out::println);
```

---

## 2. Opérations sur les Streams

### a. Opérations intermédiaires (retournent un Stream) :

- filter(Predicate) : filtre selon une condition

- map(Function) : transforme chaque élément

- sorted() : trie les éléments

- distinct() : supprime les doublons

- limit(n) / skip(n)

### b. Opérations terminales :

- forEach(Consumer) : traitement unitaire

- collect(...) : rassemble dans une collection

- count(), min(), max(), anyMatch(...), etc.

- reduce(...) : agrégation personnalisée

---

## 3. Exemple concret : traitement de stock de produits

```java
import java.util.*;
import java.util.stream.*;

class Produit {
    String nom;
    double prix;
    int quantite;

    public Produit(String nom, double prix, int quantite) {
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
    }

    public String toString() {
        return nom + " - " + prix + " € (" + quantite + ")";
    }

    public double valeurStock() {
        return prix * quantite;
    }
}

public class ExempleStream {
    public static void main(String[] args) {
        List<Produit> produits = List.of(
            new Produit("Clavier", 49.99, 15),
            new Produit("Souris", 19.99, 50),
            new Produit("Écran", 149.99, 10),
            new Produit("Casque", 89.99, 8)
        );

        // 1. Liste des produits en stock faible (< 10 unités)
        System.out.println("Produits en stock faible :");
        produits.stream()
                .filter(p -> p.quantite < 10)
                .forEach(System.out::println);

        // 2. Valeur totale du stock
        double total = produits.stream()
                               .mapToDouble(Produit::valeurStock)
                               .sum();
        System.out.println("Valeur totale du stock : " + total + " €");

        // 3. Liste triée par valeur du stock décroissante
        System.out.println("Produits triés par valeur du stock :");
        produits.stream()
                .sorted((p1, p2) -> Double.compare(p2.valeurStock(), p1.valeurStock()))
                .forEach(System.out::println);
    }
}
```

---

## 4. Stream parallèle

Java permet de paralléliser automatiquement certaines opérations :

```java
produits.parallelStream()
        .mapToDouble(Produit::valeurStock)
        .sum();
```

⚠️ À utiliser avec précaution sur de grandes collections et traitements indépendants.


---

# 7.4 Les modules de Java 9

Java 9 a introduit un **système de modules** avec le projet **Jigsaw**, permettant d’organiser et encapsuler le code en unités de déploiement nommées **modules**.

---

## **1. Objectif des modules**

- Améliorer la **modularité** des applications Java.
- Mieux contrôler les **dépendances** entre parties d’un programme.
- Cacher des parties internes du code (encapsulation forte).
- Optimiser le temps de démarrage et la taille des distributions.

---

## **2. Structure d’un module**

Un module est défini par un fichier `module-info.java` à la racine du répertoire `src`.

### Exemple minimal :
```java
module com.monapp.util {
    exports com.monapp.util;
}
```

- module com.monapp.util : nom du module.

- exports : rend le package accessible aux autres modules.

## **3. Déclaration des dépendances

Pour utiliser un autre module, on le déclare avec requires.

```java
module com.monapp.client {
    requires com.monapp.util;
}
```

## **4. Exemple de cas d’usage

### a. Structure du projet :

```cpp
src/
├── com.monapp.util/
│   ├── module-info.java
│   └── Utilitaire.java
├── com.monapp.client/
│   ├── module-info.java
│   └── Application.java
```

### b. com.monapp.util/module-info.java

```java
module com.monapp.util {
    exports com.monapp.util;
}
```

### c. com.monapp.util/Utilitaire.java

```java
package com.monapp.util;

public class Utilitaire {
    public static String bonjour(String nom) {
        return "Bonjour, " + nom + " !";
    }
}
```

### d. com.monapp.client/module-info.java

```java
module com.monapp.client {
    requires com.monapp.util;
}
```

### e. com.monapp.client/Application.java

```java
package com.monapp.client;

import com.monapp.util.Utilitaire;

public class Application {
    public static void main(String[] args) {
        System.out.println(Utilitaire.bonjour("Alice"));
    }
}
```

## **5. Compilation avec javac

```sh
javac -d out --module-source-path src $(find src -name "*.java")
```

## **6. Exécution avec java

```sh
java --module-path out -m com.monapp.client/com.monapp.client.Application
```

## **7. Avantages du système de modules

- Meilleure lisibilité et maintenabilité des dépendances.

- Séparation claire entre API publique et code interne.

- Possibilité de créer des images personnalisées de la JVM avec jlink.

---

Suite