import java.io.Serializable;
import java.net.Socket;

public class Utilisateur implements Serializable {
    private String nom;
    private int age;
    
    // Non sérialisable : il faut le rendre transient
    private transient Socket socket;

    public Utilisateur(String nom, int age, Socket socket) {
        this.nom = nom;
        this.age = age;
        this.socket = socket;
    }
}


// Exemple de setSotimeout
socket.setSoTimeout(5000);  // Timeout de 5 secondes
