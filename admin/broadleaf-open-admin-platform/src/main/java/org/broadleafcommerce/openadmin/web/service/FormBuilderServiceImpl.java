/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.openadmin.web.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.SecurityServiceException;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.media.domain.MediaDto;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.LookupType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.dto.MapStructure;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.RowLevelSecurityService;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.server.service.persistence.module.DataFormatProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.web.form.component.DefaultListGridActions;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridAction;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.component.MediaField;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilderField;
import org.broadleafcommerce.openadmin.web.form.entity.CodeField;
import org.broadleafcommerce.openadmin.web.form.entity.ComboField;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTODeserializer;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.servlet.http.HttpServletRequest;


/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blFormBuilderService")
public class FormBuilderServiceImpl implements FormBuilderService {

    private static final Log LOG = LogFactory.getLog(FormBuilderServiceImpl.class);

    @Resource(name = "blAdminEntityService")
    protected AdminEntityService adminEntityService;
    
    @Resource (name = "blAdminNavigationService")
    protected AdminNavigationService navigationService;
    
    @Resource(name = "blFormBuilderExtensionManager")
    protected FormBuilderExtensionManager extensionManager;
    
    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blAdminSecurityRemoteService")
    protected SecurityVerifier adminRemoteSecurityService;
    
    @Resource(name = "blRowLevelSecurityService")
    protected RowLevelSecurityService rowLevelSecurityService;

    @Resource(name = "blMediaBuilderService")
    protected MediaBuilderService mediaBuilderService;
    
    @Resource(name = "blListGridErrorMessageExtensionManager")
    protected ListGridErrorMessageExtensionManager listGridErrorExtensionManager;

    @Resource
    protected DataFormatProvider dataFormatProvider;

    protected static final VisibilityEnum[] FORM_HIDDEN_VISIBILITIES = new VisibilityEnum[] { 
            VisibilityEnum.HIDDEN_ALL, VisibilityEnum.FORM_HIDDEN 
    };
    
    protected static final VisibilityEnum[] GRID_HIDDEN_VISIBILITIES = new VisibilityEnum[] { 
            VisibilityEnum.HIDDEN_ALL, VisibilityEnum.GRID_HIDDEN 
    };

    @Override
    public ListGrid buildMainListGrid(DynamicResultSet drs, ClassMetadata cmd, String sectionKey, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {

        List<Field> headerFields = new ArrayList<Field>();
        ListGrid.Type type = ListGrid.Type.MAIN;
        String idProperty = "id";

        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) p.getMetadata();
                
                if (SupportedFieldType.ID.equals(fmd.getFieldType())) {
                    idProperty = fmd.getName();
                }
                
                if (fmd.isProminent() != null && fmd.isProminent() 
                        && !ArrayUtils.contains(getGridHiddenVisibilities(), fmd.getVisibility())) {
                    Field hf = createHeaderField(p, fmd);
                    headerFields.add(hf);
                }
            }
        }

        ListGrid listGrid = createListGrid(cmd.getCeilingType(), headerFields, type, drs, sectionKey, 0, idProperty, sectionCrumbs);
        
        if (CollectionUtils.isNotEmpty(listGrid.getHeaderFields())) {
            // Set the first column to be able to link to the main entity
            listGrid.getHeaderFields().iterator().next().setMainEntityLink(true);
        } else {
            String message = "There are no listgrid header fields configured for the class " + cmd.getCeilingType();
            message += "Please mark some @AdminPresentation fields with 'prominent = true'";
            LOG.error(message);
        }
        
        return listGrid;
    }
    
    protected Field createHeaderField(Property p, BasicFieldMetadata fmd) {
        Field hf;
        if (fmd.getFieldType().equals(SupportedFieldType.EXPLICIT_ENUMERATION) ||
                fmd.getFieldType().equals(SupportedFieldType.BROADLEAF_ENUMERATION) ||
                fmd.getFieldType().equals(SupportedFieldType.DATA_DRIVEN_ENUMERATION) ||
                fmd.getFieldType().equals(SupportedFieldType.EMPTY_ENUMERATION)) {
            hf = new ComboField();
            ((ComboField) hf).setOptions(fmd.getEnumerationValues());
        } else {
            hf = new Field();
        }
        
        hf.withName(p.getName())
          .withFriendlyName(fmd.getFriendlyName())
          .withOrder(fmd.getGridOrder())
          .withColumnWidth(fmd.getColumnWidth())
          .withForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty())
          .withForeignKeyClass(fmd.getForeignKeyClass())
          .withOwningEntityClass(fmd.getOwningClass() != null ? fmd.getOwningClass() : fmd.getTargetClass());
        String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();
        hf.setFieldType(fieldType);
        
        return hf;
    }

    @Override
    public ListGrid buildCollectionListGrid(String containingEntityId, DynamicResultSet drs, Property field, 
            String sectionKey, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {
        FieldMetadata fmd = field.getMetadata();
        // Get the class metadata for this particular field
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(fmd, sectionCrumbs);
        if (field != null) {
            ppr.setSectionEntityField(field.getName());
        }
        ClassMetadata cmd = adminEntityService.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();

        List<Field> headerFields = new ArrayList<Field>();
        ListGrid.Type type = null;
        boolean editable = false;
        boolean sortable = false;
        boolean readOnly = false;
        boolean hideIdColumn = false;
        boolean canFilterAndSort = true;
        boolean modalSingleSelectable = false;
        boolean modalMultiSelectable = false;
        boolean selectize = false;
        String idProperty = "id";
        for (Property property : cmd.getProperties()) {
            if (property.getMetadata() instanceof BasicFieldMetadata &&
                    SupportedFieldType.ID==((BasicFieldMetadata) property.getMetadata()).getFieldType() &&
                    //make sure it's a property for this entity - not an association
                    !property.getName().contains(".")) {
                idProperty = property.getName();
                break;
            }
        }
        // Get the header fields for this list grid. Note that the header fields are different depending on the
        // kind of field this is.
        if (fmd instanceof BasicFieldMetadata) {
            readOnly = ((BasicFieldMetadata) fmd).getReadOnly();
            modalSingleSelectable = true;
            for (Property p : cmd.getProperties()) {
                if (p.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                    
                    if (SupportedFieldType.ID.equals(md.getFieldType())) {
                        idProperty = md.getName();
                    }
                    
                    if (md.isProminent() != null && md.isProminent() 
                            && !ArrayUtils.contains(getGridHiddenVisibilities(), md.getVisibility())) {
                        Field hf = createHeaderField(p, md);
                        headerFields.add(hf);
                    }
                }
            }

            type = ListGrid.Type.TO_ONE;
        } else if (fmd instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata bcm = (BasicCollectionMetadata) fmd;
            readOnly = !bcm.isMutable();
            for (Property p : cmd.getProperties()) {
                if (p.getMetadata() instanceof BasicFieldMetadata) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                    if (md.isProminent() != null && md.isProminent() 
                            && !ArrayUtils.contains(getGridHiddenVisibilities(), md.getVisibility())) {
                        Field hf = createHeaderField(p, md);
                        headerFields.add(hf);
                    }
                }
            }

            type = ListGrid.Type.BASIC;
            
            if (bcm.getAddMethodType().equals(AddMethodType.PERSIST)) {
                editable = true;
            } else if (bcm.getAddMethodType().equals(AddMethodType.SELECTIZE_LOOKUP)) {
                selectize = true;
            } else {
                modalMultiSelectable = true;
            }

            sortable = StringUtils.isNotBlank(bcm.getSortProperty());
        } else if (fmd instanceof AdornedTargetCollectionMetadata) {
            readOnly = !((AdornedTargetCollectionMetadata) fmd).isMutable();
            AdornedTargetCollectionMetadata atcmd = (AdornedTargetCollectionMetadata) fmd;

            for (String fieldName : atcmd.getGridVisibleFields()) {
                Property p = cmd.getPMap().get(fieldName);
                BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                
                Field hf = createHeaderField(p, md);
                headerFields.add(hf);
            }

            type = ListGrid.Type.ADORNED;

            if (atcmd.getMaintainedAdornedTargetFields().length > 0) {
                editable = true;
            }
            
            AdornedTargetList adornedList = (AdornedTargetList) atcmd.getPersistencePerspective()
                    .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
            sortable = StringUtils.isNotBlank(adornedList.getSortField());
        } else if (fmd instanceof MapMetadata) {
            readOnly = !((MapMetadata) fmd).isMutable();
            MapMetadata mmd = (MapMetadata) fmd;

            Property p2 = cmd.getPMap().get("key");
            BasicFieldMetadata keyMd = (BasicFieldMetadata) p2.getMetadata();
            keyMd.setFriendlyName("Key");
            Field hf = createHeaderField(p2, keyMd);
            headerFields.add(hf);
            
            if (mmd.isSimpleValue()) {
                Property valueProperty = cmd.getPMap().get("value");
                BasicFieldMetadata valueMd = (BasicFieldMetadata) valueProperty.getMetadata();
                valueMd.setFriendlyName("Value");
                hf = createHeaderField(valueProperty, valueMd);
                headerFields.add(hf);
                idProperty = "key";
                hideIdColumn = true;
            } else {
                for (Property p : cmd.getProperties()) {
                    if (p.getMetadata() instanceof BasicFieldMetadata) {
                        BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                        String valueClassName = mmd.getValueClassName();
                        if (!StringUtils.isEmpty(mmd.getToOneTargetProperty())) {
                            Class<?> clazz;
                            try {
                                clazz = Class.forName(mmd.getValueClassName());
                            } catch (ClassNotFoundException e) {
                                throw ExceptionHelper.refineException(e);
                            }
                            java.lang.reflect.Field nestedField = FieldManager.getSingleField(clazz, mmd.getToOneTargetProperty());
                            ManyToOne manyToOne = nestedField.getAnnotation(ManyToOne.class);
                            if (manyToOne != null && !manyToOne.targetEntity().getName().equals(void.class.getName())) {
                                valueClassName = manyToOne.targetEntity().getName();
                            } else {
                                OneToOne oneToOne = nestedField.getAnnotation(OneToOne.class);
                                if (oneToOne != null && !oneToOne.targetEntity().getName().equals(void.class.getName())) {
                                    valueClassName = oneToOne.targetEntity().getName();
                                }
                            }
                        }
                        if (md.getTargetClass().equals(valueClassName)) {
                            if (md.isProminent() != null && md.isProminent() 
                                    && !ArrayUtils.contains(getGridHiddenVisibilities(), md.getVisibility())) {
                                hf = createHeaderField(p, md);
                                headerFields.add(hf);
                            }
                        }
                    }
                }
            }

            type = ListGrid.Type.MAP;
            editable = true;
            canFilterAndSort = false;
        }

        String ceilingType = "";
        if (fmd instanceof BasicFieldMetadata) {
            ceilingType = cmd.getCeilingType();
        } else if (fmd instanceof CollectionMetadata) {
            ceilingType = ((CollectionMetadata) fmd).getCollectionCeilingEntity();
        }
        
        if (CollectionUtils.isEmpty(headerFields)) {
            String message = "There are no listgrid header fields configured for the class " + ceilingType + " and property '" +
            	field.getName() + "'.";
            if (type == ListGrid.Type.ADORNED || type == ListGrid.Type.ADORNED_WITH_FORM) {
                message += " Please configure 'gridVisibleFields' in your @AdminPresentationAdornedTargetCollection configuration";
            } else {
                message += " Please mark some @AdminPresentation fields with 'prominent = true'";
            }
            LOG.error(message);
        }
        
        ListGrid listGrid = createListGrid(ceilingType, headerFields, type, drs, sectionKey, fmd.getOrder(), idProperty, sectionCrumbs);
        listGrid.setSubCollectionFieldName(field.getName());
        listGrid.setFriendlyName(field.getMetadata().getFriendlyName());
        if (StringUtils.isEmpty(listGrid.getFriendlyName())) {
            listGrid.setFriendlyName(field.getName());
        }
//        listGrid.setColumn(field.getMetadata().getColumn());
        listGrid.setContainingEntityId(containingEntityId);
        listGrid.setReadOnly(readOnly);
        listGrid.setHideIdColumn(hideIdColumn);
        listGrid.setCanFilterAndSort(canFilterAndSort);

        if (editable) {
            listGrid.getRowActions().add(DefaultListGridActions.UPDATE);
        }
        if (readOnly) {
            listGrid.getRowActions().add(DefaultListGridActions.VIEW);
        }
        if (sortable) {
            listGrid.setCanFilterAndSort(false);
            listGrid.getToolbarActions().add(DefaultListGridActions.REORDER);
        }

        if (modalSingleSelectable) {
            listGrid.addModalRowAction(DefaultListGridActions.SINGLE_SELECT);
        }
        listGrid.setSelectType(ListGrid.SelectType.SINGLE_SELECT);

        if (selectize) {
            listGrid.setSelectizeUrl(buildSelectizeUrl(listGrid));
            listGrid.setSelectType(ListGrid.SelectType.SELECTIZE);
        }

        if (modalMultiSelectable) {
            listGrid.addModalRowAction(DefaultListGridActions.MULTI_SELECT);
            listGrid.setSelectType(ListGrid.SelectType.MULTI_SELECT);
        }
        listGrid.getRowActions().add(DefaultListGridActions.REMOVE);

        return listGrid;
    }

    @Override
    public List<Map<String, String>> buildSelectizeCollectionOptions(String containingEntityId, DynamicResultSet drs, Property field,
            String sectionKey, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        FieldMetadata fmd = field.getMetadata();
        // Get the class metadata for this particular field
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(fmd, sectionCrumbs);
        if (field != null) {
            ppr.setSectionEntityField(field.getName());
        }
        ClassMetadata cmd = adminEntityService.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();

        List<Field> headerFields = new ArrayList<Field>();
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                if (md.isProminent() != null && md.isProminent()
                        && !ArrayUtils.contains(getGridHiddenVisibilities(), md.getVisibility())) {
                    Field hf = createHeaderField(p, md);
                    headerFields.add(hf);
                }
            }
        }

        for (Entity e : drs.getRecords()) {
            Map<String, String> selectizeOption = new HashMap<>();
            for (Field headerField : headerFields) {
                Property p = e.findProperty(headerField.getName());
                if (p != null) {
                    selectizeOption.put("name", p.getValue());
                    break;
                }
            }
            if (e.findProperty("id") != null) {
                selectizeOption.put("id", e.findProperty("id").getValue());
            }
            result.add(selectizeOption);
        }

        return result;
    }

    private String buildSelectizeUrl(ListGrid listGrid) {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        String url = request.getContextPath();
        url += listGrid.getSectionKey();
        url += "/" + listGrid.getContainingEntityId();
        url += "/" + listGrid.getSubCollectionFieldName();
        return url;
    }

    protected ListGrid createListGrid(String className, List<Field> headerFields, ListGrid.Type type, DynamicResultSet drs, 
            String sectionKey, int order, String idProperty, List<SectionCrumb> sectionCrumbs) {
        // Create the list grid and set some basic attributes
        ListGrid listGrid = new ListGrid();
        listGrid.setClassName(className);
        listGrid.getHeaderFields().addAll(headerFields);
        listGrid.setListGridType(type);
        listGrid.setSectionCrumbs(sectionCrumbs);
        listGrid.setSectionKey(sectionKey);
        listGrid.setOrder(order);
        listGrid.setIdProperty(idProperty);
        listGrid.setStartIndex(drs.getStartIndex());
        listGrid.setTotalRecords(drs.getTotalRecords());
        listGrid.setPageSize(drs.getPageSize());
        
        String sectionIdentifier = extractSectionIdentifierFromCrumb(sectionCrumbs);
        AdminSection section = navigationService.findAdminSectionByClassAndSectionId(className, sectionIdentifier);
        if (section != null) {
            listGrid.setExternalEntitySectionKey(section.getUrl());
        }

        // For each of the entities (rows) in the list grid, we need to build the associated
        // ListGridRecord and set the required fields on the record. These fields are the same ones
        // that are used for the header fields.
        for (Entity e : drs.getRecords()) {
            ListGridRecord record = new ListGridRecord();
            record.setListGrid(listGrid);
            record.setDirty(e.isDirty());

            if (e.findProperty("hasError") != null) {
                Boolean hasError = Boolean.parseBoolean(e.findProperty("hasError").getValue());
                record.setIsError(hasError);
                ExtensionResultStatusType messageResultStatus = listGridErrorExtensionManager
                        .getProxy().determineErrorMessageForEntity(e, record);
                
                if (ExtensionResultStatusType.NOT_HANDLED.equals(messageResultStatus)) {
                    record.setErrorKey("listgrid.record.error");
                }
            }

            if (e.findProperty(idProperty) != null) {
                record.setId(e.findProperty(idProperty).getValue());
            }

            for (Field headerField : headerFields) {
                Property p = e.findProperty(headerField.getName());
                if (p != null) {
                    Field recordField = new Field().withName(headerField.getName())
                                                   .withFriendlyName(headerField.getFriendlyName())
                                                   .withOrder(p.getMetadata().getOrder());
                    
                    if (headerField instanceof ComboField) {
                        recordField.setValue(((ComboField) headerField).getOption(p.getValue()));
                        recordField.setDisplayValue(p.getDisplayValue());
                    } else {
                        recordField.setValue(p.getValue());
                        recordField.setDisplayValue(p.getDisplayValue());
                    }
                    
                    recordField.setDerived(isDerivedField(headerField, recordField, p));
                    
                    record.getFields().add(recordField);
                }
            }

            if (e.findProperty(AdminMainEntity.MAIN_ENTITY_NAME_PROPERTY) != null) {
                Field hiddenField = new Field().withName(AdminMainEntity.MAIN_ENTITY_NAME_PROPERTY);
                hiddenField.setValue(e.findProperty(AdminMainEntity.MAIN_ENTITY_NAME_PROPERTY).getValue());
                record.getHiddenFields().add(hiddenField);
            }

            if (e.findProperty(BasicPersistenceModule.ALTERNATE_ID_PROPERTY) != null) {
                record.setAltId(e.findProperty(BasicPersistenceModule.ALTERNATE_ID_PROPERTY).getValue());
            }
            
            extensionManager.getProxy().modifyListGridRecord(className, record, e);

            listGrid.getRecords().add(record);
        }

        return listGrid;
    }
    
    /**
     * Determines whether or not a particular field in a record is derived. By default this checks the {@link BasicFieldMetadata}
     * for the given Property to see if something on the backend has marked it as derived
     * 
     * @param headerField the header for this recordField
     * @param recordField the recordField being populated
     * @param p the property that relates to this recordField
     * @return whether or not this field is derived
     * @see {@link #createListGrid(String, List, org.broadleafcommerce.openadmin.web.form.component.ListGrid.Type, DynamicResultSet, String, int, String)}
     */
    protected Boolean isDerivedField(Field headerField, Field recordField, Property p) {
        return BooleanUtils.isTrue(((BasicFieldMetadata) p.getMetadata()).getIsDerived());
    }

    protected void setEntityFormFields(EntityForm ef, List<Property> properties) {
        for (Property property : properties) {
            if (property.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) property.getMetadata();
                
                
                if (!ArrayUtils.contains(getFormHiddenVisibilities(), fmd.getVisibility())) {
                    // Depending on visibility, field for the particular property is not created on the form
                    String fieldType = fmd.getFieldType() == null ? null : fmd.getFieldType().toString();
                    
                    // Create the field and set some basic attributes
                    Field f;
                    
                    if (fieldType.equals(SupportedFieldType.BROADLEAF_ENUMERATION.toString())
                            || fieldType.equals(SupportedFieldType.EXPLICIT_ENUMERATION.toString())
                            || fieldType.equals(SupportedFieldType.DATA_DRIVEN_ENUMERATION.toString())
                            || fieldType.equals(SupportedFieldType.EMPTY_ENUMERATION.toString())) {
                        // We're dealing with fields that should render as drop-downs, so set their possible values
                        f = new ComboField();
                        ((ComboField) f).setOptions(fmd.getEnumerationValues());
                    } else if (fieldType.equals(SupportedFieldType.CODE.toString())) {
                        f = new CodeField();
                    } else if (fieldType.equals(SupportedFieldType.RULE_SIMPLE.toString())
                            || fieldType.equals(SupportedFieldType.RULE_WITH_QUANTITY.toString())) {
                        // We're dealing with rule builders, so we'll create those specialized fields
                        f = new RuleBuilderField();
                        ((RuleBuilderField) f).setJsonFieldName(property.getName() + "Json");
                        ((RuleBuilderField) f).setDataWrapper(new DataWrapper());
                        ((RuleBuilderField) f).setFieldBuilder(fmd.getRuleIdentifier());
                        
                        String blankJsonString =  "{\"data\":[]}";
                        ((RuleBuilderField) f).setJson(blankJsonString);
                        DataWrapper dw = convertJsonToDataWrapper(blankJsonString);
                        if (dw != null) {
                            ((RuleBuilderField) f).setDataWrapper(dw);
                        }
                        
                        if (fieldType.equals(SupportedFieldType.RULE_SIMPLE.toString())) {
                            ((RuleBuilderField) f).setStyleClass("rule-builder-simple");
                        } else if (fieldType.equals(SupportedFieldType.RULE_WITH_QUANTITY.toString())) {
                            ((RuleBuilderField) f).setStyleClass("rule-builder-complex");
                        }
                    } else if (LookupType.DROPDOWN.equals(fmd.getLookupType())) {
                        // We're dealing with a to-one field that wants to be rendered as a dropdown instead of in a 
                        // modal, so we'll provision the combo field here. Available options will be set as part of a
                        // subsequent operation
                        f = new ComboField();
                    } else if (fieldType.equals(SupportedFieldType.MEDIA.toString())) {
                        f = new MediaField();
                    } else {
                        // Create a default field since there was no specialized handler
                        f = new Field();
                    }
                    
                    Boolean required = fmd.getRequiredOverride();
                    if (required == null) {
                        required = fmd.getRequired();
                    }

                    f.withName(property.getName())
                         .withFieldType(fieldType)
                         .withOrder(fmd.getOrder())
                         .withFriendlyName(fmd.getFriendlyName())
                         .withForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty())
                         .withForeignKeyClass(fmd.getForeignKeyClass())
                         .withOwningEntityClass(fmd.getOwningClass()!=null?fmd.getOwningClass():fmd.getInheritedFromType())
                         .withRequired(required)
                         .withReadOnly(fmd.getReadOnly())
                         .withTranslatable(fmd.getTranslatable())
                         .withAlternateOrdering((Boolean) fmd.getAdditionalMetadata().get(Field.ALTERNATE_ORDERING))
                         .withLargeEntry(fmd.isLargeEntry())
                         .withHint(fmd.getHint())
                         .withTooltip(fmd.getTooltip())
                         .withHelp(fmd.getHelpText())
                         .withTypeaheadEnabled(fmd.getEnableTypeaheadLookup());

                    String defaultValue = fmd.getDefaultValue();
                    if (StringUtils.isNotEmpty(defaultValue)) {
                        defaultValue = extractDefaultValueFromFieldData(fieldType, fmd);
                        f.withValue(defaultValue);
                    }

                    if (StringUtils.isBlank(f.getFriendlyName())) {
                        f.setFriendlyName(f.getName());
                    }

                    // Add the field to the appropriate FieldGroup
                    ef.addField(f, fmd.getGroup(), fmd.getGroupOrder(), fmd.getTab(), fmd.getTabOrder());
                }
            }
        }
    }

    @Override
    public String extractDefaultValueFromFieldData(String fieldType, BasicFieldMetadata fmd) {
        String defaultValue = fmd.getDefaultValue();
        if (fieldType.equals(SupportedFieldType.RULE_SIMPLE.toString())
                || fieldType.equals(SupportedFieldType.RULE_WITH_QUANTITY.toString())) {
            return null;
        } else if (fieldType.equals(SupportedFieldType.INTEGER.toString())) {
            try {
                Integer.parseInt(defaultValue);
            } catch (NumberFormatException  e) {
                String msg = buildMsgForDefValException(SupportedFieldType.INTEGER.toString(), fmd, defaultValue);
                LOG.warn(msg);
                return null;
            }
        } else if (fieldType.equals(SupportedFieldType.DECIMAL.toString())) {
            try {
                BigDecimal val = new BigDecimal(defaultValue);
            } catch (NumberFormatException  e) {
                String msg = buildMsgForDefValException(SupportedFieldType.DECIMAL.toString(), fmd, defaultValue);
                LOG.warn(msg);
                return null;
            }
        } else if (fieldType.equals(SupportedFieldType.BOOLEAN.toString())) {
            if (!defaultValue.toLowerCase().equals("true") && !defaultValue.toLowerCase().equals("false")) {
                String msg = buildMsgForDefValException(SupportedFieldType.BOOLEAN.toString(), fmd, defaultValue);
                LOG.warn(msg);
                return null;
            }
        } else if (fieldType.equals(SupportedFieldType.DATE.toString())) {
            DateFormat format = dataFormatProvider.getSimpleDateFormatter();
            if (defaultValue.toLowerCase().contains("today")) {
                defaultValue = format.format(new Date());
            } else {
                try {
                    Date date = format.parse(defaultValue);
                    defaultValue = format.format(date);
                } catch (ParseException e) {
                    String msg = buildMsgForDefValException(SupportedFieldType.DATE.toString(), fmd, defaultValue);
                    LOG.warn(msg);
                    return null;
                }
            }
        }
        return defaultValue;
    }

    private String buildMsgForDefValException(String type, BasicFieldMetadata fmd, String defaultValue) {
        return fmd.getTargetClass() + " : " + fmd.getName() + " - Failed to parse " + type +
                    " from DefaultValue [ " + defaultValue + " ]";
    }

    @Override
    public void removeNonApplicableFields(ClassMetadata cmd, EntityForm entityForm, String entityType) {
        for (Property p : cmd.getProperties()) {
            if (!ArrayUtils.contains(p.getMetadata().getAvailableToTypes(), entityType)) {
                entityForm.removeField(p.getName());
            }
        }
    }

    @Override
    public EntityForm createEntityForm(ClassMetadata cmd, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {
        EntityForm ef = createStandardEntityForm();
        populateEntityForm(cmd, ef, sectionCrumbs);
        return ef;
    }
    
    protected String extractSectionIdentifierFromCrumb(List<SectionCrumb> sectionCrumbs) {
        if (sectionCrumbs != null && sectionCrumbs.size() > 0) {
            return sectionCrumbs.get(0).getSectionIdentifier();
        } else {
            return null;
        }
    }

    @Override
    public void populateEntityForm(ClassMetadata cmd, EntityForm ef, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {
        ef.setCeilingEntityClassname(cmd.getCeilingType());
        
        String sectionIdentifier = extractSectionIdentifierFromCrumb(sectionCrumbs);

        AdminSection section = navigationService.findAdminSectionByClassAndSectionId(cmd.getCeilingType(),
                sectionIdentifier);
        if (section != null) {
            ef.setSectionKey(section.getUrl());
        } else {
            ef.setSectionKey(cmd.getCeilingType());
        }
        ef.setSectionCrumbsImpl(sectionCrumbs);

        setEntityFormFields(ef, Arrays.asList(cmd.getProperties()));
        
        populateDropdownToOneFields(ef, cmd);
        
        extensionManager.getProxy().modifyUnpopulatedEntityForm(ef);
    }
    
    /**
     * This method is invoked when EntityForms are created and is meant to provide a hook to add
     * additional entity form actions for implementors of Broadleaf. Broadleaf modules will typically
     * leverage {@link FormBuilderExtensionHandler#addAdditionalFormActions(EntityForm)} method.
     * @param ef
     */
    protected void addAdditionalFormActions(EntityForm ef) {
        
    }
    
    @Override
    public EntityForm createEntityForm(ClassMetadata cmd, Entity entity, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {
        EntityForm ef = createStandardEntityForm();
        populateEntityForm(cmd, entity, ef, sectionCrumbs);
        addAdditionalFormActions(ef);
        extensionManager.getProxy().addAdditionalFormActions(ef);
        return ef;
    }

    @Override
    public void populateEntityForm(ClassMetadata cmd, Entity entity, EntityForm ef, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {
        // Get the empty form with appropriate fields
        populateEntityForm(cmd, ef, sectionCrumbs);

        String idProperty = adminEntityService.getIdProperty(cmd);
        ef.setId(entity.findProperty(idProperty).getValue());
        ef.setEntityType(entity.getType()[0]);

        populateEntityFormFieldValues(cmd, entity, ef);

        Property p = entity.findProperty(BasicPersistenceModule.MAIN_ENTITY_NAME_PROPERTY);
        if (p != null) {
            ef.setMainEntityName(p.getValue());
        }
        
        extensionManager.getProxy().modifyPopulatedEntityForm(ef, entity);
    }

    @Override
    public void populateEntityFormFieldValues(ClassMetadata cmd, Entity entity, EntityForm ef) {
        // Set the appropriate property values
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata basicFM = (BasicFieldMetadata) p.getMetadata();

                Property entityProp = entity.findProperty(p.getName());
                
                boolean explicitlyShown = VisibilityEnum.FORM_EXPLICITLY_SHOWN.equals(basicFM.getVisibility());
                //always show special map fields
                if (p.getName().equals("key") || p.getName().equals("priorKey")) {
                    explicitlyShown = true;
                }
                
                if (entityProp == null && explicitlyShown) {
                    Field field = ef.findField(p.getName());
                    if (field != null) {
                        field.setValue(null);
                    }
                } else if (entityProp == null && !SupportedFieldType.PASSWORD_CONFIRM.equals(basicFM.getExplicitFieldType())) {
                    ef.removeField(p.getName());
                } else {
                    Field field = ef.findField(p.getName());
                    if (field != null) {
                        if (entityProp != null) {
                            //protect against null - can happen with password confirmation fields (i.e. admin user)
                            field.setDirty(entityProp.getIsDirty());
                        }
                        if (basicFM.getFieldType()==SupportedFieldType.RULE_SIMPLE
                                || basicFM.getFieldType()==SupportedFieldType.RULE_WITH_QUANTITY) {
                            RuleBuilderField rbf = (RuleBuilderField) field;
                            if (entity.getPMap().containsKey(rbf.getJsonFieldName())) {
                                String json = entity.getPMap().get(rbf.getJsonFieldName()).getValue();
                                rbf.setJson(json);
                                DataWrapper dw = convertJsonToDataWrapper(json);
                                if (dw != null) {
                                    rbf.setDataWrapper(dw);
                                }
                            }
                        } 
                        if (basicFM.getFieldType() == SupportedFieldType.MEDIA) {
                            field.setValue(entityProp.getValue());
                            field.setDisplayValue(entityProp.getDisplayValue());
                            MediaField mf = (MediaField) field;
                            Class<MediaDto> type = entityConfiguration.lookupEntityClass(MediaDto.class.getName(), MediaDto.class);
                            mf.setMedia(mediaBuilderService.convertJsonToMedia(entityProp.getUnHtmlEncodedValue(), type));
                        } else if (!SupportedFieldType.PASSWORD_CONFIRM.equals(basicFM.getExplicitFieldType())){
                            field.setValue(entityProp.getValue());
                            field.setDisplayValue(entityProp.getDisplayValue());
                        }
                    }
                }
            }
        }
    }

    /**
     * When using Thymeleaf, we need to convert the JSON string back to
     * a DataWrapper object because Thymeleaf escapes JSON strings.
     * Thymeleaf uses it's own object de-serializer
     * see: https://github.com/thymeleaf/thymeleaf/issues/84
     * see: http://forum.thymeleaf.org/Spring-Javascript-and-escaped-JSON-td4024739.html
     * @param json
     * @return DataWrapper
     * @throws IOException
     */
    protected DataWrapper convertJsonToDataWrapper(String json) {
        ObjectMapper mapper = new ObjectMapper();
        DataDTODeserializer dtoDeserializer = new DataDTODeserializer();
        SimpleModule module = new SimpleModule("DataDTODeserializerModule", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(DataDTO.class, dtoDeserializer);
        mapper.registerModule(module);
        try {
            return mapper.readValue(json, DataWrapper.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void populateDropdownToOneFields(EntityForm ef, ClassMetadata cmd) 
            throws ServiceException {
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) p.getMetadata();
                if (LookupType.DROPDOWN.equals(fmd.getLookupType())
                        && !ArrayUtils.contains(getFormHiddenVisibilities(), fmd.getVisibility())) {
                    // Get the records
                    PersistencePackageRequest toOnePpr = PersistencePackageRequest.standard()
                            .withCeilingEntityClassname(fmd.getForeignKeyClass());
                    Entity[] rows = adminEntityService.getRecords(toOnePpr).getDynamicResultSet().getRecords();
                    
                    // Determine the id field
                    String idProp = null;
                    ClassMetadata foreignClassMd = adminEntityService.getClassMetadata(toOnePpr).getDynamicResultSet().getClassMetaData();
                    for (Property foreignP : foreignClassMd.getProperties()) {
                        if (foreignP.getMetadata() instanceof BasicFieldMetadata) {
                            BasicFieldMetadata foreignFmd = (BasicFieldMetadata) foreignP.getMetadata();
                            if (SupportedFieldType.ID.equals(foreignFmd.getFieldType())) {
                                idProp = foreignP.getName();
                            }
                        }
                    }
                    
                    if (idProp == null) {
                        throw new RuntimeException("Could not determine ID property for " + fmd.getForeignKeyClass());
                    }
                    
                    // Determine the display field
                    String displayProp = fmd.getLookupDisplayProperty();
                    
                    // Build the options map
                    Map<String, String> options = new HashMap<String, String>();
                    for (Entity row : rows) {
                        Property prop = row.findProperty(displayProp);
                        if (prop == null) {
                            LOG.warn("Could not find displayProp [" + displayProp + "] on entity [" + 
                                    ef.getCeilingEntityClassname() + "]");
                        } else {
                            String displayValue = prop.getDisplayValue();
                            if (StringUtils.isBlank(displayValue)) {
                                displayValue = prop.getValue();
                            }
                            options.put(row.findProperty(idProp).getValue(), displayValue);
                        }
                    }
                    
                    // Set the options on the entity field
                    ComboField cf = (ComboField) ef.findField(p.getName());
                    cf.setOptions(options);
                }
            }
        }
    }

    @Override
    public EntityForm createEntityForm(ClassMetadata cmd, Entity entity, Map<String, DynamicResultSet> collectionRecords, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {
        EntityForm ef = createStandardEntityForm();
        populateEntityForm(cmd, entity, collectionRecords, ef, sectionCrumbs);
        addAdditionalFormActions(ef);
        extensionManager.getProxy().addAdditionalFormActions(ef);
        return ef;
    }
    
    @Override
    public void populateEntityForm(ClassMetadata cmd, Entity entity, Map<String, DynamicResultSet> collectionRecords, EntityForm ef, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {
        // Get the form with values for this entity
        populateEntityForm(cmd, entity, ef, sectionCrumbs);
        
        // Attach the sub-collection list grids and specialty UI support
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                continue;
            }
            
            if (!ArrayUtils.contains(p.getMetadata().getAvailableToTypes(), entity.getType()[0])) {
                continue;
            }

            if (collectionRecords != null) {
                DynamicResultSet subCollectionEntities = collectionRecords.get(p.getName());
                String containingEntityId = entity.getPMap().get(ef.getIdProperty()).getValue();
                ListGrid listGrid = buildCollectionListGrid(containingEntityId, subCollectionEntities, p, ef.getSectionKey(), sectionCrumbs);

                CollectionMetadata md = ((CollectionMetadata) p.getMetadata());
                ef.addListGrid(listGrid, md.getTab(), md.getTabOrder());
            }
        }
        
        for (ListGrid lg : ef.getAllListGrids()) {
            // We always want the add option to be the first toolbar action for consistency
            if (lg.getToolbarActions().isEmpty()) {
                lg.addToolbarAction(DefaultListGridActions.ADD);
            } else {
                lg.getToolbarActions().add(0, DefaultListGridActions.ADD);
            }
        }
        
        if (CollectionUtils.isEmpty(ef.getActions())) {
            ef.addAction(DefaultEntityFormActions.SAVE);
        }
        
        addDeleteActionIfAllowed(ef, cmd, entity);
        setReadOnlyState(ef, cmd, entity);
        
        extensionManager.getProxy().modifyDetailEntityForm(ef);
    }
    
    /**
     * Adds the {@link DefaultEntityFormActions#DELETE} if the user is allowed to delete the <b>entity</b>. The user can
     * delete an entity for the following cases:
     * <ol>
     *  <li>The user has the security to {@link EntityOperationType#DELETE} the given class name represented by
     *  the <b>entityForm</b> (determined by {@link #getSecurityClassname(EntityForm, ClassMetadata)})</li>
     *  <li>The user has the security necessary to delete the given <b>entity</b> according to the
     *  {@link RowLevelSecurityService#canDelete(Entity)}</li>
     * </ol>
     * 
     * @param entityForm the form being generated
     * @param cmd the metatadata used to build the <b>entityForm</b> for the <b>entity</b>
     * @param entity the entity being edited
     * @see {@link SecurityVerifier#securityCheck(String, EntityOperationType)}
     * @see {@link #getSecurityClassname(EntityForm, ClassMetadata)}
     * @see {@link RowLevelSecurityService#canDelete(Entity)}
     */
    protected void addDeleteActionIfAllowed(EntityForm entityForm, ClassMetadata cmd, Entity entity) {
        boolean canDelete = true;
        try {
            String securityEntityClassname = getSecurityClassname(entityForm, cmd);
            adminRemoteSecurityService.securityCheck(securityEntityClassname, EntityOperationType.REMOVE);
        } catch (ServiceException e) {
            if (e instanceof SecurityServiceException) {
                canDelete = false;
            }
        }
        
        // If I cannot update a record then I certainly cannot delete it either
        if (canDelete) {
            canDelete = rowLevelSecurityService.canUpdate(adminRemoteSecurityService.getPersistentAdminUser(), entity);
        }
        
        if (canDelete) {
            canDelete = rowLevelSecurityService.canRemove(adminRemoteSecurityService.getPersistentAdminUser(), entity);
        }
        
        if (canDelete) {
            entityForm.addAction(DefaultEntityFormActions.DELETE);
        }
    }
    
    /**
     * The given <b>entityForm</b> is marked as readonly for the following cases:
     * <ol>
     *  <li>All of the properties from <b>cmd</b> are readonly</b></li>
     *  <li>The user does not have the security to {@link EntityOperationType#UPDATE} the given class name represented by
     *  the <b>entityForm</b> (determined by {@link #getSecurityClassname(EntityForm, ClassMetadata)})</li>
     *  <li>The user does not have the security necessary to modify the given <b>entity</b> according to the
     *  {@link RowLevelSecurityService#canUpdate(Entity)}</li>
     * </ol>
     * 
     * @param entityForm the form being generated
     * @param cmd the metatadata used to build the <b>entityForm</b> for the <b>entity</b>
     * @param entity the entity being edited
     * @see {@link SecurityVerifier#securityCheck(String, EntityOperationType)}
     * @see {@link #getSecurityClassname(EntityForm, ClassMetadata)}
     * @see {@link RowLevelSecurityService#canUpdate(Entity)}
     */
    protected void setReadOnlyState(EntityForm entityForm, ClassMetadata cmd, Entity entity) {
        boolean readOnly = true;
        
        // If all of the fields are read only, we'll mark the form as such
        for (Property property : cmd.getProperties()) {
            FieldMetadata fieldMetadata = property.getMetadata();
            if (fieldMetadata instanceof BasicFieldMetadata) {
                readOnly = ((BasicFieldMetadata) fieldMetadata).getReadOnly() != null && ((BasicFieldMetadata) fieldMetadata).getReadOnly();
                if (!readOnly) {
                    break;
                }
            } else {
                readOnly = ((CollectionMetadata) fieldMetadata).isMutable();
                if (!readOnly) {
                    break;
                }
            }
        }

        if (!readOnly) {
            // If the user does not have edit permissions, we will go ahead and make the form read only to prevent confusion
            try {
                String securityEntityClassname = getSecurityClassname(entityForm, cmd);
                adminRemoteSecurityService.securityCheck(securityEntityClassname, EntityOperationType.UPDATE);
            } catch (ServiceException e) {
                if (e instanceof SecurityServiceException) {
                    readOnly = true;
                }
            }
        }
        
        // if the normal admin security service has not deemed this readonly and the all of the properties on the entity
        // are not readonly, then check the row-level security
        if (!readOnly) {
            readOnly = !rowLevelSecurityService.canUpdate(adminRemoteSecurityService.getPersistentAdminUser(), entity);
        }

        if (readOnly) {
            entityForm.setReadOnly();
        }
    }
    
    /**
     * Obtains the class name suitable for passing along to the {@link SecurityVerifier}
     * @param form
     * @param cmd
     * @return
     */
    protected String getSecurityClassname(EntityForm entityForm, ClassMetadata cmd) {
        String securityEntityClassname = entityForm.getCeilingEntityClassname();

        if (!StringUtils.isEmpty(cmd.getSecurityCeilingType())) {
            securityEntityClassname = cmd.getSecurityCeilingType();
        } else {
            if (entityForm.getDynamicFormInfos() != null) {
                for (DynamicEntityFormInfo info : entityForm.getDynamicFormInfos().values()) {
                    if (!StringUtils.isEmpty(info.getSecurityCeilingClassName())) {
                        securityEntityClassname = info.getSecurityCeilingClassName();
                        break;
                    }
                }
            }
        }
        
        return securityEntityClassname;
    }
    
    @Override
    public void populateEntityFormFields(EntityForm ef, Entity entity) {
        populateEntityFormFields(ef, entity, true, true);
    }

    @Override
    public void populateEntityFormFields(EntityForm ef, Entity entity, boolean populateType, boolean populateId) {
        if (populateId) {
            ef.setId(entity.findProperty(ef.getIdProperty()).getValue());
        }
        if (populateType) {
            ef.setEntityType(entity.getType()[0]);
        }

        for (Entry<String, Field> entry : ef.getFields().entrySet()) {
            Property entityProp = entity.findProperty(entry.getKey());
            if (entityProp != null) {
                entry.getValue().setValue(entityProp.getValue());
                entry.getValue().setDisplayValue(entityProp.getDisplayValue());
            }
        }
    }

    @Override
    public void populateAdornedEntityFormFields(EntityForm ef, Entity entity, AdornedTargetList adornedList) {
        Field field = ef.getFields().get(adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty());
        Property entityProp = entity.findProperty(ef.getIdProperty());
        field.setValue(entityProp.getValue());

        if (StringUtils.isNotBlank(adornedList.getSortField())) {
            field = ef.getFields().get(adornedList.getSortField());
            entityProp = entity.findProperty(adornedList.getSortField());
            if (field != null && entityProp != null) {
                field.setValue(entityProp.getValue());
            }
        }
    }

    @Override
    public void populateMapEntityFormFields(EntityForm ef, Entity entity) {
        Field field = ef.getFields().get("priorKey");
        Property entityProp = entity.findProperty("key");
        if (field != null && entityProp != null) {
            field.setValue(entityProp.getValue());
        }
    }

    @Override
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId)
            throws ServiceException {
        EntityForm ef = createStandardEntityForm();
        return buildAdornedListForm(adornedMd, adornedList, parentId, ef);
    }
    
    @Override
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId, EntityForm ef)
            throws ServiceException {
        ef.setEntityType(adornedList.getAdornedTargetEntityClassname());

        // Get the metadata for this adorned field
        PersistencePackageRequest request = PersistencePackageRequest.adorned()
                .withCeilingEntityClassname(adornedMd.getCollectionCeilingEntity())
                .withAdornedList(adornedList);
        ClassMetadata collectionMetadata = adminEntityService.getClassMetadata(request).getDynamicResultSet().getClassMetaData();

        // We want our entity form to only render the maintained adorned target fields
        List<Property> entityFormProperties = new ArrayList<Property>();
        for (String targetFieldName : adornedMd.getMaintainedAdornedTargetFields()) {
            Property p = collectionMetadata.getPMap().get(targetFieldName);
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                ((BasicFieldMetadata) p.getMetadata()).setVisibility(VisibilityEnum.VISIBLE_ALL);
                entityFormProperties.add(p);
            }
        }

        // Set the maintained fields on the form
        setEntityFormFields(ef, entityFormProperties);

        // Add these two additional hidden fields that are required for persistence
        Field f = new Field()
                .withName(adornedList.getLinkedObjectPath() + "." + adornedList.getLinkedIdProperty())
                .withFieldType(SupportedFieldType.HIDDEN.toString())
                .withValue(parentId);
        ef.addHiddenField(f);

        f = new Field()
                .withName(adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty())
                .withFieldType(SupportedFieldType.HIDDEN.toString())
                .withIdOverride("adornedTargetIdProperty");
        ef.addHiddenField(f);

        if (StringUtils.isNotBlank(adornedList.getSortField())) {
            f = new Field()
                    .withName(adornedList.getSortField())
                    .withFieldType(SupportedFieldType.HIDDEN.toString());
            ef.addHiddenField(f);
        }

        ef.setParentId(parentId);

        return ef;
    }


    @Override
    public EntityForm buildMapForm(MapMetadata mapMd, final MapStructure mapStructure, ClassMetadata cmd, String parentId)
            throws ServiceException {
        EntityForm ef = createStandardEntityForm();
        return buildMapForm(mapMd, mapStructure, cmd, parentId, ef);
    }
    
    @Override
    public EntityForm buildMapForm(MapMetadata mapMd, final MapStructure mapStructure, ClassMetadata cmd, String parentId, EntityForm ef)
            throws ServiceException {
        ForeignKey foreignKey = (ForeignKey) mapMd.getPersistencePerspective()
                .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
        ef.setEntityType(foreignKey.getForeignKeyClass());

        Field keyField;
        if (!mapMd.getForceFreeFormKeys()) {
            // We will use a combo field to render the key choices
            ComboField temp = new ComboField();
            temp.withName("key")
                    .withFieldType("combo_field")
                    .withFriendlyName("Key");
            if (mapMd.getKeys() != null) {
                // The keys can be explicitly set in the annotation...
                temp.setOptions(mapMd.getKeys());
            } else {
                // Or they could be based on a different entity
                PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                        .withCeilingEntityClassname(mapMd.getMapKeyOptionEntityClass());

                DynamicResultSet drs = adminEntityService.getRecords(ppr).getDynamicResultSet();
    
                for (Entity entity : drs.getRecords()) {
                    String keyValue = entity.getPMap().get(mapMd.getMapKeyOptionEntityValueField()).getValue();
                    String keyDisplayValue = entity.getPMap().get(mapMd.getMapKeyOptionEntityDisplayField()).getValue();
                    temp.putOption(keyValue, keyDisplayValue);
                }
            }
            keyField = temp;
        } else {
            keyField = new Field().withName("key")
                                .withFieldType(SupportedFieldType.STRING.toString())
                                .withFriendlyName("Key");
        }
        keyField.setRequired(true);
        ef.addMapKeyField(keyField);
        
        // Set the fields for this form
        List<Property> mapFormProperties;
        if (mapMd.isSimpleValue()) {
            ef.setIdProperty("key");
            mapFormProperties = new ArrayList<Property>();
            Property valueProp = cmd.getPMap().get("value");
            mapFormProperties.add(valueProp);
        } else {
            mapFormProperties = new ArrayList<Property>(Arrays.asList(cmd.getProperties()));
            CollectionUtils.filter(mapFormProperties, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    Property p = (Property) object;
                    return ArrayUtils.contains(p.getMetadata().getAvailableToTypes(), mapStructure.getValueClassName());
                }
            });
        }

        setEntityFormFields(ef, mapFormProperties);

        Field f = new Field()
                .withName("priorKey")
                .withFieldType(SupportedFieldType.HIDDEN.toString());
        ef.addHiddenField(f);

        ef.setParentId(parentId);

        return ef;
    }
    
    protected EntityForm createStandardEntityForm() {
        EntityForm ef = new EntityForm();
        ef.addAction(DefaultEntityFormActions.SAVE);
        return ef;
    }
    
    protected VisibilityEnum[] getGridHiddenVisibilities() {
        return FormBuilderServiceImpl.GRID_HIDDEN_VISIBILITIES;
    }
    
    protected VisibilityEnum[] getFormHiddenVisibilities() {
        return FormBuilderServiceImpl.FORM_HIDDEN_VISIBILITIES;
    }

}
