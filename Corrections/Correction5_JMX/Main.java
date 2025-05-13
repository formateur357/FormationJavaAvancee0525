import javax.management.*;
import java.lang.management.ManagementFactory;

public class Main {

    public static void main(String[] args) throws Exception {
        StockManager stockManager = new StockManager();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("entrepot:type=StockManager");
        mbs.registerMBean(stockManager, name);

        String[] products = {"Clavier", "Souris", "Écran", "Casque"};
        for (int i = 0; i < 5; i++) {
            new Thread(new AgentDeCommande(stockManager, products[i % products.length], 5)).start();
        }

        new Thread(new AgentDeReapprovisionnement(stockManager, products)).start();

        System.out.println("Serveur JMX en cours d'exécution...");
        Thread.sleep(60000);
    }
}