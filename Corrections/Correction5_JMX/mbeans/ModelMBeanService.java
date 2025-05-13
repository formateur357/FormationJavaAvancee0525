package mbeans;

import javax.management.*;
import javax.management.modelmbean.*;

public class ModelMBeanService {
    public RequiredModelMBean createModelMBean() throws Exception {
        ModelMBeanInfo mbeanInfo = new ModelMBeanInfoSupport(
            StockManagerStandard.class.getName(),
            "ModelMBean stock avancé",
            null, null, null, null
        );

        RequiredModelMBean modelMBean = new RequiredModelMBean(mbeanInfo);
        StockManagerStandard impl = new StockManagerStandard();
        modelMBean.setManagedResource(impl, "ObjectReference");

        return modelMBean;
    }
}