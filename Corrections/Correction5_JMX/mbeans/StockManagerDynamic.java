package mbeans;

import javax.management.*;
import java.util.*;

public class StockManagerDynamic implements DynamicMBean {
    private final Map<String, Integer> stock = new HashMap<>();

    @Override
    public Object getAttribute(String attribute) {
        return stock.getOrDefault(attribute, 0);
    }

    @Override
    public void setAttribute(Attribute attribute) {
        stock.put(attribute.getName(), (Integer) attribute.getValue());
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList list = new AttributeList();
        for (String attr : attributes) {
            list.add(new Attribute(attr, getAttribute(attr)));
        }
        return list;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        AttributeList list = new AttributeList();
        for (Attribute attr : attributes.asList()) {
            setAttribute(attr);
            list.add(attr);
        }
        return list;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) {
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return new MBeanInfo(
            this.getClass().getName(), "Stock Dynamic MBean",
            new MBeanAttributeInfo[0], null, null, null
        );
    }
}