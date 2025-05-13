import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientChat {
    private final String nom;
    private final int portLocalP2P;

    public ClientChat(String nom, int portLocalP2P) {
        this.nom = nom;
        this.portLocalP2P = portLocalP2P;
    }

    public void demarrer(String hoteServeur, int portServeur) throws IOException {
        // Connexion au serveur central (serveur de chat)
        Socket socket = new Socket(hoteServeur, portServeur);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        // Thread pour recevoir les messages du serveur (asynchrone)
        new Thread(() -> {
            try {
                Object obj;
                while ((obj = in.readObject()) != null) {
                    if (obj instanceof Message) {
                        System.out.println(((Message) obj).toString());
                    }
                }
            } catch (Exception e) {
                System.out.println("Déconnecté du serveur.");
            }
        }).start();

        // === Partie SERVEUR P2P ===
        // Thread qui écoute les connexions entrantes des autres clients (pairs)
        new Thread(() -> {
            try (ServerSocket p2pServer = new ServerSocket(portLocalP2P)) {
                while (true) {
                    // Accepte une connexion entrante d’un pair (client P2P)
                    Socket client = p2pServer.accept();

                    // Prépare la lecture du message envoyé
                    BufferedReader inP2P = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                    );

                    // Lit et affiche le message reçu via le protocole P2P
                    String msg = inP2P.readLine();
                    System.out.println("[P2P] " + msg);

                    // Ferme la connexion après réception (protocole simple : 1 message = 1 connexion)
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // === Partie CLIENT (entrée utilisateur et envoi) ===
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String texte = scanner.nextLine();

            if (texte.startsWith("@peer")) {
                // Commande spéciale pour envoyer un message directement à un autre pair
                // Format attendu : @peer IP PORT message
                String[] parts = texte.split(" ", 4);
                if (parts.length == 4) {
                    String ip = parts[1];
                    int port = Integer.parseInt(parts[2]);
                    String msg = parts[3];
                    envoyerP2P(ip, port, "[" + nom + " -> P2P] " + msg);
                } else {
                    System.out.println("Utilisation : @peer IP PORT message");
                }
            } else {
                // Message classique envoyé au serveur central
                Message msg = new Message(nom, texte);
                out.writeObject(msg);
                out.flush();
            }
        }
    }

    // Méthode utilitaire pour envoyer un message directement à un autre client (P2P)
    private void envoyerP2P(String ip, int port, String message) {
        try (
            Socket socket = new Socket(ip, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Envoie du message au client destinataire (serveur P2P distant)
            out.println(message);
        } catch (IOException e) {
            System.out.println("Échec envoi P2P : " + e.getMessage());
        }
    }
}
