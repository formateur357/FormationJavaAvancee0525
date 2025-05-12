# 5.1 Le modèle JMX (Java Management Extensions)

## **Introduction**

JMX (Java Management Extensions) est une technologie Java permettant de **surveiller**, **gérer** et **administrer** des ressources Java (applications, services, composants système).

Il est largement utilisé pour exposer des métriques, gérer dynamiquement des composants, ou encore modifier des comportements à chaud.

---

## **Concepts de base**

### **MBean (Managed Bean)**

- Un MBean est une classe Java spéciale exposant une **interface de gestion**.
- Il existe 4 types de MBeans :
  - Standard MBean
  - Dynamic MBean
  - Open MBean
  - Model MBean

Le plus courant est le **Standard MBean**.

### **MBeanServer**

- Un **registre** centralisé où sont enregistrés tous les MBeans.
- Sert de point d'accès aux MBeans pour les outils de gestion.

### **ObjectName**

- Identifie de manière unique un MBean dans le serveur de MBeans.

---

## **Cas d’usage typique**

- Exposer l’état ou les statistiques d’un service Java (ex: nombre de connexions, threads actifs…).
- Modifier la configuration à la volée.
- Lancer une action administrative (vidage cache, rechargement config…).
- Supervision avec **JConsole** ou **VisualVM**.

---

## **Exemple concret : supervision d’un stock**

### 1. Interface MBean

```java
public interface StockManagerMBean {
    int getStockTotal();
    void reinitialiserStock();
}
```

### 2. Implémentation

```java
public class StockManager implements StockManagerMBean {
    private final Stock stock;

    public StockManager(Stock stock) {
        this.stock = stock;
    }

    public int getStockTotal() {
        return stock.getProduitsSnapshot().values().stream().mapToInt(Integer::intValue).sum();
    }

    public void reinitialiserStock() {
        stock.getProduitsSnapshot().keySet().forEach(nom -> stock.ajouterProduit(nom, 20));
    }
}
```

### 3. Enregistrement du MBean

```java
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
ObjectName nom = new ObjectName("com.monapp:type=StockManager");
StockManager manager = new StockManager(stock);
mbs.registerMBean(manager, nom);
```

### Accès via JConsole

- Lancez votre application.

- Ouvrez jconsole (inclus dans le JDK).

- Connectez-vous à l’application locale.

- Naviguez dans l’arborescence com.monapp → StockManager.

- Appelez getStockTotal ou déclenchez reinitialiserStock.

---

# 5.2 Les MBeans et MBeanServer

## **Qu’est-ce qu’un MBean ?**

Un **MBean (Managed Bean)** est un objet Java représentant une **ressource managée** exposée via le système JMX. Un MBean expose des :

- **Attributs** : accessibles en lecture/écriture (ex : `getStatus`, `setThreshold`)
- **Opérations** : appelables dynamiquement (ex : `viderCache()`, `rafraichirConfig()`)

### **Structure standard d’un MBean**

Un **Standard MBean** est défini par une **interface** suffixée par `MBean`, que la classe implémente.

#### Exemple :

```java
// Interface MBean
public interface ServeurStatistiquesMBean {
    int getNbConnexions();
    void resetCompteur();
}

// Implémentation
public class ServeurStatistiques implements ServeurStatistiquesMBean {
    private int nbConnexions = 0;

    public int getNbConnexions() {
        return nbConnexions;
    }

    public void resetCompteur() {
        nbConnexions = 0;
    }

    public void connexionEffectuee() {
        nbConnexions++;
    }
}
```

### Le MBeanServer

Un MBeanServer est un registre qui centralise tous les MBeans de l’application.

Java fournit un MBeanServer par défaut via la JVM :

```java
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
```

### Enregistrement d’un MBean

Chaque MBean est identifié de façon unique par un ObjectName :

```java
ObjectName name = new ObjectName("com.monapp:type=ServeurStatistiques");
mbs.registerMBean(new ServeurStatistiques(), name);
```

Le format de l'ObjectName est :

```bash
domaine:type=NomDuType[,clé=valeur,...]
```

### Cas d’usage

Prenons un exemple d’un service REST qui expose :

- Le nombre de requêtes traitées

- Une méthode pour le remettre à zéro

### Interface MBean

```java
public interface MonitoringRequetesMBean {
    long getNbRequetes();
    void reset();
}
```

### Classe

```java
public class MonitoringRequetes implements MonitoringRequetesMBean {
    private AtomicLong compteur = new AtomicLong();

    public void incrementer() {
        compteur.incrementAndGet();
    }

    public long getNbRequetes() {
        return compteur.get();
    }

    public void reset() {
        compteur.set(0);
    }
}
```

### Enregistrement

```java
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
ObjectName name = new ObjectName("com.api:type=MonitoringRequetes");
mbs.registerMBean(new MonitoringRequetes(), name);
```


Ensuite, vous pouvez :

- Consulter nbRequetes depuis JConsole

- Réinitialiser dynamiquement le compteur

- Ou automatiser via un script de supervision

---

# 5.3 Mise en place d’une couche d’administration

## **Objectif**

Une **couche d’administration** permet de superviser et de piloter dynamiquement une application en production, sans l’arrêter. Grâce à **JMX**, cette couche peut :

- exposer des indicateurs de performance,
- déclencher des opérations de maintenance,
- surveiller des seuils ou états métier.

---

## **Étapes de mise en place**

### 1. **Créer les MBeans nécessaires**
Chaque composant critique à surveiller doit être modélisé en MBean (ex : cache, pool de connexion, file de messages...).

```java
public interface CacheManagerMBean {
    int getTailleCache();
    void viderCache();
}

public class CacheManager implements CacheManagerMBean {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public int getTailleCache() {
        return cache.size();
    }

    public void viderCache() {
        cache.clear();
    }

    public void ajouter(String cle, Object valeur) {
        cache.put(cle, valeur);
    }
}
```

### 2. Enregistrer les MBeans auprès du MBeanServer

Cela rend les composants accessibles à distance via JMX :

```java
MBeanServer serveur = ManagementFactory.getPlatformMBeanServer();
ObjectName nom = new ObjectName("com.monapp:type=CacheManager");
serveur.registerMBean(new CacheManager(), nom);
```

### 3. Accès via JConsole ou outils externes

- Lancez votre application

- Ouvrez JConsole (jconsole)

- Connectez-vous au processus

- Accédez à l’arbre MBeans > com.monapp > CacheManager

- Visualisez les attributs et invoquez les opérations

### Cas d’usage concret : Administration d’un entrepôt

- Dans une application de gestion d'entrepôt, la couche JMX pourrait :

- Exposer le nombre total d’articles (StockManagerMBean)

- Offrir une méthode pour recharger tout le stock depuis une base

- Permettre le déclenchement d’un inventaire manuel

```java
public interface StockManagerMBean {
    int getStockTotal();
    void rechargerDepuisBase();
}

public class StockManager implements StockManagerMBean {
    private Stock stock;

    public StockManager(Stock stock) {
        this.stock = stock;
    }

    public int getStockTotal() {
        return stock.getProduitsSnapshot().values().stream().mapToInt(Integer::intValue).sum();
    }

    public void rechargerDepuisBase() {
        // Simulation rechargement
        stock.ajouterProduit("Clavier", 100);
        stock.ajouterProduit("Souris", 50);
    }
}
```

### Enregistrement :

```java
StockManager gestionnaire = new StockManager(monStock);
ObjectName nom = new ObjectName("entrepot:type=StockManager");
mbs.registerMBean(gestionnaire, nom);
```
Cette approche facilite une administration sans interruption de service.

---

# 5.4 La console d’administration (JConsole)

## **Objectif**

**JConsole** est un outil graphique fourni avec le JDK qui permet de **surveiller et gérer une application Java** via **JMX**. Elle offre une interface conviviale pour :

- visualiser la consommation mémoire, les threads, les classes chargées,
- interagir avec les **MBeans** exposés,
- observer les attributs et invoquer les méthodes administratives dynamiquement.

---

## **Lancer JConsole**

Depuis un terminal :

```bash
jconsole
```

Vous pouvez alors :

- Choisir un processus local (votre application),

- Ou entrer une URL distante (service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi).


## Fonctionnalités principales
### 1. Vue d'ensemble (Overview)

- CPU utilisé

- Mémoire vive

- Nombre de threads actifs

- Classes chargées/déchargées

### 2. Mémoire (Memory)

- Visualisation en temps réel des zones mémoire (Heap, PermGen, etc.)

- Forçage du garbage collector manuellement

- Suivi de l’empreinte mémoire de l’application

### 3. Threads

- Liste de tous les threads Java actifs

- Vue hiérarchique

- Identification des threads bloqués, en attente, etc.

### 4. MBeans

- L’onglet MBeans est le cœur de l’administration applicative :

- Accès aux MBeans enregistrés (ex : com.monapp:type=CacheManager)

- Affichage des attributs dynamiques

- Invoquer des méthodes d’administration exposées

## Exemple pratique : Interagir avec un MBean

Si vous avez exposé ce MBean :

```java
public interface StockManagerMBean {
    int getStockTotal();
    void rechargerDepuisBase();
}
```

Depuis JConsole :

1. Onglet MBeans

2. Naviguer vers entrepot > StockManager

3. Cliquer sur Attributes pour lire le stock actuel

4. Aller dans Operations et appeler rechargerDepuisBase()

5. Le stock sera modifié immédiatement dans l'application

### Avantages de JConsole

- Aucune configuration complexe nécessaire

- Surveillance en temps réel

- Interface graphique intuitive

- Permet une administration proactive

---

# 5.5 La communication à l’aide des adaptateurs et des connecteurs

## **Objectif**

Dans JMX, les **adaptateurs** et **connecteurs** permettent d’**exposer les MBeans** à des clients distants ou via d’autres protocoles. Cela rend l’administration possible **à distance**, sans interface graphique embarquée.

---

## **Différence entre adaptateur et connecteur**

| Élément     | Rôle principal                                              |
|-------------|-------------------------------------------------------------|
| **Adaptateur** | Interface locale d’accès (ex : HTTP, SNMP, etc.)         |
| **Connecteur** | Fournit un accès à distance via un protocole de transport (ex : RMI) |

---

## **Exemple : Connecteur RMI**

Permet d’**exposer les MBeans via le protocole RMI**, pour se connecter avec JConsole à distance.

### **Code d’exemple**

```java
import javax.management.*;
import javax.management.remote.*;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

public class ServeurJMX {
    public static void main(String[] args) throws Exception {
        // MBean Server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // Enregistrement du MBean
        ObjectName name = new ObjectName("entrepot:type=StockManager");
        StockManager mbean = new StockManager();
        mbs.registerMBean(mbean, name);

        // Démarrage du registre RMI (port 9999)
        LocateRegistry.createRegistry(9999);

        // Création de l’URL de service JMX
        JMXServiceURL url = new JMXServiceURL(
            "service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi"
        );

        // Création du connecteur
        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
        cs.start();

        System.out.println("Serveur JMX démarré sur : " + url);
    }
}
```

### Connexion avec JConsole à distance

Dans JConsole :

1. Démarrer l’outil (jconsole)

2. Sélectionner "Remote Process"

3. Entrer l’URL : service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi

4. Interagir avec les MBeans comme si l’application était locale

### Cas d’usage typique

- Administration d’une application déployée sur un serveur (Tomcat, application Spring Boot, etc.)

- Monitoring centralisé d’un parc d’applications Java

- Connexion distante via un outil comme VisualVM, JConsole, ou Prometheus JMX Exporter

### Sécurité

Le connecteur peut être sécurisé avec :

- SSL

- Authentification par fichier (JMXRemote.access, JMXRemote.password)

- Contrôle d’accès fin aux MBeans