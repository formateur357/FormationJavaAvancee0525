import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BanqueServiceImpl extends UnicastRemoteObject implements BanqueService {
    private final Map<String, Double> comptes = new ConcurrentHashMap<>();

    public BanqueServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void creerCompte(String numero) throws RemoteException {
        comptes.putIfAbsent(numero, 0.0);
        System.out.println("[SERVEUR] Compte créé : " + numero);
    }

    @Override
    public void depot(String numero, double montant) throws RemoteException, CompteInexistantException {
        verifierCompte(numero);
        comptes.computeIfPresent(numero, (k, v) -> v + montant);
        System.out.println("[SERVEUR] Dépôt de " + montant + " sur " + numero);
    }

    @Override
    public void retrait(String numero, double montant) throws RemoteException, CompteInexistantException, SoldeInsuffisantException {
        verifierCompte(numero);
        synchronized (comptes) {
            double solde = comptes.get(numero);
            if (solde < montant) {
                throw new SoldeInsuffisantException("Solde insuffisant pour le retrait");
            }
            comptes.put(numero, solde - montant);
        }
        System.out.println("[SERVEUR] Retrait de " + montant + " sur " + numero);
    }

    @Override
    public double getSolde(String numero) throws RemoteException, CompteInexistantException {
        verifierCompte(numero);
        return comptes.get(numero);
    }

    @Override
    public void virement(String from, String to, double montant) throws RemoteException, CompteInexistantException, SoldeInsuffisantException {
        verifierCompte(from);
        verifierCompte(to);
        synchronized (comptes) {
            double soldeFrom = comptes.get(from);
            if (soldeFrom < montant) {
                throw new SoldeInsuffisantException("Solde insuffisant pour le virement");
            }
            comptes.put(from, soldeFrom - montant);
            comptes.put(to, comptes.get(to) + montant);
        }
        System.out.println("[SERVEUR] Virement de " + montant + " de " + from + " vers " + to);
    }

    private void verifierCompte(String numero) throws CompteInexistantException {
        if (!comptes.containsKey(numero)) {
            throw new CompteInexistantException("Compte non trouvé : " + numero);
        }
    }
}