package agents;

import mbeans.StockManagerStandardMBean;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class AgentDeReapprovisionnement implements Runnable {
    @Override
    public void run() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("warehouse:type=StockStandard");
            StockManagerStandardMBean proxy = MBeanServerInvocationHandler.newProxyInstance(
                server, name, StockManagerStandardMBean.class, false
            );

            while (true) {
                proxy.replenishStock("produit-A", 10);
                System.out.println("Réapprovisionnement automatique effectué.");
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}