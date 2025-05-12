import java.rmi.RemoteException;

public class SoldeInsuffisantException extends RemoteException {
    public SoldeInsuffisantException(String message) {
        super(message);
    }
}