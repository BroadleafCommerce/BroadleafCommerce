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
package org.broadleafcommerce.openadmin.server.service.persistence;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.NoPossibleResultsException;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.util.ValidationUtil;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.remote.AdminSecurityServiceRemote;
import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerFilter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.PersistenceModule;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.server.service.type.ChangeType;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;

@Component("blPersistenceManager")
@Scope("prototype")
public class PersistenceManagerImpl implements InspectHelper, PersistenceManager, ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(PersistenceManagerImpl.class);

    @Resource(name="blDynamicEntityDao")
    protected DynamicEntityDao dynamicEntityDao;

    @Resource(name="blCustomPersistenceHandlers")
    protected List<CustomPersistenceHandler> customPersistenceHandlers = new ArrayList<CustomPersistenceHandler>();

    @Resource(name="blCustomPersistenceHandlerFilters")
    protected List<CustomPersistenceHandlerFilter> customPersistenceHandlerFilters = new ArrayList<CustomPersistenceHandlerFilter>();

    @Resource(name="blTargetEntityManagers")
    protected Map<String, String> targetEntityManagers = new HashMap<String, String>();

    @Resource(name="blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;

    @Resource(name="blPersistenceModules")
    protected PersistenceModule[] modules;

    @Resource(name="blPersistenceManagerEventHandlers")
    protected List<PersistenceManagerEventHandler> persistenceManagerEventHandlers;

    @Autowired(required = false)
    protected FetchTypeDetection fetchDetection = null;

    protected TargetModeType targetMode;
    protected ApplicationContext applicationContext;

    @PostConstruct
    public void postConstruct() {
        for (PersistenceModule module : modules) {
            module.setPersistenceManager(this);
        }
        Collections.sort(persistenceManagerEventHandlers, new Comparator<PersistenceManagerEventHandler>() {
            @Override
            public int compare(PersistenceManagerEventHandler o1, PersistenceManagerEventHandler o2) {
                return Integer.valueOf(o1.getOrder()).compareTo(Integer.valueOf(o2.getOrder()));
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass) {
        return dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingClass);
    }

    @Override
    public Class<?>[] getUpDownInheritance(String testClassname) throws ClassNotFoundException {
        return getUpDownInheritance(Class.forName(testClassname));
    }

    @Override
    public Class<?>[] getUpDownInheritance(Class<?> testClass) {
        return dynamicEntityDao.getUpDownInheritance(testClass);
    }

    @Override
    public Class<?>[] getPolymorphicEntities(String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException {
        Class<?>[] entities = getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
        return entities;
    }

    @Override
    public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) {
        return dynamicEntityDao.getSimpleMergedProperties(entityName, persistencePerspective);
    }

    public Property[] processMergedProperties(Class<?>[] entities, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties) {
        List<Property> propertiesList = new ArrayList<Property>();
        for (PersistenceModule module : modules) {
            module.extractProperties(entities, mergedProperties, propertiesList);
        }

        Property[] properties = new Property[propertiesList.size()];
        properties = propertiesList.toArray(properties);
        Arrays.sort(properties, new Comparator<Property>() {
            @Override
            public int compare(Property o1, Property o2) {
                Integer tabOrder1 = o1.getMetadata().getTabOrder() == null ? 99999 : o1.getMetadata().getTabOrder();
                Integer tabOrder2 = o2.getMetadata().getTabOrder() == null ? 99999 : o2.getMetadata().getTabOrder();

                Integer groupOrder1 = null;
                Integer groupOrder2 = null;
                if (o1.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata b1 = (BasicFieldMetadata) o1.getMetadata();
                    groupOrder1 = b1.getGroupOrder();
                }
                groupOrder1 = groupOrder1 == null ? 99999 : groupOrder1;

                if (o2.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata b2 = (BasicFieldMetadata) o2.getMetadata();
                    groupOrder2 = b2.getGroupOrder();
                }
                groupOrder2 = groupOrder2 == null ? 99999 : groupOrder2;

                Integer fieldOrder1 = o1.getMetadata().getOrder() == null ? 99999 : o1.getMetadata().getOrder();
                Integer fieldOrder2 = o2.getMetadata().getOrder() == null ? 99999 : o2.getMetadata().getOrder();

                String friendlyName1 = o1.getMetadata().getFriendlyName() == null ? "zzzz" : o1.getMetadata().getFriendlyName();
                String friendlyName2 = o2.getMetadata().getFriendlyName() == null ? "zzzz" : o2.getMetadata().getFriendlyName();

                String name1 = o1.getName() == null ? "zzzzz" : o1.getName();
                String name2 = o2.getName() == null ? "zzzzz" : o2.getName();

                return new CompareToBuilder()
                    .append(tabOrder1, tabOrder2)
                    .append(groupOrder1, groupOrder2)
                    .append(fieldOrder1, fieldOrder2)
                    .append(friendlyName1, friendlyName2)
                    .append(name1, name2)
                    .toComparison();
            }
        });
        return properties;
    }

    @Override
    public ClassMetadata buildClassMetadata(Class<?>[] entities, PersistencePackage persistencePackage, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties) {
        ClassMetadata cmd = new ClassMetadata();
        cmd.setCeilingType(persistencePackage.getCeilingEntityFullyQualifiedClassname());
        cmd.setSecurityCeilingType(persistencePackage.getSecurityCeilingEntityFullyQualifiedClassname());
        cmd.setPolymorphicEntities(dynamicEntityDao.getClassTree(entities));
        cmd.setCurrencyCode(Money.defaultCurrency().getCurrencyCode());

        cmd.setProperties(processMergedProperties(entities, mergedProperties));
        cmd.setTabAndGroupMetadata(dynamicEntityDao.getTabAndGroupMetadata(entities, cmd));

        return cmd;
    }

    @Override
    public PersistenceResponse inspect(PersistencePackage persistencePackage) throws ServiceException, ClassNotFoundException {
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.preInspect(this, persistencePackage);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                break;
            }
        }
        // check to see if there is a custom handler registered
        for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
            if (handler.canHandleInspect(persistencePackage)) {
                if (!handler.willHandleSecurity(persistencePackage)) {
                    adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.INSPECT);
                }
                DynamicResultSet results = handler.inspect(persistencePackage, dynamicEntityDao, this);
                return executePostInspectHandlers(persistencePackage, new PersistenceResponse().withDynamicResultSet
                        (results));
            }
        }

        adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.INSPECT);
        Class<?>[] entities = getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
        Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
        for (PersistenceModule module : modules) {
            module.updateMergedProperties(persistencePackage, allMergedProperties);
        }
        ClassMetadata classMetadata = buildClassMetadata(entities, persistencePackage, allMergedProperties);
        DynamicResultSet results = new DynamicResultSet(classMetadata);

        return executePostInspectHandlers(persistencePackage, new PersistenceResponse().withDynamicResultSet(results));
    }

    @Override
    public String getIdPropertyName(String entityClass) {
        String response = null;
        try {
            Class<?> clazz = Class.forName(entityClass);
            Class<?>[] entities = getUpDownInheritance(clazz);
            if (!org.apache.commons.lang.ArrayUtils.isEmpty(entities)) {
                Map<String, Object> idData = getDynamicEntityDao().getIdMetadata(entities[0]);
                if (idData != null) {
                    response = (String) idData.get("name");
                }
            }
        } catch (ClassNotFoundException e) {
            throw ExceptionHelper.refineException(e);
        }
        return response;
    }

    protected PersistenceResponse executePostInspectHandlers(PersistencePackage persistencePackage,
                                                             PersistenceResponse persistenceResponse) throws ServiceException {
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.postInspect(this, persistenceResponse.getDynamicResultSet(), persistencePackage);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                persistenceResponse.setDynamicResultSet(response.getDynamicResultSet());
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
                break;
            } else if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED==response.getStatus()) {
                persistenceResponse.setDynamicResultSet(response.getDynamicResultSet());
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
            }
        }
        return persistenceResponse;
    }

    @Override
    public PersistenceResponse fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.preFetch(this, persistencePackage, cto);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                break;
            }
        }
        //check to see if there is a custom handler registered
        for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
            if (handler.canHandleFetch(persistencePackage)) {
                if (!handler.willHandleSecurity(persistencePackage)) {
                    adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.FETCH);
                }
                DynamicResultSet results = handler.fetch(persistencePackage, cto, dynamicEntityDao, (RecordHelper) getCompatibleModule(OperationType.BASIC));
                return executePostFetchHandlers(persistencePackage, cto, new PersistenceResponse().withDynamicResultSet(results));
            }
        }
        adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.FETCH);
        PersistenceModule myModule = getCompatibleModule(persistencePackage.getPersistencePerspective().getOperationTypes().getFetchType());

        try {
            DynamicResultSet results = myModule.fetch(persistencePackage, cto);
            return executePostFetchHandlers(persistencePackage, cto, new PersistenceResponse().withDynamicResultSet(results));
        } catch (ServiceException e) {
            if (e.getCause() instanceof NoPossibleResultsException) {
                DynamicResultSet drs = new DynamicResultSet(null, new Entity[] {}, 0);
                return executePostFetchHandlers(persistencePackage, cto, new PersistenceResponse().withDynamicResultSet(drs));
            }
            throw e;
        }
    }

    protected PersistenceResponse executePostFetchHandlers(PersistencePackage persistencePackage, CriteriaTransferObject
            cto, PersistenceResponse persistenceResponse) throws ServiceException {
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.postFetch(this, persistenceResponse.getDynamicResultSet(), persistencePackage, cto);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                persistenceResponse.setDynamicResultSet(response.getDynamicResultSet());
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
                break;
            } else if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED==response.getStatus()) {
                persistenceResponse.setDynamicResultSet(response.getDynamicResultSet());
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
            }
        }
        //support legacy api
        persistenceResponse.setDynamicResultSet(postFetch(persistenceResponse.getDynamicResultSet(), persistencePackage, cto));
        persistenceResponse.getDynamicResultSet().setStartIndex(cto.getFirstResult());
        persistenceResponse.getDynamicResultSet().setPageSize(cto.getMaxResults());
        Integer upperCount;
        Integer lowerCount;
        if (cto.getFirstId() != null) {
            upperCount = cto.getLowerCount() - 1;
            lowerCount = cto.getLowerCount() - persistenceResponse.getDynamicResultSet().getTotalRecords();
        } else {
            if (cto.getLowerCount() == null) {
                lowerCount = 1;
            } else {
                lowerCount = cto.getUpperCount() + 1;
            }
            upperCount = lowerCount + persistenceResponse.getDynamicResultSet().getTotalRecords() - 1;
            if (lowerCount == 1 && persistenceResponse.getDynamicResultSet().getTotalRecords() == 0) {
                lowerCount = 0;
            }
        }
        if (cto.getFirstId() == null && cto.getLastId() == null) {
            persistenceResponse.getDynamicResultSet().setTotalCountLessThanPageSize(persistenceResponse.getDynamicResultSet().getTotalRecords() < cto.getMaxResults());
        }
        persistenceResponse.getDynamicResultSet().setUpperCount(upperCount);
        persistenceResponse.getDynamicResultSet().setLowerCount(lowerCount);
        Entity[] payload = persistenceResponse.getDynamicResultSet().getRecords();
        if (!ArrayUtils.isEmpty(payload)) {
            Entity first = payload[0];
            Entity last = payload[payload.length-1];
            String idProperty = getIdPropertyName(persistencePackage.getCeilingEntityFullyQualifiedClassname());
            if (!StringUtils.isEmpty(idProperty)) {
                {
                    Property property = first.findProperty(idProperty);
                    if (property != null) {
                        try {
                            persistenceResponse.getDynamicResultSet().setFirstId(Long.parseLong(property.getValue()));
                        } catch (NumberFormatException e) {
                            //do nothing
                        }
                    }
                }
                {
                    Property property = last.findProperty(idProperty);
                    if (property != null) {
                        try {
                            persistenceResponse.getDynamicResultSet().setLastId(Long.parseLong(property.getValue()));
                        } catch (NumberFormatException e) {
                            //do nothing
                        }
                    }
                }
            }
        }
        if (fetchDetection != null) {
            persistenceResponse.getDynamicResultSet().setFetchType(fetchDetection.getFetchType(persistencePackage, cto));
            persistenceResponse.getDynamicResultSet().setPromptSearch(fetchDetection.shouldPromptForSearch(persistencePackage, cto));
        }

        return persistenceResponse;
    }

    /**
     * Called after the fetch event
     *
     * @param resultSet
     * @param persistencePackage
     * @param cto
     * @return the modified result set
     * @throws ServiceException
     * @deprecated use the PersistenceManagerEventHandler api instead
     */
    @Deprecated
    protected DynamicResultSet postFetch(DynamicResultSet resultSet, PersistencePackage persistencePackage,
            CriteriaTransferObject cto)
            throws ServiceException {
        return resultSet;
    }

    @Override
    public PersistenceResponse add(PersistencePackage persistencePackage) throws ServiceException {
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.preAdd(this, persistencePackage);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                break;
            }
        }
        //check to see if there is a custom handler registered
        //execute the root PersistencePackage
        Entity response;
        try {
            checkRoot: {
                //if there is a validation exception in the root check, let it bubble, as we need a valid, persisted
                //entity to execute the subPackage code later
                for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
                    if (handler.canHandleAdd(persistencePackage)) {
                        if (!handler.willHandleSecurity(persistencePackage)) {
                            adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.ADD);
                        }
                        response = handler.add(persistencePackage, dynamicEntityDao, (RecordHelper) getCompatibleModule(OperationType.BASIC));
                        break checkRoot;
                    }
                }
                adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.ADD);
                PersistenceModule myModule = getCompatibleModule(persistencePackage.getPersistencePerspective().getOperationTypes().getAddType());
                response = myModule.add(persistencePackage);
            }
        }  catch (ValidationException e) {
            response = e.getEntity();
        } catch (ServiceException e) {
            if (e.getCause() instanceof ValidationException) {
                response = ((ValidationException) e.getCause()).getEntity();
            } else {
                throw e;
            }
        }

        if (!MapUtils.isEmpty(persistencePackage.getSubPackages())) {
            // Once the entity has been saved, we can utilize its id for the subsequent dynamic forms
            Class<?> entityClass;
            try {
                entityClass = Class.forName(response.getType()[0]);
            } catch (ClassNotFoundException e) {
                throw new ServiceException(e);
            }
            Map<String, Object> idMetadata = getDynamicEntityDao().getIdMetadata(entityClass);
            String idProperty = (String) idMetadata.get("name");
            String idVal = response.findProperty(idProperty).getValue();

            Map<String, List<String>> subPackageValidationErrors = new HashMap<String, List<String>>();
            for (Map.Entry<String,PersistencePackage> subPackage : persistencePackage.getSubPackages().entrySet()) {
                Entity subResponse;
                try {
                    subPackage.getValue().getCustomCriteria()[1] = idVal;
                    //Run through any subPackages -- add up any validation errors
                    checkHandler: {
                        for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
                            if (handler.canHandleAdd(subPackage.getValue())) {
                                subResponse = handler.add(subPackage.getValue(), dynamicEntityDao, (RecordHelper) getCompatibleModule(OperationType.BASIC));
                                subPackage.getValue().setEntity(subResponse);

                                break checkHandler;
                            }
                        }
                        PersistenceModule subModule = getCompatibleModule(subPackage.getValue().getPersistencePerspective().getOperationTypes().getAddType());
                        subResponse = subModule.add(persistencePackage);
                        subPackage.getValue().setEntity(subResponse);
                    }
                } catch (ValidationException e) {
                    subPackage.getValue().setEntity(e.getEntity());
                } catch (ServiceException e) {
                    if (e.getCause() instanceof ValidationException) {
                        response = ((ValidationException) e.getCause()).getEntity();
                    } else {
                        throw e;
                    }
                }
            }
            
            //Build up validation errors in all of the subpackages, even those that might not have thrown ValidationExceptions
            for (Map.Entry<String, PersistencePackage> subPackage : persistencePackage.getSubPackages().entrySet()) {
                for (Map.Entry<String, List<String>> error : subPackage.getValue().getEntity().getPropertyValidationErrors().entrySet()) {
                    subPackageValidationErrors.put(subPackage.getKey() + DynamicEntityFormInfo.FIELD_SEPARATOR + error.getKey(), error.getValue());
                }
            }
            
            response.getPropertyValidationErrors().putAll(subPackageValidationErrors);
        }

        if (response.isValidationFailure()) {
            PersistenceResponse validationResponse = executeValidationProcessors(persistencePackage, new PersistenceResponse().withEntity(response));
            Entity entity = validationResponse.getEntity();
            String message = ValidationUtil.buildErrorMessage(entity.getPropertyValidationErrors(), entity.getGlobalValidationErrors());
            throw new ValidationException(entity, message);
        }

        return executePostAddHandlers(persistencePackage, new PersistenceResponse().withEntity(response));
    }

    protected PersistenceResponse executeValidationProcessors(PersistencePackage persistencePackage, PersistenceResponse persistenceResponse) throws ServiceException {
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.processValidationError(this,
                    persistenceResponse.getEntity(), persistencePackage);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                persistenceResponse.setEntity(response.getEntity());
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
                break;
            } else if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED==response.getStatus()) {
                persistenceResponse.setEntity(response.getEntity());
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
            }
        }

        return persistenceResponse;
    }

    protected PersistenceResponse executePostAddHandlers(PersistencePackage persistencePackage, PersistenceResponse persistenceResponse) throws ServiceException {
        dynamicEntityDao.flush();
        setMainEntityName(persistencePackage, persistenceResponse.getEntity());
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.postAdd(this, persistenceResponse.getEntity(), persistencePackage);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                persistenceResponse.setEntity(response.getEntity());
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
                break;
            } else if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED==response.getStatus()) {
                persistenceResponse.setEntity(response.getEntity());
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
            }
        }
        //support legacy api
        persistenceResponse.setEntity(postAdd(persistenceResponse.getEntity(), persistencePackage));

        executeDeferredOperations(persistencePackage);

        return persistenceResponse;
    }

    protected void executeDeferredOperations(PersistencePackage persistencePackage) throws ServiceException {
        if (!persistencePackage.getDeferredOperations().isEmpty()) {
            for (Map.Entry<ChangeType, List<PersistencePackage>> entry : persistencePackage.getDeferredOperations().entrySet()) {
                for (PersistencePackage change : entry.getValue()) {
                    switch (entry.getKey()) {
                        case UPDATE:
                            update(change);
                            break;
                        case ADD:
                            add(change);
                            break;
                        case DELETE:
                            remove(change);
                            break;
                    }
                }
            }
        }
        for (Map.Entry<String, PersistencePackage> subPackage : persistencePackage.getSubPackages().entrySet()) {
            for (Map.Entry<ChangeType, List<PersistencePackage>> entry : subPackage.getValue().getDeferredOperations().entrySet()) {
                for (PersistencePackage change : entry.getValue()) {
                    switch (entry.getKey()) {
                        case UPDATE:
                            update(change);
                            break;
                        case ADD:
                            add(change);
                            break;
                        case DELETE:
                            remove(change);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Called after the add event
     *
     * @param entity
     * @param persistencePackage
     * @return the modified Entity instance
     * @throws ServiceException
     * @deprecated use the PersistenceManagerEventHandler api instead
     */
    @Deprecated
    protected Entity postAdd(Entity entity, PersistencePackage persistencePackage) throws ServiceException {
        //do nothing
        return entity;
    }

    @Override
    public PersistenceResponse update(PersistencePackage persistencePackage) throws ServiceException {
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.preUpdate(this, persistencePackage);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                break;
            }
        }
        //check to see if there is a custom handler registered
        //execute the root PersistencePackage
        Entity response;
        try {
            checkRoot: {
                for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
                    if (handler.canHandleUpdate(persistencePackage)) {
                        if (!handler.willHandleSecurity(persistencePackage)) {
                            adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.UPDATE);
                        }
                        response = handler.update(persistencePackage, dynamicEntityDao, (RecordHelper) getCompatibleModule(OperationType.BASIC));
                        break checkRoot;
                    }
                }
                adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.UPDATE);
                PersistenceModule myModule = getCompatibleModule(persistencePackage.getPersistencePerspective().getOperationTypes().getUpdateType());
                response = myModule.update(persistencePackage);
            }
        } catch (ValidationException e) {
            response = e.getEntity();
        } catch (ServiceException e) {
            if (e.getCause() instanceof ValidationException) {
                response = ((ValidationException) e.getCause()).getEntity();
            } else {
                throw e;
            }
        }

        Map<String, List<String>> subPackageValidationErrors = new HashMap<String, List<String>>();
        for (Map.Entry<String,PersistencePackage> subPackage : persistencePackage.getSubPackages().entrySet()) {
            try {
                //Run through any subPackages -- add up any validation errors
                checkHandler: {
                    for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
                        if (handler.canHandleUpdate(subPackage.getValue())) {
                            Entity subResponse = handler.update(subPackage.getValue(), dynamicEntityDao, (RecordHelper) getCompatibleModule(OperationType.BASIC));
                            subPackage.getValue().setEntity(subResponse);
                            break checkHandler;
                        }
                    }
                    PersistenceModule subModule = getCompatibleModule(subPackage.getValue().getPersistencePerspective().getOperationTypes().getUpdateType());
                    Entity subResponse = subModule.update(persistencePackage);
                    subPackage.getValue().setEntity(subResponse);
                }
            } catch (ValidationException e) {
                subPackage.getValue().setEntity(e.getEntity());
            } catch (ServiceException e) {
                if (e.getCause() instanceof ValidationException) {
                    response = ((ValidationException) e.getCause()).getEntity();
                } else {
                    throw e;
                }
            }
        }
        
        //Build up validation errors in all of the subpackages, even those that might not have thrown ValidationExceptions
        for (Map.Entry<String, PersistencePackage> subPackage : persistencePackage.getSubPackages().entrySet()) {
            for (Map.Entry<String, List<String>> error : subPackage.getValue().getEntity().getPropertyValidationErrors().entrySet()) {
                subPackageValidationErrors.put(subPackage.getKey() + DynamicEntityFormInfo.FIELD_SEPARATOR + error.getKey(), error.getValue());
            }
        }

        response.getPropertyValidationErrors().putAll(subPackageValidationErrors);

        if (response.isValidationFailure()) {
            PersistenceResponse validationResponse = executeValidationProcessors(persistencePackage, new PersistenceResponse().withEntity(response));
            Entity entity = validationResponse.getEntity();
            String message = ValidationUtil.buildErrorMessage(entity.getPropertyValidationErrors(), entity.getGlobalValidationErrors());
            throw new ValidationException(entity, message);
        }

        return executePostUpdateHandlers(persistencePackage, new PersistenceResponse().withEntity(response));
    }

    protected PersistenceResponse executePostUpdateHandlers(PersistencePackage persistencePackage, PersistenceResponse persistenceResponse) throws ServiceException {
        dynamicEntityDao.flush();
        setMainEntityName(persistencePackage, persistenceResponse.getEntity());
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.postUpdate(this, persistenceResponse.getEntity(), persistencePackage);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                persistenceResponse.setEntity(response.getEntity());
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
                break;
            } else if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED==response.getStatus()) {
                persistenceResponse.setEntity(response.getEntity());
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
            }
        }
        //support legacy api
        persistenceResponse.setEntity(postUpdate(persistenceResponse.getEntity(), persistencePackage));

        executeDeferredOperations(persistencePackage);

        return persistenceResponse;
    }

    /**
     * Called after the update event
     *
     * @param entity
     * @param persistencePackage
     * @return the modified Entity instance
     * @throws ServiceException
     * @deprecated use the PersistenceManagerEventHandler api instead
     */
    @Deprecated
    protected Entity postUpdate(Entity entity, PersistencePackage persistencePackage) throws ServiceException {
        //do nothing
        return entity;
    }

    @Override
    public PersistenceResponse remove(PersistencePackage persistencePackage) throws ServiceException {
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.preRemove(this, persistencePackage);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                break;
            }
        }
        //check to see if there is a custom handler registered
        for (CustomPersistenceHandler handler : getCustomPersistenceHandlers()) {
            if (handler.canHandleRemove(persistencePackage)) {
                if (!handler.willHandleSecurity(persistencePackage)) {
                    adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.REMOVE);
                }
                handler.remove(persistencePackage, dynamicEntityDao, (RecordHelper) getCompatibleModule(OperationType.BASIC));
                return executePostRemoveHandlers(persistencePackage, new PersistenceResponse());
            }
        }
        adminRemoteSecurityService.securityCheck(persistencePackage, EntityOperationType.REMOVE);
        PersistenceModule myModule = getCompatibleModule(persistencePackage.getPersistencePerspective().getOperationTypes().getRemoveType());
        myModule.remove(persistencePackage);

        return executePostRemoveHandlers(persistencePackage, new PersistenceResponse());
    }

    protected PersistenceResponse executePostRemoveHandlers(PersistencePackage persistencePackage, PersistenceResponse persistenceResponse) throws ServiceException {
        dynamicEntityDao.flush();
        setMainEntityName(persistencePackage, persistenceResponse.getEntity());
        for (PersistenceManagerEventHandler handler : persistenceManagerEventHandlers) {
            PersistenceManagerEventHandlerResponse response = handler.postRemove(this, persistencePackage);
            if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED_BREAK==response.getStatus()) {
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
                break;
            } else if (PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED==response.getStatus()) {
                if (!MapUtils.isEmpty(response.getAdditionalData())) {
                    persistenceResponse.getAdditionalData().putAll(response.getAdditionalData());
                }
            }
        }

        executeDeferredOperations(persistencePackage);

        return persistenceResponse;
    }

    @Override
    public PersistenceModule getCompatibleModule(OperationType operationType) {
        PersistenceModule myModule = null;
        for (PersistenceModule module : modules) {
            if (module.isCompatible(operationType)) {
                myModule = module;
                break;
            }
        }
        if (myModule == null) {
            LOG.error("Unable to find a compatible remote service module for the operation type: " + operationType);
            throw new RuntimeException("Unable to find a compatible remote service module for the operation type: " + operationType);
        }

        return myModule;
    }

    @Override
    public DynamicEntityDao getDynamicEntityDao() {
        return dynamicEntityDao;
    }

    @Override
    public void setDynamicEntityDao(DynamicEntityDao dynamicEntityDao) {
        this.dynamicEntityDao = dynamicEntityDao;
    }

    @Override
    public Map<String, String> getTargetEntityManagers() {
        return targetEntityManagers;
    }

    @Override
    public void setTargetEntityManagers(Map<String, String> targetEntityManagers) {
        this.targetEntityManagers = targetEntityManagers;
    }

    @Override
    public TargetModeType getTargetMode() {
        return targetMode;
    }

    @Override
    public void setTargetMode(TargetModeType targetMode) {
        String targetManagerRef = targetEntityManagers.get(targetMode.getType());
        EntityManager targetManager = (EntityManager) applicationContext.getBean(targetManagerRef);
        if (targetManager == null) {
            throw new RuntimeException("Unable to find a target entity manager registered with the key: " + targetMode + ". Did you add an entity manager with this key to the targetEntityManagers property?");
        }
        dynamicEntityDao.setStandardEntityManager(targetManager);
        this.targetMode = targetMode;
    }

    @Override
    public List<CustomPersistenceHandler> getCustomPersistenceHandlers() {
        List<CustomPersistenceHandler> cloned = new ArrayList<CustomPersistenceHandler>();
        cloned.addAll(customPersistenceHandlers);
        if (getCustomPersistenceHandlerFilters() != null) {
            for (CustomPersistenceHandlerFilter filter : getCustomPersistenceHandlerFilters()) {
                Iterator<CustomPersistenceHandler> itr = cloned.iterator();
                while (itr.hasNext()) {
                    CustomPersistenceHandler handler = itr.next();
                    if (!filter.shouldUseHandler(handler.getClass().getName())) {
                        itr.remove();
                    }
                }
            }
        }
        Collections.sort(cloned, new Comparator<CustomPersistenceHandler>() {
            @Override
            public int compare(CustomPersistenceHandler o1, CustomPersistenceHandler o2) {
                return new Integer(o1.getOrder()).compareTo(new Integer(o2.getOrder()));
            }
        });
        return cloned;
    }
    
    protected void setMainEntityName(PersistencePackage pp, Entity entity) {
        if (StringUtils.isBlank(pp.getRequestingEntityName()) && entity != null) {
            Property nameProp = entity.getPMap().get(AdminMainEntity.MAIN_ENTITY_NAME_PROPERTY);
            if (nameProp != null) {
                pp.setRequestingEntityName(nameProp.getValue());
            }
        }
    }

    @Override
    public void setCustomPersistenceHandlers(List<CustomPersistenceHandler> customPersistenceHandlers) {
        this.customPersistenceHandlers = customPersistenceHandlers;
    }

    public SecurityVerifier getAdminRemoteSecurityService() {
        return adminRemoteSecurityService;
    }

    public void setAdminRemoteSecurityService(AdminSecurityServiceRemote adminRemoteSecurityService) {
        this.adminRemoteSecurityService = adminRemoteSecurityService;
    }

    public List<CustomPersistenceHandlerFilter> getCustomPersistenceHandlerFilters() {
        return customPersistenceHandlerFilters;
    }

    public void setCustomPersistenceHandlerFilters(List<CustomPersistenceHandlerFilter> customPersistenceHandlerFilters) {
        this.customPersistenceHandlerFilters = customPersistenceHandlerFilters;
    }

    public PersistenceModule[] getModules() {
        return modules;
    }

    public void setModules(PersistenceModule[] modules) {
        this.modules = modules;
    }
}
