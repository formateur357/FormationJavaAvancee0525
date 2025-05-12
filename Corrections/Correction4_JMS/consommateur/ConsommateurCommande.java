package consommateur;

import jakarta.jms.*;
import modele.Commande;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class ConsommateurCommande {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("COMMANDES");
        Topic topic = session.createTopic("ALERTES_STOCK");

        MessageConsumer consumer = session.createConsumer(queue);
        MessageProducer alertPublisher = session.createProducer(topic);

        Map<String, Integer> stock = new HashMap<>();
        consumer.setMessageListener(message -> {
            try {
                ObjectMessage objMsg = (ObjectMessage) message;
                Commande cmd = (Commande) objMsg.getObject();

                int reste = stock.getOrDefault(cmd.getProduit(), 20) - cmd.getQuantite();
                stock.put(cmd.getProduit(), reste);

                System.out.println("Commande reçue : " + cmd + ", stock restant : " + reste);

                if (reste < 5) {
                    TextMessage alert = session.createTextMessage("ALERTE: Stock faible pour " + cmd.getProduit() + " (" + reste + " restants)");
                    alertPublisher.send(alert);
                    System.out.println("Alerte envoyée pour produit " + cmd.getProduit());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("Consommateur démarré, en attente de commandes...");
        Thread.sleep(30000);
        connection.close();
    }
}