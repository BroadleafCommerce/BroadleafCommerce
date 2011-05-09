package org.broadleafcommerce.gwt.server.module;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.JoinTable;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.cto.BaseCtoConverter;
import org.broadleafcommerce.gwt.server.dao.FieldMetadata;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;

public class JoinTableModule extends BasicEntityModule {
	
	private static final Log LOG = LogFactory.getLog(JoinTableModule.class);
	
	public boolean isCompatible(OperationType operationType) {
		return OperationType.JOINTABLE.equals(operationType);
	}
	
	public void extractProperties(Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException {
		if (mergedProperties.get(MergedPropertyType.JOINTABLE) != null) {
			extractPropertiesFromMetadata(mergedProperties.get(MergedPropertyType.JOINTABLE), properties, true);
		}
	}

	protected BaseCtoConverter getJoinTableCtoConverter(CriteriaTransferObject cto, Map<String, FieldMetadata> mergedProperties, JoinTable joinTable) {
		BaseCtoConverter ctoConverter = getCtoConverter(cto, joinTable.getJoinTableEntityClassname(), mergedProperties);
		ctoConverter.addLongEQMapping(joinTable.getJoinTableEntityClassname(), joinTable.getManyToField(), AssociationPath.ROOT, joinTable.getLinkedObjectPath() + "." + joinTable.getLinkedIdProperty());
		ctoConverter.addLongEQMapping(joinTable.getJoinTableEntityClassname(), joinTable.getManyToField() + "Target", AssociationPath.ROOT, joinTable.getTargetObjectPath() + "." + joinTable.getTargetIdProperty());
		return ctoConverter;
	}
	
	protected Serializable createPopulatedJoinTableInstance(JoinTable joinTable, Entity entity) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NumberFormatException, InvocationTargetException, NoSuchMethodException {
		Serializable instance = (Serializable) Class.forName(joinTable.getJoinTableEntityClassname()).newInstance();
		PropertyUtils.setProperty(instance, joinTable.getLinkedObjectPath()+"."+joinTable.getLinkedIdProperty(), Long.valueOf(entity.findProperty(joinTable.getLinkedObjectPath()+"."+joinTable.getLinkedIdProperty()).getValue()));
		PropertyUtils.setProperty(instance, joinTable.getTargetObjectPath()+"."+joinTable.getTargetIdProperty(), Long.valueOf(entity.findProperty(joinTable.getTargetObjectPath()+"."+joinTable.getTargetIdProperty()).getValue()));
		
		return instance;
	}
	
	@Override
	public void updateMergedProperties(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException {
		try {
			JoinTable joinTable = (JoinTable) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINTABLE);
			if (joinTable != null) {
				Map<String, FieldMetadata> joinMergedProperties = getMergedProperties(
					joinTable.getJoinTableEntityClassname(), 
					new Class[]{Class.forName(joinTable.getJoinTableEntityClassname())}, 
					null, 
					new String[]{}, 
					new ForeignKey[]{},
					MergedPropertyType.JOINTABLE
				);
				allMergedProperties.put(MergedPropertyType.JOINTABLE, joinMergedProperties);
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
		JoinTable joinTable = (JoinTable) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINTABLE);
		Entity payload;
		try {
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedPropertiesTarget = getMergedProperties(
				ceilingEntityFullyQualifiedClassname, 
				entities, 
				null, 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalNonPersistentForeignKeys(),
				MergedPropertyType.PRIMARY
			);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(
				joinTable.getJoinTableEntityClassname(), 
				new Class[]{Class.forName(joinTable.getJoinTableEntityClassname())}, 
				null, 
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINTABLE
			);
			
			CriteriaTransferObject ctoInserted = new CriteriaTransferObject();
			FilterAndSortCriteria filterCriteriaInsertedLinked = ctoInserted.get(joinTable.getManyToField());
			filterCriteriaInsertedLinked.setFilterValue(entity.findProperty(joinTable.getLinkedObjectPath() + "." + joinTable.getLinkedIdProperty()).getValue());
			FilterAndSortCriteria filterCriteriaInsertedTarget = ctoInserted.get(joinTable.getManyToField()+"Target");
			filterCriteriaInsertedTarget.setFilterValue(entity.findProperty(joinTable.getTargetObjectPath() + "." + joinTable.getTargetIdProperty()).getValue());
			BaseCtoConverter ctoConverterInserted = getJoinTableCtoConverter(ctoInserted, mergedProperties, joinTable);
			PersistentEntityCriteria queryCriteriaInserted = ctoConverterInserted.convert(ctoInserted, joinTable.getJoinTableEntityClassname());
			List<Serializable> recordsInserted = dynamicEntityDao.query(queryCriteriaInserted, Class.forName(joinTable.getJoinTableEntityClassname()));
			if (recordsInserted.size() > 0) {
				payload = getRecords(mergedPropertiesTarget, recordsInserted, mergedProperties, joinTable.getTargetObjectPath())[0];
			} else {
				Serializable instance = createPopulatedJoinTableInstance(joinTable, entity);
				instance = createPopulatedInstance(instance, entity, mergedProperties, false);
				instance = createPopulatedInstance(instance, entity, mergedPropertiesTarget, false);
				if (PropertyUtils.getPropertyDescriptor(instance, "id") != null) {
					PropertyUtils.setProperty(instance, "id", null);
				}
				CriteriaTransferObject cto = new CriteriaTransferObject();
				FilterAndSortCriteria filterCriteria = cto.get(joinTable.getManyToField());
				filterCriteria.setFilterValue(entity.findProperty(joinTable.getLinkedObjectPath() + "." + joinTable.getLinkedIdProperty()).getValue());
				FilterAndSortCriteria sortCriteria = cto.get(joinTable.getSortField());
		        sortCriteria.setSortAscending(joinTable.getSortAscending());
				
				BaseCtoConverter ctoConverter = getJoinTableCtoConverter(cto, mergedProperties, joinTable);
				
				int totalRecords = getTotalRecords(joinTable.getJoinTableEntityClassname(), cto, ctoConverter);
				
				PropertyUtils.setProperty(instance, joinTable.getSortField(), Long.valueOf(totalRecords + 1));
				instance = dynamicEntityDao.merge(instance);
				dynamicEntityDao.flush();
				dynamicEntityDao.clear();
				
				List<Serializable> recordsInserted2 = dynamicEntityDao.query(queryCriteriaInserted, Class.forName(joinTable.getJoinTableEntityClassname()));
				
				payload = getRecords(mergedPropertiesTarget, recordsInserted2, mergedProperties, joinTable.getTargetObjectPath())[0];
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
		JoinTable joinTable = (JoinTable) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINTABLE);
		try {
			CriteriaTransferObject cto = new CriteriaTransferObject();
			FilterAndSortCriteria filterCriteria = cto.get(joinTable.getManyToField());
			filterCriteria.setFilterValue(entity.findProperty(joinTable.getLinkedObjectPath() + "." + joinTable.getLinkedIdProperty()).getValue());
			if (joinTable.getSortField() != null) {
				FilterAndSortCriteria sortCriteria = cto.get(joinTable.getSortField());
				sortCriteria.setSortAscending(joinTable.getSortAscending());
			}
			
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(
				joinTable.getJoinTableEntityClassname(), 
				new Class[]{Class.forName(joinTable.getJoinTableEntityClassname())}, 
				null, 
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINTABLE
			);
			BaseCtoConverter ctoConverter = getJoinTableCtoConverter(cto, mergedProperties, joinTable);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, joinTable.getJoinTableEntityClassname());
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Class.forName(joinTable.getJoinTableEntityClassname()));
			
			int index = 0;
			Long myEntityId = Long.valueOf(entity.findProperty(joinTable.getTargetObjectPath() + "." + joinTable.getTargetIdProperty()).getValue());	
			for (Serializable record : records) {
				Long targetId = (Long) PropertyUtils.getProperty(record, joinTable.getTargetObjectPath() + "." + joinTable.getTargetIdProperty());
				if (myEntityId.equals(targetId)) {
					break;
				}
				index++;
			}
			if (joinTable.getSortField() != null && entity.findProperty(joinTable.getSortField()).getValue() != null) {
				Serializable myRecord = records.remove(index);
				myRecord = createPopulatedInstance(myRecord, entity, mergedProperties, false);
				Integer newPos = Integer.valueOf(entity.findProperty(joinTable.getSortField()).getValue());
				records.add(newPos, myRecord);
				index = 1;
				for (Serializable record : records) {
					PropertyUtils.setProperty(record, joinTable.getSortField(), Long.valueOf(index));
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
			JoinTable joinTable = (JoinTable) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINTABLE);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(
				joinTable.getJoinTableEntityClassname(), 
				new Class[]{Class.forName(joinTable.getJoinTableEntityClassname())}, 
				null, 
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINTABLE
			);
			CriteriaTransferObject ctoInserted = new CriteriaTransferObject();
			FilterAndSortCriteria filterCriteriaInsertedLinked = ctoInserted.get(joinTable.getManyToField());
			filterCriteriaInsertedLinked.setFilterValue(entity.findProperty(joinTable.getLinkedObjectPath() + "." + joinTable.getLinkedIdProperty()).getValue());
			FilterAndSortCriteria filterCriteriaInsertedTarget = ctoInserted.get(joinTable.getManyToField()+"Target");
			filterCriteriaInsertedTarget.setFilterValue(entity.findProperty(joinTable.getTargetObjectPath() + "." + joinTable.getTargetIdProperty()).getValue());
			BaseCtoConverter ctoConverterInserted = getJoinTableCtoConverter(ctoInserted, mergedProperties, joinTable);
			PersistentEntityCriteria queryCriteriaInserted = ctoConverterInserted.convert(ctoInserted, joinTable.getJoinTableEntityClassname());
			List<Serializable> recordsInserted = dynamicEntityDao.query(queryCriteriaInserted, Class.forName(joinTable.getJoinTableEntityClassname()));
			
			dynamicEntityDao.remove(recordsInserted.get(0));
		} catch (Exception e) {
			LOG.error("Problem removing entity", e);
			throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
		}
	}
	
	@Override
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException {
		JoinTable joinTable = (JoinTable) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINTABLE);
		Entity[] payload;
		int totalRecords;
		try {
			Class<?>[] entities = dynamicEntityRemoteService.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
			Map<String, FieldMetadata> mergedPropertiesTarget = getMergedProperties(
				ceilingEntityFullyQualifiedClassname, 
				entities, 
				null, 
				persistencePerspective.getAdditionalNonPersistentProperties(), 
				persistencePerspective.getAdditionalNonPersistentForeignKeys(),
				MergedPropertyType.PRIMARY
			);
			Map<String, FieldMetadata> mergedProperties = getMergedProperties(
				joinTable.getJoinTableEntityClassname(), 
				new Class[]{Class.forName(joinTable.getJoinTableEntityClassname())}, 
				null, 
				new String[]{}, 
				new ForeignKey[]{},
				MergedPropertyType.JOINTABLE
			);
			BaseCtoConverter ctoConverter = getJoinTableCtoConverter(cto, mergedProperties, joinTable);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, joinTable.getJoinTableEntityClassname());
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Class.forName(joinTable.getJoinTableEntityClassname()));
			payload = getRecords(mergedPropertiesTarget, records, mergedProperties, joinTable.getTargetObjectPath());
			totalRecords = getTotalRecords(joinTable.getJoinTableEntityClassname(), cto, ctoConverter);
		} catch (Exception e) {
			LOG.error("Problem fetching results for " + joinTable.getJoinTableEntityClassname(), e);
			throw new ServiceException("Unable to fetch results for " + joinTable.getJoinTableEntityClassname(), e);
		}
		
		DynamicResultSet results = new DynamicResultSet(null, payload, totalRecords);
		
		return results;
	}
}
