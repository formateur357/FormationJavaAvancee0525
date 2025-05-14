package superviseur;

// Import des classes JMS nécessaires
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Superviseur {
    public static void main(String[] args) throws Exception {

        // 1. Création de la fabrique de connexions JMS pour se connecter au broker ActiveMQ
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // 2. Création de la connexion au broker
        Connection connection = factory.createConnection();

        // 3. Définition d’un ID client — nécessaire **uniquement** si on souhaite un abonnement durable
        connection.setClientID("superviseur-1"); // Utile si on veut activer les durable subscribers

        // 4. Démarrage explicite de la connexion (obligatoire côté consommateur)
        connection.start();

        // 5. Création d’une session non transactionnelle avec acquittement automatique
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 6. Accès (ou création) au topic nommé "ALERTES_STOCK"
        Topic topic = session.createTopic("ALERTES_STOCK");

        // 7. Création d’un consommateur de messages sur le topic
        // 👉 Ici c'est un **abonnement simple**, pas un abonnement durable (même si clientID est défini)
        MessageConsumer consumer = session.createConsumer(topic);

        // 8. Définition du comportement asynchrone : exécution automatique lorsqu’un message est reçu
        consumer.setMessageListener(message -> {
            try {
                // 8.1 On suppose que le message reçu est de type texte (TextMessage)
                TextMessage textMessage = (TextMessage) message;

                // 8.2 Affichage du contenu de l'alerte sur la console
                System.out.println("🔔 Alerte reçue : " + textMessage.getText());
            } catch (Exception e) {
                // En cas d’erreur, on affiche la trace
                e.printStackTrace();
            }
        });

        // 9. Message d'information pour l'utilisateur
        System.out.println("Superviseur en écoute des alertes...");

        // 10. Le superviseur reste actif 30 secondes avant de se fermer
        Thread.sleep(30000);

        // 11. Fermeture de la connexion proprement
        connection.close();
    }
}
