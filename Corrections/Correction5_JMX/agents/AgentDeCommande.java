package agents;

import mbeans.CommandeNotificationMBean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MBeanServerInvocationHandler;
import java.lang.management.ManagementFactory;
import java.util.UUID;

public class AgentDeCommande implements Runnable {
    @Override
    public void run() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("warehouse:type=CommandeNotifier");
            CommandeNotificationMBean mbean = MBeanServerInvocationHandler.newProxyInstance(
                server, name, CommandeNotificationMBean.class, false
            );

            while (true) {
                Thread.sleep(15000);
                mbean.sendOrderNotification(UUID.randomUUID().toString(), "URGENT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}