# Section 2 — La communication par socket

## 1. Rappels sur les principaux concepts réseaux

### 1.1 Introduction

En Java, la communication réseau repose sur le modèle OSI et le protocole TCP/IP. Les sockets permettent à deux programmes (souvent un client et un serveur) de communiquer via un réseau (local ou distant).

Un **socket** est une extrémité d’une communication. Une socket Java encapsule un canal de communication réseau, abstrait sous forme d’objet.

---

### 1.2 Modèle client-serveur

- Le **serveur** écoute un port donné, accepte les connexions entrantes, et traite les requêtes.
- Le **client** se connecte au serveur via une socket, en précisant l’IP (ou l’hôte) et le port.

---

### 1.3 Les classes clés de `java.net`

| Classe              | Description                                                   |
|---------------------|---------------------------------------------------------------|
| `ServerSocket`      | Crée un serveur qui écoute sur un port spécifique             |
| `Socket`            | Représente une connexion côté client ou après `accept()`      |
| `InetAddress`       | Manipule les adresses IP                                      |
| `InputStream` / `OutputStream` | Permettent de lire/écrire des données via la socket       |

---

### 1.4 Exemple de cas d’usage : client-serveur simple

**Objectif** : créer un serveur qui répond "Bonjour client" à chaque connexion.

#### Serveur

```java
// ServeurSimple.java
import java.io.*;
import java.net.*;

public class ServeurSimple {
    public static void main(String[] args) throws IOException {
        // Création du serveur sur le port 5000
        ServerSocket serveur = new ServerSocket(5000);

        // Boucle infinie pour accepter les connexions des clients
        while (true) {
            Socket socketClient = serveur.accept(); // Attente d’un client

            // Préparation du flux de sortie pour envoyer un message au client
            PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true);
            out.println("Bonjour client"); // Message envoyé au client

            socketClient.close(); // Connexion fermée après la réponse
        }
    }
}
```

---

```java
// ClientSimple.java
import java.io.*;
import java.net.*;

public class ClientSimple {
    public static void main(String[] args) throws IOException {
        // Connexion au serveur sur le port 5000
        Socket socket = new Socket("localhost", 5000);

        // Préparation du flux pour recevoir la réponse du serveur
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String message = in.readLine(); // Lecture du message

        System.out.println("Message du serveur : " + message); // Affichage
        socket.close(); // Fermeture de la connexion
    }
}

```

---

### 1.5 Cas d’usage typique

Système de notification en LAN : un serveur central reçoit des événements de plusieurs machines clientes et renvoie une réponse ("ACK", message d'erreur, etc.).

```java
// ServeurNotification.java
import java.io.*;
import java.net.*;

public class ServeurNotification {
    public static void main(String[] args) {
        // Création d'un serveur socket qui écoute sur le port 6000
        try (ServerSocket serveur = new ServerSocket(6000)) {
            System.out.println("Serveur de notification en attente sur le port 6000...");

            // Boucle infinie pour accepter plusieurs connexions clients
            while (true) {
                // Attente d'une connexion client (bloquant jusqu'à ce qu'un client se connecte)
                Socket clientSocket = serveur.accept();

```

---

```java

                // Chaque client est traité dans un thread séparé pour permettre la concurrence
                // C'est ici que le multithreading est utilisé : chaque client aura son propre thread
                new Thread(() -> {
                    try (
                        // Préparation des flux pour lire et écrire avec le client
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                    ) {
                        // Lecture du message envoyé par le client
                        String message = in.readLine();
                        System.out.println("Notification reçue : " + message);

                        // Envoi d'une réponse d'accusé de réception au client
                        out.println("ACK - Notification bien reçue");

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        // Fermeture du socket client pour libérer les ressources
                        try {
                            clientSocket.close();
                        } catch (IOException ignored) {}
                    }
                }).start(); // Démarrage du thread : traitement en parallèle du client
            }

        } catch (IOException e) {
            // Gestion des erreurs de serveur (ex: port occupé, problèmes réseau)
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }
}
```

---

```java
// ClientNotification.java
import java.io.*;
import java.net.*;

public class ClientNotification {
    public static void main(String[] args) {
        // Connexion au serveur sur le port 6000 (localhost)
        try (
            Socket socket = new Socket("localhost", 6000);
            // Préparation des flux pour envoyer et recevoir des données
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Envoi d'une notification au serveur
            String notification = "ALERTE : Température élevée détectée";
            out.println(notification);

            // Lecture de la réponse du serveur
            String reponse = in.readLine();
            System.out.println("Réponse du serveur : " + reponse);

        } catch (IOException e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }
}
```

---

**Résumé** :

- Ce système client/serveur est un exemple simple et efficace de communication en réseau.

- L’usage du multithreading dans le serveur permet d’éviter les blocages et d’accepter plusieurs clients en simultané.

- Chaque client s’exécute dans son propre thread, ce qui est crucial dans les applications réseau en production.

--- 

## 2.2 Le modèle client/serveur en mode connecté

### Principe

Le modèle client/serveur en mode connecté repose sur une communication **persistante** entre deux entités :
- **Le serveur** : attend les connexions entrantes sur un port donné.
- **Le client** : initie une connexion au serveur.

Une fois la connexion établie (via TCP), les deux entités peuvent échanger des données **fiablement** et **dans les deux sens** jusqu'à la fermeture explicite de la connexion.

### Étapes principales

- Le serveur crée un `ServerSocket` sur un port.
- Le client établit une connexion avec `Socket`.
- Le serveur accepte la connexion avec `accept()`, ce qui renvoie un `Socket` connecté au client.
- Des flux d'entrée/sortie (`InputStream`, `OutputStream`) permettent l’échange de messages.

### Avantages

- Fiabilité des échanges (grâce à TCP).
- Ordre de réception garanti.
- Détection des coupures de connexion.

---

### Exemple de cas d’usage : service de consultation bancaire

**Contexte :** une banque propose un service en ligne de consultation de solde. Le client se connecte et interroge son solde.

```java
// ServeurBanque.java
import java.io.*;
import java.net.*;

public class ServeurBanque {
    public static void main(String[] args) {
        // Création du serveur socket sur le port 7000
        try (ServerSocket serveur = new ServerSocket(7000)) {
            System.out.println("Serveur Banque prêt sur le port 7000...");

            // Boucle infinie pour accepter les connexions clients successives
            while (true) {
                // Attente d'une connexion entrante (bloque jusqu'à ce qu'un client se connecte)
                Socket client = serveur.accept();
```

---

```java
                // Création d'un nouveau thread pour gérer la requête du client
                new Thread(() -> {
                    try (
                        // Préparation des flux d'entrée/sortie pour communiquer avec le client
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true)
                    ) {
                        // Lecture de l'identifiant client envoyé depuis le client
                        String idClient = in.readLine();

                        // Récupération simulée du solde bancaire associé à cet identifiant
                        String solde = getSoldePourClient(idClient);

                        out.println("Solde pour client " + idClient + ": " + solde + "€");

                    } catch (IOException e) {
                        // Affichage d'une éventuelle erreur côté communication
                        e.printStackTrace();
                    } finally {
                        // Fermeture du socket client pour libérer les ressources
                        try {
                            client.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    ```
                }).start(); // Lancement du thread pour traiter le client en parallèle
            }

        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    // Méthode simulant une base de données pour renvoyer un solde à partir d'un identifiant client
    private static String getSoldePourClient(String id) {
        switch (id) {
            case "123": return "2500.50";
            case "456": return "135.75";
            default: return "0.00";
        }
    }
}
```

---

```java
// ClientBanque.java
import java.io.*;
import java.net.*;

public class ClientBanque {
    public static void main(String[] args) {
        // Bloc try-with-resources pour assurer la fermeture automatique des ressources réseau
        try (
            // Connexion au serveur situé sur la même machine (localhost) via le port 7000
            Socket socket = new Socket("localhost", 7000);

            // Flux pour recevoir les données du serveur
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Flux pour envoyer des données vers le serveur
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String idClient = "123"; // Identifiant client simulé (normalement saisi par l'utilisateur ou l'IHM)

            out.println(idClient); // Envoi de l'ID au serveur

            String reponse = in.readLine(); // Réception de la réponse du serveur (le solde)

            System.out.println("Réponse du serveur : " + reponse);

        } catch (IOException e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }
}
```

---

## 2.3 Serveur séquentiel vs serveur concurrent

### Serveur séquentiel

Un serveur séquentiel traite **une seule connexion client à la fois**. Il est simple à implémenter mais ne peut pas gérer plusieurs clients simultanément.

#### Inconvénients :
- Bloquant : si un client prend du temps, les autres doivent attendre.
- Pas adapté à des systèmes avec plusieurs utilisateurs actifs.

### Serveur concurrent

Un serveur concurrent **délègue chaque connexion à un thread distinct** (ou un pool de threads), permettant de gérer plusieurs clients en parallèle.

#### Avantages :
- Réactivité améliorée.
- Utilisation efficace des ressources processeur.
- Permet de gérer des centaines de connexions (avec un pool).

---

### Exemple de cas d’usage : service d’écho (le serveur renvoie les messages envoyés par le client)

**Comparaison des deux approches**

```java
// ServeurEchoSeq.java
import java.io.*;
import java.net.*;

public class ServeurEchoSeq {
    public static void main(String[] args) throws IOException {
        // Création du serveur sur le port 6000
        ServerSocket serverSocket = new ServerSocket(6000);
        System.out.println("Serveur écho séquentiel en écoute...");

        // Boucle principale pour traiter les connexions entrantes
        while (true) {
            // Accepte une seule connexion à la fois (mode séquentiel)
            Socket clientSocket = serverSocket.accept();

            // Préparation des flux de communication avec le client
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String ligne;
            // Lecture ligne par ligne des messages envoyés par le client
            while ((ligne = in.readLine()) != null) {
                // Réponse écho : le serveur renvoie le même message avec un préfixe
                out.println("ÉCHO : " + ligne);

                // Si le client envoie "exit", la communication avec ce client s’arrête
                if ("exit".equalsIgnoreCase(ligne)) break;
            }

            // Fin de la session avec le client
            clientSocket.close();
        }
    }
}
```

---

```java
// ServeurEchoConc.java
import java.io.*;
import java.net.*;

public class ServeurEchoConc {
    public static void main(String[] args) throws IOException {
        // Création du serveur sur le port 6000
        ServerSocket serverSocket = new ServerSocket(6000);
        System.out.println("Serveur écho concurrent en écoute...");

        // Boucle principale : le serveur accepte les connexions en continu
        while (true) {
            Socket clientSocket = serverSocket.accept();

            // Pour chaque client, on crée un thread dédié
            new Thread(() -> {
                try (
                    // Préparation des flux de communication
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    String ligne;
                    // Lecture ligne par ligne des messages du client
                    while ((ligne = in.readLine()) != null) {
                        // Réponse écho envoyée au client
                        out.println("ÉCHO : " + ligne);

                        // Condition d'arrêt : si le client envoie "exit", on interrompt le dialogue
                        if ("exit".equalsIgnoreCase(ligne)) break;
                    }

                    // Fermeture de la connexion avec le client
                    clientSocket.close();

                } catch (IOException e) {
                    // Gestion des erreurs liées à la communication avec un client
                    e.printStackTrace();
                }
            }).start(); // Le thread démarre ici, permettant une gestion concurrente
        }
    }
}
```

---

```java
// ClientEcho.java
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientEcho {
    public static void main(String[] args) {
        try (
            // Création de la connexion socket au serveur sur localhost:6000
            Socket socket = new Socket("localhost", 6000);

            // Flux pour recevoir la réponse du serveur
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Flux pour envoyer des messages au serveur
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Scanner pour lire les entrées de l'utilisateur via la console
            Scanner scanner = new Scanner(System.in)
        ) {
            String message;
            // Boucle pour envoyer des messages au serveur jusqu'à ce que l'utilisateur tape "exit"
            while (true) {
                System.out.print("> "); // Affichage du prompt
                message = scanner.nextLine(); // Lecture du message à envoyer au serveur

                out.println(message); // Envoi du message au serveur

                // Condition d'arrêt : si l'utilisateur tape "exit", on termine la communication
                if ("exit".equalsIgnoreCase(message)) break;

                // Affichage de la réponse du serveur
                System.out.println("Réponse : " + in.readLine());
            }
        } catch (IOException e) {
            // Gestion des erreurs (connexion, lecture/écriture, etc.)
            e.printStackTrace();
        }
    }
}
```

---

## 2.4 Utilisation de la sérialisation

### Définition

La **sérialisation** est le processus de conversion d’un objet Java en un flux de bytes afin de :
- l’envoyer via un réseau (ex: socket),
- le sauvegarder dans un fichier,
- ou le transférer entre processus JVM.

L’objet peut ensuite être **désérialisé** pour reconstruire l’objet original.

### Avantages

- Transfert d'objets complexes via socket (ex: liste de produits, utilisateur, etc.).
- Encapsulation de la logique métier dans les objets transmis.
- Plus flexible qu’un échange ligne par ligne.

### Conditions

- L'objet doit implémenter l'interface `Serializable`.
- Tous les champs doivent être sérialisables, ou marqués `transient`.

---

### Exemple de cas d’usage : envoi d'un objet `Produit` du client vers le serveur

#### 1. Classe `Produit` sérialisable

```java
// Produit.java
import java.io.Serializable;

public class Produit implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private int quantite;

    public Produit(String nom, int quantite) {
        this.nom = nom;
        this.quantite = quantite;
    }

    public String getNom() { return nom; }
    public int getQuantite() { return quantite; }

    @Override
    public String toString() {
        return nom + " (" + quantite + " unités)";
    }
}
```

---


### 2. Serveur : reçoit un objet Produit

```java
// ServeurObjet.java
import java.io.*;
import java.net.*;

public class ServeurObjet {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Création d'un serveur sur le port 7000
        ServerSocket serverSocket = new ServerSocket(7000);
        System.out.println("Serveur en attente d'un objet Produit...");

        // Acceptation d'une seule connexion client (version simple, non concurrente)
        try (
            Socket socket = serverSocket.accept();

            // Récupération d'un flux d'entrée orienté objet
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            // Lecture de l'objet envoyé par le client
            Produit produit = (Produit) ois.readObject();

            // Affichage de l'objet reçu (toString de Produit doit être redéfini)
            System.out.println("Reçu : " + produit);
        }
    }
}
```

---

### 3. Client : envoie un objet Produit

```java
// ClientObjet.java
import java.io.*;
import java.net.*;

public class ClientObjet {
    public static void main(String[] args) throws IOException {
        Produit produit = new Produit("Imprimante", 3);

        try (Socket socket = new Socket("localhost", 7000);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
             
            oos.writeObject(produit);
            System.out.println("Produit envoyé : " + produit);
        }
    }
}
```

---

## 2.5 La programmation en mode non connecté

### Définition

Contrairement au mode connecté (TCP), le mode **non connecté** repose sur le protocole **UDP (User Datagram Protocol)**, qui :
- **n'établit pas de connexion** avant l'envoi,
- envoie les **données sous forme de datagrammes**,
- est plus rapide, mais **moins fiable** (pas de garantie de livraison ni d’ordre).

---

### Caractéristiques

- Pas de confirmation de réception.
- Peut être utilisé pour la diffusion à plusieurs récepteurs.
- Adapté pour les applications temps réel (jeux en réseau, capteurs, vidéos, etc.).

### Classes Java principales

- `DatagramSocket` : point d’envoi ou de réception de datagrammes.
- `DatagramPacket` : paquet de données envoyé ou reçu.

---

### Exemple de cas d’usage : système de notification sans connexion

#### 1. Le serveur écoute et affiche les messages reçus

```java
// ServeurUDP.java
import java.net.*;

public class ServeurUDP {
    public static void main(String[] args) throws Exception {
        // Allocation d'un tampon pour stocker les données reçues (taille 1024 octets)
        byte[] buffer = new byte[1024];

        // Création du socket UDP sur le port 5000
        DatagramSocket socket = new DatagramSocket(5000);
        System.out.println("Serveur UDP en écoute...");

        // Boucle de réception continue des datagrammes UDP
        while (true) {
            // Création d’un paquet datagramme "réceptacle"
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Blocage jusqu’à la réception d’un datagramme
            socket.receive(packet);

            // Conversion des données reçues en String, avec découpage à la taille réelle du message
            String message = new String(packet.getData(), 0, packet.getLength());

            // Affichage du message reçu
            System.out.println("Reçu : " + message);
        }
    }
}
```

---

### 2. Le client envoie un message

```java
// ClientUDP.java
import java.net.*;

public class ClientUDP {
    public static void main(String[] args) throws Exception {
        // Message à envoyer au serveur
        String message = "Notification système : mise à jour disponible.";

        // Conversion du message en tableau d'octets
        byte[] buffer = message.getBytes();

        // Création du socket UDP côté client (pas besoin de bind explicite)
        DatagramSocket socket = new DatagramSocket();

        // Adresse IP du serveur (localhost dans ce cas)
        InetAddress address = InetAddress.getByName("localhost");

        // Création du datagramme à envoyer, destiné au port 5000
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 5000);

        // Envoi du datagramme via le socket
        socket.send(packet);

        System.out.println("Message envoyé.");

        // Fermeture du socket UDP
        socket.close();
    }
}
```

---

## 2.6 Le modèle Peer-to-Peer (P2P)

### Définition

Le modèle **Peer-to-Peer (P2P)** est une architecture réseau décentralisée dans laquelle chaque nœud (appelé **pair**) :
- peut agir **à la fois comme client et serveur**,
- communique **directement** avec d’autres pairs sans passer par un serveur central.

---

### Caractéristiques

- Tous les nœuds sont égaux.
- Les ressources (fichiers, messages, services) sont partagées entre pairs.
- Plus résilient et scalable que le modèle client/serveur classique.

---

### Utilisation typique

- Partage de fichiers (BitTorrent),
- Messageries instantanées décentralisées,
- Réseaux blockchain.

---

### Mise en œuvre Java (simplifiée)

Java n’a pas de support P2P natif, mais on peut simuler un modèle pair-à-pair avec des sockets, où chaque **peer** ouvre un `ServerSocket` tout en se connectant à d’autres via des `Socket`.

---

### Exemple de cas d’usage : Chat simplifié entre pairs

#### 1. Le pair écoute et envoie à tour de rôle

```java
// Peer.java
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Peer {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Lancement du serveur local sur un port donné
        ServerSocket serverSocket = new ServerSocket(6000);
        new Thread(() -> {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = in.readLine();
                    System.out.println("[Reçu] " + message);
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Envoi de message à un autre pair
        while (true) {
            System.out.print("Entrez l’IP du pair destinataire : ");
            String ip = scanner.nextLine();
            System.out.print("Message à envoyer : ");
            String msg = scanner.nextLine();

            Socket socket = new Socket(ip, 6000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);
            socket.close();
        }
    }
}
```

---

### Explication

- Chaque Peer agit à la fois comme serveur (en écoute sur un port) et client (envoi vers un autre pair).

Cela permet une architecture décentralisée, sans serveur principal.

### Limitations de l'exemple
Pas de gestion de plusieurs connexions simultanées.

Pas de découverte automatique des pairs.

Pas de NAT traversal ni gestion de topologie réseau.