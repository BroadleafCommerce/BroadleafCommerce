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
package org.broadleafcommerce.openadmin.server.security.handler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.Resource;

/**
 * @author jfischer
 */
@Component("blAdminUserCustomPersistenceHandler")
public class AdminUserCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(AdminUserCustomPersistenceHandler.class);

    @Resource(name = "blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;

    @Resource(name = "blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;
    
    @Autowired
    protected Environment environment;

    protected boolean getRequireUniqueEmailAddress() {
        return  environment.getProperty("admin.user.requireUniqueEmailAddress").isEmpty();
    }

    @Override
    public Boolean willHandleSecurity(PersistencePackage persistencePackage) {
        return true;
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        try {
            return persistencePackage.getCeilingEntityFullyQualifiedClassname() != null
                    && AdminUser.class.isAssignableFrom(Class.forName(persistencePackage.getCeilingEntityFullyQualifiedClassname()))
                    && persistencePackage.getPersistencePerspectiveItems().isEmpty();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.ADD);
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            AdminUser adminInstance = (AdminUser) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(AdminUser.class.getName(), persistencePerspective);
            adminInstance = (AdminUser) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            Entity errorEntity = validateLegalUsernameAndEmail(entity, adminInstance, true);
            if (errorEntity != null) {
                return errorEntity;
            }

            adminInstance.setUnencodedPassword(adminInstance.getPassword());
            adminInstance.setPassword(null);

            adminInstance = adminSecurityService.saveAdminUser(adminInstance);

            Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            return adminEntity;
        } catch (Exception e) {
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }


    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(AdminUser.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            AdminUser adminInstance = (AdminUser) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            Entity errorEntity = validateLegalUsernameAndEmail(entity, adminInstance, false);
            if (errorEntity != null) {
                return errorEntity;
            }

            String passwordBefore = adminInstance.getPassword();
            adminInstance.setPassword(null);
            adminInstance = (AdminUser) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
            Property passwordProperty = entity.getPMap().get("password");
            if (passwordProperty != null) {
                if (StringUtils.isNotEmpty(passwordProperty.getValue())) {
                    adminInstance.setUnencodedPassword(passwordProperty.getValue());
                    adminInstance.setPassword(null);
                } else {
                    adminInstance.setPassword(passwordBefore);
                }
            }

            validateUserUpdateSecurity(persistencePackage, adminInstance);

            adminInstance = adminSecurityService.saveAdminUser(adminInstance);
            Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            return adminEntity;

        } catch (Exception e) {
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper)
            throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        String idValue = entity.findProperty("id").getValue();
        String userLoginToRemove = entity.findProperty("login")==null?null:entity.findProperty("login").getValue();

        AdminUser persistentAdminUser = adminRemoteSecurityService.getPersistentAdminUser();

        if (persistentAdminUser != null && persistentAdminUser.getLogin() != null && userLoginToRemove != null) {
            if (persistentAdminUser.getLogin().equals(userLoginToRemove)) {
                throw new ValidationException(entity, "admin.cantDeleteCurrentUserError");
            }
        }

        if (idValue != null) {
            Long id = Long.parseLong(idValue);
            AdminUser adminInstance = adminSecurityService.readAdminUserById(id);
            // Check if Status was Weaved in
            if (Status.class.isAssignableFrom(adminInstance.getClass())) {
                ((Status) adminInstance).setArchived('Y');
                adminSecurityService.saveAdminUser(adminInstance);
                return;
            }

        }

        OperationType removeType = persistencePackage.getPersistencePerspective().getOperationTypes().getRemoveType();
        helper.getCompatibleModule(removeType).remove(persistencePackage);


    }

    protected void validateUserUpdateSecurity(PersistencePackage persistencePackage, AdminUser changingUser) throws ServiceException {
        // The current user can update their data, but they cannot update other user's data.
        if (!adminRemoteSecurityService.getPersistentAdminUser().getId().equals(changingUser.getId())) {
            adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.UPDATE);
        }
    }


    protected Entity validateLegalUsernameAndEmail(Entity entity, AdminUser adminInstance, boolean isAdd) {
        String login = entity.findProperty("login").getValue();
        String email = entity.findProperty("email").getValue();

        // We know the username/email is ok if we're doing an update and they're unchanged
        boolean skipLoginCheck = false;
        boolean skipEmailCheck = !getRequireUniqueEmailAddress();
        if (!isAdd) {
            if (StringUtils.equals(login, adminInstance.getLogin())) {
                skipLoginCheck = true;
            }

            if (!getRequireUniqueEmailAddress() || StringUtils.equals(email, adminInstance.getEmail())) {
                skipEmailCheck = true;
            }
        }

        if (!skipLoginCheck && adminSecurityService.readAdminUserByUserName(login) != null) {
            entity.addValidationError("login", "admin.nonUniqueUsernameError");
            return entity;
        }

        if (!skipEmailCheck && CollectionUtils.isNotEmpty(adminSecurityService.readAdminUsersByEmail(email))) {
            entity.addValidationError("email", "admin.nonUniqueEmailError");
            return entity;
        }

        return null;
    }

}
