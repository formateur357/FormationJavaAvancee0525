# 1.1 Les concepts de la programmation multithread : le modèle d’activités de Java (`Runnable` et `Thread`)

## Introduction

La programmation multithread permet à une application Java d'exécuter plusieurs tâches simultanément. Cela améliore la réactivité d'une application, en particulier pour les tâches longues ou bloquantes (comme le traitement réseau ou disque).

Java fournit un modèle de programmation basé sur deux principales interfaces/classes pour créer des threads : `Thread` et `Runnable`.

---

## Création d'un thread avec la classe `Thread`

La classe `Thread` représente un fil d’exécution. On peut créer un thread en héritant de cette classe et en surchargeant sa méthode `run()`.

```java
public class MonThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread démarré : " + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        MonThread t1 = new MonThread();
        t1.start(); // Démarre le thread (appel asynchrone à run)
    }
}
```

> ⚠️ Ne jamais appeler `run()` directement, cela exécuterait le code sur le thread courant.

---

## Création d'un thread avec l'interface `Runnable`

`Runnable` est une interface fonctionnelle contenant une unique méthode : `run()`. Cela permet une séparation entre le code métier (logique à exécuter) et le thread d’exécution.

```java
public class TacheRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Tâche exécutée dans le thread : " + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new TacheRunnable());
        thread.start();
    }
}
```

---

## Utilisation avec une lambda (depuis Java 8)

Puisque `Runnable` est une interface fonctionnelle, on peut l'implémenter avec une expression lambda :

```java
public class ExempleLambda {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("Tâche exécutée avec une lambda !");
        });
        thread.start();
    }
}
```

---

## Cas d’usage concrets

### 1. Traitement en parallèle de plusieurs fichiers

```java
public class TraitementFichier implements Runnable {
    private final String nomFichier;

    public TraitementFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    @Override
    public void run() {
        System.out.println("Traitement du fichier : " + nomFichier);
        // Simuler un traitement long
        try { Thread.sleep(2000); } catch (InterruptedException e) { }
        System.out.println("Fichier traité : " + nomFichier);
    }

    public static void main(String[] args) {
        String[] fichiers = {"doc1.txt", "doc2.txt", "doc3.txt"};
        for (String fichier : fichiers) {
            new Thread(new TraitementFichier(fichier)).start();
        }
    }
}
```

---

### 2. Mise à jour périodique d’un tableau de bord (thread de fond)

```java
public class RafraichissementDashboard extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.println("Rafraîchissement du tableau de bord...");
            try {
                Thread.sleep(5000); // toutes les 5 secondes
            } catch (InterruptedException e) {
                break; // en cas d'arrêt demandé
            }
        }
    }

    public static void main(String[] args) {
        RafraichissementDashboard dashboard = new RafraichissementDashboard();
        dashboard.setDaemon(true); // thread de fond
        dashboard.start();

        System.out.println("Application principale en cours...");
        try { Thread.sleep(15000); } catch (InterruptedException e) { }
        System.out.println("Fin de l'application principale.");
    }
}
```

---

### 3. Affichage d’un chargement en parallèle d’un traitement long

```java
public class AnimationChargement extends Thread {
    private boolean enCours = true;

    public void arreter() {
        enCours = false;
    }

    @Override
    public void run() {
        while (enCours) {
            System.out.print(".");
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }
        System.out.println("\nChargement terminé !");
    }

    public static void main(String[] args) {
        AnimationChargement anim = new AnimationChargement();
        anim.start();

        // Simuler un traitement principal
        try {
            Thread.sleep(3000); // traitement long
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        anim.arreter();
    }
}
```

---

## Remarques importantes

- Un thread ne peut être démarré **qu'une seule fois**.
- Appeler `.start()` crée un nouveau thread d'exécution.
- Appeler `.run()` exécute la tâche dans le **thread courant**, sans créer de nouveau thread.
- Chaque thread a un **nom**, une **priorité** et peut être défini comme **daemon** (en arrière-plan).
- L'ordre d'exécution des threads est **non garanti** : il dépend du système d'exploitation.

---

## Exemple : exécuter plusieurs threads simultanément

```java
public class MultiThreadExample {
    public static void main(String[] args) {
        for (int i = 1; i <= 3; i++) {
            Thread t = new Thread(() -> {
                System.out.println("Thread : " + Thread.currentThread().getName());
            });
            t.start();
        }
    }
}
```

## Bonnes pratiques

- Utiliser `Runnable` quand possible : favorise la composition (vs. héritage avec `Thread`).
- Éviter de manipuler directement `Thread` pour des cas complexes : préférez `ExecutorService` (abordé plus loin).
- Toujours anticiper les problèmes de synchronisation et de partage de ressources entre threads.

---

# 1.2 Création/destruction des threads. Ordonnancement des threads

## Cycle de vie d’un thread

Un thread Java passe par plusieurs états définis dans l’énumération `Thread.State` :

- `NEW` : le thread est créé mais pas encore démarré (`new Thread()`).
- `RUNNABLE` : le thread est prêt à s'exécuter et peut être planifié par le système.
- `RUNNING` : le thread est en cours d'exécution.
- `BLOCKED` : le thread attend un verrou.
- `WAITING` ou `TIMED_WAITING` : le thread attend une notification (`wait()`, `sleep()`, etc.).
- `TERMINATED` : le thread a terminé son exécution.

```java
public class EtatThread {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("Thread actif !");
        });

        System.out.println("État initial : " + t.getState()); // NEW
        t.start();
        System.out.println("État après start() : " + t.getState());
    }
}
```

---

## Création de threads – rappel

On peut créer un thread via :

- Héritage de `Thread`
- Implémentation de `Runnable`
- Expression lambda avec `Runnable`
- `ExecutorService` (voir section 1.6)

## Destruction d’un thread

Un thread Java se termine automatiquement lorsque sa méthode `run()` se termine.

Il est **déconseillé** d’utiliser `Thread.stop()`, car cela peut laisser des ressources dans un état incohérent. À la place, il faut :

- Utiliser un **drapeau (flag)** pour demander l'arrêt.
- Utiliser l’interruption via `thread.interrupt()`.

---

### Exemple avec un flag d’arrêt

```java
public class TacheAvecArret implements Runnable {
    private volatile boolean enCours = true;

    public void arreter() {
        enCours = false;
    }

    @Override
    public void run() {
        while (enCours) {
            System.out.println("Travail en cours...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Thread interrompu !");
                break;
            }
        }
        System.out.println("Tâche terminée.");
    }

    public static void main(String[] args) throws InterruptedException {
        TacheAvecArret tache = new TacheAvecArret();
        Thread thread = new Thread(tache);
        thread.start();

        Thread.sleep(2000);
        tache.arreter();
    }
}
```

---

## Ordonnancement des threads

### Principe

L'ordonnancement (scheduling) détermine quel thread s’exécute à un instant donné. Il dépend du système d'exploitation et de la machine virtuelle.

### Méthodes de la classe `Thread` liées à l’ordonnancement

- `setPriority(int)` : définit la priorité (entre `Thread.MIN_PRIORITY` (1) et `Thread.MAX_PRIORITY` (10))
- `yield()` : suggère à la JVM de céder le CPU à un autre thread.
- `sleep(ms)` : suspend l’exécution pendant un temps donné.

> ⚠️ Ces méthodes sont **indicatives** : elles n’offrent aucune garantie d’ordre d’exécution.

---

### Exemple de priorité

```java
public class PrioriteThread {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++)
                System.out.println("Thread 1");
        });
        t1.setPriority(Thread.MIN_PRIORITY);

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++)
                System.out.println("Thread 2");
        });
        t2.setPriority(Thread.MAX_PRIORITY);

        t1.start();
        t2.start();
    }
}
```

---

## Cas d’usage concrets

### 1. Boucle de traitement longue avec pause contrôlée

```java
public class Calculateur implements Runnable {
    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Traitement " + i);
            try {
                Thread.sleep(1000); // simule une opération coûteuse
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("Traitement terminé.");
    }

    public static void main(String[] args) {
        Thread t = new Thread(new Calculateur());
        t.start();
    }
}
```

---

### 2. Coordination avec `yield()`

```java
public class ExempleYield {
    public static void main(String[] args) {
        Runnable r = () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + " - " + i);
                Thread.yield();
            }
        };

        new Thread(r, "Thread-A").start();
        new Thread(r, "Thread-B").start();
    }
}
```

## Bonnes pratiques

- Évitez de forcer l’ordre des threads. Utilisez des outils de synchronisation si l’ordre est crucial.
- N’utilisez **jamais** `Thread.stop()`. Préférez les interruptions ou des drapeaux.
- Le scheduling est **non déterministe**. Ne comptez pas sur `priority()` ou `yield()` pour orchestrer des comportements précis.

---

# 1.3 La synchronisation des threads

## Pourquoi synchroniser ?

Quand plusieurs threads accèdent aux **mêmes ressources partagées** (variables, objets, fichiers…), il peut y avoir des effets de bord, comme :

- des incohérences (ex. : deux threads modifient un compteur en même temps),
- des conditions de course (`race conditions`),
- des lectures/écritures partiellement visibles.

> 🔐 **La synchronisation garantit que les opérations critiques ne seront exécutées que par un thread à la fois.**

---

## Le mot-clé `synchronized`

Il existe deux formes principales :

### 1. Méthode synchronisée

```java
public synchronized void incrementer() {
    compteur++;
}
```

> L’objet courant (`this`) est verrouillé tant que la méthode est en cours.

### 2. Bloc synchronisé

```java
synchronized (verrou) {
    // code critique
}
```

> On peut ainsi verrouiller un objet spécifique, évitant de bloquer toute la méthode.

---

## Exemple : Accès concurrent à une variable

### Sans synchronisation (problème)

```java
public class CompteurNonSecurise {
    private int compteur = 0;

    public void incrementer() {
        compteur++;
    }

    public int getCompteur() {
        return compteur;
    }

    public static void main(String[] args) throws InterruptedException {
        CompteurNonSecurise c = new CompteurNonSecurise();

        Runnable r = () -> {
            for (int i = 0; i < 10000; i++) c.incrementer();
        };

        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Compteur final : " + c.getCompteur()); // < 20000
    }
}
```

---

### Avec synchronisation (résolu)

```java
public class CompteurSecurise {
    private int compteur = 0;

    public synchronized void incrementer() {
        compteur++;
    }

    public int getCompteur() {
        return compteur;
    }

    // Code main identique
}
```

---

## Moniteurs Java

Chaque objet en Java peut servir de **moniteur**, c’est-à-dire :

- une **file d’attente** pour les threads qui veulent un accès exclusif à une ressource,
- un **verrou implicite** utilisé avec `synchronized`.

Lorsqu’un thread entre dans un bloc/méthode `synchronized`, il **acquiert le verrou**. Les autres attendent qu’il soit libéré.

---

## Problème classique : condition de course

Deux threads qui modifient une variable sans synchronisation peuvent produire des résultats **aléatoires** ou **erronés**.

```java
// Voir l'exemple CompteurNonSecurise ci-dessus
```

---

## Cas d’usage concrets

### 1. Banque : retrait concurrent du même compte

```java
public class CompteBancaire {
    private int solde = 100;

    public synchronized void retirer(int montant) {
        if (solde >= montant) {
            System.out.println(Thread.currentThread().getName() + " retire " + montant);
            solde -= montant;
        } else {
            System.out.println(Thread.currentThread().getName() + " solde insuffisant !");
        }
    }

    public static void main(String[] args) {
        CompteBancaire compte = new CompteBancaire();

        Runnable client = () -> compte.retirer(70);

        Thread t1 = new Thread(client, "Client 1");
        Thread t2 = new Thread(client, "Client 2");

        t1.start(); t2.start();
    }
}
```

---

### 2. Verrou sur un objet partagé

```java
public class Imprimante {
    public void imprimer(String document) {
        synchronized (this) {
            System.out.println("Impression du document : " + document);
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            System.out.println("Document imprimé : " + document);
        }
    }
}
```

---

## Synchronisation statique

Pour les méthodes `static`, on synchronise sur la **classe elle-même**, pas l'instance :

```java
public static synchronized void methodeStatique() {
    // verrou sur ClassName.class
}
```

---

## Risques avec `synchronized`

### Interblocage (deadlock)

Si deux threads se verrouillent **mutuellement**, aucun ne peut continuer :

```java
public class Deadlock {
    static final Object verrou1 = new Object();
    static final Object verrou2 = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (verrou1) {
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                synchronized (verrou2) {
                    System.out.println("Thread A a tout verrouillé");
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (verrou2) {
                synchronized (verrou1) {
                    System.out.println("Thread B a tout verrouillé");
                }
            }
        }).start();
    }
}
```

> 💡 **Solution** : toujours verrouiller les ressources dans le **même ordre**.

---

## Bonnes pratiques

- Toujours synchroniser **le moins de code possible** (éviter de bloquer tout un objet).
- Privilégier les classes thread-safe comme `ConcurrentHashMap` (section 1.6).
- Utiliser `final Object verrou = new Object();` pour mieux contrôler les blocs critiques.

---

# 1.4 Problèmes classiques du multithreading

La programmation concurrente expose à des situations difficiles à déboguer. Les problèmes les plus fréquents sont :

- L’interblocage (`deadlock`)
- La famine (`starvation`)
- L’inversion de priorité (`priority inversion`)

---

## 1. L’interblocage (Deadlock)

### Définition

Un **interblocage** se produit lorsque deux (ou plus) threads s'attendent mutuellement à libérer des ressources qu'ils détiennent déjà. Aucun ne peut progresser.

### Conditions d’apparition (modèle de Coffman)

Un interblocage peut apparaître si **les 4 conditions suivantes** sont réunies :

1. **Exclusion mutuelle** : une ressource ne peut être utilisée que par un seul thread à la fois.
2. **Maintien et attente** : un thread détient une ressource et attend une autre.
3. **Non-préemption** : une ressource ne peut pas être reprise de force.
4. **Attente circulaire** : une chaîne circulaire de dépendance existe.

---

### Exemple de deadlock

```java
public class DeadlockDemo {
    private static final Object resA = new Object();
    private static final Object resB = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (resA) {
                System.out.println("Thread 1 a verrouillé resA");
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                synchronized (resB) {
                    System.out.println("Thread 1 a verrouillé resB");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (resB) {
                System.out.println("Thread 2 a verrouillé resB");
                synchronized (resA) {
                    System.out.println("Thread 2 a verrouillé resA");
                }
            }
        });

        t1.start();
        t2.start();
    }
}
```

> 💥 Les deux threads se bloquent mutuellement. Aucun n'avance.

---

## Prévention de l’interblocage

### Stratégies

- **Ordre d’acquisition** : toujours verrouiller les ressources dans le même ordre.
- **Timeouts** : utiliser des verrous explicites avec timeout (`tryLock()` de `ReentrantLock`).
- **Éviter les attentes multiples** : regrouper les besoins dans un seul verrou quand possible.
- **Détection automatique** (rare en Java pur).

---

### Exemple avec `ReentrantLock` et `tryLock()`

```java
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class DeadlockEvite {
    private final ReentrantLock lockA = new ReentrantLock();
    private final ReentrantLock lockB = new ReentrantLock();

    public void methode() {
        try {
            if (lockA.tryLock(100, TimeUnit.MILLISECONDS)) {
                try {
                    if (lockB.tryLock(100, TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println("Traitement sans interblocage");
                        } finally {
                            lockB.unlock();
                        }
                    }
                } finally {
                    lockA.unlock();
                }
            } else {
                System.out.println("Timeout, pas de blocage !");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

---

## 2. La famine (`Starvation`)

### Définition

Un thread subit une **famine** lorsqu’il attend indéfiniment pour accéder à une ressource, car d'autres threads plus prioritaires monopolisent le CPU ou les verrous.

### Causes fréquentes

- Priorités mal configurées
- Ressources trop limitées
- Synchronisation excessive

---

### Exemple théorique

```java
public class Starvation {
    private final Object verrou = new Object();

    public void tacheHautePriorite() {
        synchronized (verrou) {
            while (true) {
                // Exécute en boucle, ne libère jamais le verrou
            }
        }
    }

    public void tacheNormale() {
        synchronized (verrou) {
            System.out.println("Tâche normale exécutée !");
        }
    }
}
```

> La tâche normale ne s’exécutera **jamais** tant que la tâche prioritaire ne libère pas le verrou.

---

## Solutions à la famine

- **Utiliser des verrous équitables** (`ReentrantLock(true)`)
- **Limiter les durées critiques** dans les blocs `synchronized`
- **Éviter les boucles infinies verrouillées**
- **Utiliser des files de tâches** (`ExecutorService`) avec gestion des priorités

---

## 3. L’inversion de priorité

### Définition

Un thread de haute priorité est bloqué par un thread de basse priorité, lui-même bloqué par une ressource détenue par un thread de priorité intermédiaire.

> Ce cas peut ralentir un thread critique de façon inattendue.

### Solution

- La JVM moderne et certains systèmes OS gèrent cela via un mécanisme de **donation de priorité**.
- Sinon, limiter les verrous entre threads de priorité différente.

---

## Cas d’usage réel

### Application bancaire multi-agents

- Agent A : écrit les opérations dans la base (besoin d’exclusivité)
- Agent B : lit les opérations (besoin partagé)
- Agent C : envoie les emails de confirmation

> S’il y a mauvaise synchronisation ou verrouillage croisé, on peut observer des interblocages ou des délais importants.

### Système de tickets en ligne

- Plusieurs utilisateurs accèdent aux mêmes stocks de billets
- Des verrous mal gérés peuvent provoquer des **blocages**, **doubles réservations**, ou **famine** pour certains utilisateurs

---

## Bonnes pratiques

- Analyser les flux de dépendances entre threads et ressources
- Privilégier les **outils de haut niveau** (ExecutorService, Semaphore, etc.)
- Tester avec des charges concurrentes simulées (stress test)
- Limiter l’usage du mot-clé `synchronized` au strict nécessaire

---

# 1.5 Extensions du modèle introduites à partir de Java 5

Depuis Java 5, le JDK a introduit une API plus robuste et souple pour la programmation concurrente : le package `java.util.concurrent`.

Ce package offre :

- des interfaces modernes (`Callable`, `Future`),
- des **pools de threads** (`ExecutorService`),
- des outils puissants (`Lock`, `Semaphore`, `CountDownLatch`, etc.),
- des collections concurrentes (`ConcurrentHashMap`…),
- des modèles évolués (`ForkJoinPool`, `CompletableFuture`).

---

## 1. `Callable<T>` et `Future<T>`

Contrairement à `Runnable`, `Callable` peut **retourner un résultat** et **lever des exceptions**.

### Exemple : tâche avec résultat

```java
import java.util.concurrent.*;

public class CallableExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Integer> tache = () -> {
            Thread.sleep(1000);
            return 42;
        };

        Future<Integer> futur = executor.submit(tache);
        System.out.println("Tâche soumise, en attente du résultat...");
        Integer resultat = futur.get(); // Bloque jusqu'à la fin
        System.out.println("Résultat : " + resultat);

        executor.shutdown();
    }
}
```

---

## 2. `ExecutorService` : exécuter des tâches avec un pool

Au lieu de créer manuellement des `Thread`, on utilise un **pool de threads réutilisables**.

### Exemple : soumettre plusieurs tâches

```java
ExecutorService pool = Executors.newFixedThreadPool(3);

for (int i = 1; i <= 5; i++) {
    int finalI = i;
    pool.submit(() -> {
        System.out.println("Tâche " + finalI + " exécutée par " + Thread.currentThread().getName());
    });
}

pool.shutdown();
```

> ✅ Avantages :
> - Contrôle du nombre de threads
> - Réutilisation
> - Gestion automatique

---

## 3. Collections concurrentes

Les collections classiques (`HashMap`, `ArrayList`, etc.) **ne sont pas thread-safe**.

### Collections alternatives :

| Classe | Description |
|--------|-------------|
| `ConcurrentHashMap` | Carte thread-safe sans verrou global |
| `CopyOnWriteArrayList` | Liste optimisée pour la lecture |
| `BlockingQueue` | File d’attente avec blocage (`ArrayBlockingQueue`, `LinkedBlockingQueue`, etc.) |

### Exemple : producteur/consommateur avec `BlockingQueue`

```java
BlockingQueue<String> file = new ArrayBlockingQueue<>(10);

new Thread(() -> {
    try {
        file.put("Bonjour !");
    } catch (InterruptedException e) { e.printStackTrace(); }
}).start();

new Thread(() -> {
    try {
        String msg = file.take();
        System.out.println("Message reçu : " + msg);
    } catch (InterruptedException e) { e.printStackTrace(); }
}).start();
```

---

## 4. Modèle Fork/Join (Java 7)

Permet de **diviser un travail récursivement** (modèle "divide & conquer") et de l’exécuter en parallèle.

### Exemple : somme d’un tableau

```java
import java.util.concurrent.*;

class Somme extends RecursiveTask<Integer> {
    private int[] tableau;
    private int debut, fin;

    public Somme(int[] tableau, int debut, int fin) {
        this.tableau = tableau;
        this.debut = debut;
        this.fin = fin;
    }

    protected Integer compute() {
        if (fin - debut <= 3) {
            int somme = 0;
            for (int i = debut; i < fin; i++) somme += tableau[i];
            return somme;
        } else {
            int milieu = (debut + fin) / 2;
            Somme gauche = new Somme(tableau, debut, milieu);
            Somme droite = new Somme(tableau, milieu, fin);
            gauche.fork(); // exécution parallèle
            return droite.compute() + gauche.join();
        }
    }
}

---

public class ForkJoinExample {
    public static void main(String[] args) {
        int[] t = {1, 2, 3, 4, 5, 6};
        ForkJoinPool pool = new ForkJoinPool();
        int resultat = pool.invoke(new Somme(t, 0, t.length));
        System.out.println("Somme = " + resultat);
    }
}
```

---

## 5. `CompletableFuture` (Java 8)

Permet une programmation **asynchrone et fluide** avec chaînes de traitement (`thenApply`, `thenAccept`, etc.).

### Exemple simple

```java
import java.util.concurrent.*;

public class CompletableExample {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            return "Bonjour";
        }).thenApply(msg -> msg + " monde")
          .thenAccept(System.out::println);

        // Laisser le temps à l'exécution asynchrone
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }
}
```

> 🔄 Exécution non bloquante, fluide, adaptée aux architectures réactives.

---

## Cas d’usage concrets

### Traitement parallèle d’un gros fichier CSV

- Chaque bloc est traité avec une tâche `Callable`
- Coordination avec un `ExecutorService` et `Future`
- Chargement parallèle en base

### Application web asynchrone (Java 8+)

- Traitement d’une requête HTTP avec `CompletableFuture`
- Appels de services en parallèle
- Renvoi de la réponse une fois tous les résultats agrégés

---

## Bonnes pratiques

- Utiliser `ExecutorService` au lieu de créer des `Thread` manuellement
- Fermer proprement les pools avec `shutdown()`
- Préférer les outils non bloquants quand c’est possible (`CompletableFuture`, `ForkJoin`)
- Éviter les implémentations maison de thread pool