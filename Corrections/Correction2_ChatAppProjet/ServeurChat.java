import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServeurChat {

    // Liste synchronisée pour gérer tous les flux de sortie des clients connectés
    private final List<ObjectOutputStream> fluxClients = Collections.synchronizedList(new ArrayList<>());

    // Historique des messages reçus, que l'on pourra éventuellement sauvegarder ou recharger
    private final List<Message> historique = new ArrayList<>();

    // Pool de threads pour gérer plusieurs clients en parallèle
    private final ExecutorService pool = Executors.newCachedThreadPool();

    // Démarrage du serveur sur un port donné
    public void demarrer(int port) throws IOException {
        ServerSocket serveur = new ServerSocket(port);
        System.out.println("Serveur en écoute sur le port " + port);

        // Boucle infinie d’attente de clients
        while (true) {
            Socket client = serveur.accept(); // Acceptation d’un nouveau client
            pool.execute(() -> gererClient(client)); // Le client est pris en charge dans un thread du pool
        }
    }

    // Gère la communication avec un client
    private void gererClient(Socket socket) {
        try (
            // Création des flux pour lire/écrire des objets Java (sérialisés)
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            fluxClients.add(out); // Ajoute le flux du client à la liste des clients pour diffusion

            Object obj;
            // Boucle de réception des objets envoyés par le client
            while ((obj = in.readObject()) != null) {
                if (obj instanceof Message) {
                    Message msg = (Message) obj;
                    System.out.println(msg); // Affiche le message côté serveur
                    historique.add(msg); // Ajoute à l’historique
                    enregistrerHistorique(); // Sauvegarde l’historique sur le disque
                    diffuserMessage(msg); // Envoie le message à tous les autres clients
                }
            }

        } catch (Exception e) {
            // Gestion des erreurs ou de la déconnexion du client
            System.out.println("Client déconnecté.");
        }
    }

    // Envoie un message à tous les clients connectés
    private void diffuserMessage(Message msg) {
        synchronized (fluxClients) {
            for (ObjectOutputStream out : fluxClients) {
                try {
                    out.writeObject(msg);
                    out.flush();
                } catch (IOException ignored) {
                    // Si un flux échoue (client déconnecté), on ignore simplement ici
                }
            }
        }
    }

    // Sauvegarde l’historique des messages dans un fichier sérialisé
    private void enregistrerHistorique() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("messages.ser"))) {
            oos.writeObject(historique);
        } catch (IOException e) {
            e.printStackTrace(); // Affiche l’erreur si l’enregistrement échoue
        }
    }
}
