package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerAddressImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author by reginaldccole
 */
@Component("blCustomerAddressCustomPersistenceHandler")
public class CustomerAddressCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(CustomerAddressCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        boolean canHandle = false;
        ArrayList<String> clazzNames = new ArrayList<>();
        clazzNames.add(CustomerAddress.class.getName());
        clazzNames.add(CustomerAddressImpl.class.getName());
        String ceilingClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        if ( ceilingClassname!= null) {
            for (String clazzName : clazzNames) {
                if (ceilingClassname.equalsIgnoreCase(clazzName)) {
                    canHandle = true;
                    break;
                }
            }
        }
        return canHandle;
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            Long entityId = Long.parseLong(entity.findProperty("id").getValue());
            if (entityId != null) {
                CustomerAddress adminInstance = (CustomerAddress)dynamicEntityDao.find(CustomerAddressImpl.class,entityId);
                if (Status.class.isAssignableFrom(adminInstance.getClass())) {
                    ((Status)adminInstance).setArchived('Y');
                }
                dynamicEntityDao.merge(adminInstance);
                return;
            }
            helper.getCompatibleModule(OperationType.BASIC).remove(persistencePackage);
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
        }
    }
}
