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

### Specifications de types pour une methode de la classe generique

```java
public interface Ajouteur<T> {
    void afficherAjout(T element);
}

public class AjouteurString implements Ajouteur<String> {
    @Override
    public void afficherAjout(String element) {
        System.out.println("Ajout de chaîne : " + element.toUpperCase());
    }
}

public class AjouteurParDefaut<T> implements Ajouteur<T> {
    @Override
    public void afficherAjout(T element) {
        System.out.println("Ajout : " + element);
    }
}

public class Depot<T> {
    private final List<T> elements = new ArrayList<>();
    private final Ajouteur<T> ajouteur;

    public Depot(Ajouteur<T> ajouteur) {
        this.ajouteur = ajouteur;
    }

    public void ajouter(T element) {
        ajouteur.afficherAjout(element);
        elements.add(element);
    }

    public T retirer() {
        return elements.remove(0);
    }
}
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

## **6. Exécution avec java**

```sh
java --module-path out -m com.monapp.client/com.monapp.client.Application
```

## **7. Avantages du système de modules**

- Meilleure lisibilité et maintenabilité des dépendances.

- Séparation claire entre API publique et code interne.

- Possibilité de créer des images personnalisées de la JVM avec jlink.

---

# **7.5 Apports des nouvelles versions de Java (Java 10 et au-delà)**

Depuis Java 10, le langage a évolué pour offrir une syntaxe plus concise, une meilleure performance et de nouvelles API. Voici un aperçu des principales nouveautés introduites depuis cette version.

## 1. Java 10 – Inference de type local et autres apports

L’introduction du mot-clé var permet d’inférer automatiquement le type des variables locales. Cela permet de réduire le code boilerplate tout en gardant la clarté du code. Voici quelques exemples améliorés :

### Exemple de base

```java
var message = "Bonjour, Java 10!";
System.out.println(message);
```

Le compilateur déduit que message est de type String.

### Exemple avec calculs et collections

```java
var nombre = 42;       // déduit int
var decimale = 3.14;   // déduit double

var liste = new ArrayList<String>();
liste.add("Java");
liste.add("10");

var resultat = liste.stream()
                    .map(s -> s.toUpperCase())
                    .collect(Collectors.joining(" - "));
System.out.println(resultat);
```

Ce deuxième exemple montre comment var peut simplifier la déclaration de variables pour les calculs et manipuler les collections Java.

## 2. Autres améliorations de Java 10

### a. Améliorations du ramasse-miettes et performances

Java 10 a apporté des optimisations dans la gestion mémoire permettant une meilleure réactivité de l’application, bien que ces améliorations soient transparents au niveau du code.

### b. Nouvelle API pour la création de collections immuables

Java 10 a renforcé certaines API introduites dans les versions précédentes en facilitant la création de collections immuables. Par exemple, la méthode `copyOf` permet de créer facilement des copies en lecture seule d’une collection existante.

```java
List<String> original = List.of("Alice", "Bob", "Claire");
List<String> copie = List.copyOf(original);
System.out.println(copie);
```

Cette méthode garantit que la nouvelle collection est immuable et ne peut pas être modifiée.

### c. Amélioration de l'inférence dans les expressions lambda

Même dans les expressions lambda, Java 10 profite de cette inférence de type pour rendre le code plus concis :

```java
var addition = (Integer a, Integer b) -> a + b;
System.out.println("Addition : " + addition.apply(5, 3));
```

Avec l’inférence de type sur les paramètres explicitement déclarés, le code reste clair tout en bénéficiant des avantages de la simplification.

---

Ces exemples et apports rendent Java 10 particulièrement attractif pour les développeurs souhaitant écrire du code plus lisible et moins verbeux tout en profitant d’un ensemble d’optimisations en coulisses.

## 2. Java 11 – Améliorations de l'API et performances

Java 11, version LTS, introduit plusieurs améliorations significatives qui facilitent le développement :

- Nouvelle API HTTP Client pour simplifier les appels réseau.
- Méthodes utilitaires avancées pour la manipulation des chaînes de caractères et des fichiers.
- Améliorations des API de collections.
- Possibilité d'écrire du code asynchrone et réactif plus facilement.
- Optimisations internes pour une gestion de la mémoire plus efficace.

### a. Nouvelle API HTTP Client

Java 11 fournit une API HTTP moderne qui supporte les opérations synchrones et asynchrones.

Exemple d'appel synchrone :
```java
var client = HttpClient.newHttpClient();
var request = HttpRequest.newBuilder()
                         .uri(URI.create("https://example.com"))
                         .GET()
                         .build();
var response = client.send(request, HttpResponse.BodyHandlers.ofString());
System.out.println("Réponse : " + response.body());
```

Exemple d'appel asynchrone :
```java
var client = HttpClient.newHttpClient();
var request = HttpRequest.newBuilder()
                         .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
                         .GET()
                         .build();
client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
      .thenApply(HttpResponse::body)
      .thenAccept(System.out::println)
      .join();
```

### b. Améliorations de la manipulation des chaînes

La classe String de Java 11 est enrichie avec plusieurs méthodes pratiques :

- **isBlank()** : vérifie si la chaîne est vide ou ne contient que des espaces.
- **strip()** : retire les espaces en début et en fin, avec une gestion complète de l'Unicode.
- **repeat(int count)** : répète la chaîne un nombre donné de fois.
- **lines()** : retourne un Stream comprenant toutes les lignes de la chaîne.

Exemple :
```java
String texte = "   Bonjour Java 11   ";
System.out.println("Est blanc ? " + texte.isBlank());
System.out.println("Après strip : '" + texte.strip() + "'");
System.out.println("Répété trois fois : " + texte.strip().repeat(3));

String multiLigne = "Première ligne\nDeuxième ligne\nTroisième ligne";
multiLigne.lines().forEach(line -> System.out.println("* " + line));
```

### c. Nouveaux outils pour la lecture et l’écriture de fichiers

Java 11 simplifie la manipulation des fichiers texte grâce aux méthodes **Files.readString** et **Files.writeString**.

Exemple de lecture :
```java
Path path = Paths.get("exemple.txt");
try {
    String contenu = Files.readString(path);
    System.out.println(contenu);
} catch (IOException e) {
    e.printStackTrace();
}
```

Exemple d'écriture :
```java
Path path = Paths.get("sortie.txt");
String message = "Ceci est un test en Java 11.";
try {
    Files.writeString(path, message);
    System.out.println("Fichier écrit avec succès !");
} catch (IOException e) {
    e.printStackTrace();
}
```

### d. Autres apports de Java 11

- **Améliorations sur les collections :** L'ajout ou la modification de méthodes (comme toArray avec IntFunction) facilite certaines conversions et manipulations.
- **Optimisations de la JVM :** Améliorations de la gestion de la mémoire et du ramassage des ordures pour une performance renforcée.
- **Outils de diagnostics :** De nouvelles options de monitoring et de diagnostic ont été introduites pour mieux observer le comportement d'une application en production.

Ainsi, Java 11 renforce non seulement les fonctionnalités existantes mais propose également de nouveaux outils pour rendre le développement plus fluide et performant.

---

## 3. Java 12 et 13 – Switch Expressions en Preview et Autres Améliorations

Les versions 12 et 13 ont révolutionné la gestion des conditions multiples en introduisant les switch expressions en preview. Cette fonctionnalité permet non seulement de simplifier la syntaxe du switch, mais aussi de le rendre plus expressif. De plus, ces versions ont apporté d'autres améliorations expérimentales qui préparent le terrain pour des évolutions futures de Java.

### A. Switch Expressions Améliorées

En Java 12, les expressions switch ont été introduites en mode preview. Elles permettent, grâce à la syntaxe « flèche » (->) et l’instruction yield, de retourner directement une valeur. Java 13 a affiné cette syntaxe pour en améliorer la lisibilité et réduire les ambiguïtés.

Exemple amélioré pour déterminer un message selon le jour de la semaine :
```java
var day = DayOfWeek.MONDAY; // Exemple d'énumération java.time.DayOfWeek

var message = switch(day) {
    case MONDAY, FRIDAY, SUNDAY -> "Jour partiellement ouvré";
    case TUESDAY              -> "Journée de travail complète";
    default -> {
        // Traitement complexe possible dans un bloc
        String res = day.toString().toLowerCase();
        yield "Jour défini : " + res;
    }
};
System.out.println("Message: " + message);
```

Un autre exemple qui montre comment imbriquer des conditions pour calculer un tarif sur un produit :
```java
var typeProduit = "PREMIUM"; // Exemple : "BASIC", "PREMIUM" ou "DELUXE"

var tarif = switch(typeProduit) {
    case "BASIC" -> 29.99;
    case "PREMIUM" -> {
        // Calcul complexe possible
        double base = 29.99;
        double majoration = 10;
        yield base + majoration;
    }
    case "DELUXE" -> 59.99;
    default -> 0.0;
};
System.out.println("Tarif: " + tarif + " €");
```

### B. Autres Améliorations de Java 12

- Correction et simplification de la syntaxe pour faciliter la maintenance du code.
- Possibilité d’utiliser les switch expressions dans des affectations ou directement dans des expressions.
- Introduction d’autres améliorations expérimentales (comme le JVM Constants API) qui permettent d’accéder de façon optimisée aux métadonnées des classes.

### C. Raffinements Apportés par Java 13

- La syntaxe des switch expressions a été encore affinée pour réduire le besoin d’un bloc de code lorsque la logique est simple.
- Une meilleure gestion des cas par défaut, assurant que le compilateur vérifie la couverture de tous les cas possibles.
- Les exemples en Java 13 gagnent en clarté grâce à ces ajustements syntaxiques. Par exemple, il est désormais possible d’éviter l’utilisation du mot-clé yield dans des cas simples :
```java
var season = switch(month) {
    case 3, 4, 5 -> "Printemps";
    case 6, 7, 8 -> "Été";
    case 9, 10, 11 -> "Automne";
    case 12, 1, 2 -> "Hiver";
};
System.out.println("Saison: " + season);
```

Ces améliorations dans Java 12 et 13 témoignent de la volonté de Java de réduire le boilerplate et d’introduire progressivement des paradigmes plus fonctionnels, tout en gardant la lisibilité et la robustesse du code.


## 4. Java 14 et 15 – Records, Text Blocks et autres apports

Java 14 et 15 apportent plusieurs améliorations qui simplifient le code et renforcent la sécurité en compile-time. Ci-dessous, des exemples détaillés pour illustrer ces nouveautés.

### 4.1 Records améliorés (Java 14)
Les Records permettent de déclarer des classes immuables en une seule ligne. En Java 14, ils sont proposés en mode preview et permettent de simplifier la création de POJOs.

#### Exemple avancé avec constructeur compact et validation
```java
public record Point(int x, int y) {
    // Constructeur compact pour valider les coordonnées
    public Point {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Les coordonnées doivent être positives");
        }
    }

    // Méthode d'instance possible
    public double distance() {
        return Math.sqrt(x * x + y * y);
    }
}

public class TestPoint {
    public static void main(String[] args) {
        Point p = new Point(3, 4);
        System.out.println("Distance à l'origine : " + p.distance());
    }
}
```
Cet exemple montre comment définir une validation dans le constructeur compact du record.

### 4.2 Text Blocks améliorés (Java 15)
Les Text Blocks permettent de créer des chaînes multi-lignes sans échappements fastidieux. Java 15 a officialisé cette fonctionnalité, rendant leur utilisation plus stable.

#### Exemple de Text Block pour du contenu HTML
```java
String html = """
    <html>
        <head>
            <title>Exemple de Text Block</title>
        </head>
        <body>
            <h1>Bienvenue</h1>
            <p>Ceci est un exemple de text block en Java 15.</p>
        </body>
    </html>
    """;
System.out.println(html);
```
L'utilisation des text blocks améliore la lisibilité et facilite l'inclusion de contenu formatté.

### 4.3 Autres apports intéressants

#### Pattern Matching pour instanceof (Java 14 - Preview)
Cette fonctionnalité simplifie le casting et réduit le boilerplate lors du test des types.
```java
Object obj = "Bonjour Java";
if (obj instanceof String s && s.length() > 5) {
    System.out.println("La chaîne étendue est : " + s.toUpperCase());
}
```

#### Sealed Classes (Java 15 - Preview)
Les sealed classes permettent de limiter les classes qui peuvent étendre ou implémenter une classe ou interface.
```java
public sealed class Animal permits Chien, Chat {
}

final class Chien extends Animal {
}

final class Chat extends Animal {
}

public class TestAnimal {
    public static void main(String[] args) {
        Animal animal = new Chien();
        if (animal instanceof Chien) {
            System.out.println("Ceci est un chien.");
        }
    }
}
```
Cette approche renforce l'encapsulation et offre un meilleur contrôle des hiérarchies de classes.

Ces améliorations apportées par Java 14 et 15 permettent de réduire le boilerplate, d'améliorer la lisibilité du code et de renforcer la sécurité lors de la manipulation des types et des structures de données.


## Java 16 – Sécurité, performance et fonctionnalités avancées

### 1. Classes Scellées et Contrôle Précis de l'Héritage

Les classes scellées permettent de limiter les sous-classes autorisées, renforçant l'encapsulation.

```java
public sealed class Animal permits Chien, Chat, Oiseau {
    public abstract void crier();
}

final class Chien extends Animal {
    @Override
    public void crier() {
        System.out.println("Wouf!");
    }
}

final class Chat extends Animal {
    @Override
    public void crier() {
        System.out.println("Miaou!");
    }
}

non-sealed class Oiseau extends Animal {
    @Override
    public void crier() {
        System.out.println("Cui cui!");
    }
}
```

### 2. Records Standardisés et Améliorations

Les records, désormais officiels en Java 16, permettent de définir des classes immuables de manière concise avec validation intégrée.

```java
public record Utilisateur(String nom, int age) {
    public Utilisateur {
        if(age < 0) {
            throw new IllegalArgumentException("L'âge doit être positif");
        }
    }
}
```

### 3. Pattern Matching pour instanceof

Java 16 affine le pattern matching pour simplifier les vérifications et extractions de types.

```java
Object obj = "Ceci est une chaîne de caractères";
if (obj instanceof String s && s.length() > 10) {
    System.out.println("Longueur de la chaîne : " + s.length());
}
```

### 4. Autres Améliorations Majeures de Java 16

- Optimisations des Garbage Collectors (ZGC, Shenandoah) pour réduire les temps de pause.
- Encapsulation renforcée du JDK pour une sécurité accrue.
- API Unix-Domain Socket Channels facilitant la communication inter-processus.
- API Foreign Function & Memory (Incubateur) pour une interaction sécurisée avec du code natif.

### 5. Exemple Concret d'Utilisation Avancée

Combinaison de records et pattern matching pour simplifier le traitement d'une transaction financière :

```java
public record Transaction(String id, double montant) {}

public class TraitementTransaction {
    public static void traiter(Object obj) {
        if (obj instanceof Transaction t && t.montant() > 1000) {
            System.out.println("Transaction importante : " + t);
        } else {
            System.out.println("Transaction standard.");
        }
    }

    public static void main(String[] args) {
        Transaction t1 = new Transaction("TXN001", 500);
        Transaction t2 = new Transaction("TXN002", 1500);
        traiter(t1);
        traiter(t2);
    }
}
```

# 7.6 Nouveautés des Versions Récentes de Java

## Java 17 – Premier LTS depuis Java 11

Java 17 finalise et enrichit plusieurs fonctionnalités initiées dans les versions précédentes tout en introduisant de nouveaux apports pour améliorer la qualité et la maintenabilité du code.

### 1. Sealed Classes et Interfaces

Les classes scellées permettent de contrôler explicitement les sous-classes autorisées et ainsi renforcer l'encapsulation.

#### Exemples améliorés

Sealed interface avec implémentations concrètes et utilisation du pattern matching pour instanceof :

```java
public sealed interface Forme permits Cercle, Rectangle {
    double aire();
}

final class Cercle implements Forme {
    private final double rayon;
    
    public Cercle(double rayon) {
        this.rayon = rayon;
    }
    
    @Override
    public double aire() {
        return Math.PI * rayon * rayon;
    }
}

final class Rectangle implements Forme {
    private final double largeur;
    private final double hauteur;
    
    public Rectangle(double largeur, double hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
    
    @Override
    public double aire() {
        return largeur * hauteur;
    }
}

public class TestForme {
    public static void main(String[] args) {
        Forme forme = new Cercle(5);
        if (forme instanceof Cercle c) {
            System.out.println("Aire du cercle : " + c.aire());
        } else if (forme instanceof Rectangle r) {
            System.out.println("Aire du rectangle : " + r.aire());
        }
    }
}
```

### 2. Amélioration du Pattern Matching pour instanceof

Java 17 étend l'utilisation du pattern matching dans les conditions, simplifiant ainsi le cast et la vérification de types.

#### Exemple avec vérification simplifiée

```java
public class Traitement {
    public static void afficherTaille(Object obj) {
        if (obj instanceof String s && s.length() > 10) {
            System.out.println("Longueur de la chaîne : " + s.length());
        } else {
            System.out.println("Objet non string ou chaîne trop courte.");
        }
    }

    public static void main(String[] args) {
        afficherTaille("Bonjour, Java 17 !");
        afficherTaille(42);
    }
}
```

### 3. Nouveaux API et Optimisations

Java 17 introduit plusieurs améliorations API et optimisations de la JVM, notamment :
- Une API améliorée de génération de nombres pseudo-aléatoires avec l’interface RandomGenerator.
- Des messages d'exception plus explicites facilitant le débogage.
- Des optimisations internes pour une gestion de la mémoire et du ramassage des ordures renforcée.

#### Exemple – Utilisation du générateur de nombres aléatoires

```java
import java.util.random.RandomGenerator;

public class RandomGeneratorDemo {
    public static void main(String[] args) {
        RandomGenerator generator = RandomGenerator.getDefault();
        System.out.println("Nombre aléatoire entre 1 et 100 : " + generator.nextInt(1, 100));
    }
}
```

### 4. Autres Apports de Java 17

- **Enhanced Exception Messages :** Les exceptions offrent désormais des messages plus détaillés, facilitant leur compréhension et leur correction.
- **Stabilité et Performance :** Les améliorations internes de la JVM se traduisent par une meilleure optimisation des ressources et des performances accrues.
- **Finalisation des Préviews :** Plusieurs fonctionnalités auparavant en mode preview (comme le pattern matching) sont stabilisées et intégrées officiellement dans le langage, offrant ainsi une syntaxe modernisée et plus sûre.

Ces ajouts et améliorations font de Java 17 une version LTS robuste et avantageuse, simplifiant la conception du code tout en le rendant plus lisible et sécurisé.


## Java 18 – Simplicité et nouvelles API

Java 18 continue à améliorer la productivité en introduisant des API ciblées sur l’expérimentation et des configurations par défaut optimisées. Voici quelques apports majeurs :

- Introduction d’un serveur web simple (JEP 408) pour des scénarios de tests rapides.
- Exécution asynchrone et gestion de la concurrence grâce à l’intégration d’un Executor.
- Amélioration de la configuration par défaut (UTF-8 par exemple) pour simplifier le développement multiplateforme.
- Optimisations internes de la JVM améliorant les performances et la gestion de la mémoire.

### Exemple – Serveur web simple avec gestion asynchrone
Cet exemple montre comment démarrer un serveur web simple en Java 18 en utilisant un Executor pour gérer plusieurs requêtes simultanément :

```java
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class AsyncSimpleWebServer {
    public static void main(String[] args) throws IOException {
        // Création et configuration du serveur
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String response = "Bonjour depuis Java 18 avec Executor!";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        });
        // Attribution d'un pool de threads pour gérer les requêtes de façon concurrente
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("Serveur asynchrone démarré sur le port 8000");
    }
}
```

### Autres apports et exemples illustratifs

#### 1. Charset UTF-8 par défaut
Java 18 configure désormais UTF-8 comme charset par défaut pour la plupart des API, ce qui simplifie le traitement des chaînes de caractères et la compatibilité multiplateforme. Par exemple, la lecture d’un fichier texte ne nécessite plus de spécifier explicitement l’encodage :

```java
import java.nio.file.Files;
import java.nio.file.Path;

public class ReadFileExample {
    public static void main(String[] args) throws Exception {
        Path fichier = Path.of("exemple.txt");
        // UTF-8 est utilisé par défaut
        String contenu = Files.readString(fichier);
        System.out.println(contenu);
    }
}
```

#### 2. Améliorations des diagnostics et des performances JVM
Java 18 inclut des optimisations internes qui se traduisent par :
- Une gestion améliorée de la mémoire.
- Des messages d’exception plus clairs facilitant le débogage.
- Une meilleure optimisation des ressources sous contrainte, ce qui est particulièrement utile dans les environnements conteneurisés.

Ces améliorations rendent le développement et le déploiement d’applications plus fiables et leur comportement en production plus prévisible.

En résumé, Java 18 simplifie le prototypage de services web et augmente la productivité grâce à des API modernes et des configurations par défaut optimisées.

## Java 19 – Expérimentation et Concurrence Avancée

Java 19 poursuit l'exploration de nouvelles approches en matière de concurrence et de simplification de la gestion asynchrone grâce à plusieurs fonctionnalités en mode preview. En plus des Virtual Threads, la version 19 enrichit le langage avec des améliorations dans les Record Patterns et inaugure une API de Structured Concurrency en expérimentation.

### 1. Virtual Threads en profondeur

Les Virtual Threads offrent un modèle de concurrence léger, permettant de lancer un grand nombre de threads sans le coût habituel lié aux threads traditionnels. Cela simplifie notamment l'écriture d'applications réseau et asynchrones.

#### Exemple amélioré – Lancement massif de Virtual Threads

```java
import java.util.stream.IntStream;

public class VirtualThreadMassif {
    public static void main(String[] args) {
        // Lancer 1000 tâches concurrentes utilisant des Virtual Threads
        IntStream.range(0, 1000).forEach(i -> 
            Thread.startVirtualThread(() -> {
                System.out.println("Virtual Thread numéro : " + i);
            })
        );
    }
}
```

### 2. Structured Concurrency

La Structured Concurrency vise à simplifier la gestion des tâches asynchrones en regroupant les threads lancés dans un scope contrôlé. Ceci offre une meilleure lisibilité du code et un contrôle fluide des erreurs.

#### Exemple – Utilisation de Structured Task Scope

```java
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;

public class StructuredConcurrencyDemo {
    public static void main(String[] args) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> future1 = scope.fork(() -> {
                Thread.sleep(500);
                return "Résultat de la tâche 1";
            });
            Future<String> future2 = scope.fork(() -> {
                Thread.sleep(300);
                return "Résultat de la tâche 2";
            });
            scope.join(); // Attendre la fin de toutes les tâches
            scope.throwIfFailed(); // Propager d’éventuelles exceptions
            System.out.println(future1.resultNow() + " et " + future2.resultNow());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Tâche interrompue");
        }
    }
}
```

Cette approche garantit que toutes les tâches démarrées au sein du scope se terminent correctement, améliorant ainsi la lisibilité et la robustesse du code asynchrone.

### 3. Record Patterns et Pattern Matching pour switch

Java 19 enrichit le Pattern Matching dans le contexte des records, permettant d'extraire et de vérifier des propriétés directement dans un switch. Cela rend le code plus expressif et concis.

#### Exemple – Traitement de transactions avec Record Patterns

```java
public record Transaction(String id, double montant) {}

public class TransactionProcessor {
    public static void main(String[] args) {
        Thread.startVirtualThread(() -> {
            Transaction txn = new Transaction("TXN-001", 1500.0);
            String evaluation = switch (txn) {
                case Transaction(String id, double montant) && (montant > 1000) -> 
                    "Transaction " + id + " a un montant élevé";
                default -> "Transaction standard";
            };
            System.out.println(evaluation);
        });
    }
}
```

Ce pattern matching avancé permet de combiner extraction et vérification dans une seule expression, ce qui simplifie le flux de code et réduit considérablement le boilerplate.

### 4. Autres Apports de Java 19

Outre ces fonctionnalités phares, Java 19 propose plusieurs autres améliorations expérimentales :

- Optimisations internes des Virtual Threads pour une meilleure gestion des ressources.
- Amélioration des outils de diagnostics et monitoring pour observer les comportements concurrentiels.
- Prévisualisation d'interfaces permettant d'intégrer plus facilement les callbacks et les transformations asynchrones dans les API existantes.

En combinant ces apports, Java 19 offre aux développeurs un environnement puissant pour écrire des applications hautement concurrentes, maintenables et plus faciles à comprendre.


## Java 20 – Perfectionnement et Stabilisation des Fonctionnalités Préliminaires

Java 20 poursuit l'évolution amorcée par Java 19 en stabilisant et en améliorant les fonctionnalités expérimentales, tout en en introduisant de nouveaux apports pour optimiser la programmation concurrente et le traitement des données.

### Principaux Apports de Java 20

- **Virtual Threads Optimisés :**
    - Réduction significative de la latence, rendant les virtual threads encore plus fiables pour des applications de haute concurrence.
    - Simplification du parallélisme dans des environnements I/O intensifs.

- **Record Patterns et Pattern Matching Améliorés :**
    - Affinage des conditions dans le switch, permettant de combiner extraction et validation dans une syntaxe plus expressive.
    - Prise en charge de vérifications complexes à l'aide de Record Patterns qui s'intègrent de manière transparente dans des blocs de code conditionnels.

- **Structured Concurrency Renforcée :**
    - Meilleure gestion de la durée de vie et du contrôle des erreurs pour les tâches asynchrones.
    - Permet d'avoir des scopes de tâches plus lisibles et contrôlés, facilitant la gestion des ressources partagées.

- **Améliorations des API Utilitaires et Diagnostics :**
    - Optimisations internes pour une meilleure gestion de la mémoire.
    - Messages d’exception détaillés qui facilitent le débogage.
    - Amélioration des outils de monitoring pour observer le comportement des applications en production.

### Exemples Détaillés

#### 1. Utilisation Avancée des Virtual Threads avec Record Patterns
Voici un exemple qui montre l'utilisation combinée des virtual threads et des Record Patterns pour détecter et traiter les transactions de grande valeur en plus d'extraire des informations détaillées.

```java
public record Transaction(String id, double montant, String type) {}

public class TransactionProcessor {
        public static void main(String[] args) {
                // Lancement d'une tâche asynchrone avec un Virtual Thread
                Thread.startVirtualThread(() -> {
                        Transaction t = new Transaction("TXN123", 1200, "TRANSFERT");
                        
                        // Utilisation avancée du pattern matching dans un switch
                        String message = switch (t) {
                                case Transaction(String id, double montant, String type) 
                                                when montant > 1000 && type.equals("TRANSFERT") ->
                                        "Transaction " + id + " de type TRANSFERT à haute valeur détectée.";
                                case Transaction(String id, double montant, String type) 
                                                when montant > 1000 ->
                                        "Transaction " + id + " à haute valeur détectée.";
                                default ->
                                        "Transaction standard.";
                        };
                        
                        System.out.println(message);
                });
        }
}
```

#### 2. Structured Concurrency pour le Contrôle des Tâches Asynchrones
Le scope de tâches permet de regrouper plusieurs opérations asynchrones, de gérer les erreurs et leurs ressources de manière sécurisée.

```java
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;

public class MultiTaskProcessor {
        public static void main(String[] args) {
                try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                        Future<String> task1 = scope.fork(() -> {
                                Thread.sleep(400);
                                return "Résultat de la tâche A";
                        });
                        Future<String> task2 = scope.fork(() -> {
                                Thread.sleep(600);
                                return "Résultat de la tâche B";
                        });
                        scope.join(); // Attend que toutes les tâches se terminent
                        scope.throwIfFailed(); // Propagation automatique d'une exception, si présente
                        
                        System.out.println(task1.resultNow());
                        System.out.println(task2.resultNow());
                } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("La tâche a été interrompue.");
                }
        }
}
```

#### 3. Nouveaux Apports Utilitaires et Diagnostics
Java 20 propose également des améliorations dans les API utilitaires qui facilitent la manipulation et le diagnostic du code.

- **Optimisation de la Lecture/Ecriture de Fichiers :**
    Utilisation améliorée des API de lecture et d’écriture avec de meilleurs contrôles sur l’unicode et les performances.

```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class FileUtils {
        public static void main(String[] args) {
                Path fichier = Path.of("donnees.txt");
                try {
                        // Lecture avec gestion optimisée des encodages
                        String contenu = Files.readString(fichier);
                        System.out.println("Contenu du fichier :\n" + contenu);
                        
                        String message = "Nouvelles données écrites en Java 20.";
                        Files.writeString(fichier, message);
                        System.out.println("Écriture réussie !");
                } catch (IOException e) {
                        System.err.println("Erreur lors de l'accès au fichier : " + e.getMessage());
                }
        }
}
```

- **Meilleur Monitoring et Messages d'Exception :**
    Les messages d'exception en Java 20 sont plus explicites, facilitant ainsi le débogage et la correction rapide des erreurs lors des phases de développement.

## Conclusion

Java 20 continue de consolider les avancées offertes par les versions précédentes, en se concentrant sur des performances accrues, une meilleure gestion de la concurrence et une expressivité améliorée du langage. Ces améliorations rendent le développement d'applications plus robuste, facilitent la maintenance et augmentent la productivité des développeurs.


## Java 21 – Dernier LTS et Consolidation des Nouveautés

Java 21, la dernière version LTS, consolide et standardise les innovations récentes du langage. Cette version apporte plusieurs améliorations majeures, notamment :

- Une prise en charge robuste des Virtual Threads et de la Structured Concurrency pour une gestion simplifiée et performante de la concurrence.
- Une intégration complète du Pattern Matching et des Record Patterns, permettant d'écrire des conditions plus expressives et des extraits de données concis.
- Des optimisations supplémentaires sur la performance, la sécurité et la gestion de la mémoire, ainsi que des améliorations dans les API natives et langagières.

### 1. Virtual Threads et Structured Concurrency

Les Virtual Threads simplifient la parallélisation en permettant de lancer des milliers de tâches avec une empreinte minimale. Combinez-les avec la Structured Concurrency pour regrouper plusieurs opérations asynchrones et mieux gérer leurs erreurs.

#### Exemple Avancé – Traitement Concurrent de Tâches avec Gestion d'Erreurs

```java
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;

public class ProcessConcurrentTasks {
    public static void main(String[] args) {
        // Utilisation d'un scope pour gérer plusieurs tâches avec Structured Concurrency
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> task1 = scope.fork(() -> {
                // Simuler une opération gourmande en I/O
                Thread.sleep(300);
                return "Résultat de la tâche 1";
            });
            Future<String> task2 = scope.fork(() -> {
                Thread.sleep(500);
                return "Résultat de la tâche 2";
            });
            scope.join(); // Attend que toutes les tâches se terminent
            scope.throwIfFailed(); // Propagation d'une exception si rencontré

            System.out.println(task1.resultNow());
            System.out.println(task2.resultNow());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("La tâche a été interrompue.");
        }
    }
}
```

Dans cet exemple, le scope garantit que toutes les tâches asynchrones se terminent correctement et gère automatiquement la propagation des erreurs.

### 2. Record Patterns et Pattern Matching amélioré

Java 21 renforce l’utilisation des Record Patterns. Cette fonctionnalité permet d’extraire directement des informations d’un record avec des conditions plus complexes, rendant le code plus clair et plus succinct.

#### Exemple – Validation Avancée d’Utilisateurs avec Record Patterns

```java
public record Utilisateur(String nom, int age) {
    public Utilisateur {
        if(age < 0) {
            throw new IllegalArgumentException("L'âge doit être positif");
        }
    }
}

public class VerificationUtilisateur {
    public static void main(String[] args) {
        Thread.startVirtualThread(() -> {
            Utilisateur user = new Utilisateur("Alice", 30);
            String message = switch (user) {
                case Utilisateur(String nom, int age) when age >= 18 -> 
                    nom + " est majeur et peut accéder aux fonctionnalités avancées.";
                case Utilisateur(String nom, int age) -> 
                    nom + " est mineur.";
                default ->
                    "Utilisateur non reconnu.";
            };
            System.out.println(message);
        });
    }
}
```

Ce code démontre comment combiner Virtual Threads, Record Patterns et Pattern Matching pour gérer de manière concise des logiques conditionnelles complexes.

### 3. Autres Améliorations et Apports de Java 21

Outre la gestion améliorée de la concurrence et du pattern matching, Java 21 propose :

- **Optimisations de la JVM :** Améliorations du ramassage de mémoire et optimisations internes qui réduisent la latence et améliorent la réactivité des applications.
- **API de Sécurité Renforcée :** Nouvelles API et mécanismes plus robustes pour la gestion de la sécurité, facilitant l'écriture d'applications plus sûres.
- **Améliorations des Outils Natifs :** Les Foreign Function & Memory API reçoivent des mises à jour qui simplifient l'interfaçage avec du code natif et la gestion de la mémoire hors-heap.
- **Diagnostic et Monitoring :** Des messages d'exception plus explicites et des outils de diagnostic améliorés aident les développeurs à mieux observer et déboguer leurs applications.

### Conclusion

Java 21 se positionne comme une version LTS de grande envergure qui intègre pleinement les innovations récentes. La robustesse des Virtual Threads et de la Structured Concurrency, associée aux capacités avancées du Pattern Matching et des Record Patterns, permet aux développeurs d'écrire du code moderne, efficace et plus facile à maintenir. Ces nouveautés, alliées aux optimisations de la JVM et aux améliorations en matière de sécurité, font de Java 21 une plateforme idéale pour le développement d'applications de nouvelle génération.

