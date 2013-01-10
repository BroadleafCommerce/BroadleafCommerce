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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferCodeImpl;
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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tool.hbm2x.StringUtils;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.FilterCriterion;
import com.anasoft.os.daofusion.criteria.NestedPropertyCriteria;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.criteria.SimpleFilterCriterionProvider;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;

import javax.annotation.Resource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class OfferCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    public static final String IDENTITYCRITERIA = "Offer";
    private static final Log LOG = LogFactory.getLog(OfferCustomPersistenceHandler.class);
    
    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        boolean canHandle = false;
        if (customCriteria != null) {
            for (String criteria : customCriteria) {
                if (criteria != null && criteria.equals(IDENTITYCRITERIA)) {
                    canHandle = true;
                    break;
                }
            }
        }
        return canHandle;
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
            Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
            Map<String, FieldMetadata> mergedProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePackage.getPersistencePerspective());
            allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
            /*
             * Add a fake property to hold the fulfillment group rules. This property is the same type as appliesToOrderRules
             */
            mergedProperties.put("appliesToFulfillmentGroupRules", mergedProperties.get("appliesToOrderRules"));
            
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
            Entity[] entities = helper.getRecords(offerProperties, records, null, null);
            
            //populate the rules from the new map associated with Offer
            for (int j=0;j<entities.length;j++) {
                Offer offer = (Offer) records.get(j);
                OfferRule orderRule = offer.getOfferMatchRules().get(OfferRuleType.ORDER.getType());
                if (orderRule != null) {
                    entities[j].findProperty("appliesToOrderRules").setValue(orderRule.getMatchRule());
                }
                OfferRule customerRule = offer.getOfferMatchRules().get(OfferRuleType.CUSTOMER.getType());
                if (customerRule != null) {
                    entities[j].findProperty("appliesToCustomerRules").setValue(customerRule.getMatchRule());
                }
                OfferRule fgRule = offer.getOfferMatchRules().get(OfferRuleType.FULFILLMENT_GROUP.getType());
                if (fgRule != null) {
                    Property prop = new Property();
                    prop.setName("appliesToFulfillmentGroupRules");
                    prop.setValue(fgRule.getMatchRule());
                    entities[j].addProperty(prop);
                }
            }
            
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
            
            int totalRecords = helper.getTotalRecords(persistencePackage, cto, ctoConverter);
            
            DynamicResultSet response = new DynamicResultSet(null, entities, totalRecords);
            
            return response;
        } catch (Exception e) {
            LOG.error("Unable to perform fetch for entity" + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected void removeHTMLEncoding(Entity entity) {
        Property prop = entity.findProperty("targetItemCriteria.orderItemMatchRule");
        if (prop != null && prop.getValue() != null) {
            //antisamy XSS protection encodes the values in the MVEL
            //reverse this behavior
            prop.setValue(prop.getRawValue());
        }
        prop = entity.findProperty("appliesToCustomerRules");
        if (prop != null && prop.getValue() != null) {
            //antisamy XSS protection encodes the values in the MVEL
            //reverse this behavior
            prop.setValue(prop.getRawValue());
        }
        prop = entity.findProperty("appliesToOrderRules");
        if (prop != null && prop.getValue() != null) {
            //antisamy XSS protection encodes the values in the MVEL
            //reverse this behavior
            prop.setValue(prop.getRawValue());
        }
        prop = entity.findProperty("appliesToFulfillmentGroupRules");
        if (prop != null && prop.getValue() != null) {
            //antisamy XSS protection encodes the values in the MVEL
            //reverse this behavior
            prop.setValue(prop.getRawValue());
        }
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        removeHTMLEncoding(entity);
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Offer offerInstance = (Offer) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective);
            offerInstance = (Offer) helper.createPopulatedInstance(offerInstance, entity, offerProperties, false);
            
            addRule(entity, offerInstance, "appliesToOrderRules", OfferRuleType.ORDER);
            addRule(entity, offerInstance, "appliesToCustomerRules", OfferRuleType.CUSTOMER);
            addRule(entity, offerInstance, "appliesToFulfillmentGroupRules", OfferRuleType.FULFILLMENT_GROUP);
            
            dynamicEntityDao.persist(offerInstance);
            
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
            Property fgProperty = entity.findProperty("appliesToFulfillmentGroupRules");
            if (fgProperty != null) {
                offerEntity.addProperty(fgProperty);
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
        removeHTMLEncoding(entity);
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, offerProperties);
            Offer offerInstance = (Offer) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            offerInstance = (Offer) helper.createPopulatedInstance(offerInstance, entity, offerProperties, false);
            
            updateRule(entity, offerInstance, "appliesToOrderRules", OfferRuleType.ORDER);
            updateRule(entity, offerInstance, "appliesToCustomerRules", OfferRuleType.CUSTOMER);
            updateRule(entity, offerInstance, "appliesToFulfillmentGroupRules", OfferRuleType.FULFILLMENT_GROUP);
            
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
            Property fgProperty = entity.findProperty("appliesToFulfillmentGroupRules");
            if (fgProperty != null) {
                offerEntity.addProperty(fgProperty);
            }

            return offerEntity;
        } catch (Exception e) {
            LOG.error("Unable to update entity for " + entity.getType()[0], e);
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }
    
    protected void addRule(Entity entity, Offer offerInstance, String propertyName, OfferRuleType type) {
        Property ruleProperty = entity.findProperty(propertyName);
        if (ruleProperty != null && !StringUtils.isEmpty(ruleProperty.getValue())) {
            OfferRule rule = (OfferRule) entityConfiguration.createEntityInstance(OfferRule.class.getName());
            rule.setMatchRule(ruleProperty.getValue());
            offerInstance.getOfferMatchRules().put(type.getType(), rule);
        }
    }

    protected void updateRule(Entity entity, Offer offerInstance, String propertyName, OfferRuleType type) {
        Property ruleProperty = entity.findProperty(propertyName);
        if (ruleProperty != null && !StringUtils.isEmpty(ruleProperty.getValue())) {
            OfferRule rule = offerInstance.getOfferMatchRules().get(type.getType());
            if (rule == null) {
                rule = (OfferRule) entityConfiguration.createEntityInstance(OfferRule.class.getName());
            }
            rule.setMatchRule(ruleProperty.getValue());
            offerInstance.getOfferMatchRules().put(type.getType(), rule);
        } else {
            offerInstance.getOfferMatchRules().remove(type.getType());
        }
    }

}
