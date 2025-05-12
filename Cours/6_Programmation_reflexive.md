# 6.1 Les objectifs et les principes de la programmation réflexive

## **Objectif**

La **programmation réflexive** (ou réflexion) permet à un programme Java d’**inspecter et manipuler dynamiquement des classes, objets, méthodes ou champs** à l'exécution, **sans connaître leur nom à la compilation**.

### Pourquoi utiliser la réflexion ?

- **Découverte dynamique** de types, champs, méthodes, annotations
- Chargement **d’extensions ou de plugins à la volée**
- Implémentation de **frameworks génériques** (ex : Spring, Hibernate)
- Réalisation de **tests automatisés avancés** (mocking, injection, etc.)
- Accès à des **éléments privés** (dans des cas très spécifiques)

---

## **Principes fondamentaux**

### 1. **Découverte de classes dynamiquement**

```java
Class<?> clazz = Class.forName("com.mondomaine.MonObjet");
```

### 2. Accès aux méthodes, champs, constructeurs

```java
Method methode = clazz.getMethod("afficher");
Field champ = clazz.getDeclaredField("nom");
Constructor<?> constructeur = clazz.getConstructor();
```

### 3. Appels dynamiques

```java
Object instance = constructeur.newInstance();
methode.invoke(instance);
```

### 4. Modification de champs

```java
champ.setAccessible(true);
champ.set(instance, "NouveauNom");
```

## Exemple de cas d’usage concret

### Objectif : Charger une classe dont le nom est inconnu à la compilation et exécuter une méthode

Classe cible

```java
public class Plugin {
    public void executer() {
        System.out.println("Plugin exécuté !");
    }
}
```

Programme réflexif

```java
public class ChargeurDynamique {
    public static void main(String[] args) throws Exception {
        String classeCible = "Plugin";

        // Chargement de la classe dynamiquement
        Class<?> pluginClass = Class.forName(classeCible);

        // Création d'une instance
        Object pluginInstance = pluginClass.getDeclaredConstructor().newInstance();

        // Récupération de la méthode "executer"
        Method methode = pluginClass.getMethod("executer");

        // Invocation
        methode.invoke(pluginInstance);
    }
}
```

## Cas d’usage réel

- Les frameworks de tests (JUnit, Mockito) utilisent la réflexion pour trouver et exécuter des tests automatiquement.

- Spring Framework utilise la réflexion pour l’injection de dépendances.

- ORM comme Hibernate inspectent les classes avec la réflexion pour mapper les entités vers des tables SQL.

---

# 6.2 Découverte dynamique des informations relatives à une classe ou à un objet

## **Objectif**

Cette étape consiste à **interroger dynamiquement une classe ou un objet** pour obtenir :

- Sa **classe réelle**
- Ses **champs**, **méthodes**, **constructeurs**
- Ses **modificateurs d’accès** (public, private, static…)
- Ses **annotations**, **interfaces**, **classes parentes**

---

## **1. Obtenir la classe d’un objet à l’exécution**

```java
Object obj = new String("Hello");
Class<?> clazz = obj.getClass();
System.out.println("Classe : " + clazz.getName());
```

## 2. Obtenir les champs d'une classe

```java
Field[] champs = clazz.getDeclaredFields();
for (Field champ : champs) {
    System.out.println("Champ : " + champ.getName() + " de type " + champ.getType().getSimpleName());
}
```

### Astuce :

- getFields() retourne uniquement les champs publics (y compris hérités).

- getDeclaredFields() retourne tous les champs déclarés, même private.

## 3. Obtenir les méthodes d'une classe

```java
Method[] methodes = clazz.getDeclaredMethods();
for (Method m : methodes) {
    System.out.println("Méthode : " + m.getName());
}
```

## 4. Obtenir les constructeurs

```java
Constructor<?>[] constructeurs = clazz.getDeclaredConstructors();
for (Constructor<?> c : constructeurs) {
    System.out.println("Constructeur avec " + c.getParameterCount() + " paramètre(s)");
}
```

## 5. Obtenir les interfaces et superclasses

```java
Class<?> superClasse = clazz.getSuperclass();
System.out.println("Classe parente : " + superClasse.getName());

Class<?>[] interfaces = clazz.getInterfaces();
for (Class<?> i : interfaces) {
    System.out.println("Implémente : " + i.getName());
}
```

### 6. Exemple complet : introspection de la classe java.util.ArrayList

```java
import java.lang.reflect.*;

public class Introspecteur {
    public static void main(String[] args) {
        try {
            Class<?> clazz = Class.forName("java.util.ArrayList");

            System.out.println("Nom de la classe : " + clazz.getName());
            System.out.println("Classe mère : " + clazz.getSuperclass().getName());

            System.out.println("\nMéthodes :");
            for (Method m : clazz.getDeclaredMethods()) {
                System.out.println("- " + m.getName());
            }

            System.out.println("\nChamps :");
            for (Field f : clazz.getDeclaredFields()) {
                System.out.println("- " + f.getName() + " : " + f.getType().getName());
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

---

# 6.3 Instanciation et invocation dynamique

## **Objectif**

L’idée ici est de créer et manipuler des objets **à l’exécution**, sans connaître à l'avance leur classe ou leurs méthodes.

---

## **1. Instancier une classe dynamiquement**

### a. En utilisant le constructeur par défaut :

```java
Class<?> clazz = Class.forName("java.util.Date");
Object instance = clazz.getDeclaredConstructor().newInstance();
System.out.println("Instance : " + instance);
```
newInstance() est obsolète ; on utilise getDeclaredConstructor().newInstance() pour plus de sécurité.

### b. En utilisant un constructeur spécifique :

```java
Class<?> clazz = Class.forName("java.lang.String");
Constructor<?> constructor = clazz.getConstructor(String.class);
Object s = constructor.newInstance("Bonjour");
System.out.println("Valeur : " + s);
```

## 2. Appeler une méthode dynamiquement

```java
Class<?> clazz = Class.forName("java.lang.String");
Method method = clazz.getMethod("toUpperCase");
String resultat = (String) method.invoke("bonjour", (Object[]) null);
System.out.println(resultat); // BONJOUR
```
Attention : le premier paramètre de invoke est l'objet sur lequel on appelle la méthode, les suivants sont les arguments.

### Appel avec paramètres :

```java
Class<?> clazz = Class.forName("java.lang.StringBuilder");
Object sb = clazz.getDeclaredConstructor().newInstance();

Method append = clazz.getMethod("append", String.class);
append.invoke(sb, "Hello ");

append.invoke(sb, "World");
System.out.println(sb.toString()); // Hello World
```

## 3. Lire et modifier un champ dynamique

```java
class Personne {
    private String nom = "Alice";
}

Class<?> clazz = Personne.class;
Object p = clazz.getDeclaredConstructor().newInstance();

Field champNom = clazz.getDeclaredField("nom");
champNom.setAccessible(true); // nécessaire pour accéder aux champs privés
System.out.println("Nom actuel : " + champNom.get(p));

champNom.set(p, "Bob");
System.out.println("Nom modifié : " + champNom.get(p));
```

## 4. Exemple d’usage dans un mini framework

On peut imaginer un système qui lit un nom de classe depuis un fichier de configuration et instancie dynamiquement l’objet pour appeler une méthode donnée.

```java
// Fichier de config : "classe=configurations.MonService"
String className = Files.readString(Path.of("classe.txt")).strip();
Class<?> clazz = Class.forName(className);
Object service = clazz.getDeclaredConstructor().newInstance();

Method m = clazz.getMethod("executer");
m.invoke(service);
```

---

# 6.4 Réflexivité et annotations en Java 5

## **Objectif**

Depuis Java 5, les **annotations** permettent d’ajouter des **métadonnées** à du code. Grâce à la **réflexion**, on peut lire ces annotations à l’exécution et modifier dynamiquement le comportement d’une application.

---

## **1. Définir une annotation personnalisée**

```java
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // indispensable pour la réflexion
@Target(ElementType.METHOD)         // on applique cette annotation aux méthodes
public @interface Action {
    String description() default "Aucune description";
}
```
@Retention(RetentionPolicy.RUNTIME) est crucial : sans cela, l’annotation n’est pas accessible via la réflexion.

## 2. Utiliser l’annotation sur une méthode

```java
public class Tâches {
    @Action(description = "Exécute une tâche de nettoyage")
    public void nettoyer() {
        System.out.println("Nettoyage en cours...");
    }

    @Action(description = "Exécute une tâche de sauvegarde")
    public void sauvegarder() {
        System.out.println("Sauvegarde en cours...");
    }

    public void ignorer() {
        System.out.println("Cette méthode n’est pas annotée.");
    }
}
```

## 3. Lire dynamiquement les annotations

```java
import java.lang.reflect.*;

public class Analyseur {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Tâches.class;
        Object instance = clazz.getDeclaredConstructor().newInstance();

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Action.class)) {
                Action a = m.getAnnotation(Action.class);
                System.out.println("Action : " + m.getName() + " -> " + a.description());
                m.invoke(instance); // appel dynamique de la méthode
            }
        }
    }
}
```

## 4. Cas d’usage réel

### Un mini système de traitement automatique de services

Imaginons un framework léger qui détecte les méthodes annotées comme des opérations à exposer via une API ou à exécuter automatiquement à un moment donné (startup, cron, etc).

Exemple :

```java
public class MonService {

    @Action(description = "Tâche de démarrage")
    public void init() {
        System.out.println("Initialisation terminée !");
    }

    @Action(description = "Nettoyage automatique")
    public void clean() {
        System.out.println("Nettoyage terminé !");
    }
}
```
Et un lanceur dynamique basé sur la réflexion :
```java
public class Lanceur {
    public static void lancer(Class<?> serviceClass) throws Exception {
        Object service = serviceClass.getDeclaredConstructor().newInstance();
        for (Method method : serviceClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Action.class)) {
                method.invoke(service);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        lancer(MonService.class);
    }
}
```