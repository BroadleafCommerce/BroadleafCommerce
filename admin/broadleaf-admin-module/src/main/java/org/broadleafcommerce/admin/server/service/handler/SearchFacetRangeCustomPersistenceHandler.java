/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.search.domain.SearchFacetRangeImpl;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.PersistenceModule;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

/**
 * @author gdiaz
 */
@Component("blSearchFacetRangeCustomPersistenceHandler")
public class SearchFacetRangeCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(SearchFacetRangeCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return SearchFacetRangeImpl.class.getName().equals(ceilingEntityFullyQualifiedClassname);

    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        addDefaultSort(cto);

        PersistenceModule myModule = helper.getCompatibleModule(persistencePackage.getPersistencePerspective().getOperationTypes().getFetchType());
        DynamicResultSet results = myModule.fetch(persistencePackage, cto);

        return results;
    }

    private static String[] sortFields = new String[] { "embeddablePriceList.priceList", "minValue", "maxValue" };

    protected void addDefaultSort(CriteriaTransferObject cto) {
        boolean userSort = false;
        for (FilterAndSortCriteria fasc : cto.getCriteriaMap().values()) {
            if (fasc.getSortDirection() != null) {
                userSort = true;
                break;
            }
        }

        if (!userSort) {
            for (int i = 0; i < sortFields.length; i++) {
                String string = sortFields[i];
                FilterAndSortCriteria fsc = cto.getCriteriaMap().get(string);
                if (fsc == null) {
                    fsc = new FilterAndSortCriteria(string, i);
                    cto.add(fsc);
                }
                fsc.setSortAscending(true);
            }
        }
    }

}
