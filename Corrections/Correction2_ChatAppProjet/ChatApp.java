public class ChatApp {
    public static void main(String[] args) throws Exception {
        // Vérifie que des arguments ont été fournis (sinon, affiche l'aide)
        if (args.length < 1) {
            System.out.println("Usage : java ChatApp serveur <port> | client <nom> <serveur> <port> <portP2P>");
            return;
        }

        // Utilise un switch pour déterminer si l'application démarre en mode "serveur" ou "client"
        switch (args[0]) {

            // === Lancement du serveur de chat ===
            case "serveur" -> {
                int port = Integer.parseInt(args[1]); // Récupère le port depuis les arguments
                new ServeurChat().demarrer(port);     // Démarre le serveur de chat
            }

            // === Lancement d'un client de chat ===
            case "client" -> {
                String nom = args[1];                         // Nom de l'utilisateur
                String hote = args[2];                        // Adresse IP ou nom du serveur
                int portServeur = Integer.parseInt(args[3]);  // Port du serveur de chat
                int portP2P = Integer.parseInt(args[4]);      // Port pour le P2P local (réception directe)
                new ClientChat(nom, portP2P).demarrer(hote, portServeur); // Lancement du client
            }

            // === Gestion d'une commande inconnue ===
            default -> System.out.println("Argument inconnu.");
        }
    }
}
