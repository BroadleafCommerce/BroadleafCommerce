package org.broadleafcommerce.gwt.server.service.module;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.JoinStructure;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.cto.BaseCtoConverter;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;

public class JoinStructureModule extends BasicEntityModule {
	
	private static final Log LOG = LogFactory.getLog(JoinStructureModule.class);
	
	public boolean isCompatible(OperationType operationType) {
		return OperationType.JOINSTRUCTURE.equals(operationType);
	}
	
	public void extractProperties(Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException {
		if (mergedProperties.get(MergedPropertyType.JOINSTRUCTURE) != null) {
			extractPropertiesFromMetadata(mergedProperties.get(MergedPropertyType.JOINSTRUCTURE), properties, true);
		}
	}

	protected BaseCtoConverter getJoinStructureCtoConverter(CriteriaTransferObject cto, Map<String, FieldMetadata> mergedProperties, JoinStructure joinStructure) {
		BaseCtoConverter ctoConverter = getCtoConverter(cto, joinStructure.getJoinStructureEntityClassname(), mergedProperties);
		ctoConverter.addLongEQMapping(joinStructure.getJoinStructureEntityClassname(), joinStructure.getManyToField(), AssociationPath.ROOT, joinStructure.getLinkedObjectPath() + "." + joinStructure.getLinkedIdProperty());
		ctoConverter.addLongEQMapping(joinStructure.getJoinStructureEntityClassname(), joinStructure.getManyToField() + "Target", AssociationPath.ROOT, joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty());
		return ctoConverter;
	}
	
	protected Serializable createPopulatedJoinStructureInstance(JoinStructure joinStructure, Entity entity) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NumberFormatException, InvocationTargetException, NoSuchMethodException {
		Serializable instance = (Serializable) Class.forName(joinStructure.getJoinStructureEntityClassname()).newInstance();
		String targetPath = joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty();
		String linkedPath = joinStructure.getLinkedObjectPath() + "." + joinStructure.getLinkedIdProperty();
		getFieldManager().setFieldValue(instance, linkedPath, Long.valueOf(entity.findProperty(linkedPath).getValue()));
		getFieldManager().setFieldValue(instance, targetPath, Long.valueOf(entity.findProperty(targetPath).getValue()));
		
		return instance;
	}
	
	@Override
	public void updateMergedProperties(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties, Map<String, FieldMetadata> metadataOverrides) throws ServiceException {
		try {
			JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
			if (joinStructure != null) {
				Map<String, FieldMetadata> joinMergedProperties = dynamicEntityDao.getMergedProperties(
					joinStructure.getJoinStructureEntityClassname(), 
					new Class[]{Class.forName(joinStructure.getJoinStructureEntityClassname())}, 
					null, 
					new String[]{}, 
					new ForeignKey[]{},
					MergedPropertyType.JOINSTRUCTURE,
					persistencePerspective.getPopulateManyToOneFields(), 
					persistencePerspective.getIncludeFields(), 
					persistencePerspective.getExcludeFields(),
					metadataOverrides
				);
				allMergedProperties.put(MergedPropertyType.JOINSTRUCTURE, joinMergedProperties);
			}
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
		}
	}
	
	@Override
	public Entity add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		if (customCriteria != null && customCriteria.length > 0) {
			LOG.warn("custom persistence handlers and custom criteria not supported for add types other than ENTITY");
		}
		JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
		Entity payload;
		try {
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedPropertiesTarget = dynamicEntityDao.getMergedProperties(
				ceilingEntityFullyQualifiedClassname, 
				entities, 
				null, 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalForeignKeys(),
				MergedPropertyType.PRIMARY,
				persistencePerspective.getPopulateManyToOneFields(), 
				persistencePerspective.getIncludeFields(), 
				persistencePerspective.getExcludeFields(),
				null
			);
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				joinStructure.getJoinStructureEntityClassname(), 
				new Class[]{Class.forName(joinStructure.getJoinStructureEntityClassname())}, 
				null, 
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINSTRUCTURE,
				false,
				new String[]{},
				new String[]{},
				null
			);
			
			CriteriaTransferObject ctoInserted = new CriteriaTransferObject();
			FilterAndSortCriteria filterCriteriaInsertedLinked = ctoInserted.get(joinStructure.getManyToField());
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
			FilterAndSortCriteria filterCriteriaInsertedTarget = ctoInserted.get(joinStructure.getManyToField()+"Target");
			filterCriteriaInsertedTarget.setFilterValue(entity.findProperty(joinStructure.getInverse()?linkedPath:targetPath).getValue());
			BaseCtoConverter ctoConverterInserted = getJoinStructureCtoConverter(ctoInserted, mergedProperties, joinStructure);
			PersistentEntityCriteria queryCriteriaInserted = ctoConverterInserted.convert(ctoInserted, joinStructure.getJoinStructureEntityClassname());
			List<Serializable> recordsInserted = dynamicEntityDao.query(queryCriteriaInserted, Class.forName(joinStructure.getJoinStructureEntityClassname()));
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
					FilterAndSortCriteria filterCriteria = cto.get(joinStructure.getManyToField());
					filterCriteria.setFilterValue(entity.findProperty(joinStructure.getInverse()?targetPath:linkedPath).getValue());
					FilterAndSortCriteria sortCriteria = cto.get(joinStructure.getSortField());
					sortCriteria.setSortAscending(joinStructure.getSortAscending());
					BaseCtoConverter ctoConverter = getJoinStructureCtoConverter(cto, mergedProperties, joinStructure);
					int totalRecords = getTotalRecords(joinStructure.getJoinStructureEntityClassname(), cto, ctoConverter);
					fieldManager.setFieldValue(instance, joinStructure.getSortField(), Long.valueOf(totalRecords + 1));
				}
				instance = dynamicEntityDao.merge(instance);
				dynamicEntityDao.flush();
				dynamicEntityDao.clear();
				
				List<Serializable> recordsInserted2 = dynamicEntityDao.query(queryCriteriaInserted, Class.forName(joinStructure.getJoinStructureEntityClassname()));
				
				payload = getRecords(mergedPropertiesTarget, recordsInserted2, mergedProperties, joinStructure.getTargetObjectPath())[0];
			}
		} catch (Exception e) {
			LOG.error("Problem editing entity", e);
			throw new ServiceException("Problem adding new entity : " + e.getMessage(), e);
		}
		
		return payload;
	}
	
	@Override
	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		if (customCriteria != null && customCriteria.length > 0) {
			LOG.warn("custom persistence handlers and custom criteria not supported for update types other than ENTITY");
		}
		JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
		try {
			CriteriaTransferObject cto = new CriteriaTransferObject();
			FilterAndSortCriteria filterCriteria = cto.get(joinStructure.getManyToField());
			filterCriteria.setFilterValue(entity.findProperty(joinStructure.getLinkedObjectPath() + "." + joinStructure.getLinkedIdProperty()).getValue());
			if (joinStructure.getSortField() != null) {
				FilterAndSortCriteria sortCriteria = cto.get(joinStructure.getSortField());
				sortCriteria.setSortAscending(joinStructure.getSortAscending());
			}
			
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				joinStructure.getJoinStructureEntityClassname(), 
				new Class[]{Class.forName(joinStructure.getJoinStructureEntityClassname())}, 
				null, 
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINSTRUCTURE,
				persistencePerspective.getPopulateManyToOneFields(), 
				persistencePerspective.getIncludeFields(), 
				persistencePerspective.getExcludeFields(),
				null
			);
			BaseCtoConverter ctoConverter = getJoinStructureCtoConverter(cto, mergedProperties, joinStructure);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, joinStructure.getJoinStructureEntityClassname());
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Class.forName(joinStructure.getJoinStructureEntityClassname()));
			
			int index = 0;
			Long myEntityId = Long.valueOf(entity.findProperty(joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty()).getValue());	
			FieldManager fieldManager = getFieldManager();
			for (Serializable record : records) {
				Long targetId = (Long) fieldManager.getFieldValue(record, joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty());
				if (myEntityId.equals(targetId)) {
					break;
				}
				index++;
			}
			if (joinStructure.getSortField() != null && entity.findProperty(joinStructure.getSortField()).getValue() != null) {
				Serializable myRecord = records.remove(index);
				myRecord = createPopulatedInstance(myRecord, entity, mergedProperties, false);
				Integer newPos = Integer.valueOf(entity.findProperty(joinStructure.getSortField()).getValue());
				records.add(newPos, myRecord);
				index = 1;
				for (Serializable record : records) {
					fieldManager.setFieldValue(record, joinStructure.getSortField(), Long.valueOf(index));
					index++;
				}
			} else {
				Serializable myRecord = records.get(index);
				myRecord = createPopulatedInstance(myRecord, entity, mergedProperties, false);
				dynamicEntityDao.merge(myRecord);
			}
			
			return entity;
		} catch (Exception e) {
			LOG.error("Problem editing entity", e);
			throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
		}
	}
	
	@Override
	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		if (customCriteria != null && customCriteria.length > 0) {
			LOG.warn("custom persistence handlers and custom criteria not supported for remove types other than ENTITY");
		}
		try {
			JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				joinStructure.getJoinStructureEntityClassname(), 
				new Class[]{Class.forName(joinStructure.getJoinStructureEntityClassname())}, 
				null, 
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINSTRUCTURE,
				false,
				new String[]{},
				new String[]{},
				null
			);
			CriteriaTransferObject ctoInserted = new CriteriaTransferObject();
			FilterAndSortCriteria filterCriteriaInsertedLinked = ctoInserted.get(joinStructure.getManyToField());
			filterCriteriaInsertedLinked.setFilterValue(entity.findProperty(joinStructure.getLinkedObjectPath() + "." + joinStructure.getLinkedIdProperty()).getValue());
			FilterAndSortCriteria filterCriteriaInsertedTarget = ctoInserted.get(joinStructure.getManyToField()+"Target");
			filterCriteriaInsertedTarget.setFilterValue(entity.findProperty(joinStructure.getTargetObjectPath() + "." + joinStructure.getTargetIdProperty()).getValue());
			BaseCtoConverter ctoConverterInserted = getJoinStructureCtoConverter(ctoInserted, mergedProperties, joinStructure);
			PersistentEntityCriteria queryCriteriaInserted = ctoConverterInserted.convert(ctoInserted, joinStructure.getJoinStructureEntityClassname());
			List<Serializable> recordsInserted = dynamicEntityDao.query(queryCriteriaInserted, Class.forName(joinStructure.getJoinStructureEntityClassname()));
			
			dynamicEntityDao.remove(recordsInserted.get(0));
		} catch (Exception e) {
			LOG.error("Problem removing entity", e);
			throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
		}
	}
	
	@Override
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
		Entity[] payload;
		int totalRecords;
		try {
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedPropertiesTarget = dynamicEntityDao.getMergedProperties(
				ceilingEntityFullyQualifiedClassname, 
				entities, 
				null, 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalForeignKeys(),
				MergedPropertyType.PRIMARY,
				persistencePerspective.getPopulateManyToOneFields(), 
				persistencePerspective.getIncludeFields(), 
				persistencePerspective.getExcludeFields(),
				null
			);
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				joinStructure.getJoinStructureEntityClassname(), 
				new Class[]{Class.forName(joinStructure.getJoinStructureEntityClassname())}, 
				null, 
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINSTRUCTURE,
				false,
				new String[]{},
				new String[]{},
				null
			);
			BaseCtoConverter ctoConverter = getJoinStructureCtoConverter(cto, mergedProperties, joinStructure);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, joinStructure.getJoinStructureEntityClassname());
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Class.forName(joinStructure.getJoinStructureEntityClassname()));
			payload = getRecords(mergedPropertiesTarget, records, mergedProperties, joinStructure.getTargetObjectPath());
			totalRecords = getTotalRecords(joinStructure.getJoinStructureEntityClassname(), cto, ctoConverter);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + joinStructure.getJoinStructureEntityClassname(), e);
			throw new ServiceException("Unable to fetch results for " + joinStructure.getJoinStructureEntityClassname(), e);
		}
		
		DynamicResultSet results = new DynamicResultSet(null, payload, totalRecords);
		
		return results;
	}
}
