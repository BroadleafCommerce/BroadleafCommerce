package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/27/11
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class OfferEntityInspectCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(OfferEntityInspectCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return !ArrayUtils.isEmpty(persistencePackage.getCustomCriteria()) && persistencePackage.getCustomCriteria()[0].equals("offerInspect");
    }
    
    protected void removeItems(String[] removalKeys, Map<String, FieldMetadata> data) {
        List<String> removeItems = new ArrayList<String>();
        for (String propertyName : removalKeys) {
            for (String key : data.keySet()) {
                if (key.startsWith(propertyName)) {
                    removeItems.add(key);
                }
            }
        }

        for (String key : removeItems) {
            data.remove(key);
        }
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		try {
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
            Map<String, FieldMetadata> data = helper.getSimpleMergedProperties(ceilingEntityFullyQualifiedClassname, persistencePackage.getPersistencePerspective(), entityClasses);

            if (ceilingEntityFullyQualifiedClassname.equals("org.broadleafcommerce.profile.core.domain.Customer")) {
                removeItems(new String[]{
                    "password",
                    "challengeAnswer",
                    "passwordChangeRequired",
                    "challengeQuestion",
                    "firstName",
                    "lastName",
                    "emailAddress",
                    "auditable.dateCreated",
                    "auditable.dateUpdated"
                }, data);
            } else if (ceilingEntityFullyQualifiedClassname.equals("org.broadleafcommerce.core.order.domain.FulfillmentGroup")) {
                removeItems(new String[]{
                    "order",
                    "personalMessage"
                }, data);
            } else if (ceilingEntityFullyQualifiedClassname.equals("org.broadleafcommerce.core.order.domain.OrderItem")) {
                removeItems(new String[]{
                    "order",
                    "giftWrapOrderItem",
                    "bundleOrderItem",
                    "product.defaultCategory",
                    "product.name",
                    "product.description",
                    "product.longDescription",
                    "product.activeStartDate",
                    "product.activeEndDate",
                    "product.sku",
                    "sku.name",
                    "sku.salePrice",
                    "sku.retailPrice",
                    "category.activeEndDate",
                    "category.activeStartDate",
                    "personalMessage"
                }, data);
            } else if (ceilingEntityFullyQualifiedClassname.equals("org.broadleafcommerce.core.order.domain.Order")) {
                removeItems(new String[]{
                    "customer",
                    "status",
                    "name",
                    "cityTax",
                    "countyTax",
                    "stateTax",
                    "districtTax",
                    "countryTax",
                    "totalTax",
                    "totalShipping",
                    "total",
                    "submitDate",
                    "orderNumber",
                    "emailAddress",
                    "auditable.dateCreated",
                    "auditable.dateUpdated"
                }, data);
            }

            Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
            allMergedProperties.put(MergedPropertyType.PRIMARY, data);
			ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
			DynamicResultSet results = new DynamicResultSet(mergedMetadata);

			return results;
		} catch (Exception e) {
            ServiceException exception = new ServiceException("Unable to retrieve inspection results for " + ceilingEntityFullyQualifiedClassname, e);
            LOG.error("Unable to retrieve inspection results for " + ceilingEntityFullyQualifiedClassname, exception);
            throw exception;
		}
    }
}
