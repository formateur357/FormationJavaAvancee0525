package superviseur;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Superviseur {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = factory.createConnection();
        connection.setClientID("superviseur-1"); // Pour abonnement durable
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("ALERTES_STOCK");

        // Abonnement simple (non durable pour simplifier)
        MessageConsumer consumer = session.createConsumer(topic);

        consumer.setMessageListener(message -> {
            try {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("🔔 Alerte reçue : " + textMessage.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("Superviseur en écoute des alertes...");
        Thread.sleep(30000);
        connection.close();
    }
}