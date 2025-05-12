import java.util.concurrent.CompletableFuture;

public class JournalisationAsynchrone {
    public static void log(String message) {
        // Utilisation de CompletableFuture pour journaliser de façon non bloquante
        CompletableFuture.runAsync(() -> {
            System.out.println("[LOG] " + message);
        });
    }
}