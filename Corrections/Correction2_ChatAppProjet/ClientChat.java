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
        Socket socket = new Socket(hoteServeur, portServeur);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

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

        new Thread(() -> {
            try (ServerSocket p2pServer = new ServerSocket(portLocalP2P)) {
                while (true) {
                    Socket client = p2pServer.accept();
                    BufferedReader inP2P = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String msg = inP2P.readLine();
                    System.out.println("[P2P] " + msg);
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String texte = scanner.nextLine();
            if (texte.startsWith("@peer")) {
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
                Message msg = new Message(nom, texte);
                out.writeObject(msg);
                out.flush();
            }
        }
    }

    private void envoyerP2P(String ip, int port, String message) {
        try (Socket socket = new Socket(ip, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
        } catch (IOException e) {
            System.out.println("Échec envoi P2P : " + e.getMessage());
        }
    }
}