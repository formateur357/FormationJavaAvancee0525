import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServeurBanqueRMI {
    public static void main(String[] args) {
        try {
            BanqueService service = new BanqueServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("BanqueService", service);
            System.out.println("[SERVEUR] Service Banque RMI lancé sur le port 1099.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}