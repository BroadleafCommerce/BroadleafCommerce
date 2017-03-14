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
package org.broadleafcommerce.openadmin.server.factory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.broadleafcommerce.openadmin.dto.OperationTypes;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blPersistencePackageFactory")
public class PersistencePackageFactoryImpl implements PersistencePackageFactory {

    @Resource(name = "blAdminNavigationService")
    protected AdminNavigationService adminNavigationService;

    protected DynamicDaoHelper dynamicDaoHelper = new DynamicDaoHelperImpl();

    @Override
    public PersistencePackage create(PersistencePackageRequest request) {
        PersistencePerspective persistencePerspective = new PersistencePerspective();

        persistencePerspective.setAdditionalForeignKeys(request.getAdditionalForeignKeys());
        persistencePerspective.setAdditionalNonPersistentProperties(new String[] {});
        
        if (request.getForeignKey() != null) {
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, 
                    request.getForeignKey());
        }

        switch (request.getType()) {
            case STANDARD:
                persistencePerspective.setOperationTypes(getDefaultOperationTypes());
                break;

            case ADORNED:
                if (request.getAdornedList() == null) {
                    throw new IllegalArgumentException("ADORNED type requires the adornedList to be set");
                }

                persistencePerspective.setOperationTypes(getOperationTypes(OperationType.ADORNEDTARGETLIST));
                persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.ADORNEDTARGETLIST,
                        request.getAdornedList());
                break;

            case MAP:
                if (request.getMapStructure() == null) {
                    throw new IllegalArgumentException("MAP type requires the mapStructure to be set");
                }

                persistencePerspective.setOperationTypes(getOperationTypes(OperationType.MAP));
                persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.MAPSTRUCTURE,
                        request.getMapStructure());
                break;
        }

        if (request.getOperationTypesOverride() != null) {
            persistencePerspective.setOperationTypes(request.getOperationTypesOverride());
        }

        PersistencePackage pp = new PersistencePackage();
        pp.setCeilingEntityFullyQualifiedClassname(request.getCeilingEntityClassname());
        if (!StringUtils.isEmpty(request.getSecurityCeilingEntityClassname())) {
            pp.setSecurityCeilingEntityFullyQualifiedClassname(request.getSecurityCeilingEntityClassname());
        }
        if (!ArrayUtils.isEmpty(request.getSectionCrumbs())) {
            SectionCrumb[] converted = new SectionCrumb[request.getSectionCrumbs().length];
            int index = 0;
            for (SectionCrumb crumb : request.getSectionCrumbs()) {
                SectionCrumb temp = new SectionCrumb();
                String originalSectionIdentifier = crumb.getSectionIdentifier();
                String sectionAsClassName;
                try {
                    sectionAsClassName = getClassNameForSection(crumb.getSectionIdentifier());
                } catch (Exception e) {
                    sectionAsClassName = request.getCeilingEntityClassname();
                }
                if (sectionAsClassName != null && !sectionAsClassName.equals(originalSectionIdentifier)) {
                    temp.setOriginalSectionIdentifier(originalSectionIdentifier);
                }
                temp.setSectionIdentifier(sectionAsClassName);

                temp.setSectionId(crumb.getSectionId());
                converted[index] = temp;
                index++;
            }
            pp.setSectionCrumbs(converted);
        } else {
            pp.setSectionCrumbs(new SectionCrumb[0]);
        }
        pp.setSectionEntityField(request.getSectionEntityField());
        pp.setFetchTypeFullyQualifiedClassname(null);
        pp.setPersistencePerspective(persistencePerspective);
        pp.setCustomCriteria(request.getCustomCriteria());
        pp.setCsrfToken(null);
        pp.setRequestingEntityName(request.getRequestingEntityName());
        pp.setValidateUnsubmittedProperties(request.isValidateUnsubmittedProperties());
        pp.setIsTreeCollection(request.isTreeCollection());
        pp.setAddOperationInspect(request.isAddOperationInspect());

        if (request.getEntity() != null) {
            pp.setEntity(request.getEntity());
        }

        for (Map.Entry<String, PersistencePackageRequest> subRequest : request.getSubRequests().entrySet()) {
            pp.getSubPackages().put(subRequest.getKey(), create(subRequest.getValue()));
        }

        return pp;
    }

    protected OperationTypes getDefaultOperationTypes() {
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setFetchType(OperationType.BASIC);
        operationTypes.setRemoveType(OperationType.BASIC);
        operationTypes.setAddType(OperationType.BASIC);
        operationTypes.setUpdateType(OperationType.BASIC);
        operationTypes.setInspectType(OperationType.BASIC);
        return operationTypes;
    }
    
    protected OperationTypes getOperationTypes(OperationType nonInspectOperationType) {
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setFetchType(nonInspectOperationType);
        operationTypes.setRemoveType(nonInspectOperationType);
        operationTypes.setAddType(nonInspectOperationType);
        operationTypes.setUpdateType(nonInspectOperationType);
        operationTypes.setInspectType(OperationType.BASIC);
        return operationTypes;
    }

    protected String getClassNameForSection(String sectionKey) {
        try {
            AdminSection section = adminNavigationService.findAdminSectionByURI("/" + sectionKey);
            String className = (section == null) ? sectionKey : section.getCeilingEntity();

            if (className == null) {
                throw new RuntimeException("Could not determine the class related to the following Section: " + section.getName());
            }

            SessionFactory sessionFactory = getEntityManager(className).unwrap(Session.class).getSessionFactory();
            Class<?>[] entities = dynamicDaoHelper.getAllPolymorphicEntitiesFromCeiling(Class.forName(className), sessionFactory, true, true);

            return entities[entities.length - 1].getName();
        } catch (ClassNotFoundException e) {
            throw ExceptionHelper.refineException(RuntimeException.class, RuntimeException.class, e);
        }
    }

    protected EntityManager getEntityManager(String className) {
        PersistenceManager persistenceManager = PersistenceManagerFactory.getPersistenceManager(className);
        return persistenceManager.getDynamicEntityDao().getStandardEntityManager();
    }
}
