## 3.1 Les principes généraux des ORB (Object Request Broker)

### Définition

Un **ORB (Object Request Broker)** est un **intermédiaire** qui permet à un objet local d'invoquer des méthodes sur un objet distant **comme s'il était local**, en masquant :
- la **localisation** de l'objet distant,
- la **sérialisation/désérialisation** des données,
- la **communication réseau** sous-jacente.

### Objectif

Fournir une **abstraction de la distribution** des objets dans un système réparti.

---

### Modèle d'invocation

1. Le client invoque une méthode sur une **référence distante**.
2. L’appel est intercepté et transmis via l’ORB au **serveur distant**.
3. Le serveur exécute la méthode réelle et renvoie le résultat via l’ORB.

### Technologies fondées sur le concept d’ORB

- **Java RMI** (Remote Method Invocation),
- **CORBA** (Common Object Request Broker Architecture),
- **gRPC**, **Thrift**, **Web Services**, etc.

### Cas d’usage courant

Une **application de banque distribuée** où :
- Le client souhaite consulter le solde d’un compte,
- La logique métier est sur un serveur distant,
- L’appel est transparent grâce à l’ORB (RMI dans notre cas).

---

### Exemple simplifié : ORB via RMI Java

#### 1. Interface distante

```java
// BanqueService.java
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BanqueService extends Remote {
    double consulterSolde(String numeroCompte) throws RemoteException;
}
```

### 2. Implémentation du serveur

```java
// BanqueServiceImpl.java
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;

public class BanqueServiceImpl extends UnicastRemoteObject implements BanqueService {
// Structure de données simulant une base de comptes
private final HashMap<String, Double> comptes;

// Constructeur : initialise la base de comptes
// Appelle le constructeur de UnicastRemoteObject (export automatique de l'objet)
public BanqueServiceImpl() throws RemoteException {
    comptes = new HashMap<>();
    comptes.put("123", 2500.0);    // Exemple de compte
    comptes.put("456", 12500.5);   // Exemple de compte
}

// Implémentation de la méthode distante
// Retourne le solde du compte s’il existe, ou 0.0 sinon
public double consulterSolde(String numeroCompte) throws RemoteException {
    return comptes.getOrDefault(numeroCompte, 0.0);
}

}
```

### 3. Lancement du registre RMI et du serveur

```java
// Serveur.java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Serveur {
    public static void main(String[] args) throws Exception {
        // Création de l’implémentation du service
        BanqueServiceImpl obj = new BanqueServiceImpl();

        // Création d’un registre RMI sur le port 1099 (par défaut)
        Registry registry = LocateRegistry.createRegistry(1099);

        // Enregistrement du service sous le nom "BanqueService"
        registry.rebind("BanqueService", obj);

        // Message d’information
        System.out.println("Serveur RMI prêt...");
    }

}
```

### 4. Client distant

```java
// Client.java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) throws Exception {
        // Accès au registre RMI local (localhost)
        Registry registry = LocateRegistry.getRegistry("localhost");

        // Récupération du stub du service distant par lookup
        BanqueService stub = (BanqueService) registry.lookup("BanqueService");

        // Appel à distance de la méthode consulterSolde sur le stub
        System.out.println("Solde du compte 123 : " + stub.consulterSolde("123") + " €");
    }
}
```

### Résultat
Solde du compte 123 : 2500.0 €

### Avantages du modèle ORB avec RMI

- Transparence d'accès à distance,

- Invocation simple (comme une méthode locale),

- Support natif des objets Java.


---

## 3.2 Le modèle RMI (les concepts, les interfaces, les classes de base)

### Objectif de RMI

**Java RMI (Remote Method Invocation)** permet à une application Java d'appeler des méthodes sur un objet situé dans une **machine virtuelle distante**, en utilisant des **références distantes**.

---

### Concepts clés du modèle RMI

| Concept | Description |
|-------- |-------------|
| **Objet distant** | Objet dont les méthodes peuvent être invoquées à distance |
| **Interface distante** | Interface que l'objet distant implémente, elle hérite de `java.rmi.Remote` |
| **Squelette (Stub)** | Représentation locale de l'objet distant côté client (généré automatiquement depuis Java 5) |
| **Serveur RMI** | Héberge les objets distants |
| **Registre RMI** | Annuaire permettant aux clients de retrouver les objets distants à l'aide d'un nom logique |
| **RMI Registry** | Processus d’écoute sur un port (souvent 1099) servant à publier et rechercher les objets distants |

---

### Classes et interfaces essentielles

| Élément | Rôle |
|--------|------|
| `Remote` | Interface marqueur pour définir une interface distante |
| `UnicastRemoteObject` | Classe de base utilisée pour exporter un objet distant |
| `RemoteException` | Exception requise dans toutes les méthodes distantes |
| `LocateRegistry` | Utilisé pour localiser ou créer un registre RMI |
| `Registry` | Interface représentant le registre RMI |

---

### Schéma de communication

1. **Client** utilise un `Stub` généré automatiquement.
2. Le `Stub` envoie l'appel au **serveur distant** via le **RMI runtime**.
3. Le serveur exécute la méthode et retourne le résultat au client.

---

### Cas d’usage : Service de gestion de comptes

#### Interface distante

```java
// CompteService.java
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CompteService extends Remote {
    double getSolde(String id) throws RemoteException;
    void depot(String id, double montant) throws RemoteException;
}
```

### Implémentation distante

```java
// CompteServiceImpl.java
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;

public class CompteServiceImpl extends UnicastRemoteObject implements CompteService {
    private final HashMap<String, Double> comptes = new HashMap<>();

    public CompteServiceImpl() throws RemoteException {
        comptes.put("A001", 500.0);
    }

    public double getSolde(String id) throws RemoteException {
        return comptes.getOrDefault(id, 0.0);
    }

    public void depot(String id, double montant) throws RemoteException {
        comptes.put(id, getSolde(id) + montant);
    }
}
```

### Démarrage du serveur RMI

```java
// ServeurRMI.java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServeurRMI {
    public static void main(String[] args) throws Exception {
        CompteServiceImpl service = new CompteServiceImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("CompteService", service);
        System.out.println("Service Compte prêt.");
    }
}
```

### Client RMI

```java
// ClientRMI.java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost");
        CompteService stub = (CompteService) registry.lookup("CompteService");

        stub.depot("A001", 200.0);
        System.out.println("Nouveau solde A001 : " + stub.getSolde("A001"));
    }
}
```

### Résultat attendu

Nouveau solde A001 : 700.0

---

### À retenir

- Les interfaces distantes doivent toujours étendre Remote.

- Chaque méthode distante doit déclarer throws RemoteException.

- Les objets distants doivent être exportés avec UnicastRemoteObject.

- Le registre RMI permet de publier/retrouver les objets distants via des noms.

---

## 3.3 Le service de nommage

### Objectif

Le **service de nommage** permet aux objets distants d’être publiés sous un **nom logique** dans un **registre RMI** (`rmiregistry`). Les clients peuvent alors retrouver ces objets en se connectant au registre avec ce nom.

### Fonctionnement

- Le **serveur** enregistre l’objet distant dans le registre avec un nom (`rebind(nom, objet)`).
- Le **client** recherche cet objet par son nom (`lookup(nom)`).

---

### API Principale

| Méthode | Description |
|--------|-------------|
| `LocateRegistry.createRegistry(port)` | Crée un registre local sur le port spécifié |
| `LocateRegistry.getRegistry(hôte, port)` | Obtient une référence au registre sur l’hôte et le port spécifiés |
| `Registry.rebind(String nom, Remote obj)` | Lie ou remplace un objet dans le registre |
| `Registry.lookup(String nom)` | Recherche un objet distant par nom |
| `Registry.unbind(String nom)` | Supprime la liaison d’un objet dans le registre |

---

### Exemple de cas d’usage : Service de nommage pour `CompteService`

#### Serveur : publication dans le registre

```java
// ServeurRMI.java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServeurRMI {
    public static void main(String[] args) throws Exception {
        CompteServiceImpl service = new CompteServiceImpl();

        // Création d’un registre RMI sur le port 1099
        Registry registry = LocateRegistry.createRegistry(1099);

        // Enregistrement de l'objet distant sous un nom logique
        registry.rebind("CompteService", service);

        System.out.println("Service Compte enregistré dans le registre RMI.");
    }
}
```

### Client : récupération de l’objet distant

```java
// ClientRMI.java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI {
    public static void main(String[] args) throws Exception {
        // Connexion au registre RMI distant (localhost, port 1099)
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);

        // Recherche de l’objet distant via son nom logique
        CompteService stub = (CompteService) registry.lookup("CompteService");

        // Utilisation de l’objet distant
        stub.depot("A001", 150.0);
        System.out.println("Solde actuel A001 : " + stub.getSolde("A001"));
    }
}
```

---

 ### À retenir

- Le nom logique est la clé d’accès pour le client.

- Un registre RMI peut être lancé manuellement via la commande rmiregistry ou programmé avec LocateRegistry.createRegistry().

- On peut aussi utiliser Naming.rebind() (version simplifiée) mais Registry est plus flexible.

---

## 3.4 Le processus de développement du client et du serveur

### Objectif

Développer une application distribuée avec **RMI** suit une démarche structurée :

1. Définir une **interface distante**.
2. Créer une **implémentation** de cette interface.
3. **Enregistrer** l’objet dans un registre RMI.
4. Développer un **client** qui recherche l’objet distant et invoque ses méthodes.

---

### Étapes détaillées

#### 1. Définir l’interface distante

L’interface doit hériter de `java.rmi.Remote` et chaque méthode doit déclarer `throws RemoteException`.

```java
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CompteService extends Remote {
    void depot(String numero, double montant) throws RemoteException;
    double getSolde(String numero) throws RemoteException;
}
```

---

### 2. Implémenter l’interface distante

L’implémentation doit hériter de UnicastRemoteObject ou exporter explicitement l’objet.

```java
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class CompteServiceImpl extends UnicastRemoteObject implements CompteService {
    private final Map<String, Double> comptes = new HashMap<>();

    public CompteServiceImpl() throws RemoteException {
        super();
    }

    public void depot(String numero, double montant) throws RemoteException {
        comptes.put(numero, comptes.getOrDefault(numero, 0.0) + montant);
    }

    public double getSolde(String numero) throws RemoteException {
        return comptes.getOrDefault(numero, 0.0);
    }
}
```

---

### 3. Lancer le serveur et enregistrer l’objet

```java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServeurRMI {
    public static void main(String[] args) throws Exception {
        CompteService service = new CompteServiceImpl();

        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("CompteService", service);

        System.out.println("Service CompteService enregistré avec succès !");
    }
}
```

---

### 4. Développer le client RMI

```java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        CompteService service = (CompteService) registry.lookup("CompteService");

        service.depot("C001", 200.0);
        System.out.println("Solde C001 : " + service.getSolde("C001"));
    }
}
```

### Compilation et exécution
## Compilation :

```bash
javac *.java
```

### Lancement :

```bash
# Dans un terminal :
java ServeurRMI

# Dans un autre terminal :
java ClientRMI
```

### Résultat attendu

[Serveur]
Service CompteService enregistré avec succès !

[Client]
Solde C001 : 200.0

### Remarques

- Le port 1099 est le port par défaut du registre RMI.

- Il est possible d'utiliser un SecurityManager et une politique de sécurité pour des scénarios distribués avancés.

---

## 3.5 Les contraintes de sécurité et de chargement de classes

### Objectif

Les applications RMI doivent gérer :
- La **sécurité des accès distants** (notamment en environnement non fiable).
- Le **chargement dynamique** des classes (client ou serveur peut charger des classes distantes).

---

### 1. Le `SecurityManager` (obsolète depuis Java 17)

Avant Java 17, RMI exigeait un `SecurityManager` pour contrôler les actions sensibles (lecture/écriture fichiers, exécution, etc.).

```java
if (System.getSecurityManager() == null) {
    System.setSecurityManager(new SecurityManager());
}
```

Et un fichier de politique de sécurité devait être défini, exemple :

## security.policy
```java
grant {
    permission java.security.AllPermission;
};
```

Lancement avec politique :

```bash
java -Djava.security.policy=security.policy ServeurRMI
```

Note : Depuis Java 17, cette exigence est obsolète — les politiques de sécurité sont dépréciées et désactivées.

---

### 2. Chargement dynamique de classes

RMI peut charger dynamiquement des classes inconnues localement, via des URLs. Cela permet au serveur ou au client d’utiliser des implémentations distantes non connues à la compilation.

Ancien mécanisme (obsolète en Java 11+) :

- Utilisation de java.rmi.server.codebase

- Exemple :
```bash
-Djava.rmi.server.codebase=http://localhost/classes/
```

## Exemple de contexte :
Un client télécharge dynamiquement une implémentation d’interface fournie par le serveur.

---

### 3. Bonnes pratiques actuelles (Java 11+)

- Éviter le chargement dynamique, préférer des jars partagés.

- Désactiver le SecurityManager, qui est obsolète.

- Utiliser TLS ou tunnels pour sécuriser les communications.

- Signer les classes si échange dynamique nécessaire.

## Exemple de code sécurisé minimal (Java 11+)

```java
public class ServeurRMI {
    public static void main(String[] args) throws Exception {
        CompteService service = new CompteServiceImpl();

        // Aucun SecurityManager nécessaire avec les versions modernes de Java
        LocateRegistry.createRegistry(1099).rebind("CompteService", service);

        System.out.println("Service sécurisé prêt !");
    }
}
```