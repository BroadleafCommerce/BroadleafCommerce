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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemImpl;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Component("blSandBoxItemCustomPersistenceHandler")
public class SandBoxItemCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private final Log LOG = LogFactory.getLog(SandBoxItemCustomPersistenceHandler.class);

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    @Resource(name="blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;

    @Resource(name="blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;

    @Override
    public Boolean willHandleSecurity(PersistencePackage persistencePackage) {
        return true;
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        boolean isSandboxItem = SandBoxItem.class.getName().equals(ceilingEntityFullyQualifiedClassname);
        if (isSandboxItem) {
            return persistencePackage.getCustomCriteria()[4].equals("standard");
        }
        return false;
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    protected List<SandBoxItem> retrieveSandBoxItems(List<Long> ids, DynamicEntityDao dynamicEntityDao, SandBox mySandBox) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("The passed in ids parameter is empty");
        }
        //declare SandBoxItemImpl explicitly, as we do not want to retrieve other polymorphic types (e.g. WorkflowSandBoxItemImpl)
        Criteria criteria = dynamicEntityDao.createCriteria(SandBoxItemImpl.class);
        criteria.add(Restrictions.in("id", ids));
        criteria.add(Restrictions.or(Restrictions.eq("originalSandBoxId", mySandBox.getId()), Restrictions.eq("sandBoxId", mySandBox.getId())));
        return criteria.list();
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
            throw e;
        }
        try {
            String moduleKey = customCriteria[0];
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
            String comment = customCriteria[3];

            String requiredPermission;
            if (moduleKey.equals("userSandBox")) {
                requiredPermission = "PERMISSION_ALL_USER_SANDBOX";
            } else {
                requiredPermission = "PERMISSION_ALL_APPROVER_SANDBOX";
            }

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

            SandBox originalSandBox;
            SandBox currentSandBox;
            if (moduleKey.equals("userSandBox")) {
                currentSandBox = sandBoxService.retrieveUserSandBox(null, adminUser);
                originalSandBox = currentSandBox;
            } else {
                originalSandBox = sandBoxService.retrieveUserSandBox(null, adminUser);
                currentSandBox = sandBoxService.retrieveApprovalSandBox(originalSandBox);
            }


            if (operation.equals("promoteAll")) {
                sandBoxService.promoteAllSandBoxItems(currentSandBox, comment);
            } else if (operation.equals("promoteSelected")) {
                List<SandBoxItem> items = retrieveSandBoxItems(targets, dynamicEntityDao, currentSandBox);
                sandBoxService.promoteSelectedItems(currentSandBox, comment, items);
            } else if (operation.equals("revertRejectAll")) {
                if (moduleKey.equals("userSandBox")) {
                    sandBoxService.revertAllSandBoxItems(originalSandBox, currentSandBox);
                } else {
                    sandBoxService.rejectAllSandBoxItems(originalSandBox, currentSandBox, comment);
                }
            } else if (operation.equals("revertRejectSelected")) {
                List<SandBoxItem> items = retrieveSandBoxItems(targets, dynamicEntityDao, currentSandBox);
                if (moduleKey.equals("userSandBox")) {
                    sandBoxService.revertSelectedSandBoxItems(currentSandBox, items);
                } else {
                    sandBoxService.rejectSelectedSandBoxItems(currentSandBox, comment, items);
                }
            }

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(SandBoxItem.class.getName(), persistencePerspective);
            cto.get("sandBoxId").setFilterValue(currentSandBox.getId().toString());
            cto.get("archivedFlag").setFilterValue(Boolean.FALSE.toString());
            List<FilterMapping> filterMappings = helper.getFilterMappings(persistencePerspective, cto, SandBoxItem.class.getName(), originalProps);

            //declare SandBoxItemImpl explicitly, as we do not want to retrieve other polymorphic types (e.g. WorkflowSandBoxItemImpl)
            List<Serializable> records = helper.getPersistentRecords(SandBoxItem.class.getName(), filterMappings, cto.getFirstResult(), cto.getMaxResults());
            Entity[] results = helper.getRecords(originalProps, records);

            int totalRecords = helper.getTotalRecords(SandBoxItem.class.getName(), filterMappings);

            DynamicResultSet response = new DynamicResultSet(results, totalRecords);

            return response;
        } catch (Exception e) {
            throw new ServiceException("Unable to execute persistence activity for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

}
