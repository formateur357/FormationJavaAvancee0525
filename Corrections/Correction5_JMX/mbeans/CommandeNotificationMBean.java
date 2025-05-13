package mbeans;

public interface CommandeNotificationMBean {
    void sendOrderNotification(String orderId, String status);
}