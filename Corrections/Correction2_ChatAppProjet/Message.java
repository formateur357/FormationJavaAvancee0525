import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private final String auteur;
    private final String contenu;
    private final LocalDateTime date;

    public Message(String auteur, String contenu) {
        this.auteur = auteur;
        this.contenu = contenu;
        this.date = LocalDateTime.now();
    }

    public String toString() {
        return "[" + date + "] " + auteur + ": " + contenu;
    }
}