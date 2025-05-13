package mbeans;

import javax.management.*;
import javax.management.NotificationBroadcasterSupport;

public class CommandeNotifier extends NotificationBroadcasterSupport implements CommandeNotificationMBean {
    private long seq = 1;

    @Override
    public void sendOrderNotification(String orderId, String status) {
        Notification notif = new Notification(
            "commande.alert", this, seq++,
            "Commande " + orderId + " - statut : " + status
        );
        sendNotification(notif);
    }
}