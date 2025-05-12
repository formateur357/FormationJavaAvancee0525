import java.rmi.RemoteException;

public class CompteInexistantException extends RemoteException {
    public CompteInexistantException(String message) {
        super(message);
    }
}