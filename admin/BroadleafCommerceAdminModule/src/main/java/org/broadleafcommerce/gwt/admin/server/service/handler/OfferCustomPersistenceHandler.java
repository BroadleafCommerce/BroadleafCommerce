package org.broadleafcommerce.gwt.admin.server.service.handler;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.config.EntityConfiguration;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferCodeImpl;
import org.broadleafcommerce.gwt.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.ClassMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.cto.BaseCtoConverter;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.broadleafcommerce.gwt.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.gwt.server.service.module.InspectHelper;
import org.broadleafcommerce.gwt.server.service.module.RecordHelper;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;

public class OfferCustomPersistenceHandler implements CustomPersistenceHandler {

	public static final String IDENTITYCRITERIA = "Offer";
	private static final Log LOG = LogFactory.getLog(OfferCustomPersistenceHandler.class);
	
	@Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
	
	public Boolean canHandleInspect(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && Arrays.binarySearch(customCriteria, IDENTITYCRITERIA) >= 0;
	}

	public Boolean canHandleFetch(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && Arrays.binarySearch(customCriteria, IDENTITYCRITERIA) >= 0;
	}

	public Boolean canHandleAdd(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && Arrays.binarySearch(customCriteria, IDENTITYCRITERIA) >= 0;
	}

	public Boolean canHandleRemove(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && Arrays.binarySearch(customCriteria, IDENTITYCRITERIA) >= 0;
	}

	public Boolean canHandleUpdate(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && Arrays.binarySearch(customCriteria, IDENTITYCRITERIA) >= 0;
	}

	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, String[] customCriteria, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		try {
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Offer.class);
			Map<String, FieldMetadata> mergedProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
			allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
			
			PersistencePerspective offerCodePersistencePerspective = new PersistencePerspective(null, new String[]{}, new ForeignKey[]{new ForeignKey("offer", EntityImplementations.OFFER, null)});
			Class<?>[] offerCodeEntityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(OfferCode.class);
			Map<String, FieldMetadata> offerCodeMergedProperties = helper.getSimpleMergedProperties(OfferCode.class.getName(), offerCodePersistencePerspective, dynamicEntityDao, offerCodeEntityClasses);
			FieldMetadata metadata = offerCodeMergedProperties.get("offerCode");
			metadata.getPresentationAttributes().setHidden(true);
			mergedProperties.put("offerCode.offerCode", metadata);
			FieldMetadata metadata2 = offerCodeMergedProperties.get("id");
			metadata2.getPresentationAttributes().setHidden(true);
			mergedProperties.put("offerCode.id", metadata2);
			
			ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
			
			DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);
			
			return results;
		} catch (Exception e) {
			LOG.error("Unable to retrieve inspection results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to retrieve inspection results for " + ceilingEntityFullyQualifiedClassname, e);
		}
	}

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Offer.class);
			Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
			BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, Offer.class.getName(), offerProperties);
			PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, Offer.class.getName());
			List<Serializable> records = dynamicEntityDao.query(queryCriteria, Offer.class);
			Entity[] entities = helper.getRecords(offerProperties, records, null, null);
			
			PersistencePerspective offerCodePersistencePerspective = new PersistencePerspective(null, new String[]{}, new ForeignKey[]{new ForeignKey("offer", EntityImplementations.OFFER, null)});
			Class<?>[] offerCodeEntityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(OfferCode.class);
			Map<String, FieldMetadata> offerCodeMergedProperties = helper.getSimpleMergedProperties(OfferCode.class.getName(), offerCodePersistencePerspective, dynamicEntityDao, offerCodeEntityClasses);
			for (Entity record : entities) {
				CriteriaTransferObject offerCodeCto = new CriteriaTransferObject();
				FilterAndSortCriteria filterCriteria = offerCodeCto.get("offer");
				filterCriteria.setFilterValue(record.findProperty("id").getValue());
				BaseCtoConverter offerCodeCtoConverter = helper.getCtoConverter(offerCodePersistencePerspective, offerCodeCto, OfferCode.class.getName(), offerCodeMergedProperties);
				
				PersistentEntityCriteria offerCodeQueryCriteria = offerCodeCtoConverter.convert(offerCodeCto, OfferCode.class.getName());
				List<Serializable> offerCodes = dynamicEntityDao.query(offerCodeQueryCriteria, OfferCode.class);
				Entity[] offerCodeEntities = helper.getRecords(offerCodeMergedProperties, offerCodes, null, null);
				
				if (offerCodeEntities.length > 0) {
					Entity temp = new Entity();
					temp.setType(offerCodeEntities[0].getType());
					temp.setProperties(new Property[] {offerCodeEntities[0].findProperty("offerCode"), offerCodeEntities[0].findProperty("id")});
					record.mergeProperties("offerCode", temp);
				}
			}
			
			int totalRecords = helper.getTotalRecords(ceilingEntityFullyQualifiedClassname, cto, ctoConverter);
			
			DynamicResultSet response = new DynamicResultSet(null, entities, totalRecords);
			
			return response;
		} catch (Exception e) {
			LOG.error("Unable to retrieve inspection results for " + ceilingEntityFullyQualifiedClassname, e);
			throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
		}
	}

	public Entity add(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
			Offer offerInstance = (Offer) Class.forName(entity.getType()[0]).newInstance();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Offer.class);
			Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
			offerInstance = (Offer) helper.createPopulatedInstance(offerInstance, entity, offerProperties, false);
			dynamicEntityDao.persist(offerInstance);
			
			OfferCode offerCode = null;
			if (entity.findProperty("deliveryType").getValue().equals("CODE")) {
				offerCode = (OfferCode) entityConfiguration.createEntityInstance(OfferCode.class.getName());
				offerCode.setOfferCode(entity.findProperty("offerCode.offerCode").getValue());
				offerCode.setEndDate(offerInstance.getEndDate());
				offerCode.setMaxUses(offerInstance.getMaxUses());
				offerCode.setOffer(offerInstance);
				offerCode.setStartDate(offerInstance.getStartDate());
				dynamicEntityDao.merge(offerCode);
			}
			
			Entity offerEntity = helper.getRecord(offerProperties, offerInstance, null, null);
			if (offerCode != null) {
				PersistencePerspective offerCodePersistencePerspective = new PersistencePerspective(null, new String[]{}, new ForeignKey[]{new ForeignKey("offer", EntityImplementations.OFFER, null)});
				Class<?>[] offerCodeEntityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(OfferCode.class);
				Map<String, FieldMetadata> offerCodeMergedProperties = helper.getSimpleMergedProperties(OfferCode.class.getName(), offerCodePersistencePerspective, dynamicEntityDao, offerCodeEntityClasses);
				Entity offerCodeEntity = helper.getRecord(offerCodeMergedProperties, offerCode, null, null);
				
				Entity temp = new Entity();
				temp.setType(offerCodeEntity.getType());
				temp.setProperties(new Property[] {offerCodeEntity.findProperty("offerCode"), offerCodeEntity.findProperty("id")});
				offerEntity.mergeProperties("offerCode", temp);
			}
			
			return offerEntity;
		} catch (Exception e) {
			LOG.error("Unable to add entity for " + entity.getType()[0], e);
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
	}

	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
			Property offerCodeId = entity.findProperty("offerCode.id");
			if (offerCodeId != null){
				OfferCode offerCode = (OfferCode) dynamicEntityDao.retrieve(OfferCodeImpl.class, Long.valueOf(entity.findProperty("offerCode.id").getValue()));
				if (offerCode != null) {
					offerCode.setOffer(null);
					dynamicEntityDao.remove(offerCode);
				}
			}
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Offer.class);
			Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
			Object primaryKey = helper.getPrimaryKey(entity, offerProperties);
			Offer offerInstance = (Offer) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			dynamicEntityDao.remove(offerInstance);
		} catch (Exception e) {
			LOG.error("Unable to remove entity for " + entity.getType()[0], e);
			throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
		}
	}

	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Offer.class);
			Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
			Object primaryKey = helper.getPrimaryKey(entity, offerProperties);
			Offer offerInstance = (Offer) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			offerInstance = (Offer) helper.createPopulatedInstance(offerInstance, entity, offerProperties, false);
			dynamicEntityDao.merge(offerInstance);
			
			Property offerCodeId = entity.findProperty("offerCode.id");
			OfferCode offerCode = null;
			if (entity.findProperty("deliveryType").getValue().equals("CODE")) {
				if (offerCodeId == null) {
					offerCode = (OfferCode) entityConfiguration.createEntityInstance(OfferCode.class.getName());
				} else {
					offerCode = (OfferCode) dynamicEntityDao.retrieve(OfferCodeImpl.class, Long.valueOf(entity.findProperty("offerCode.id").getValue()));
				}
				offerCode.setOfferCode(entity.findProperty("offerCode.offerCode").getValue());
				offerCode.setEndDate(offerInstance.getEndDate());
				offerCode.setMaxUses(offerInstance.getMaxUses());
				offerCode.setOffer(offerInstance);
				offerCode.setStartDate(offerInstance.getStartDate());
				dynamicEntityDao.merge(offerCode);
			} else if (offerCodeId != null){
				offerCode = (OfferCode) dynamicEntityDao.retrieve(OfferCodeImpl.class, Long.valueOf(entity.findProperty("offerCode.id").getValue()));
				offerCode.setOffer(null);
				dynamicEntityDao.remove(offerCode);
				offerCode = null;
			}
			
			Entity offerEntity = helper.getRecord(offerProperties, offerInstance, null, null);
			if (offerCode != null) {
				PersistencePerspective offerCodePersistencePerspective = new PersistencePerspective(null, new String[]{}, new ForeignKey[]{new ForeignKey("offer", EntityImplementations.OFFER, null)});
				Class<?>[] offerCodeEntityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(OfferCode.class);
				Map<String, FieldMetadata> offerCodeMergedProperties = helper.getSimpleMergedProperties(OfferCode.class.getName(), offerCodePersistencePerspective, dynamicEntityDao, offerCodeEntityClasses);
				Entity offerCodeEntity = helper.getRecord(offerCodeMergedProperties, offerCode, null, null);
				
				Entity temp = new Entity();
				temp.setType(offerCodeEntity.getType());
				temp.setProperties(new Property[] {offerCodeEntity.findProperty("offerCode"), offerCodeEntity.findProperty("id")});
				offerEntity.mergeProperties("offerCode", temp);
			}
			
			return offerEntity;
		} catch (Exception e) {
			LOG.error("Unable to update entity for " + entity.getType()[0], e);
			throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
		}
	}

}
