/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.server.handler;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/23/11
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class PendingSandBoxItemCustomPersistenceHandler extends SandBoxItemCustomPersistenceHandler {

    private Log LOG = LogFactory.getLog(PendingSandBoxItemCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        boolean isSandboxItem = SandBoxItem.class.getName().equals(ceilingEntityFullyQualifiedClassname);
        if (isSandboxItem) {
            return persistencePackage.getCustomCriteria()[4].equals("pending");
        }
        return false;
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (ArrayUtils.isEmpty(customCriteria) || customCriteria.length != 5) {
            ServiceException e = new ServiceException("Invalid request for entity: " + ceilingEntityFullyQualifiedClassname);
            LOG.error("Invalid request for entity: " + ceilingEntityFullyQualifiedClassname, e);
            throw e;
        }
        AdminUser adminUser = adminRemoteSecurityService.getPersistentAdminUser();
        if (adminUser == null) {
            ServiceException e = new ServiceException("Unable to determine current user logged in status");
            LOG.error("Unable to determine current user logged in status", e);
            throw e;
        }
        try {
            String operation = customCriteria[1];
            List<Long> targets = new ArrayList<Long>();
            if (!StringUtils.isEmpty(customCriteria[2])) {
                String[] parts = customCriteria[2].split(",");
                for (String part : parts) {
                    try {
                        targets.add(Long.valueOf(part));
                    } catch (NumberFormatException e) {
                        //do nothing
                    }
                }
            }

            String requiredPermission = "PERMISSION_ALL_USER_SANDBOX";

            boolean allowOperation = false;
            for (AdminRole role : adminUser.getAllRoles()) {
                for (AdminPermission permission : role.getAllPermissions()) {
                    if (permission.getName().equals(requiredPermission)) {
                        allowOperation = true;
                        break;
                    }
                }
            }

            if (!allowOperation) {
                ServiceException e = new ServiceException("Current user does not have permission to perform operation");
                LOG.error("Current user does not have permission to perform operation", e);
                throw e;
            }

            SandBox mySandBox = sandBoxService.retrieveUserSandBox(null, adminUser);
            SandBox approvalSandBox = sandBoxService.retrieveApprovalSandBox(mySandBox);

            if (operation.equals("releaseAll")) {
                sandBoxService.revertAllSandBoxItems(mySandBox, approvalSandBox);
            } else if (operation.equals("releaseSelected")) {
                List<SandBoxItem> items = retrieveSandBoxItems(targets, dynamicEntityDao, mySandBox);
                sandBoxService.revertSelectedSandBoxItems(approvalSandBox, items);
            } else if (operation.equals("reclaimAll")) {
                sandBoxService.rejectAllSandBoxItems(mySandBox, approvalSandBox, "reclaiming sandbox items");
            } else if (operation.equals("reclaimSelected")) {
                List<SandBoxItem> items = retrieveSandBoxItems(targets, dynamicEntityDao, mySandBox);
                sandBoxService.rejectSelectedSandBoxItems(approvalSandBox, "reclaiming sandbox item", items);
            }

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(SandBoxItem.class.getName(), persistencePerspective);
            cto.get("originalSandBoxId").setFilterValue(mySandBox.getId().toString());
            cto.get("archivedFlag").setFilterValue(Boolean.FALSE.toString());
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, SandBoxItem.class.getName(), originalProps);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, SandBoxItem.class.getName());
            List<Serializable> records = dynamicEntityDao.query(queryCriteria, SandBoxItem.class);
            Entity[] results = helper.getRecords(originalProps, records);
            int totalRecords = helper.getTotalRecords(persistencePackage, cto, ctoConverter);

            DynamicResultSet response = new DynamicResultSet(results, totalRecords);

            return response;
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to execute persistence activity for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

}
