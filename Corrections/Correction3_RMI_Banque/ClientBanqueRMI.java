import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientBanqueRMI {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            BanqueService service = (BanqueService) registry.lookup("BanqueService");

            service.creerCompte("C1001");
            service.creerCompte("C2002");
            service.depot("C1001", 200.0);
            service.depot("C2002", 50.0);
            service.retrait("C1001", 30.0);

            double solde = service.getSolde("C1001");
            System.out.println("[CLIENT] Solde C1001 : " + solde);

            service.virement("C1001", "C2002", 50.0);
            System.out.println("[CLIENT] Virement effectué.");

            System.out.println("[CLIENT] Solde C1001 : " + service.getSolde("C1001"));
            System.out.println("[CLIENT] Solde C2002 : " + service.getSolde("C2002"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}