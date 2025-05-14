package consommateur;

// Import des API JMS (Jakarta) et des classes nécessaires
import jakarta.jms.*;
import modele.Commande; // Classe métier représentant une commande (produit + quantité)
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class ConsommateurCommande {
    public static void main(String[] args) throws Exception {

        // 1. Connexion au broker ActiveMQ via la factory (TCP par défaut)
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // 2. Création et démarrage de la connexion
        Connection connection = factory.createConnection();
        connection.start();

        // 3. Création d'une session JMS sans transaction, avec acquittement automatique
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 4. Création (ou accès) à la Queue de commandes
        Queue queue = session.createQueue("COMMANDES");

        // 5. Création (ou accès) au Topic des alertes de stock
        Topic topic = session.createTopic("ALERTES_STOCK");

        // 6. Création d'un consommateur sur la Queue "COMMANDES"
        MessageConsumer consumer = session.createConsumer(queue);

        // 7. Création d’un producteur de messages pour le Topic d’alertes
        MessageProducer alertPublisher = session.createProducer(topic);

        // 8. Stock simulé (produit → quantité disponible)
        Map<String, Integer> stock = new HashMap<>();

        // 9. Définition du listener asynchrone pour traiter les messages de commande
        consumer.setMessageListener(message -> {
            try {
                // 9.1 Conversion du message en ObjectMessage (type attendu)
                ObjectMessage objMsg = (ObjectMessage) message;

                // 9.2 Récupération de l’objet Commande
                Commande cmd = (Commande) objMsg.getObject();

                // 9.3 Mise à jour du stock pour ce produit (valeur par défaut : 20)
                int reste = stock.getOrDefault(cmd.getProduit(), 20) - cmd.getQuantite();
                stock.put(cmd.getProduit(), reste);

                // 9.4 Affichage du traitement
                System.out.println("Commande reçue : " + cmd + ", stock restant : " + reste);

                // 9.5 Si stock faible (< 5), on envoie une alerte via le Topic
                if (reste < 5) {
                    TextMessage alert = session.createTextMessage(
                        "ALERTE: Stock faible pour " + cmd.getProduit() + " (" + reste + " restants)"
                    );
                    alertPublisher.send(alert);
                    System.out.println("Alerte envoyée pour produit " + cmd.getProduit());
                }

            } catch (Exception e) {
                // Gestion des erreurs éventuelles lors de la réception ou du traitement
                e.printStackTrace();
            }
        });

        // 10. Message d'information pour indiquer que le consommateur est actif
        System.out.println("Consommateur démarré, en attente de commandes...");

        // 11. Attente passive pendant 30 secondes avant de fermer (simule l'exécution continue)
        Thread.sleep(30000);

        // 12. Fermeture propre de la connexion (et donc des ressources JMS)
        connection.close();
    }
}
