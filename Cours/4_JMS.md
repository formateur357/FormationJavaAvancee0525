## 4.1 Les principes généraux de la communication par messages

### Définition

La communication par messages repose sur un modèle **asynchrone**, **découplé** et **orienté événements**. Les producteurs (senders) envoient des messages dans un système de messagerie (broker), et les consommateurs (receivers) les récupèrent.

Cette approche est au cœur des **architectures orientées services (SOA)** et des **systèmes distribués scalables**.

---

### Avantages du modèle par messages

- **Découplage temporel** : l’expéditeur et le récepteur n'ont pas besoin d’être actifs au même moment.
- **Découplage spatial** : ils n’ont pas besoin de se connaître directement.
- **Robustesse** : le broker assure le stockage temporaire des messages.
- **Scalabilité** : les producteurs et consommateurs peuvent évoluer indépendamment.

---

### Architecture générale

```text
+----------------+     Envoi      +----------------+     Réception    +----------------+
| Producteur     |  ------------> |  Broker (JMS)  |  -------------> | Consommateur    |
+----------------+                +----------------+                 +----------------+
```

Le broker peut être centralisé (ActiveMQ, RabbitMQ, etc.) ou décentralisé (Kafka, Pulsar…).

### Java Message Service (JMS)

JMS est une API standard de Java EE (et Jakarta EE) permettant la communication par messages via :

- Des connexions à un fournisseur JMS.

- Des sessions pour produire/consommer des messages.

- Des destinations : file d'attente (Queue) ou canal de publication (Topic).

- Des producteurs/consommateurs de messages.

### Cas d’usage : gestion de commandes asynchrone
Imaginez un site e-commerce. Lorsqu'un client passe une commande, le backend n’envoie pas directement l’e-mail de confirmation. Il publie un message dans une file JMS.

Le service de notification, exécuté sur un autre serveur, consomme ce message et envoie l’e-mail.

### Exemple schématique :

```text
Client -> CommandeService -> [JMS Queue: commandes]
                                   |
                        +------------------------+
                        | NotificationService    |
                        | - lit les messages     |
                        | - envoie les emails    |
                        +------------------------+
```

Cela garantit la résilience : même si le service d’e-mail est temporairement indisponible, le message reste dans la file.

---

Voici un exemple de code Java complet mettant en œuvre un système de messagerie avec JMS et ActiveMQ, pour illustrer la communication asynchrone via une Queue :

```java
// Nécessite la dépendance ActiveMQ (ex : org.apache.activemq:activemq-all)

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ProducteurCommande {
    public static void main(String[] args) throws JMSException {
        // Connexion au broker ActiveMQ local
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connexion = factory.createConnection();
        connexion.start();

        // Création d'une session non transactionnelle avec acquittement automatique
        Session session = connexion.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Création d'une file de destination (Queue)
        Destination destination = session.createQueue("fileCommandes");

        // Création d'un producteur lié à la file
        MessageProducer producteur = session.createProducer(destination);
        producteur.setDeliveryMode(DeliveryMode.PERSISTENT); // Assure la fiabilité du message

        // Envoi d'un message texte
        String commande = "Commande #127 : 2x écran HD";
        TextMessage message = session.createTextMessage(commande);
        producteur.send(message);

        System.out.println("Commande envoyée : " + commande);

        session.close();
        connexion.close();
    }
}
```

```java
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ConsommateurCommande {
    public static void main(String[] args) throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connexion = factory.createConnection();
        connexion.start();

        Session session = connexion.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("fileCommandes");

        MessageConsumer consommateur = session.createConsumer(destination);

        System.out.println("En attente de commande...");

        // Réception synchrone d’un message (bloquant)
        Message message = consommateur.receive();

        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            System.out.println("Commande reçue et traitée : " + textMessage.getText());
        }

        session.close();
        connexion.close();
    }
}
```

### Pour exécuter ce cas d’usage :

- Installe ActiveMQ localement : https://activemq.apache.org/

- Démarre le broker (bin/activemq start).

- Lance ConsommateurCommande (récepteur).

- Puis ProducteurCommande (émetteur).

---

# 4.2 Le modèle de base (les concepts de JMS, les interfaces et les classes)

Java Message Service (JMS) est une API standard permettant aux applications Java de communiquer de manière **asynchrone** par **échange de messages**. Elle repose sur une architecture orientée messages (*Message Oriented Middleware* ou MOM).

---

## **Concepts fondamentaux**

- **Producteur (Producer)** : application qui envoie des messages à une destination.
- **Consommateur (Consumer)** : application qui reçoit les messages depuis une destination.
- **Message** : unité de communication (texte, objet, map, etc.).
- **Destination** : canal logique de communication.
  - **Queue** : communication **point à point (PTP)**.
  - **Topic** : communication **publish/subscribe (pub/sub)**.
- **Session** : contexte d'envoi/réception d’un ou plusieurs messages.
- **Connection** : lien physique avec le broker JMS.

---

## **Interfaces clés de JMS**

| Interface           | Description                                                                 |
|---------------------|-----------------------------------------------------------------------------|
| `ConnectionFactory` | Fabrique des connexions vers le broker JMS                                 |
| `Connection`        | Connexion active entre l’application et le broker                          |
| `Session`           | Contexte pour la production/consommation de messages                       |
| `Destination`       | Représente la file ou le topic de communication                            |
| `MessageProducer`   | Pour envoyer des messages à une destination                                |
| `MessageConsumer`   | Pour recevoir des messages depuis une destination                          |
| `Message`           | Message de base (abstrait), avec sous-types comme `TextMessage`, `ObjectMessage` etc.|

---

## **Exemple : Envoi d’un message texte**

```java
ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
Connection connection = factory.createConnection();
Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
Destination queue = session.createQueue("maFile");
MessageProducer producer = session.createProducer(queue);

TextMessage msg = session.createTextMessage("Bonjour JMS !");
producer.send(msg);
```

### Cas d’usage : file de traitement de commandes asynchrone

Objectif : découpler une application e-commerce de son système de traitement logistique.

- Lorsqu'une commande est validée, elle est envoyée dans une queue JMS.

- Le backend logistique consomme ces messages pour les préparer et les expédier.


Avantages :

- Tolérance aux pannes (la commande reste en file).

- Scalabilité (plusieurs consommateurs peuvent lire la queue).

- Réduction de la latence perçue côté utilisateur.

---

# 4.3 Les différentes formes de messages en JMS

L’API JMS propose plusieurs **types de messages** pour répondre à différents besoins de transmission d’informations.

---

## **1. `TextMessage`**
Contient une **chaîne de caractères**.

- Utilisé pour transmettre du texte brut, du JSON, du XML, etc.

```java
TextMessage message = session.createTextMessage("Commande validée : #1001");
```

### 2. ObjectMessage

Contient un objet Java sérialisable.

- Permet d’envoyer des objets métiers (POJO).

- L’objet doit implémenter Serializable.

```java
Commande commande = new Commande("1234", "Clavier", 2);
ObjectMessage message = session.createObjectMessage(commande);
```

### 3. MapMessage

Contient une map de paires clé-valeur, typées dynamiquement (clé = String).

- Idéal pour représenter un ensemble de données structurées simples.

```java
MapMessage message = session.createMapMessage();
message.setString("produit", "Souris");
message.setInt("quantite", 5);
```

### 4. BytesMessage
Contient une séquence de données binaires (byte[]).

Utilisé pour les fichiers, images, flux binaires.

```java
BytesMessage message = session.createBytesMessage();
message.writeBytes(fichierBytes);
```

### 5. StreamMessage

Contient une suite d’éléments Java primitifs écrits en séquence.

- Peu utilisé, mais utile pour envoyer un flux structuré dynamiquement.

```java
StreamMessage message = session.createStreamMessage();
message.writeString("Clavier");
message.writeInt(2);
```

### Cas d’usage : transmission de commandes e-commerce
```text
|Type de message	| Utilisation                                                               |
|-------------------|---------------------------------------------------------------------------|
|TextMessage	    | Envoi de notifications, de logs, d’événements JSON                        |
|-------------------|---------------------------------------------------------------------------|
|ObjectMessage	    | Envoi d'une commande avec tous ses attributs (produit, client, adresse...)|
|-------------------|---------------------------------------------------------------------------|
|MapMessage	        | Envoi rapide d’un résumé de commande : produit, prix, quantité            |
|-------------------|---------------------------------------------------------------------------|
|BytesMessage	    | Envoi de bons de commande PDF                                             |
|-------------------|---------------------------------------------------------------------------|
|StreamMessage	    | Envoi dynamique de métriques ou d’historiques de commandes                |
|-------------------|---------------------------------------------------------------------------|
```

---

# 4.4 La communication en mode point à point (PTP)

Le mode **Point-à-Point (PTP)** est un des deux modèles de messagerie définis par JMS. Il repose sur la notion de **file de messages (Queue)**.

---

## **Principe**

- **Un producteur envoie un message** à une file (Queue).
- **Un seul consommateur** recevra ce message.
- Chaque message est **consommé une seule fois**.
- Sémantique de **“livrer à un destinataire unique”**.

---

## **API JMS utilisée**

- `Queue`: destination pour les messages.
- `QueueSender` (ou `MessageProducer`): envoie un message à la queue.
- `QueueReceiver` (ou `MessageConsumer`): lit un message depuis la queue.

---

## **Cas d’usage typique**
- Traitement de commandes.
- Tâches distribuées à un pool de workers.
- Systèmes où **chaque tâche ne doit être exécutée qu’une seule fois**.

---

## **Exemple concret : système de commande**

Supposons que nous avons une application e-commerce, où les commandes doivent être traitées **une par une** par un agent de traitement.

### Producteur : envoi d’une commande

```java
ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
Connection connection = factory.createConnection();
Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
Queue queue = session.createQueue("COMMANDES");

MessageProducer producer = session.createProducer(queue);
ObjectMessage message = session.createObjectMessage(new Commande("123", "Souris", 2));
producer.send(message);
connection.close();
```

### Consommateur : traitement des commandes

```java
Connection connection = factory.createConnection();
connection.start(); // Nécessaire pour consommer
Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
Queue queue = session.createQueue("COMMANDES");

MessageConsumer consumer = session.createConsumer(queue);
ObjectMessage message = (ObjectMessage) consumer.receive();
Commande cmd = (Commande) message.getObject();

System.out.println("Commande reçue : " + cmd);
connection.close();
```

### Avantages du mode PTP

- Garantie de livraison exactement une fois (avec gestion adéquate).

- Bonne répartition du travail dans des systèmes distribués.

- File tampon naturelle si les consommateurs sont plus lents que les producteurs.

---

# 4.5 La communication en mode publish/subscribe (Pub/Sub)

Le modèle **publish/subscribe** est le second grand modèle de communication dans JMS. Il repose sur le concept de **topic** plutôt que de file. Il permet à un producteur de **diffuser un message à plusieurs consommateurs** simultanément.

---

## **Principe**

- Le producteur **publie** un message sur un **Topic**.
- Tous les consommateurs **abonnés au Topic** reçoivent une **copie** du message.
- Les abonnements peuvent être **durables ou non durables** :
  - **Non durable** : le consommateur doit être connecté pour recevoir les messages.
  - **Durable** : les messages sont conservés tant que le client n’a pas lu.

---

## **API JMS utilisée**

- `Topic`: représente le canal de diffusion.
- `TopicPublisher` (ou `MessageProducer`): publie un message.
- `TopicSubscriber` (ou `MessageConsumer`): s’abonne au topic pour recevoir les messages.

---

## **Cas d’usage typique**

- Systèmes de **notifications** ou de **diffusion d’événements**.
- Mise à jour d’**interfaces graphiques** temps réel (dashboards).
- **Systèmes d’alerte**, de **messagerie** ou de **market data**.

---

## **Exemple concret : système d’alertes stock**

### Publisher : alerte sur un produit

```java
ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
Connection connection = factory.createConnection();
Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
Topic topic = session.createTopic("ALERTES_STOCK");

MessageProducer producer = session.createProducer(topic);
TextMessage message = session.createTextMessage("Stock critique : Écran < 5 unités");
producer.send(message);
connection.close();
```

### Subscriber : interface de surveillance

```java
Connection connection = factory.createConnection();
connection.setClientID("surveillance-client"); // Nécessaire pour abonnement durable
connection.start();
Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
Topic topic = session.createTopic("ALERTES_STOCK");

// Abonnement durable
TopicSubscriber subscriber = session.createDurableSubscriber(topic, "surveillance-sub");
TextMessage message = (TextMessage) subscriber.receive();
System.out.println("ALERTE REÇUE : " + message.getText());

connection.close();
```

### Avantages du mode Pub/Sub

- Permet la diffusion large à plusieurs modules/clients.

- Déconnecte les producteurs des consommateurs (faible couplage).

- Supporte des abonnements durables très utiles pour les consommateurs intermittents.