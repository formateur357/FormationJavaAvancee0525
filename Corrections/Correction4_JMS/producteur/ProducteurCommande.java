package producteur;

import jakarta.jms.*;
import modele.Commande;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ProducteurCommande {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("COMMANDES");
        MessageProducer producer = session.createProducer(queue);

        String[] produits = {"Clavier", "Souris", "Écran", "Casque"};

        for (int i = 0; i < 10; i++) {
            String id = UUID.randomUUID().toString();
            String produit = produits[ThreadLocalRandom.current().nextInt(produits.length)];
            int quantite = ThreadLocalRandom.current().nextInt(1, 10);

            Commande commande = new Commande(id, produit, quantite);
            ObjectMessage message = session.createObjectMessage(commande);
            producer.send(message);
            System.out.println("Commande envoyée : " + commande);

            Thread.sleep(1000);
        }

        connection.close();
    }
}