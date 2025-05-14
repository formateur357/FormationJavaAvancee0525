package producteur;

// Import des API JMS (Jakarta) et des classes nécessaires
import jakarta.jms.*;
import modele.Commande; // Classe métier représentant une commande
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ProducteurCommande {
    public static void main(String[] args) throws Exception {

        // 1. Connexion au broker ActiveMQ via la factory (URL par défaut en TCP)
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // 2. Création de la connexion (non démarrée ici car ce n'est pas nécessaire pour l'envoi)
        Connection connection = factory.createConnection();

        // 3. Création d'une session non transactionnelle avec acquittement automatique
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 4. Création (ou accès) à la Queue "COMMANDES"
        Queue queue = session.createQueue("COMMANDES");

        // 5. Création d'un producteur de message pour cette queue
        MessageProducer producer = session.createProducer(queue);

        // 6. Tableau de produits pour simuler les commandes
        String[] produits = {"Clavier", "Souris", "Écran", "Casque"};

        // 7. Boucle pour envoyer 10 commandes
        for (int i = 0; i < 10; i++) {

            // 7.1 Génère un identifiant unique pour la commande
            String id = UUID.randomUUID().toString();

            // 7.2 Choisit un produit aléatoirement dans le tableau
            String produit = produits[ThreadLocalRandom.current().nextInt(produits.length)];

            // 7.3 Génère une quantité aléatoire entre 1 et 9
            int quantite = ThreadLocalRandom.current().nextInt(1, 10);

            // 7.4 Crée une instance de Commande (classe métier)
            Commande commande = new Commande(id, produit, quantite);

            // 7.5 Crée un message JMS contenant l'objet Commande
            ObjectMessage message = session.createObjectMessage(commande);

            // 7.6 Envoie le message vers la queue "COMMANDES"
            producer.send(message);

            // 7.7 Affiche la commande envoyée (utile pour le suivi console)
            System.out.println("Commande envoyée : " + commande);

            // 7.8 Pause de 1 seconde entre chaque envoi
            Thread.sleep(1000);
        }

        // 8. Fermeture de la connexion JMS (nettoyage des ressources)
        connection.close();
    }
}
