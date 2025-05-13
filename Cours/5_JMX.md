# 5.1 Le modèle JMX (Java Management Extensions)

## Introduction

JMX est une technologie permettant de surveiller, gérer et administrer des ressources Java (applications, services, composants système). Elle sert à exposer des métriques, gérer dynamiquement les composants et modifier des comportements à chaud.

---

## Concepts de base

### Exploration des différents types de MBean

Dans JMX, un MBean (Managed Bean) expose des ressources de gestion (attributs, opérations). Voici quatre types principaux :

#### 1. Standard MBean  
Le type le plus courant, défini par une interface suffixée de "MBean".

Exemple :

```java
// Interface Standard MBean définissant les opérations de gestion
public interface StockManagerMBean {
    int getStockTotal();
    void reinitialiserStock();
}

// Implémentation du Standard MBean
public class StockManager implements StockManagerMBean {
    private final Map<String, Integer> stock = new HashMap<>();

    public StockManager() {
        stock.put("ProduitA", 100);
        stock.put("ProduitB", 150);
    }

    public int getStockTotal() {
        return stock.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void reinitialiserStock() {
        stock.replaceAll((k, v) -> 100);
    }
}
```

#### 2. Dynamic MBean  
Permet de définir dynamiquement attributs et opérations sans interface prédéfinie.

Exemple :

```java
import javax.management.*;
import java.util.HashMap;
import java.util.Map;

public class DynamicConfig implements DynamicMBean {
    private final Map<String, Object> attributes = new HashMap<>();

    public DynamicConfig() {
        attributes.put("ConfigValue", 42);
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException {
        if (attributes.containsKey(attribute)) {
            return attributes.get(attribute);
        }
        throw new AttributeNotFoundException("Attribut non trouvé: " + attribute);
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException {
        if (attributes.containsKey(attribute.getName())) {
            attributes.put(attribute.getName(), attribute.getValue());
        } else {
            throw new AttributeNotFoundException("Attribut non trouvé: " + attribute.getName());
        }
    }

    @Override
    public AttributeList getAttributes(String[] attributeNames) {
        AttributeList list = new AttributeList();
        for (String name : attributeNames) {
            try {
                list.add(new Attribute(name, getAttribute(name)));
            } catch (AttributeNotFoundException e) {
                // Ignorer les attributs non trouvés
            }
        }
        return list;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        AttributeList list = new AttributeList();
        for (Object obj : attributes) {
            if (obj instanceof Attribute) {
                Attribute attr = (Attribute) obj;
                try {
                    setAttribute(attr);
                    list.add(attr);
                } catch (AttributeNotFoundException e) {
                    // Ignorer les erreurs lors de la mise à jour
                }
            }
        }
        return list;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) {
        if ("reset".equals(actionName)) {
            attributes.put("ConfigValue", 42);
            return "Reset effectué";
        }
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        MBeanAttributeInfo configAttribute = new MBeanAttributeInfo(
            "ConfigValue",
            "java.lang.Integer",
            "Valeur de configuration dynamique",
            true,  true,  false
        );
        MBeanOperationInfo resetOperation = new MBeanOperationInfo(
            "reset",
            "Réinitialise la configuration",
            null,
            "java.lang.String",
            MBeanOperationInfo.ACTION
        );
        return new MBeanInfo(
            this.getClass().getName(),
            "Dynamic Config MBean",
            new MBeanAttributeInfo[]{ configAttribute },
            null,
            new MBeanOperationInfo[]{ resetOperation },
            null
        );
    }
}
```

#### 3. Open MBean  
Utilise des types de données ouverts pour garantir l’interopérabilité entre implémentations.

Exemple :

```java
import javax.management.openmbean.*;

public interface OpenConfigMBean {
    CompositeData getConfigData();
    void updateConfig(String key, int value);
}

public class OpenConfig implements OpenConfigMBean {
    private final Map<String, Integer> config = new HashMap<>();

    public OpenConfig() {
        config.put("MaxThreads", 10);
        config.put("Timeout", 5000);
    }

    public CompositeData getConfigData() {
        try {
            String[] itemNames = new String[] { "MaxThreads", "Timeout" };
            String[] itemDescriptions = new String[] { "Nombre maximum de threads", "Délai en millisecondes" };
            OpenType<Integer>[] itemTypes = new OpenType[] { SimpleType.INTEGER, SimpleType.INTEGER };

            CompositeType compositeType = new CompositeType(
                "ConfigData", "Données de configuration",
                itemNames, itemDescriptions, itemTypes
            );

            Map<String, Object> values = new HashMap<>();
            values.put("MaxThreads", config.get("MaxThreads"));
            values.put("Timeout", config.get("Timeout"));

            return new CompositeDataSupport(compositeType, values);
        } catch (OpenDataException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateConfig(String key, int value) {
        config.put(key, value);
    }
}
```

#### 4. Model MBean  
Offre un contrôle fin via des descripteurs pour une gestion personnalisée.

Exemple :

```java
import javax.management.*;
import javax.management.modelmbean.*;

public class ModelConfig {
    private int level = 1;
    
    public int getLevel() { 
        return level; 
    }
    
    public void setLevel(int level) { 
        this.level = level; 
    }
    
    public String reboot() {
        level = 1;
        return "Reboot effectué, niveau remis à 1";
    }
}

public class ModelMBeanExample {
    public static void main(String[] args) throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        RequiredModelMBean modelMBean = new RequiredModelMBean();
        ModelConfig config = new ModelConfig();
        modelMBean.setManagedResource(config, "ObjectReference");

        Descriptor levelDescriptor = new DescriptorSupport(new String[] {
            "name=Level", 
            "descriptorType=attribute", 
            "displayName=Niveau", 
            "currencyTimeLimit=20", 
            "persistPolicy=OnUpdate"
        });
        ModelMBeanAttributeInfo levelInfo = new ModelMBeanAttributeInfo(
            "Level", 
            "int", 
            "Niveau de configuration",
            true,  
            true,  
            false,
            levelDescriptor
        );

        ModelMBeanOperationInfo rebootInfo = new ModelMBeanOperationInfo(
            "Reboot",
            "Réinitialise la configuration",
            null,
            "java.lang.String",
            MBeanOperationInfo.ACTION
        );

        ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(
            "ModelConfig", 
            "Model MBean pour la configuration", 
            new ModelMBeanAttributeInfo[]{ levelInfo },
            null,
            new ModelMBeanOperationInfo[]{ rebootInfo },
            null
        );
        modelMBean.setModelMBeanInfo(info);

        ObjectName objectName = new ObjectName("com.exemple:type=ModelConfig");
        mbs.registerMBean(modelMBean, objectName);

        System.out.println("Model MBean enregistré sous : " + objectName);
    }
}
```

---

## Cas d’usage concret : supervision d’un stock

Dans cet exemple, un MBean expose l’état du stock et permet de le réinitialiser.

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

Accès via JConsole :

- Lancez l’application.
- Ouvrez jconsole et connectez-vous à l’application locale.
- Naviguez dans l’arborescence com.monapp → StockManager.
- Appelez getStockTotal ou reinitialiserStock.

---

# 5.2 Les MBeans et MBeanServer

## Qu’est-ce qu’un MBean ?

Un MBean représente une ressource managée exposée via JMX. Il permet d’accéder à des attributs (lecture/écriture) et d’invoquer des opérations dynamiques.

### Structure standard d’un MBean

Un Standard MBean est défini par une interface suffixée par "MBean" et une classe implémentant cette interface.

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

### MBeanServer et enregistrement

Le MBeanServer est le registre central où chaque MBean est enregistré sous un ObjectName unique.

#### Exemple d’obtention du MBeanServer

```java
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
```

#### Enregistrement d’un MBean

```java
ObjectName name = new ObjectName("com.monapp:type=ServeurStatistiques");
mbs.registerMBean(new ServeurStatistiques(), name);
```

---

# 5.3 Mise en place d’une couche d’administration

Une couche d’administration permet de superviser et piloter une application en production sans interruption du service.

### 1. Créer les MBeans nécessaires

Chaque composant critique à surveiller (ex. cache, pool de connexion) doit être modélisé en MBean.

Exemple d’un CacheManager :

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

```java
MBeanServer serveur = ManagementFactory.getPlatformMBeanServer();
ObjectName nom = new ObjectName("com.monapp:type=CacheManager");
serveur.registerMBean(new CacheManager(), nom);
```

### 3. Accès via JConsole ou outils externes

- Lancez votre application.
- Ouvrez JConsole et connectez-vous au processus.
- Accédez à MBeans > com.monapp > CacheManager pour visualiser les attributs et invoquer les opérations.

*Note : Le contenu lié au StockManager a été présenté précédemment (section 5.1) afin d’éviter les doublons.*

---

# 5.4 La console d’administration (JConsole)

## Objectif

JConsole est un outil du JDK qui permet de surveiller et gérer une application Java via JMX. Il offre une interface graphique pour :

- Visualiser l’utilisation du CPU, la mémoire, et les threads.
- Interagir avec les MBeans exposés.
- Invoquer des méthodes administratives dynamiquement.

## Lancer JConsole

Exécutez la commande dans un terminal :

```bash
jconsole
```

Ensuite :
- Choisissez un processus local ou entrez une URL distante.
- Inspectez l’arborescence des MBeans pour accéder aux attributs et opérations.

---

# 5.5 La communication à l’aide des adaptateurs et des connecteurs

## Objectif

Les adaptateurs et connecteurs permettent d’exposer les MBeans à des clients distants ou via divers protocoles, autorisant une administration à distance.

## Différence entre adaptateur et connecteur

| Élément       | Rôle principal                                        |
|---------------|-------------------------------------------------------|
| Adaptateur    | Interface locale d’accès (ex. HTTP, SNMP)             |
| Connecteur    | Accès à distance via un protocole de transport (ex. RMI) |

## Exemple : Connecteur RMI

Permet de se connecter à distance via JConsole.

```java
import javax.management.*;
import javax.management.remote.*;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

public class ServeurJMX {
    public static void main(String[] args) throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        ObjectName name = new ObjectName("entrepot:type=StockManager");
        StockManager mbean = new StockManager();
        mbs.registerMBean(mbean, name);

        // Démarrer le registre RMI sur le port 9999
        LocateRegistry.createRegistry(9999);

        JMXServiceURL url = new JMXServiceURL(
            "service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi"
        );

        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
        cs.start();

        System.out.println("Serveur JMX démarré sur : " + url);
    }
}
```

Pour se connecter via JConsole à distance :
1. Démarrez JConsole.
2. Sélectionnez "Remote Process" et entrez l’URL indiquée.
3. Interagissez avec les MBeans comme s’ils étaient locaux.

