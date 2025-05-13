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

Depuis Java 5, les **annotations** ne servent pas seulement à ajouter des métadonnées aux méthodes, mais aussi aux classes, champs et constructeurs. La réflexion permet de lire ces annotations à l’exécution et d’ajuster le comportement d’une application en fonction de ces métadonnées.

## 4. Utilisation avancée des annotations

### 1. Lecture d'annotations sur différents éléments

Vous pouvez définir une annotation capable de s'appliquer à plusieurs cibles :

```java
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Info {
    String details() default "Info par défaut";
}
```

Exemple d'utilisation sur une classe, ses champs, et constructeurs :

```java
@Info(details = "Classe principale")
public class Exemple {
    
    @Info(details = "Champ important")
    private String nom;
    
    @Info(details = "Constructeur standard")
    public Exemple() { }
    
    @Info(details = "Méthode de traitement")
    public void traiter() { }
}
```

Pour lire ces annotations :

```java
Class<?> clazz = Exemple.class;

// Annotation sur la classe
if (clazz.isAnnotationPresent(Info.class)) {
    Info info = clazz.getAnnotation(Info.class);
    System.out.println("Classe: " + info.details());
}

// Annotation sur les champs
for (Field f : clazz.getDeclaredFields()) {
    if (f.isAnnotationPresent(Info.class)) {
        Info info = f.getAnnotation(Info.class);
        System.out.println("Champ " + f.getName() + ": " + info.details());
    }
}

// Annotation sur le constructeur
for (Constructor<?> c : clazz.getDeclaredConstructors()) {
    if (c.isAnnotationPresent(Info.class)) {
        Info info = c.getAnnotation(Info.class);
        System.out.println("Constructeur: " + info.details());
    }
}

// Annotation sur les méthodes
for (Method m : clazz.getDeclaredMethods()) {
    if (m.isAnnotationPresent(Info.class)) {
        Info info = m.getAnnotation(Info.class);
        System.out.println("Méthode " + m.getName() + ": " + info.details());
    }
}
```

### 2. Lecture imbriquée ou conditionnelle d'annotations

Il est possible d'effectuer une lecture conditionnelle, par exemple, lire l'annotation d'une méthode et, si elle est présente, inspecter un champ spécifique annoté dans la même classe.

Définissons une nouvelle annotation pour configurer un champ :

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Config {
    String valeur() default "defaut";
}
```

Utilisation dans une classe :

```java
public class TâchesAvancées {

    @Config(valeur = "vérification")
    private String parametre;

    @Action(description = "Méthode avec configuration")
    public void executer() {
        System.out.println("Exécution de la méthode.");
    }
}
```

Lecture conditionnelle :

```java
Class<?> clazz = TâchesAvancées.class;
Object instance = clazz.getDeclaredConstructor().newInstance();

// Parcourir les méthodes annotées avec @Action
for (Method m : clazz.getDeclaredMethods()) {
    if (m.isAnnotationPresent(Action.class)) {
        Action action = m.getAnnotation(Action.class);
        System.out.println("Action: " + m.getName() + " -> " + action.description());
        
        // Vérifier si la classe contient un champ annoté avec @Config
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Config.class)) {
                Config config = f.getAnnotation(Config.class);
                System.out.println("Champ " + f.getName() + " configuré avec: " + config.valeur());
            }
        }
        m.invoke(instance);
    }
}
```

### 3. Les méta-annotations : @Inherited et @Repeatable

#### @Inherited

L'annotation @Inherited permet à une annotation placée sur une classe d'être automatiquement présente sur ses sous-classes.

Exemple :

```java
import java.lang.annotation.*;
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClasseInfo {
    String valeur();
}

@ClasseInfo(valeur = "Super classe")
public class Parent { }

public class Enfant extends Parent { }
```

Lors de l'inspection de la classe Enfant, on retrouve l'annotation héritée :

```java
if (Enfant.class.isAnnotationPresent(ClasseInfo.class)) {
    ClasseInfo info = Enfant.class.getAnnotation(ClasseInfo.class);
    System.out.println("Enfant hérite de: " + info.valeur());
}
```

#### @Repeatable

L'annotation @Repeatable permet d'appliquer plusieurs instances d'une même annotation sur un même élément.

Déclarons une annotation répétable :

```java
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Labels.class)
public @interface Label {
    String name();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Labels {
    Label[] value();
}
```

Utilisation sur une méthode :

```java
public class MultiEtiquettes {

    @Label(name = "Critique")
    @Label(name = "Testée")
    public void verifier() {
        System.out.println("Vérification en cours...");
    }
}
```

Lecture des annotations répétées :

```java
Method m = MultiEtiquettes.class.getMethod("verifier");
if (m.isAnnotationPresent(Labels.class)) {
    Labels labels = m.getAnnotation(Labels.class);
    for (Label l : labels.value()) {
        System.out.println("Label: " + l.name());
    }
} else if (m.isAnnotationPresent(Label.class)) {
    // Si non regroupées automatiquement, lecture individuelle
    Label label = m.getAnnotation(Label.class);
    System.out.println("Label: " + label.name());
}
```

Ces exemples montrent comment exploiter pleinement la puissance des annotations avec la réflexion en Java pour adapter dynamiquement le comportement de vos applications selon des métadonnées enrichies.

## 6.5 Sécurité, limitations et bonnes pratiques

### Risques et limitations

- **Contournement des règles d'encapsulation :**  
    Utiliser setAccessible(true) permet d'accéder à des membres privés, contournant ainsi les principes d'encapsulation. Ceci peut compromettre l'intégrité des objets et rendre le code plus difficile à maintenir.

- **Impact sur les performances :**  
    Les opérations en réflexion sont généralement plus lentes que les accès directs. Dans des contextes critiques en termes de performances, leur utilisation doit être limitée.

- **Incompatibilités et restrictions :**  
    - Avec Java 9+ et son système de modules, l'accès non autorisé peut poser des problèmes, en particulier dans les environnements sécurisés (Java EE, Applets).  
    - Java 17+ impose des restrictions plus strictes via le module system et l'option --illegal-access=deny, ce qui peut empêcher certaines utilisations de la réflexion dans des modules protégés.

### Bonnes pratiques

- **Limiter l'usage :**  
    Utiliser la réflexion uniquement lorsque cela est nécessaire. Privilégier l'accès direct lorsque cela est possible.

- **Gérer les exceptions :**  
    La réflexion peut générer de nombreuses exceptions (ClassNotFoundException, IllegalAccessException, etc.). Assurer une gestion appropriée de ces cas pour améliorer la robustesse de l'application.

- **Penser à la sécurité :**  
    Evaluer les risques d'exposer des membres privés et utiliser des contrôles d'accès pour éviter les abus.