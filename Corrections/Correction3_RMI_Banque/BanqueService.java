import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BanqueService extends Remote {
    void creerCompte(String numero) throws RemoteException;
    void depot(String numero, double montant) throws RemoteException, CompteInexistantException;
    void retrait(String numero, double montant) throws RemoteException, CompteInexistantException, SoldeInsuffisantException;
    double getSolde(String numero) throws RemoteException, CompteInexistantException;
    void virement(String from, String to, double montant) throws RemoteException, CompteInexistantException, SoldeInsuffisantException;
}