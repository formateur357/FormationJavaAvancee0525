# 🧪 Exercice Pratique : Système de messagerie entre utilisateurs avec sockets

## 🎯 Objectif

Créer un **système de messagerie réseau** basé sur les sockets en Java, permettant :
- à plusieurs **clients** de se connecter à un **serveur**,
- d’**envoyer et recevoir des messages**,
- de **sauvegarder les messages** via **sérialisation**,
- et de permettre un **mode peer-to-peer** entre deux clients.

---

## 🧩 Spécifications fonctionnelles

### Serveur
- Accepte plusieurs connexions clients (multithread).
- Reçoit les messages envoyés par les clients.
- Redistribue les messages à tous les clients connectés (**broadcast**).
- Journalise tous les messages dans un fichier `.ser`.

### Client
- Se connecte au serveur via un socket.
- Envoie des messages saisis au clavier.
- Affiche les messages reçus du serveur.
- Peut aussi initier une connexion P2P à un autre client pour un message privé (option).

### Mode P2P (bonus)
- Chaque client peut ouvrir un mini-serveur sur un port (ex : 6000).
- Il peut choisir d’envoyer un message directement à un autre client en entrant son IP/port.

---

## 🔧 Contraintes techniques

- Utiliser `ServerSocket`, `Socket`, `ObjectInputStream` / `ObjectOutputStream` pour la sérialisation.
- Chaque client est un thread ; le serveur utilise un `ExecutorService`.
- Tous les messages sont des objets `Message` sérialisables.

---

## 📦 Classes à implémenter

### 1. `Message.java`

```java
import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String auteur;
    private String contenu;
    private LocalDateTime date;

    public Message(String auteur, String contenu) {
        this.auteur = auteur;
        this.contenu = contenu;
        this.date = LocalDateTime.now();
    }

    public String toString() {
        return "[" + date + "] " + auteur + ": " + contenu;
    }
}
```

---

### 2. ServeurChat.java

- Gère les connexions.

- Reçoit les messages des clients et les rediffuse.

- Sauvegarde chaque message dans une liste et sérialise celle-ci.

### 3. ClientChat.java

- Se connecte au serveur.

- Lance un thread pour l’écoute des messages entrants.

- Permet d’envoyer des messages via Scanner.

### 4. ClientPeer.java (bonus)

- Version modifiée de ClientChat pouvant aussi recevoir des messages entrants (via un ServerSocket personnel).

- Permet l’envoi direct à une IP.