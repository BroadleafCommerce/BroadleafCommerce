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

package org.broadleafcommerce.admin.server.service.handler;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.FilterCriterion;
import com.anasoft.os.daofusion.criteria.NestedPropertyCriteria;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.criteria.SimpleFilterCriterionProvider;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferCodeImpl;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTODeserializer;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTOToMVELTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELToDataWrapperTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldServiceFactory;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tool.hbm2x.StringUtils;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The main reason for this persistence handler is to support <tt>OfferCode</tt>, which is not directly related
 * in the domain.
 *
 * @author jfischer
 *
 */
public class OfferCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    public static final String IDENTITYCRITERIA = "Offer";
    private static final Log LOG = LogFactory.getLog(OfferCustomPersistenceHandler.class);
    
    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blRuleBuilderFieldServiceFactory")
    protected RuleBuilderFieldServiceFactory ruleBuilderFieldServiceFactory;

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(Offer.class.getName());
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        try {
            /*
            Add offercode fields. These are not on the Offer entity, but are useful in the UI when someone wants to define an offercode for an offer.
             */
            Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
            Map<String, FieldMetadata> mergedProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePackage.getPersistencePerspective());
            allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
            PersistencePerspective offerCodePersistencePerspective = new PersistencePerspective(null, new String[]{}, new ForeignKey[]{new ForeignKey("offer", EntityImplementations.OFFER, null)});
            Map<String, FieldMetadata> offerCodeMergedProperties = helper.getSimpleMergedProperties(OfferCode.class.getName(), offerCodePersistencePerspective);
            BasicFieldMetadata metadata = (BasicFieldMetadata) offerCodeMergedProperties.get("offerCode");
            metadata.setVisibility(VisibilityEnum.HIDDEN_ALL);
            mergedProperties.put("offerCode.offerCode", metadata);
            BasicFieldMetadata metadata2 = (BasicFieldMetadata) offerCodeMergedProperties.get("id");
            metadata2.setVisibility(VisibilityEnum.HIDDEN_ALL);
            mergedProperties.put("offerCode.id", metadata2);

            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Offer.class);
            ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
            
            DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);
            
            return results;
        } catch (Exception e) {
            ServiceException ex = new ServiceException("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
            LOG.error("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), ex);
            throw ex;
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective);
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, Offer.class.getName(), offerProperties);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, Offer.class.getName());
            
            //If necessary, filter out the archived Offers
            if (!persistencePackage.getPersistencePerspective().getShowArchivedFields()) {
                SimpleFilterCriterionProvider criterionProvider = new  SimpleFilterCriterionProvider(SimpleFilterCriterionProvider.FilterDataStrategy.NONE, 0) {
                    @Override
                    public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                        return Restrictions.or(Restrictions.eq(targetPropertyName, 'N'), Restrictions.isNull(targetPropertyName));
                    }
                };
                FilterCriterion filterCriterion = new FilterCriterion(AssociationPath.ROOT, "archiveStatus.archived", criterionProvider);
                ((NestedPropertyCriteria) queryCriteria).add(filterCriterion);
            }
            
            List<Serializable> records = dynamicEntityDao.query(queryCriteria, Offer.class);
            Entity[] entities = helper.getRecords(offerProperties, records);

            addAssociatedOfferCodes(dynamicEntityDao, helper, entities);

            int totalRecords = helper.getTotalRecords(persistencePackage, cto, ctoConverter);
            DynamicResultSet response = new DynamicResultSet(null, entities, totalRecords);
            
            return response;
        } catch (Exception e) {
            LOG.error("Unable to perform fetch for entity" + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected void addAssociatedOfferCodes(DynamicEntityDao dynamicEntityDao, RecordHelper helper, Entity[] entities)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, ParserConfigurationException, TransformerException {
        //retrieve any offer code associated with this offer
        PersistencePerspective offerCodePersistencePerspective = new PersistencePerspective(null, new String[]{}, new ForeignKey[]{new ForeignKey("offer", EntityImplementations.OFFER, null)});
        Map<String, FieldMetadata> offerCodeMergedProperties = helper.getSimpleMergedProperties(OfferCode.class.getName(), offerCodePersistencePerspective);
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
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Offer offerInstance = (Offer) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective);
            offerInstance = (Offer) helper.createPopulatedInstance(offerInstance, entity, offerProperties, false);
            
            OfferCode offerCode = null;
            if (entity.findProperty("deliveryType").getValue().equals("CODE")) {
                offerCode = (OfferCode) entityConfiguration.createEntityInstance(OfferCode.class.getName());
                offerCode.setOfferCode(entity.findProperty("offerCode.offerCode").getValue());
                offerCode.setEndDate(offerInstance.getEndDate());
                offerCode.setMaxUses(offerInstance.getMaxUses());
                offerCode.setOffer(offerInstance);
                offerCode.setStartDate(offerInstance.getStartDate());
                offerCode = (OfferCode) dynamicEntityDao.merge(offerCode);
            }
            
            Entity offerEntity = helper.getRecord(offerProperties, offerInstance, null, null);
            if (offerCode != null) {
                PersistencePerspective offerCodePersistencePerspective = new PersistencePerspective(null, new String[]{}, new ForeignKey[]{new ForeignKey("offer", EntityImplementations.OFFER, null)});
                Map<String, FieldMetadata> offerCodeMergedProperties = helper.getSimpleMergedProperties(OfferCode.class.getName(), offerCodePersistencePerspective);
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

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Property offerCodeId = entity.findProperty("offerCode.id");
            if (offerCodeId != null){
                OfferCode offerCode = (OfferCode) dynamicEntityDao.retrieve(OfferCodeImpl.class, Long.valueOf(entity.findProperty("offerCode.id").getValue()));
                if (offerCode != null) {
                    offerCode.setOffer(null);
                    dynamicEntityDao.remove(offerCode);
                }
            }
            Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, offerProperties);
            Offer offerInstance = (Offer) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            dynamicEntityDao.remove(offerInstance);
        } catch (Exception e) {
            LOG.error("Unable to remove entity for " + entity.getType()[0] + ". It is likely this offer is currently associated with one or more orders. Only unused offers may be deleted.", e);
            throw new ServiceException("Unable to remove entity for " + entity.getType()[0] + ". It is likely this offer is currently associated with one or more orders. Only unused offers may be deleted.", e);
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, offerProperties);
            Offer offerInstance = (Offer) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            offerInstance = (Offer) helper.createPopulatedInstance(offerInstance, entity, offerProperties, false);

            dynamicEntityDao.merge(offerInstance);
            
            Property offerCodeId = entity.findProperty("offerCode.id");
            OfferCode offerCode = null;
            if (entity.findProperty("deliveryType") != null && entity.findProperty("deliveryType").getValue().equals("CODE")) {
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
                offerCode = (OfferCode) dynamicEntityDao.merge(offerCode);
            } else if (offerCodeId != null){
                offerCode = (OfferCode) dynamicEntityDao.retrieve(OfferCodeImpl.class, Long.valueOf(entity.findProperty("offerCode.id").getValue()));
                offerCode.setOffer(null);
                dynamicEntityDao.remove(offerCode);
                offerCode = null;
            }
            
            Entity offerEntity = helper.getRecord(offerProperties, offerInstance, null, null);
            if (offerCode != null) {
                PersistencePerspective offerCodePersistencePerspective = new PersistencePerspective(null, new String[]{}, new ForeignKey[]{new ForeignKey("offer", EntityImplementations.OFFER, null)});
                Map<String, FieldMetadata> offerCodeMergedProperties = helper.getSimpleMergedProperties(OfferCode.class.getName(), offerCodePersistencePerspective);
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
