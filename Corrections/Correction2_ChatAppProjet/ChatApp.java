public class ChatApp {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage : java ChatApp serveur <port> | client <nom> <serveur> <port> <portP2P>");
            return;
        }

        switch (args[0]) {
            case "serveur" -> {
                int port = Integer.parseInt(args[1]);
                new ServeurChat().demarrer(port);
            }
            case "client" -> {
                String nom = args[1];
                String hote = args[2];
                int portServeur = Integer.parseInt(args[3]);
                int portP2P = Integer.parseInt(args[4]);
                new ClientChat(nom, portP2P).demarrer(hote, portServeur);
            }
            default -> System.out.println("Argument inconnu.");
        }
    }
}