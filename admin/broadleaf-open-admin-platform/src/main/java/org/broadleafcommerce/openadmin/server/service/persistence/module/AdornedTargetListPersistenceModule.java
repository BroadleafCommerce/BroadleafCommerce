/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.FilterCriterion;
import com.anasoft.os.daofusion.criteria.NestedPropertyCriteria;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.criteria.SimpleFilterCriterionProvider;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jfischer
 */
@Component("blAdornedTargetListPersistenceModule")
@Scope("prototype")
public class AdornedTargetListPersistenceModule extends BasicPersistenceModule {

    private static final Log LOG = LogFactory.getLog(AdornedTargetListPersistenceModule.class);

    public boolean isCompatible(OperationType operationType) {
        return OperationType.ADORNEDTARGETLIST.equals(operationType);
    }

    public void extractProperties(Class<?>[] inheritanceLine, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException {
        if (mergedProperties.get(MergedPropertyType.ADORNEDTARGETLIST) != null) {
            extractPropertiesFromMetadata(inheritanceLine, mergedProperties.get(MergedPropertyType.ADORNEDTARGETLIST), properties, true, MergedPropertyType.ADORNEDTARGETLIST);
        }
    }

    public BaseCtoConverter getAdornedTargetCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, Map<String, FieldMetadata> mergedProperties, AdornedTargetList adornedTargetList) throws ClassNotFoundException {
        BaseCtoConverter ctoConverter = getCtoConverter(persistencePerspective, cto, adornedTargetList.getAdornedTargetEntityClassname(), mergedProperties);
        ctoConverter.addLongEQMapping(adornedTargetList.getAdornedTargetEntityClassname(), adornedTargetList.getCollectionFieldName(), AssociationPath.ROOT, adornedTargetList.getLinkedObjectPath() + "." + adornedTargetList.getLinkedIdProperty());
        ctoConverter.addLongEQMapping(adornedTargetList.getAdornedTargetEntityClassname(), adornedTargetList.getCollectionFieldName() + "Target", AssociationPath.ROOT, adornedTargetList.getTargetObjectPath() + "." + adornedTargetList.getTargetIdProperty());
        return ctoConverter;
    }

    protected Serializable createPopulatedAdornedTargetInstance(AdornedTargetList adornedTargetList, Entity entity) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NumberFormatException, InvocationTargetException, NoSuchMethodException {
        Serializable instance = (Serializable) Class.forName(StringUtils.isEmpty(adornedTargetList.getAdornedTargetEntityPolymorphicType())? adornedTargetList.getAdornedTargetEntityClassname(): adornedTargetList.getAdornedTargetEntityPolymorphicType()).newInstance();
        String targetPath = adornedTargetList.getTargetObjectPath() + "." + adornedTargetList.getTargetIdProperty();
        String linkedPath = adornedTargetList.getLinkedObjectPath() + "." + adornedTargetList.getLinkedIdProperty();
        getFieldManager().setFieldValue(instance, linkedPath, Long.valueOf(entity.findProperty(linkedPath).getValue()));
        getFieldManager().setFieldValue(instance, targetPath, Long.valueOf(entity.findProperty(targetPath).getValue()));

        return instance;
    }

    @Override
    public void updateMergedProperties(PersistencePackage persistencePackage, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            AdornedTargetList adornedTargetList = (AdornedTargetList) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
            if (adornedTargetList != null) {
                Class<?>[] entities = persistenceManager.getPolymorphicEntities(adornedTargetList.getAdornedTargetEntityClassname());
                Map<String, FieldMetadata> joinMergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                        adornedTargetList.getAdornedTargetEntityClassname(),
                        entities,
                        null,
                        new String[]{},
                        new ForeignKey[]{},
                        MergedPropertyType.ADORNEDTARGETLIST,
                        persistencePerspective.getPopulateToOneFields(),
                        persistencePerspective.getIncludeFields(),
                        persistencePerspective.getExcludeFields(),
                        persistencePerspective.getConfigurationKey(),
                        ""
                );
                String idProp = null;
                for (String key : joinMergedProperties.keySet()) {
                    if (joinMergedProperties.get(key) instanceof BasicFieldMetadata && ((BasicFieldMetadata) joinMergedProperties.get(key)).getFieldType() == SupportedFieldType.ID) {
                        idProp = key;
                        break;
                    }
                }
                if (idProp != null) {
                    joinMergedProperties.remove(idProp);
                }
                allMergedProperties.put(MergedPropertyType.ADORNEDTARGETLIST, joinMergedProperties);
            }
        } catch (Exception e) {
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public Entity add(PersistencePackage persistencePackage) throws ServiceException {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (customCriteria != null && customCriteria.length > 0) {
            LOG.warn("custom persistence handlers and custom criteria not supported for add types other than BASIC");
        }
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        Entity entity = persistencePackage.getEntity();
        AdornedTargetList adornedTargetList = (AdornedTargetList) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
        Entity payload;
        try {
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
            Map<String, FieldMetadata> mergedPropertiesTarget = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    ceilingEntityFullyQualifiedClassname,
                    entities,
                    null,
                    persistencePerspective.getAdditionalNonPersistentProperties(),
                    persistencePerspective.getAdditionalForeignKeys(),
                    MergedPropertyType.PRIMARY,
                    persistencePerspective.getPopulateToOneFields(),
                    persistencePerspective.getIncludeFields(),
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
            );
            Class<?>[] entities2 = persistenceManager.getPolymorphicEntities(adornedTargetList.getAdornedTargetEntityClassname());
            Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    adornedTargetList.getAdornedTargetEntityClassname(),
                    entities2,
                    null,
                    new String[]{},
                    new ForeignKey[]{},
                    MergedPropertyType.ADORNEDTARGETLIST,
                    false,
                    new String[]{},
                    new String[]{},
                    null,
                    ""
            );

            CriteriaTransferObject ctoInserted = new CriteriaTransferObject();
            FilterAndSortCriteria filterCriteriaInsertedLinked = ctoInserted.get(adornedTargetList.getCollectionFieldName());
            String linkedPath;
            String targetPath;
            if (adornedTargetList.getInverse()) {
                linkedPath = adornedTargetList.getTargetObjectPath() + "." + adornedTargetList.getTargetIdProperty();
                targetPath = adornedTargetList.getLinkedObjectPath() + "." + adornedTargetList.getLinkedIdProperty();
            } else {
                targetPath = adornedTargetList.getTargetObjectPath() + "." + adornedTargetList.getTargetIdProperty();
                linkedPath = adornedTargetList.getLinkedObjectPath() + "." + adornedTargetList.getLinkedIdProperty();
            }
            filterCriteriaInsertedLinked.setFilterValue(entity.findProperty(adornedTargetList.getInverse() ? targetPath : linkedPath).getValue());
            FilterAndSortCriteria filterCriteriaInsertedTarget = ctoInserted.get(adornedTargetList.getCollectionFieldName() + "Target");
            filterCriteriaInsertedTarget.setFilterValue(entity.findProperty(adornedTargetList.getInverse() ? linkedPath : targetPath).getValue());
            BaseCtoConverter ctoConverterInserted = getAdornedTargetCtoConverter(persistencePerspective, ctoInserted, mergedProperties, adornedTargetList);
            PersistentEntityCriteria queryCriteriaInserted = ctoConverterInserted.convert(ctoInserted, adornedTargetList.getAdornedTargetEntityClassname());
            List<Serializable> recordsInserted = persistenceManager.getDynamicEntityDao().query(queryCriteriaInserted, Class.forName(adornedTargetList.getAdornedTargetEntityClassname()));
            if (recordsInserted.size() > 0) {
                payload = getRecords(mergedPropertiesTarget, recordsInserted, mergedProperties, adornedTargetList.getTargetObjectPath())[0];
            } else {
                Serializable instance = createPopulatedAdornedTargetInstance(adornedTargetList, entity);
                instance = createPopulatedInstance(instance, entity, mergedProperties, false);
                instance = createPopulatedInstance(instance, entity, mergedPropertiesTarget, false);
                FieldManager fieldManager = getFieldManager();
                if (fieldManager.getField(instance.getClass(), "id") != null) {
                    fieldManager.setFieldValue(instance, "id", null);
                }
                if (adornedTargetList.getSortField() != null) {
                    CriteriaTransferObject cto = new CriteriaTransferObject();
                    FilterAndSortCriteria filterCriteria = cto.get(adornedTargetList.getCollectionFieldName());
                    filterCriteria.setFilterValue(entity.findProperty(adornedTargetList.getInverse() ? targetPath : linkedPath).getValue());
                    FilterAndSortCriteria sortCriteria = cto.get(adornedTargetList.getSortField());
                    sortCriteria.setSortAscending(adornedTargetList.getSortAscending());
                    BaseCtoConverter ctoConverter = getAdornedTargetCtoConverter(persistencePerspective, cto, mergedProperties, adornedTargetList);
                    int totalRecords = getTotalRecords(persistencePackage, cto, ctoConverter);
                    fieldManager.setFieldValue(instance, adornedTargetList.getSortField(), Long.valueOf(totalRecords + 1));
                }
                instance = persistenceManager.getDynamicEntityDao().merge(instance);
                persistenceManager.getDynamicEntityDao().clear();

                List<Serializable> recordsInserted2 = persistenceManager.getDynamicEntityDao().query(queryCriteriaInserted, Class.forName(adornedTargetList.getAdornedTargetEntityClassname()));

                payload = getRecords(mergedPropertiesTarget, recordsInserted2, mergedProperties, adornedTargetList.getTargetObjectPath())[0];
            }
        } catch (Exception e) {
            LOG.error("Problem editing entity", e);
            throw new ServiceException("Problem adding new entity : " + e.getMessage(), e);
        }

        return payload;
    }

    @Override
    public Entity update(PersistencePackage persistencePackage) throws ServiceException {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (customCriteria != null && customCriteria.length > 0) {
            LOG.warn("custom persistence handlers and custom criteria not supported for update types other than BASIC");
        }
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        Entity entity = persistencePackage.getEntity();
        AdornedTargetList adornedTargetList = (AdornedTargetList) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
        try {
            AdornedTargetRetrieval adornedTargetRetrieval = new AdornedTargetRetrieval(persistencePerspective, entity, adornedTargetList).invoke();
            List<Serializable> records = adornedTargetRetrieval.getRecords();
            int index = adornedTargetRetrieval.getIndex();
            Map<String, FieldMetadata> mergedProperties = adornedTargetRetrieval.getMergedProperties();
            FieldManager fieldManager = getFieldManager();
            if (adornedTargetList.getSortField() != null && entity.findProperty(adornedTargetList.getSortField()).getValue() != null) {
                Serializable myRecord = records.get(index);
                
                Integer requestedSequence = Integer.valueOf(entity.findProperty(adornedTargetList.getSortField()).getValue());
                Integer previousSequence = Integer.parseInt(String.valueOf(getFieldManager().getFieldValue(myRecord, adornedTargetList.getSortField())));
                
                if (!previousSequence.equals(requestedSequence)) {
                    // Sequence has changed. Rebalance the list
                    myRecord = records.remove(index);
                    myRecord = createPopulatedInstance(myRecord, entity, mergedProperties, false);
                    if (CollectionUtils.isEmpty(records)) {
                        records.add(myRecord);
                    } else {
                        records.add(requestedSequence - 1, myRecord);
                    }
                    
                    index = 1;
                    for (Serializable record : records) {
                        fieldManager.setFieldValue(record, adornedTargetList.getSortField(), Long.valueOf(index));
                        index++;
                    }
                }
            } else {
                String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
                Class<?>[] entities = persistenceManager.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
                Map<String, FieldMetadata> mergedPropertiesTarget = persistenceManager.getDynamicEntityDao().getMergedProperties(
                        ceilingEntityFullyQualifiedClassname,
                        entities,
                        null,
                        persistencePerspective.getAdditionalNonPersistentProperties(),
                        persistencePerspective.getAdditionalForeignKeys(),
                        MergedPropertyType.PRIMARY,
                        persistencePerspective.getPopulateToOneFields(),
                        persistencePerspective.getIncludeFields(),
                        persistencePerspective.getExcludeFields(),
                        persistencePerspective.getConfigurationKey(),
                        ""
                );
                Serializable myRecord = records.get(index);
                myRecord = createPopulatedInstance(myRecord, entity, mergedProperties, false);
                myRecord = persistenceManager.getDynamicEntityDao().merge(myRecord);
                List<Serializable> myList = new ArrayList<Serializable>();
                myList.add(myRecord);
                Entity[] payload = getRecords(mergedPropertiesTarget, myList, mergedProperties, adornedTargetList.getTargetObjectPath());
                entity = payload[0];
            }

            return entity;
        } catch (Exception e) {
            LOG.error("Problem editing entity", e);
            throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
        }
    }

    @Override
    public void remove(PersistencePackage persistencePackage) throws ServiceException {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (customCriteria != null && customCriteria.length > 0) {
            LOG.warn("custom persistence handlers and custom criteria not supported for remove types other than BASIC");
        }
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        Entity entity = persistencePackage.getEntity();
        try {
            AdornedTargetList adornedTargetList = (AdornedTargetList) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(adornedTargetList.getAdornedTargetEntityClassname());
            Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    adornedTargetList.getAdornedTargetEntityClassname(),
                    entities,
                    null,
                    new String[]{},
                    new ForeignKey[]{},
                    MergedPropertyType.ADORNEDTARGETLIST,
                    false,
                    new String[]{},
                    new String[]{},
                    null,
                    ""
            );
            CriteriaTransferObject ctoInserted = new CriteriaTransferObject();
            FilterAndSortCriteria filterCriteriaInsertedLinked = ctoInserted.get(adornedTargetList.getCollectionFieldName());
            filterCriteriaInsertedLinked.setFilterValue(entity.findProperty(adornedTargetList.getLinkedObjectPath() + "." + adornedTargetList.getLinkedIdProperty()).getValue());
            FilterAndSortCriteria filterCriteriaInsertedTarget = ctoInserted.get(adornedTargetList.getCollectionFieldName() + "Target");
            filterCriteriaInsertedTarget.setFilterValue(entity.findProperty(adornedTargetList.getTargetObjectPath() + "." + adornedTargetList.getTargetIdProperty()).getValue());
            BaseCtoConverter ctoConverterInserted = getAdornedTargetCtoConverter(persistencePerspective, ctoInserted, mergedProperties, adornedTargetList);
            PersistentEntityCriteria queryCriteriaInserted = ctoConverterInserted.convert(ctoInserted, adornedTargetList.getAdornedTargetEntityClassname());
            List<Serializable> recordsInserted = persistenceManager.getDynamicEntityDao().query(queryCriteriaInserted, Class.forName(adornedTargetList.getAdornedTargetEntityClassname()));

            persistenceManager.getDynamicEntityDao().remove(recordsInserted.get(0));
        } catch (Exception e) {
            LOG.error("Problem removing entity", e);
            throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
        }
    }

    @Override
    public int getTotalRecords(PersistencePackage persistencePackage, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) {
        AdornedTargetList adornedTargetList = (AdornedTargetList) persistencePackage.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
        PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), adornedTargetList.getAdornedTargetEntityClassname());

        Class<?>[] entities;
        try {
            entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(Class.forName(adornedTargetList.getAdornedTargetEntityClassname()));
        } catch (ClassNotFoundException e) {
            throw new PersistenceException(e);
        }
        boolean isArchivable = false;
        for (Class<?> entity : entities) {
            if (Status.class.isAssignableFrom(entity)) {
                isArchivable = true;
                break;
            }
        }
        if (isArchivable && !persistencePackage.getPersistencePerspective().getShowArchivedFields()) {
            SimpleFilterCriterionProvider criterionProvider = new SimpleFilterCriterionProvider(SimpleFilterCriterionProvider.FilterDataStrategy.NONE, 0) {
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    return Restrictions.or(Restrictions.eq(targetPropertyName, 'N'), Restrictions.isNull(targetPropertyName));
                }
            };
            FilterCriterion filterCriterion = new FilterCriterion(AssociationPath.ROOT, "archiveStatus.archived", criterionProvider);
            ((NestedPropertyCriteria) countCriteria).add(filterCriterion);
        }
        try {
            return persistenceManager.getDynamicEntityDao().count(countCriteria, Class.forName(adornedTargetList.getAdornedTargetEntityClassname()));
        } catch (ClassNotFoundException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        AdornedTargetList adornedTargetList = (AdornedTargetList) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
        Entity[] payload;
        int totalRecords;
        try {
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
            Map<String, FieldMetadata> mergedPropertiesTarget = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    ceilingEntityFullyQualifiedClassname,
                    entities,
                    null,
                    persistencePerspective.getAdditionalNonPersistentProperties(),
                    persistencePerspective.getAdditionalForeignKeys(),
                    MergedPropertyType.PRIMARY,
                    persistencePerspective.getPopulateToOneFields(),
                    persistencePerspective.getIncludeFields(),
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
            );
            Class<?>[] entities2 = persistenceManager.getPolymorphicEntities(adornedTargetList.getAdornedTargetEntityClassname());
            Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    adornedTargetList.getAdornedTargetEntityClassname(),
                    entities2,
                    null,
                    new String[]{},
                    new ForeignKey[]{},
                    MergedPropertyType.ADORNEDTARGETLIST,
                    false,
                    new String[]{},
                    new String[]{},
                    null,
                    ""
            );
            BaseCtoConverter ctoConverter = getAdornedTargetCtoConverter(persistencePerspective, cto, mergedProperties, adornedTargetList);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, adornedTargetList.getAdornedTargetEntityClassname());
            List<Serializable> records = persistenceManager.getDynamicEntityDao().query(queryCriteria, Class.forName(adornedTargetList.getAdornedTargetEntityClassname()));
            payload = getRecords(mergedPropertiesTarget, records, mergedProperties, adornedTargetList.getTargetObjectPath());
            totalRecords = getTotalRecords(persistencePackage, cto, ctoConverter);
        } catch (Exception e) {
            LOG.error("Problem fetching results for " + adornedTargetList.getAdornedTargetEntityClassname(), e);
            throw new ServiceException("Unable to fetch results for " + adornedTargetList.getAdornedTargetEntityClassname(), e);
        }

        DynamicResultSet results = new DynamicResultSet(null, payload, totalRecords);

        return results;
    }

    public class AdornedTargetRetrieval {
        private PersistencePerspective persistencePerspective;
        private Entity entity;
        private AdornedTargetList adornedTargetList;
        private Map<String, FieldMetadata> mergedProperties;
        private List<Serializable> records;
        private int index;

        public AdornedTargetRetrieval(PersistencePerspective persistencePerspective, Entity entity, AdornedTargetList adornedTargetList) {
            this.persistencePerspective = persistencePerspective;
            this.entity = entity;
            this.adornedTargetList = adornedTargetList;
        }

        public Map<String, FieldMetadata> getMergedProperties() {
            return mergedProperties;
        }

        public List<Serializable> getRecords() {
            return records;
        }

        public int getIndex() {
            return index;
        }

        public AdornedTargetRetrieval invoke() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, FieldNotAvailableException, NoSuchFieldException {
            CriteriaTransferObject cto = new CriteriaTransferObject();
            FilterAndSortCriteria filterCriteria = cto.get(adornedTargetList.getCollectionFieldName());
            filterCriteria.setFilterValue(entity.findProperty(adornedTargetList.getLinkedObjectPath() + "." + adornedTargetList.getLinkedIdProperty()).getValue());
            if (adornedTargetList.getSortField() != null) {
                FilterAndSortCriteria sortCriteria = cto.get(adornedTargetList.getSortField());
                sortCriteria.setSortAscending(adornedTargetList.getSortAscending());
            }

            Class<?>[] entities2 = persistenceManager.getPolymorphicEntities(adornedTargetList.getAdornedTargetEntityClassname());
            mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    adornedTargetList.getAdornedTargetEntityClassname(),
                    entities2,
                    null,
                    new String[]{},
                    new ForeignKey[]{},
                    MergedPropertyType.ADORNEDTARGETLIST,
                    persistencePerspective.getPopulateToOneFields(),
                    persistencePerspective.getIncludeFields(),
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
            );
            BaseCtoConverter ctoConverter = getAdornedTargetCtoConverter(persistencePerspective, cto, mergedProperties, adornedTargetList);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, adornedTargetList.getAdornedTargetEntityClassname());
            records = persistenceManager.getDynamicEntityDao().query(queryCriteria, Class.forName(adornedTargetList.getAdornedTargetEntityClassname()));

            index = 0;
            Long myEntityId = Long.valueOf(entity.findProperty(adornedTargetList.getTargetObjectPath() + "." + adornedTargetList.getTargetIdProperty()).getValue());
            FieldManager fieldManager = getFieldManager();
            for (Serializable record : records) {
                Long targetId = (Long) fieldManager.getFieldValue(record, adornedTargetList.getTargetObjectPath() + "." + adornedTargetList.getTargetIdProperty());
                if (myEntityId.equals(targetId)) {
                    break;
                }
                index++;
            }
            return this;
        }
    }
}
