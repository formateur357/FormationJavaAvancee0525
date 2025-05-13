import mbeans.*;
import agents.*;

import javax.management.*;
import java.lang.management.ManagementFactory;
import javax.management.modelmbean.RequiredModelMBean;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.management.remote.*;

public class Main {
    public static void main(String[] args) throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        StockManagerStandard standard = new StockManagerStandard();
        ObjectName stdName = new ObjectName("warehouse:type=StockStandard");
        server.registerMBean(standard, stdName);

        StockManagerDynamic dynamic = new StockManagerDynamic();
        ObjectName dynName = new ObjectName("warehouse:type=StockDynamic");
        server.registerMBean(dynamic, dynName);

        CommandeNotifier notifier = new CommandeNotifier();
        ObjectName notifName = new ObjectName("warehouse:type=CommandeNotifier");
        server.registerMBean(notifier, notifName);

        ModelMBeanService modelService = new ModelMBeanService();
        RequiredModelMBean modelMBean = modelService.createModelMBean();
        ObjectName modelName = new ObjectName("warehouse:type=StockModel");
        server.registerMBean(modelMBean, modelName);

        Registry registry = LocateRegistry.createRegistry(9999);
        JMXServiceURL url = new JMXServiceURL(
            "service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi"
        );
        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
        cs.start();
        System.out.println("Serveur JMX exposé via RMI sur port 9999...");

        new Thread(new AgentDeCommande()).start();
        new Thread(new AgentDeReapprovisionnement()).start();
    }
}