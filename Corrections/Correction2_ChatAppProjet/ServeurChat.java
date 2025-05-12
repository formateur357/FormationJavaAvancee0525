import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServeurChat {
    private final List<ObjectOutputStream> fluxClients = Collections.synchronizedList(new ArrayList<>());
    private final List<Message> historique = new ArrayList<>();
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public void demarrer(int port) throws IOException {
        ServerSocket serveur = new ServerSocket(port);
        System.out.println("Serveur en écoute sur le port " + port);

        while (true) {
            Socket client = serveur.accept();
            pool.execute(() -> gererClient(client));
        }
    }

    private void gererClient(Socket socket) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            fluxClients.add(out);
            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof Message) {
                    Message msg = (Message) obj;
                    System.out.println(msg);
                    historique.add(msg);
                    enregistrerHistorique();
                    diffuserMessage(msg);
                }
            }
        } catch (Exception e) {
            System.out.println("Client déconnecté.");
        }
    }

    private void diffuserMessage(Message msg) {
        synchronized (fluxClients) {
            for (ObjectOutputStream out : fluxClients) {
                try {
                    out.writeObject(msg);
                    out.flush();
                } catch (IOException ignored) {}
            }
        }
    }

    private void enregistrerHistorique() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("messages.ser"))) {
            oos.writeObject(historique);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}