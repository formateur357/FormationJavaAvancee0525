import javax.management.*;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

public class JMXServer {
    public static void main(String[] args) throws Exception {
        StockManager stockManager = new StockManager();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("entrepot:type=StockManager");
        mbs.registerMBean(stockManager, name);

        LocateRegistry.createRegistry(1099);
        System.out.println("Serveur JMX démarré...");
        Thread.sleep(60000);
    }
}