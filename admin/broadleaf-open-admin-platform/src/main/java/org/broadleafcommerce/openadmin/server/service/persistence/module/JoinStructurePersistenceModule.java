/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.persistence.module;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.FilterCriterion;
import com.anasoft.os.daofusion.criteria.NestedPropertyCriteria;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.criteria.SimpleFilterCriterionProvider;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.JoinStructure;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.common.presentation.OperationType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.common.presentation.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
@Component("blJoinStructurePersistenceModule")
@Scope("prototype")
public class JoinStructurePersistenceModule extends BasicPersistenceModule {
	
	private static final Log LOG = LogFactory.getLog(JoinStructurePersistenceModule.class);
	
	public boolean isCompatible(OperationType operationType) {
		return OperationType.JOINSTRUCTURE.equals(operationType);
	}
	
	public void extractProperties(Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException {
		if (mergedProperties.get(MergedPropertyType.JOINSTRUCTURE) != null) {
			extractPropertiesFromMetadata(mergedProperties.get(MergedPropertyType.JOINSTRUCTURE), properties, true);
		}
	}

	public BaseCtoConverter getJoinStructureCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, Map<String, FieldMetadata> mergedProperties, JoinStructure joinStructure) throws ClassNotFoundException {
		BaseCtoConverter ctoConverter = getCtoConverter(persistencePerspective, cto, joinStructure.getJoinStructureEntityClassname(), mergedProperties);
		ctoConverter.addLongEQMapping(joinStructure.getJoinStructureEntityClassname(), joinStructure.getName(), AssociationPath.ROOT, joinStructure.getLinkedObjectPath() + "." + joinStructure.getLinkedIdProperty());
		ctoConverter.addLongEQMapping(joinStructure.getJoinStructureEntityClassname(), joinStructure.getName() + "Target", AssociationPath.ROOT, joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty());
		return ctoConverter;
	}
	
	protected Serializable createPopulatedJoinStructureInstance(JoinStructure joinStructure, Entity entity) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NumberFormatException, InvocationTargetException, NoSuchMethodException {
		Serializable instance = (Serializable) Class.forName(StringUtils.isEmpty(joinStructure.getJoinStructureEntityPolymorphicType())?joinStructure.getJoinStructureEntityClassname():joinStructure.getJoinStructureEntityPolymorphicType()).newInstance();
		String targetPath = joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty();
		String linkedPath = joinStructure.getLinkedObjectPath() + "." + joinStructure.getLinkedIdProperty();
		getFieldManager().setFieldValue(instance, linkedPath, Long.valueOf(entity.findProperty(linkedPath).getValue()));
		getFieldManager().setFieldValue(instance, targetPath, Long.valueOf(entity.findProperty(targetPath).getValue()));
		
		return instance;
	}
	
	@Override
	public void updateMergedProperties(PersistencePackage persistencePackage, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException {
		String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
			if (joinStructure != null) {
                Class<?>[] entities = persistenceManager.getPolymorphicEntities(joinStructure.getJoinStructureEntityClassname());
				Map<String, FieldMetadata> joinMergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
					joinStructure.getJoinStructureEntityClassname(), 
                    entities,
					null, 
					new String[]{}, 
					new ForeignKey[]{},
					MergedPropertyType.JOINSTRUCTURE,
					persistencePerspective.getPopulateToOneFields(), 
					persistencePerspective.getIncludeFields(), 
					persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
					""
				);
                String idProp = null;
                for (String key : joinMergedProperties.keySet()) {
                    if (joinMergedProperties.get(key) instanceof BasicFieldMetadata && ((BasicFieldMetadata) joinMergedProperties.get(key)).getFieldType()== SupportedFieldType.ID) {
                        idProp = key;
                        break;
                    }
                }
                if (idProp != null) {
                    joinMergedProperties.remove(idProp);
                }
				allMergedProperties.put(MergedPropertyType.JOINSTRUCTURE, joinMergedProperties);
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
			LOG.warn("custom persistence handlers and custom criteria not supported for add types other than ENTITY");
		}
		PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
		String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		Entity entity = persistencePackage.getEntity();
		JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
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
            Class<?>[] entities2 = persistenceManager.getPolymorphicEntities(joinStructure.getJoinStructureEntityClassname());
			Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
				joinStructure.getJoinStructureEntityClassname(), 
                entities2,
				null, 
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINSTRUCTURE,
				false,
				new String[]{},
				new String[]{},
                null,
				""
			);
			
			CriteriaTransferObject ctoInserted = new CriteriaTransferObject();
			FilterAndSortCriteria filterCriteriaInsertedLinked = ctoInserted.get(joinStructure.getName());
			String linkedPath;
			String targetPath;
			if (joinStructure.getInverse()) {
				linkedPath = joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty();
				targetPath = joinStructure.getLinkedObjectPath() + "." + joinStructure.getLinkedIdProperty();
			} else {
				targetPath = joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty();
				linkedPath = joinStructure.getLinkedObjectPath() + "." + joinStructure.getLinkedIdProperty();
			}
			filterCriteriaInsertedLinked.setFilterValue(entity.findProperty(joinStructure.getInverse()?targetPath:linkedPath).getValue());
			FilterAndSortCriteria filterCriteriaInsertedTarget = ctoInserted.get(joinStructure.getName()+"Target");
			filterCriteriaInsertedTarget.setFilterValue(entity.findProperty(joinStructure.getInverse()?linkedPath:targetPath).getValue());
			BaseCtoConverter ctoConverterInserted = getJoinStructureCtoConverter(persistencePerspective, ctoInserted, mergedProperties, joinStructure);
			PersistentEntityCriteria queryCriteriaInserted = ctoConverterInserted.convert(ctoInserted, joinStructure.getJoinStructureEntityClassname());
			List<Serializable> recordsInserted = persistenceManager.getDynamicEntityDao().query(queryCriteriaInserted, Class.forName(joinStructure.getJoinStructureEntityClassname()));
			if (recordsInserted.size() > 0) {
				payload = getRecords(mergedPropertiesTarget, recordsInserted, mergedProperties, joinStructure.getTargetObjectPath())[0];
			} else {
				Serializable instance = createPopulatedJoinStructureInstance(joinStructure, entity);
				instance = createPopulatedInstance(instance, entity, mergedProperties, false);
				instance = createPopulatedInstance(instance, entity, mergedPropertiesTarget, false);
				FieldManager fieldManager = getFieldManager();
				if (fieldManager.getField(instance.getClass(), "id") != null) {
					fieldManager.setFieldValue(instance, "id", null);
				}
				if (joinStructure.getSortField() != null) {
					CriteriaTransferObject cto = new CriteriaTransferObject();
					FilterAndSortCriteria filterCriteria = cto.get(joinStructure.getName());
					filterCriteria.setFilterValue(entity.findProperty(joinStructure.getInverse()?targetPath:linkedPath).getValue());
					FilterAndSortCriteria sortCriteria = cto.get(joinStructure.getSortField());
					sortCriteria.setSortAscending(joinStructure.getSortAscending());
					BaseCtoConverter ctoConverter = getJoinStructureCtoConverter(persistencePerspective, cto, mergedProperties, joinStructure);
					int totalRecords = getTotalRecords(persistencePackage, cto, ctoConverter);
					fieldManager.setFieldValue(instance, joinStructure.getSortField(), Long.valueOf(totalRecords + 1));
				}
				instance = persistenceManager.getDynamicEntityDao().merge(instance);
				persistenceManager.getDynamicEntityDao().flush();
				persistenceManager.getDynamicEntityDao().clear();
				
				List<Serializable> recordsInserted2 = persistenceManager.getDynamicEntityDao().query(queryCriteriaInserted, Class.forName(joinStructure.getJoinStructureEntityClassname()));
				
				payload = getRecords(mergedPropertiesTarget, recordsInserted2, mergedProperties, joinStructure.getTargetObjectPath())[0];
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
			LOG.warn("custom persistence handlers and custom criteria not supported for update types other than ENTITY");
		}
		PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
		Entity entity = persistencePackage.getEntity();
		JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
		try {
            JoinStructureRetrieval joinStructureRetrieval = new JoinStructureRetrieval(persistencePerspective, entity, joinStructure).invoke();
            List<Serializable> records = joinStructureRetrieval.getRecords();
            int index = joinStructureRetrieval.getIndex();
            Map<String, FieldMetadata> mergedProperties = joinStructureRetrieval.getMergedProperties();
            FieldManager fieldManager = getFieldManager();
			if (joinStructure.getSortField() != null && entity.findProperty(joinStructure.getSortField()).getValue() != null) {
				Serializable myRecord = records.remove(index);
				myRecord = createPopulatedInstance(myRecord, entity, mergedProperties, false);
				Integer newPos = Integer.valueOf(entity.findProperty(joinStructure.getSortField()).getValue());
                if (CollectionUtils.isEmpty(records)) {
                    records.add(myRecord);
                } else {
				    records.add(newPos, myRecord);
                }
				index = 1;
				for (Serializable record : records) {
					fieldManager.setFieldValue(record, joinStructure.getSortField(), Long.valueOf(index));
					index++;
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
                Entity[] payload = getRecords(mergedPropertiesTarget, myList, mergedProperties, joinStructure.getTargetObjectPath());
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
			LOG.warn("custom persistence handlers and custom criteria not supported for remove types other than ENTITY");
		}
		PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
		Entity entity = persistencePackage.getEntity();
		try {
			JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(joinStructure.getJoinStructureEntityClassname());
			Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
				joinStructure.getJoinStructureEntityClassname(), 
                entities,
				null, 
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINSTRUCTURE,
				false,
				new String[]{},
				new String[]{},
                null,
				""
			);
			CriteriaTransferObject ctoInserted = new CriteriaTransferObject();
			FilterAndSortCriteria filterCriteriaInsertedLinked = ctoInserted.get(joinStructure.getName());
			filterCriteriaInsertedLinked.setFilterValue(entity.findProperty(joinStructure.getLinkedObjectPath() + "." + joinStructure.getLinkedIdProperty()).getValue());
			FilterAndSortCriteria filterCriteriaInsertedTarget = ctoInserted.get(joinStructure.getName()+"Target");
			filterCriteriaInsertedTarget.setFilterValue(entity.findProperty(joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty()).getValue());
			BaseCtoConverter ctoConverterInserted = getJoinStructureCtoConverter(persistencePerspective, ctoInserted, mergedProperties, joinStructure);
			PersistentEntityCriteria queryCriteriaInserted = ctoConverterInserted.convert(ctoInserted, joinStructure.getJoinStructureEntityClassname());
			List<Serializable> recordsInserted = persistenceManager.getDynamicEntityDao().query(queryCriteriaInserted, Class.forName(joinStructure.getJoinStructureEntityClassname()));
			
			persistenceManager.getDynamicEntityDao().remove(recordsInserted.get(0));
		} catch (Exception e) {
			LOG.error("Problem removing entity", e);
			throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
		}
	}

    @Override
    public int getTotalRecords(PersistencePackage persistencePackage, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException {
        JoinStructure joinStructure = (JoinStructure) persistencePackage.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
        PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), joinStructure.getJoinStructureEntityClassname());
        Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(Class.forName(joinStructure.getJoinStructureEntityClassname()));
        boolean isArchivable = false;
        for (Class<?> entity : entities) {
            if (Status.class.isAssignableFrom(entity)) {
                isArchivable = true;
                break;
            }
        }
        if (isArchivable && !persistencePackage.getPersistencePerspective().getShowArchivedFields()) {
            SimpleFilterCriterionProvider criterionProvider = new  SimpleFilterCriterionProvider(SimpleFilterCriterionProvider.FilterDataStrategy.NONE, 0) {
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    return Restrictions.or(Restrictions.eq(targetPropertyName, 'N'), Restrictions.isNull(targetPropertyName));
                }
            };
            FilterCriterion filterCriterion = new FilterCriterion(AssociationPath.ROOT, "archiveStatus.archived", criterionProvider);
            ((NestedPropertyCriteria) countCriteria).add(filterCriterion);
        }
        return persistenceManager.getDynamicEntityDao().count(countCriteria, Class.forName(joinStructure.getJoinStructureEntityClassname()));
    }
	
	@Override
	public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
		PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
		String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
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
            Class<?>[] entities2 = persistenceManager.getPolymorphicEntities(joinStructure.getJoinStructureEntityClassname());
			Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
				joinStructure.getJoinStructureEntityClassname(), 
                entities2,
				null,
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINSTRUCTURE,
				false,
				new String[]{},
				new String[]{},
                null,
				""
			);
			BaseCtoConverter ctoConverter = getJoinStructureCtoConverter(persistencePerspective, cto, mergedProperties, joinStructure);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, joinStructure.getJoinStructureEntityClassname());
			List<Serializable> records = persistenceManager.getDynamicEntityDao().query(queryCriteria, Class.forName(joinStructure.getJoinStructureEntityClassname()));
			payload = getRecords(mergedPropertiesTarget, records, mergedProperties, joinStructure.getTargetObjectPath());
			totalRecords = getTotalRecords(persistencePackage, cto, ctoConverter);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + joinStructure.getJoinStructureEntityClassname(), e);
			throw new ServiceException("Unable to fetch results for " + joinStructure.getJoinStructureEntityClassname(), e);
		}
		
		DynamicResultSet results = new DynamicResultSet(null, payload, totalRecords);
		
		return results;
	}

    public class JoinStructureRetrieval {
        private PersistencePerspective persistencePerspective;
        private Entity entity;
        private JoinStructure joinStructure;
        private Map<String, FieldMetadata> mergedProperties;
        private List<Serializable> records;
        private int index;

        public JoinStructureRetrieval(PersistencePerspective persistencePerspective, Entity entity, JoinStructure joinStructure) {
            this.persistencePerspective = persistencePerspective;
            this.entity = entity;
            this.joinStructure = joinStructure;
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

        public JoinStructureRetrieval invoke() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, FieldNotAvailableException {
            CriteriaTransferObject cto = new CriteriaTransferObject();
            FilterAndSortCriteria filterCriteria = cto.get(joinStructure.getName());
            filterCriteria.setFilterValue(entity.findProperty(joinStructure.getLinkedObjectPath() + "." + joinStructure.getLinkedIdProperty()).getValue());
            if (joinStructure.getSortField() != null) {
                FilterAndSortCriteria sortCriteria = cto.get(joinStructure.getSortField());
                sortCriteria.setSortAscending(joinStructure.getSortAscending());
            }

            Class<?>[] entities2 = persistenceManager.getPolymorphicEntities(joinStructure.getJoinStructureEntityClassname());
            mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    joinStructure.getJoinStructureEntityClassname(),
                    entities2,
                    null,
                    new String[]{},
                    new ForeignKey[]{},
                    MergedPropertyType.JOINSTRUCTURE,
                    persistencePerspective.getPopulateToOneFields(),
                    persistencePerspective.getIncludeFields(),
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
            );
            BaseCtoConverter ctoConverter = getJoinStructureCtoConverter(persistencePerspective, cto, mergedProperties, joinStructure);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, joinStructure.getJoinStructureEntityClassname());
            records = persistenceManager.getDynamicEntityDao().query(queryCriteria, Class.forName(joinStructure.getJoinStructureEntityClassname()));

            index = 0;
            Long myEntityId = Long.valueOf(entity.findProperty(joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty()).getValue());
            FieldManager fieldManager = getFieldManager();
            for (Serializable record : records) {
                Long targetId = (Long) fieldManager.getFieldValue(record, joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty());
                if (myEntityId.equals(targetId)) {
                    break;
                }
                index++;
            }
            return this;
        }
    }
}
