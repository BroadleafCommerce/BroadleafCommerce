package org.broadleafcommerce.cms.admin.server.service.handler;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.field.domain.FieldData;
import org.broadleafcommerce.cms.field.domain.FieldDefinition;
import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.cms.page.domain.PageTemplateImpl;
import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by jfischer
 */
public class PageTemplateCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private Log LOG = LogFactory.getLog(PageTemplateCustomPersistenceHandler.class);

    @Resource(name="blPageService")
	protected PageService pageService;

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return
            PageTemplate.class.getName().equals(ceilingEntityFullyQualifiedClassname) &&
            persistencePackage.getCustomCriteria() != null &&
            persistencePackage.getCustomCriteria().length > 0 &&
            persistencePackage.getCustomCriteria()[0].equals("constructForm");
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String pageTemplateId = persistencePackage.getCustomCriteria()[1];
            PageTemplate template = pageService.findPageTemplateById(Long.valueOf(pageTemplateId));
            ClassMetadata metadata = new ClassMetadata();
            metadata.setCeilingType(PageTemplate.class.getName());
            PolymorphicEntity entity = new PolymorphicEntity();
            entity.setName("PageTemplateImpl");
            entity.setType(PageTemplateImpl.class.getName());
            PolymorphicEntity[] entities = new PolymorphicEntity[]{entity};
            metadata.setPolymorphicEntities(entities);
            int groupCount = 1;
            int fieldCount = 0;
            List<Property> propertiesList = new ArrayList<Property>();
            List<FieldGroup> groups = template.getFieldGroups();
            for (FieldGroup group : groups) {
                List<FieldDefinition> definitions = group.getFieldDefinitions();
                for (FieldDefinition definition : definitions) {
                    Property property = new Property();
                    property.setName(definition.getName());
                    FieldMetadata fieldMetadata = new FieldMetadata();
                    property.setMetadata(fieldMetadata);
                    fieldMetadata.setFieldType(definition.getFieldType());
                    fieldMetadata.setMutable(true);
                    fieldMetadata.setInheritedFromType(PageTemplateImpl.class.getName());
                    fieldMetadata.setAvailableToTypes(new String[] {PageTemplateImpl.class.getName()});
                    fieldMetadata.setCollection(false);
                    fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
                    fieldMetadata.setLength(definition.getMaxLength());
                    FieldPresentationAttributes attributes = new FieldPresentationAttributes();
                    fieldMetadata.setPresentationAttributes(attributes);
                    attributes.setName(definition.getName());
                    attributes.setFriendlyName(definition.getFriendlyName());
                    attributes.setSecurityLevel(definition.getSecurityLevel()==null?"":definition.getSecurityLevel());
                    attributes.setOrder(fieldCount++);
                    attributes.setHidden(definition.getHiddenFlag());
                    attributes.setGroup(group.getName());
                    attributes.setGroupOrder(groupCount);
                    attributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
                    attributes.setLargeEntry(definition.getTextAreaFlag());
                    attributes.setProminent(false);
                    attributes.setColumnWidth(String.valueOf(definition.getColumnWidth()));
                    attributes.setBroadleafEnumeration("");
                    attributes.setReadOnly(false);
                    if (definition.getValidationRegEx() != null) {
                        Map<String, String> itemMap = new HashMap<String, String>();
                        itemMap.put("regularExpression", definition.getValidationRegEx());
                        itemMap.put("errorMessageKey", definition.getValidationErrorMesageKey());
                        attributes.getValidationConfigurations().put("com.smartgwt.client.widgets.form.validator.RegExpValidator", itemMap);
                    }
                    propertiesList.add(property);
                }
                groupCount++;
                fieldCount = 0;
            }
            Property property = new Property();
            property.setName("id");
            FieldMetadata fieldMetadata = new FieldMetadata();
            property.setMetadata(fieldMetadata);
            fieldMetadata.setFieldType(SupportedFieldType.ID);
            fieldMetadata.setSecondaryType(SupportedFieldType.INTEGER);
            fieldMetadata.setMutable(true);
            fieldMetadata.setInheritedFromType(PageTemplateImpl.class.getName());
            fieldMetadata.setAvailableToTypes(new String[] {PageTemplateImpl.class.getName()});
            fieldMetadata.setCollection(false);
            fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
            FieldPresentationAttributes attributes = new FieldPresentationAttributes();
            fieldMetadata.setPresentationAttributes(attributes);
            attributes.setName("id");
            attributes.setFriendlyName("ID");
            attributes.setSecurityLevel("");
            attributes.setHidden(true);
            attributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
            attributes.setLargeEntry(false);
            attributes.setProminent(false);
            attributes.setColumnWidth("*");
            attributes.setBroadleafEnumeration("");
            attributes.setReadOnly(true);
            propertiesList.add(property);

            Property[] properties = new Property[propertiesList.size()];
		    properties = propertiesList.toArray(properties);
		    Arrays.sort(properties, new Comparator<Property>() {
                public int compare(Property o1, Property o2) {
                    /*
                         * First, compare properties based on order fields
                         */
                    if (o1.getMetadata().getPresentationAttributes().getOrder() != null && o2.getMetadata().getPresentationAttributes().getOrder() != null) {
                        return o1.getMetadata().getPresentationAttributes().getOrder().compareTo(o2.getMetadata().getPresentationAttributes().getOrder());
                    } else if (o1.getMetadata().getPresentationAttributes().getOrder() != null && o2.getMetadata().getPresentationAttributes().getOrder() == null) {
                        /*
                              * Always favor fields that have an order identified
                              */
                        return -1;
                    } else if (o1.getMetadata().getPresentationAttributes().getOrder() == null && o2.getMetadata().getPresentationAttributes().getOrder() != null) {
                        /*
                              * Always favor fields that have an order identified
                              */
                        return 1;
                    } else if (o1.getMetadata().getPresentationAttributes().getFriendlyName() != null && o2.getMetadata().getPresentationAttributes().getFriendlyName() != null) {
                        return o1.getMetadata().getPresentationAttributes().getFriendlyName().compareTo(o2.getMetadata().getPresentationAttributes().getFriendlyName());
                    } else {
                        return o1.getName().compareTo(o2.getName());
                    }
                }
            });
		    metadata.setProperties(properties);
            DynamicResultSet results = new DynamicResultSet(metadata);

            return results;
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException("Unable to perform inspect for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String pageId = persistencePackage.getCustomCriteria()[1];
            Entity entity = fetchEntityBasedOnId(pageId);
            DynamicResultSet results = new DynamicResultSet(new Entity[]{entity}, 1);

            return results;
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected Entity fetchEntityBasedOnId(String pageId) throws Exception {
        Page page = (Page) pageService.findPageById(Long.valueOf(pageId));
        Map<String, PageField> pageFieldMap = page.getPageFields();
        Entity entity = new Entity();
        entity.setType(new String[]{PageTemplateImpl.class.getName()});
        List<Property> propertiesList = new ArrayList<Property>();
        for (String key : pageFieldMap.keySet()) {
            Property property = new Property();
            propertiesList.add(property);
            property.setName(key);
            PageField pageField = pageFieldMap.get(key);
            List<FieldData> fieldDataList = pageField.getFieldDataList();
            String value;
            if (!CollectionUtils.isEmpty(fieldDataList)) {
                //TODO add support for multiple values
                value = fieldDataList.get(0).getValue();
            } else {
                value = null;
            }
            property.setValue(value);
        }
        Property property = new Property();
        propertiesList.add(property);
        property.setName("id");
        property.setValue(pageId);

        entity.setProperties(propertiesList.toArray(new Property[]{}));

        return entity;
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String pageId = persistencePackage.getCustomCriteria()[1];
            Page page = (Page) pageService.findPageById(Long.valueOf(pageId));
            Map<String, PageField> pageFieldMap = page.getPageFields();
            for (Property property : persistencePackage.getEntity().getProperties()) {
                PageField pageField = pageFieldMap.get(property.getName());
                if (pageField != null) {
                    FieldData fieldData = pageField.getFieldDataList().get(0);
                    fieldData.setValue(property.getValue());
                }
            }

            return fetchEntityBasedOnId(pageId);
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }
}
