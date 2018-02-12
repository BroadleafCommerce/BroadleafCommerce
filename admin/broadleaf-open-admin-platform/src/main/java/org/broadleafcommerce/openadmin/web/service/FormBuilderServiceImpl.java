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
import org.broadleafcommerce.common.presentation.client.AdornedTargetAddMethodType;
import org.broadleafcommerce.common.presentation.client.LookupType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.common.util.FormatUtil;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.dto.MapStructure;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.dto.TabMetadata;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType;
import org.broadleafcommerce.openadmin.server.security.remote.SecurityVerifier;
import org.broadleafcommerce.openadmin.server.security.service.EntityFormModifier;
import org.broadleafcommerce.openadmin.server.security.service.EntityFormModifierConfiguration;
import org.broadleafcommerce.openadmin.server.security.service.EntityFormModifierData;
import org.broadleafcommerce.openadmin.server.security.service.EntityFormModifierDataPoint;
import org.broadleafcommerce.openadmin.server.security.service.EntityFormModifierRequest;
import org.broadleafcommerce.openadmin.server.security.service.ExceptionAwareRowLevelSecurityProvider;
import org.broadleafcommerce.openadmin.server.security.service.RowLevelSecurityService;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.web.form.component.DefaultListGridActions;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridAction;
import org.broadleafcommerce.openadmin.web.form.component.ListGridActionGroup;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.component.MediaField;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilderField;
import org.broadleafcommerce.openadmin.web.form.entity.CodeField;
import org.broadleafcommerce.openadmin.web.form.entity.ComboField;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultAdornedEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTODeserializer;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldWrapper;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

    public static final String ALTERNATE_ID_PROPERTY = "ALTERNATE_ID";

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

    @Resource(name = "blAdminNavigationService")
    protected AdminNavigationService adminNavigationService;

    protected static final VisibilityEnum[] FORM_HIDDEN_VISIBILITIES = new VisibilityEnum[] { 
            VisibilityEnum.HIDDEN_ALL, VisibilityEnum.FORM_HIDDEN
    };
    
    protected static final VisibilityEnum[] GRID_HIDDEN_VISIBILITIES = new VisibilityEnum[] { 
            VisibilityEnum.HIDDEN_ALL, VisibilityEnum.GRID_HIDDEN 
    };

    @Override
    public ListGrid buildMainListGrid(DynamicResultSet drs, ClassMetadata cmd, String sectionKey, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {

        List<Field> headerFields = new ArrayList<>();
        ListGrid.Type type = ListGrid.Type.MAIN;
        String idProperty = "id";

        FieldWrapper wrapper = new FieldWrapper();
        ArrayList<FieldDTO> defaultWrapperFields = new ArrayList<>();
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
                    defaultWrapperFields.add(constructFieldDTOFromFieldData(hf, fmd));
                }

                if (fmd.getIsFilter() != null && fmd.getIsFilter()) {
                    wrapper.getFields().add(constructFieldDTOFromFieldData(createHeaderField(p, fmd), fmd));
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

        Date c = new Date();
        String friendlyName = "listGrid" + c.getTime();
        // Set up the filter builder params
        listGrid.setJsonFieldName(friendlyName + "Json");
        listGrid.setFriendlyName(friendlyName);
        listGrid.setFieldBuilder("RULE_SIMPLE");
        if (CollectionUtils.isEmpty(wrapper.getFields())) {
            wrapper.setFields(defaultWrapperFields);
        }
        listGrid.setFieldWrapper(wrapper);
        listGrid.setHideFriendlyName(true);

        String blankJsonString =  "{\"data\":[]}";
        listGrid.setJson(blankJsonString);
        DataWrapper dw = convertJsonToDataWrapper(blankJsonString);
        if (dw != null) {
            listGrid.setDataWrapper(dw);
        }

        listGrid.addModalRowAction(DefaultListGridActions.SINGLE_SELECT);
        listGrid.setSelectType(ListGrid.SelectType.SINGLE_SELECT);

        extensionManager.getProxy().modifyListGrid(listGrid.getClassName(), listGrid);

        return listGrid;
    }

    protected FieldDTO constructFieldDTOFromFieldData(Field field, BasicFieldMetadata fmd) {
        FieldDTO fieldDTO = new FieldDTO();
        //translate the label to display
        String label = field.getFriendlyName();
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        MessageSource messages = context.getMessageSource();
        if (messages != null) {
            label = messages.getMessage(label, null, label, context.getJavaLocale());
        }
        fieldDTO.setLabel(label);

        fieldDTO.setId(field.getName());
        if (field.getFieldType().equals("STRING")) {
            fieldDTO.setOperators("blcFilterOperators_Text");
        } else if (field.getFieldType().equals("DATE")) {
            fieldDTO.setOperators("blcFilterOperators_Date");
        } else if (field.getFieldType().equals("NUMBER") || field.getFieldType().equals("MONEY") || field.getFieldType().equals("DECIMAL")) {
            fieldDTO.setOperators("blcFilterOperators_Numeric");
        } else if (field.getFieldType().equals("BOOLEAN")) {
            fieldDTO.setOperators("blcFilterOperators_Boolean");
        } else if (field.getFieldType().equals("BROADLEAF_ENUMERATION")) {
            fieldDTO.setOperators("blcFilterOperators_Enumeration");
            fieldDTO.setInput("select");
            fieldDTO.setType("string");
            String[][] enumerationValues = fmd.getEnumerationValues ();
            Map<String, String> enumMap = new HashMap<>();
            for (int i = 0; i < enumerationValues.length; i++) {
                enumMap.put(enumerationValues[i][0], enumerationValues[i][1]);
            }

            fieldDTO.setValues(new JSONObject(enumMap).toString());
        } else if (field.getFieldType().equals("ADDITIONAL_FOREIGN_KEY")) {
            fieldDTO.setOperators("blcFilterOperators_Selectize");
            fieldDTO.setType("string");

            AdminSection section = adminNavigationService.findAdminSectionByClassAndSectionId(fmd.getForeignKeyClass(), null);
            if (section != null) {
                String sectionKey = section.getUrl().substring(1);
                fieldDTO.setSelectizeSectionKey(sectionKey);
            } else {
                fieldDTO.setSelectizeSectionKey(fmd.getForeignKeyClass());
            }
        } else {
            fieldDTO.setOperators("blcFilterOperators_Text");
        }

        return fieldDTO;
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
          .withFriendlyName(StringUtils.isNotEmpty(fmd.getFriendlyName()) ? fmd.getFriendlyName() : p.getName())
          .withOrder(fmd.getGridOrder())
          .withColumnWidth(fmd.getColumnWidth())
          .withForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty())
          .withForeignKeyClass(fmd.getForeignKeyClass())
          .withForeignKeySectionPath(getAdminSectionPath(fmd.getForeignKeyClass()))
          .withOwningEntityClass(fmd.getOwningClass() != null ? fmd.getOwningClass() : fmd.getTargetClass())
          .withCanLinkToExternalEntity(fmd.getCanLinkToExternalEntity());
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

        List<Field> headerFields = new ArrayList<>();
        ListGrid.Type type = null;
        boolean editable = false;
        boolean sortable = false;
        boolean readOnly = false;
        boolean hideIdColumn = false;
        boolean canFilterAndSort = true;
        boolean modalSingleSelectable = false;
        boolean modalMultiSelectable = false;
        boolean selectize = false;
        boolean isMedia = false;
        boolean isLookup = false;
        String sortProperty = null;
        FieldWrapper wrapper = new FieldWrapper();
        ArrayList<FieldDTO> defaultWrapperFields = new ArrayList<>();


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
                        defaultWrapperFields.add(constructFieldDTOFromFieldData(hf, md));
                    }

                    if (md.getIsFilter() != null && md.getIsFilter()) {
                        Field f = createHeaderField(p, md);
                        wrapper.getFields().add(constructFieldDTOFromFieldData(f, md));
                    }
                }
            }

            type = ListGrid.Type.TO_ONE;
        } else if (fmd instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata bcm = (BasicCollectionMetadata) fmd;
            readOnly = !bcm.isMutable();

            if(AddMethodType.LOOKUP.equals(bcm.getAddMethodType())) {
                isLookup = true;
            }

            if (AddMethodType.SELECTIZE_LOOKUP.equals(bcm.getAddMethodType())) {
                Property p = cmd.getPMap().get(bcm.getSelectizeVisibleField());
                if (p != null) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();

                    Field hf = createHeaderField(p, md);
                    headerFields.add(hf);
                    wrapper.getFields().add(constructFieldDTOFromFieldData(hf, md));
                }
            } else {
                for (Property p : cmd.getProperties()) {
                    if (p.getMetadata() instanceof BasicFieldMetadata) {
                        BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();
                        if (md.isProminent() != null && md.isProminent()
                                && !ArrayUtils.contains(getGridHiddenVisibilities(), md.getVisibility())) {
                            Field hf = createHeaderField(p, md);
                            headerFields.add(hf);
                            defaultWrapperFields.add(constructFieldDTOFromFieldData(hf, md));
                        }

                        if (md.getIsFilter() != null && md.getIsFilter()) {
                            Field f = createHeaderField(p, md);
                            wrapper.getFields().add(constructFieldDTOFromFieldData(f, md));
                        }
                    }
                }
            }

            type = ListGrid.Type.BASIC;
            
            if (AddMethodType.PERSIST.equals(bcm.getAddMethodType()) || AddMethodType.PERSIST_EMPTY.equals(bcm.getAddMethodType())) {
                editable = true;
            } else if (AddMethodType.SELECTIZE_LOOKUP.equals(bcm.getAddMethodType())) {
                selectize = true;
                modalSingleSelectable = true;
            } else {
                modalSingleSelectable = true;
            }
            sortable = StringUtils.isNotBlank(bcm.getSortProperty());
            if (sortable) {
                sortProperty = bcm.getSortProperty();
            }
        } else if (fmd instanceof AdornedTargetCollectionMetadata) {
            modalSingleSelectable = true;
            readOnly = !((AdornedTargetCollectionMetadata) fmd).isMutable();
            AdornedTargetCollectionMetadata atcmd = (AdornedTargetCollectionMetadata) fmd;

            if(AdornedTargetAddMethodType.LOOKUP.equals(atcmd.getAdornedTargetAddMethodType())) {
                isLookup = true;
            }

            if (AdornedTargetAddMethodType.SELECTIZE_LOOKUP.equals(atcmd.getAdornedTargetAddMethodType())) {
                selectize = true;

                Property p = cmd.getPMap().get(atcmd.getSelectizeVisibleField());
                if (p != null) {
                    BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();

                    Field hf = createHeaderField(p, md);
                    headerFields.add(hf);
                    wrapper.getFields().add(constructFieldDTOFromFieldData(hf, md));
                }
            } else {
                for (String fieldName : atcmd.getGridVisibleFields()) {
                    Property p = cmd.getPMap().get(fieldName);
                    if (p != null) {
                        BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();

                        Field hf = createHeaderField(p, md);
                        headerFields.add(hf);
                        wrapper.getFields().add(constructFieldDTOFromFieldData(hf, md));
                    }

                }
            }

            type = ListGrid.Type.ADORNED;

            if (atcmd.getMaintainedAdornedTargetFields().length > 0) {
                editable = true;
            }
            
            AdornedTargetList adornedList = (AdornedTargetList) atcmd.getPersistencePerspective()
                    .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
            sortable = StringUtils.isNotBlank(adornedList.getSortField());
            if (sortable) {
                sortProperty = adornedList.getSortField();
            }
        } else if (fmd instanceof MapMetadata) {
            readOnly = !((MapMetadata) fmd).isMutable();
            MapMetadata mmd = (MapMetadata) fmd;

            Property p2 = cmd.getPMap().get("key");
            BasicFieldMetadata keyMd = (BasicFieldMetadata) p2.getMetadata();
            keyMd.setFriendlyName(getMapKeyFriendlyName(p2));
            Field hf = createHeaderField(p2, keyMd);
            headerFields.add(hf);
            wrapper.getFields().add(constructFieldDTOFromFieldData(hf, keyMd));

            if (mmd.isSimpleValue()) {
                Property valueProperty = cmd.getPMap().get("value");
                BasicFieldMetadata valueMd = (BasicFieldMetadata) valueProperty.getMetadata();
                valueMd.setFriendlyName("Value");
                hf = createHeaderField(valueProperty, valueMd);
                headerFields.add(hf);
                wrapper.getFields().add(constructFieldDTOFromFieldData(hf, valueMd));

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
                                defaultWrapperFields.add(constructFieldDTOFromFieldData(hf, md));

                                // Is this a media listgrid
                                if (hf.getFieldType().equals("ASSET_LOOKUP")) {
                                    isMedia = true;
                                }
                            }

                            if (md.getIsFilter() != null && md.getIsFilter()) {
                                wrapper.getFields().add(constructFieldDTOFromFieldData(hf,md));
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
                    StringUtil.sanitize(field.getName()) + "'.";
            if (selectize && (type == ListGrid.Type.ADORNED || type == ListGrid.Type.ADORNED_WITH_FORM)) {
                message += " Please configure 'selectizeVisibleField' in your @AdminPresentationAdornedTargetCollection configuration";
            } else if (type == ListGrid.Type.ADORNED || type == ListGrid.Type.ADORNED_WITH_FORM) {
                message += " Please configure 'gridVisibleFields' in your @AdminPresentationAdornedTargetCollection configuration";
            } else if (selectize && type == ListGrid.Type.BASIC) {
                message += " Please configure 'selectizeVisibleField' in your @AdminPresentationCollection configuration";
            } else {
                message += " Please mark some @AdminPresentation fields with 'prominent = true'";
            }
            LOG.error(message);
        }

        ListGrid listGrid = createListGrid(ceilingType, headerFields, type, drs, sectionKey, fmd.getOrder(), idProperty, sectionCrumbs, sortProperty);
        listGrid.setSubCollectionFieldName(field.getName());
        listGrid.setFriendlyName(field.getMetadata().getFriendlyName());
        if (StringUtils.isEmpty(listGrid.getFriendlyName())) {
            listGrid.setFriendlyName(field.getName());
        }
        listGrid.setContainingEntityId(containingEntityId);
        listGrid.setIsReadOnly(readOnly);
        listGrid.setHideIdColumn(hideIdColumn);
        listGrid.setCanFilterAndSort(canFilterAndSort);

        // Set up the filter builder params
        Date c = new Date();
        String friendlyName = field.getMetadata().getFriendlyName();
        String jsonFriendlyName = friendlyName.replaceAll(" ", "_");
        listGrid.setJsonFieldName(jsonFriendlyName + c.getTime() + "Json");
        listGrid.setFriendlyName(friendlyName);
        listGrid.setFieldBuilder("RULE_SIMPLE");
        if (CollectionUtils.isEmpty(wrapper.getFields())) {
            wrapper.setFields(defaultWrapperFields);
        }
        listGrid.setFieldWrapper(wrapper);

        String blankJsonString =  "{\"data\":[]}";
        listGrid.setJson(blankJsonString);
        DataWrapper dw = convertJsonToDataWrapper(blankJsonString);
        if (dw != null) {
            listGrid.setDataWrapper(dw);
        }

        if (editable) {
            listGrid.getRowActions().add(DefaultListGridActions.UPDATE);
        }
        if (readOnly) {
            listGrid.getRowActions().add(DefaultListGridActions.VIEW);
        }
        if (sortable) {
            listGrid.setCanFilterAndSort(false);
            listGrid.setIsSortable(true);
        }

        if (modalSingleSelectable) {
            if (readOnly) {
                listGrid.addModalRowAction(DefaultListGridActions.SINGLE_SELECT.clone().withForListGridReadOnly(true));
            } else {
                listGrid.addModalRowAction(DefaultListGridActions.SINGLE_SELECT);

            }
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

        if (fmd.getManualFetch()) {
            listGrid.setManualFetch(true);
            listGrid.getToolbarActions().add(DefaultListGridActions.MANUAL_FETCH);
        }
        if (isMedia) {
            listGrid.setListGridType(ListGrid.Type.ASSET_GRID);
        }

        extensionManager.getProxy().modifyListGrid(listGrid.getClassName(), listGrid);

        //If someone has replaced RowLevelSecurityService, check here to make sure the replacement implements the expected interface
        if (rowLevelSecurityService instanceof ExceptionAwareRowLevelSecurityProvider) {
            EntityFormModifierConfiguration entityFormModifierConfiguration = ((ExceptionAwareRowLevelSecurityProvider) rowLevelSecurityService).getUpdateDenialExceptions();
            for (EntityFormModifierData<EntityFormModifierDataPoint> data : entityFormModifierConfiguration.getData()) {
                for (EntityFormModifier modifier : entityFormModifierConfiguration.getModifier()) {
                    if (modifier.isQualified(data.getModifierType())) {
                        modifier.modifyListGrid(new EntityFormModifierRequest()
                                .withListGrid(listGrid)
                                .withConfiguration(data)
                                .withCurrentUser(adminRemoteSecurityService.getPersistentAdminUser())
                                .withRowLevelSecurityService(rowLevelSecurityService));
                    }
                }
            }
        }
        return listGrid;
    }

    protected String getMapKeyFriendlyName(Property property) {
        String friendlyNameFromMetadata = property.getMetadata().getFriendlyName();

        return (friendlyNameFromMetadata == null) ? "Key" : friendlyNameFromMetadata;
    }

    @Override
    public Map<String, Object> buildSelectizeCollectionInfo(String containingEntityId, DynamicResultSet drs, Property field,
        String sectionKey, List<SectionCrumb> sectionCrumbs)
            throws ServiceException {
        FieldMetadata fmd = field.getMetadata();
        // Get the class metadata for this particular field
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(fmd, sectionCrumbs);
        if (field != null) {
            ppr.setSectionEntityField(field.getName());
        }
        ClassMetadata cmd = adminEntityService.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();

        Map<String, Object> result = constructSelectizeOptionMap(drs, cmd);

        AdornedTargetList adornedList = ppr.getAdornedList();
        if (adornedList != null && adornedList.getLinkedObjectPath() != null
            && adornedList.getTargetObjectPath() != null && adornedList.getLinkedIdProperty() != null
            && adornedList.getTargetIdProperty() != null) {
            result.put("linkedObjectPath", adornedList.getLinkedObjectPath() + "." + adornedList.getLinkedIdProperty());
            result.put("linkedObjectId", containingEntityId);
            result.put("targetObjectPath", adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty());
        }

        return result;
    }

    @Override
    public Map<String, Object> constructSelectizeOptionMap(DynamicResultSet drs, ClassMetadata cmd) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> options = new ArrayList<>();

        List<Field> headerFields = new ArrayList<>();
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
            if (e.findProperty(ALTERNATE_ID_PROPERTY) != null) {
                selectizeOption.put("alternateId", e.findProperty(ALTERNATE_ID_PROPERTY).getValue());
            }
            options.add(selectizeOption);
        }
        result.put("options", options);

        return result;
    }

    protected String buildSelectizeUrl(ListGrid listGrid) {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        String url = request.getContextPath();
        url += url.endsWith("/") || listGrid.getSectionKey().startsWith("/") ? listGrid.getSectionKey() : "/" + listGrid.getSectionKey();
        url += "/" + listGrid.getContainingEntityId();
        url += "/" + listGrid.getSubCollectionFieldName();
        return url;
    }

    /**
     * @deprecated use {@link #createListGrid(String, List, ListGrid.Type, DynamicResultSet, String, Integer, String, List, String)}
     * @param className
     * @param headerFields
     * @param type
     * @param drs
     * @param sectionKey
     * @param order
     * @param idProperty
     * @param sectionCrumbs
     * @return
     */
    @Deprecated
    protected ListGrid createListGrid(String className, List<Field> headerFields, ListGrid.Type type, DynamicResultSet drs, 
            String sectionKey, int order, String idProperty, List<SectionCrumb> sectionCrumbs) {
       return createListGrid(className,headerFields,type,drs,sectionKey,order,idProperty,sectionCrumbs,null);
    }

    /**
     * Populate a ListGrid with ListGridRecords.
     *
     * @param className
     * @param headerFields
     * @param type
     * @param drs
     * @param sectionKey
     * @param order
     * @param idProperty
     * @param sectionCrumbs
     * @param sortPropery
     * @return
     */
    protected ListGrid createListGrid(String className, List<Field> headerFields, ListGrid.Type type, DynamicResultSet drs,
            String sectionKey, Integer order, String idProperty, List<SectionCrumb> sectionCrumbs, String sortPropery) {
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

        // format date list grid cells
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, Y @ hh:mma");
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(new String[] { "am", "pm" });
        formatter.setDateFormatSymbols(symbols);

        // For each of the entities (rows) in the list grid, we need to build the associated
        // ListGridRecord and set the required fields on the record. These fields are the same ones
        // that are used for the header fields.
        for (Entity e : drs.getRecords()) {
            ListGridRecord record = new ListGridRecord();
            record.setListGrid(listGrid);
            record.setDirty(e.isDirty());
            record.setEntity(e);
            if (StringUtils.isNotBlank(sortPropery) && e.findProperty(sortPropery) != null) {
                Property sort = e.findProperty(sortPropery);
                record.setDisplayOrder(sort.getValue());
            }
            if (e.findProperty("hasError") != null) {
                Boolean hasError = Boolean.parseBoolean(e.findProperty("hasError").getValue());
                record.setIsError(hasError);
                if (hasError) {
                    ExtensionResultStatusType messageResultStatus = listGridErrorExtensionManager
                            .getProxy().determineErrorMessageForEntity(e, record);

                    if (ExtensionResultStatusType.NOT_HANDLED.equals(messageResultStatus)) {
                        record.setErrorKey("listgrid.record.error");
                    }
                }
            }

            if (e.findProperty("progressStatus") != null) {
                ExtensionResultStatusType messageResultStatus = listGridErrorExtensionManager
                        .getProxy().determineStatusMessageForEntity(e, record);
                if (ExtensionResultStatusType.NOT_HANDLED.equals(messageResultStatus)) {
                    record.setStatus(e.findProperty("progressStatus").getValue());
                    record.setStatusCssClass("listgrid.record.status");
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
                        if (headerField.getFieldType().equals("DATE")) {
                            try {
                                Date date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse(p.getValue());
                                String newValue = formatter.format(date);
                                recordField.setValue(newValue);
                            } catch (Exception ex) {
                                recordField.setValue(p.getValue());
                            }
                        } else {
                            recordField.setValue(p.getValue());
                        }
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

        if (drs.getFirstId() != null) {
            listGrid.setFirstId(drs.getFirstId());
        }
        if (drs.getLastId() != null) {
            listGrid.setLastId(drs.getLastId());
        }
        if (drs.getUpperCount() != null) {
            listGrid.setUpperCount(drs.getUpperCount());
        }
        if (drs.getLowerCount() != null) {
            listGrid.setLowerCount(drs.getLowerCount());
        }
        if (drs.getFetchType() != null) {
            listGrid.setFetchType(drs.getFetchType().toString());
        }
        if (drs.getTotalCountLessThanPageSize() != null) {
            listGrid.setTotalCountLessThanPageSize(drs.getTotalCountLessThanPageSize());
        }
        if (drs.getPromptSearch() != null) {
            listGrid.setPromptSearch(drs.getPromptSearch());
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
     * @see {@link #createListGrid(String, List, ListGrid.Type, DynamicResultSet, String, Integer, String, List, String)}
     */
    protected Boolean isDerivedField(Field headerField, Field recordField, Property p) {
        return BooleanUtils.isTrue(((BasicFieldMetadata) p.getMetadata()).getIsDerived());
    }

    protected void setEntityFormFields(ClassMetadata cmd, EntityForm ef, List<Property> properties) {
        List<Field> homelessFields = new ArrayList<>();
        List<Field> fieldsWithAssociations = new ArrayList<>();

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
                        if (fmd.getHideEnumerationIfEmpty() != null && fmd.getHideEnumerationIfEmpty().booleanValue()
                                && ((ComboField) f).getOptions().size() == 0) {
                            f.setIsVisible(false);
                        }
                    } else if (fieldType.equals(SupportedFieldType.CODE.toString())) {
                        f = new CodeField();
                    } else if (fieldType.equals(SupportedFieldType.RULE_SIMPLE.toString())
                            || fieldType.equals(SupportedFieldType.RULE_SIMPLE_TIME.toString())
                            || fieldType.equals(SupportedFieldType.RULE_WITH_QUANTITY.toString())) {
                        // We're dealing with rule builders, so we'll create those specialized fields
                        f = new RuleBuilderField();
                        ((RuleBuilderField) f).setJsonFieldName(property.getName() + "Json");
                        ((RuleBuilderField) f).setDataWrapper(new DataWrapper());
                        ((RuleBuilderField) f).setFieldBuilder(fmd.getRuleIdentifier());
                        ((RuleBuilderField) f).setDisplayType(fmd.getDisplayType().toString());

                        String blankJsonString =  "{\"data\":[]}";
                        ((RuleBuilderField) f).setJson(blankJsonString);
                        DataWrapper dw = convertJsonToDataWrapper(blankJsonString);
                        if (dw != null) {
                            ((RuleBuilderField) f).setDataWrapper(dw);
                        }
                        
                        if (fieldType.equals(SupportedFieldType.RULE_SIMPLE.toString())) {
                            ((RuleBuilderField) f).setRuleType("rule-builder-simple");
                        } else if (fieldType.equals(SupportedFieldType.RULE_WITH_QUANTITY.toString())) {
                            ((RuleBuilderField) f).setRuleType("rule-builder-with-quantity");
                        } else if (fieldType.equals(SupportedFieldType.RULE_SIMPLE_TIME.toString())) {
                            ((RuleBuilderField) f).setRuleType("rule-builder-simple-time");
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

                    Boolean allowNoValueEnum = fmd.getAllowNoValueEnumOption();
                    if (allowNoValueEnum != null) {
                        f.setAllowNoValueEnumOption(allowNoValueEnum);
                    }

                    f.withName(property.getName())
                     .withFieldType(fieldType)
                     .withFieldComponentRenderer(fmd.getFieldComponentRenderer()==null?null:fmd.getFieldComponentRenderer().toString())
                     .withGridFieldComponentRenderer(fmd.getGridFieldComponentRenderer()==null?null:fmd.getGridFieldComponentRenderer().toString())
                     .withOrder(fmd.getOrder())
                     .withFriendlyName(fmd.getFriendlyName())
                     .withForeignKeyDisplayValueProperty(fmd.getForeignKeyDisplayValueProperty())
                     .withForeignKeyClass(fmd.getForeignKeyClass())
                     .withForeignKeySectionPath(getAdminSectionPath(fmd.getForeignKeyClass()))
                     .withOwningEntityClass(fmd.getOwningClass()!=null?fmd.getOwningClass():fmd.getInheritedFromType())
                     .withRequired(required)
                     .withReadOnly(fmd.getReadOnly())
                     .withTranslatable(fmd.getTranslatable())
                     .withAlternateOrdering((Boolean) fmd.getAdditionalMetadata().get(Field.ALTERNATE_ORDERING))
                     .withLargeEntry(fmd.isLargeEntry())
                     .withHint(fmd.getHint())
                     .withTooltip(fmd.getTooltip())
                     .withHelp(fmd.getHelpText())
                     .withTypeaheadEnabled(fmd.getEnableTypeaheadLookup())
                     .withCanLinkToExternalEntity(fmd.getCanLinkToExternalEntity())
                     .withAssociatedFieldName(fmd.getAssociatedFieldName());

                    String defaultValue = fmd.getDefaultValue();
                    if (defaultValue != null) {
                        defaultValue = extractDefaultValueFromFieldData(fieldType, fmd);
                        f.withValue(defaultValue);
                    }

                    if (StringUtils.isBlank(f.getFriendlyName())) {
                        f.setFriendlyName(f.getName());
                    }

                    // If is form hidden, set visible to false
                    if (VisibilityEnum.FORM_EXPLICITLY_HIDDEN.equals(fmd.getVisibility())) {
                        f.setIsVisible(false);
                    }

                    if (VisibilityEnum.VISIBLE_ALL.equals(fmd.getVisibility())) {
                        f.setIsVisible(true);
                    }

                    // Add the field to the appropriate FieldGroup
                    if (fmd.getGroup() == null) {
                        homelessFields.add(f);
                    } else {
                        ef.addField(cmd, f, fmd.getGroup(), fmd.getGroupOrder(), fmd.getTab(), fmd.getTabOrder());
                    }

                    if (StringUtils.isNotEmpty(fmd.getAssociatedFieldName())) {
                        fieldsWithAssociations.add(f);
                    }
                }
            }
        }

        for (Field f : homelessFields) {
            ef.addField(cmd, f, null, null, null, null);
        }

        for (Field f : fieldsWithAssociations) {
            Field associatedField = findAssociatedField(ef, f);
            if (associatedField != null) {
                associatedField.setShouldRender(false);
                f.setAssociatedFieldName(associatedField.getName());
            } else {
                f.setAssociatedFieldName(null);
            }
        }
    }

    private Field findAssociatedField(EntityForm ef, Field f) {
        // Try on the parent object
        Field associatedField = ef.findField(f.getAssociatedFieldName());

        if (associatedField == null) {
            // Check the field's path
            String[] fieldPathParts = f.getName().split("\\.");
            String testPath = "";

            for (String path : fieldPathParts) {
                testPath += path + ".";

                associatedField = ef.findField(testPath + f.getAssociatedFieldName());
                if (associatedField != null) {
                    break;
                }
            }
        }
        return associatedField;
    }

    /**
     * This method gets the {@link AdminSection} for the given foreignKeyClass parameter. If none exists,
     * it returns the foreignKeyClass.
     *
     * @param foreignKeyClass the {@link String} class name
     * @return the admin section pathname
     */
    protected String getAdminSectionPath(String foreignKeyClass) {
        if (foreignKeyClass != null) {
            AdminSection foreignKeySection = adminNavigationService.findAdminSectionByClassAndSectionId(foreignKeyClass, null);
            return foreignKeySection != null ? foreignKeySection.getUrl() : foreignKeyClass;
        }
        return null;
    }

    /**
     * NOTE: This method will attempt to merge tabs if the unprocessed {@link TabMetadata#getTabName()} is equal to
     *  the processed value of another tab.
     *
     * For example, if {@link TabMetadata#getTabName()} is "Example", there is a another tab where
     *  {@link TabMetadata#getTabName()} is "Example_Tab", and a message property where 'Example_Tab=Example',
     *  then the tabs should be merged together, so that we do not end up rendering multiple "Example" tabs.
     */
    protected void setEntityFormTabsAndGroups(EntityForm ef, Map<String, TabMetadata> tabMetadataMap) {
        if (tabMetadataMap != null) {
            Set<String> tabMetadataKeySet = tabMetadataMap.keySet();
            for (String tabKey : tabMetadataKeySet) {
                TabMetadata tabMetadata = tabMetadataMap.get(tabKey);
                String unprocessedTabName = getUnprocessedNameOfMatchingTab(tabMetadata, tabMetadataKeySet);

                if (foundMatchingTab(unprocessedTabName)) {
                    if (!tabExists(ef, unprocessedTabName)) {
                        TabMetadata originalTabMetadata = tabMetadataMap.get(unprocessedTabName);
                        unprocessedTabName = ef.addTabFromTabMetadata(originalTabMetadata);
                    }
                } else {
                    if (tabExists(ef, tabKey)) {
                        unprocessedTabName = tabKey;
                    } else {
                        unprocessedTabName = ef.addTabFromTabMetadata(tabMetadata);
                    }
                }

                Set<String> groupMetadataKeySet = tabMetadata.getGroupMetadata().keySet();
                for (String groupKey : groupMetadataKeySet) {
                    ef.addGroupFromGroupMetadata(tabMetadata.getGroupMetadata().get(groupKey), unprocessedTabName);
                }
            }
        }
    }

    /**
     * Search for any other tab on the target entity that has the same display value
     *  as the value provided tab name.
     *
     * This assumes that the {@link TabMetadata#getTabName()} is in the form of a processed message property.
     *  For example, if {@link TabMetadata#getTabName()} is "Example", there is a tabKey from the tabMetadataKeySet
     *  that is "Example_Tab", and a message property where 'Example_Tab=Example', then this method should return
     *  "Example_Tab" which will end up causing the tabs to merge together.
     *
     * If a tab with the same display value cannot be found, then we should return null to indicate that there
     *  is no matching tab with the same processed tabName value.
     */
    protected String getUnprocessedNameOfMatchingTab(TabMetadata tabMetadata, Set<String> tabMetadataKeySet) {
        for (String tabKey : tabMetadataKeySet) {
            String tabName = tabMetadata.getTabName();

            if (processedTabKeyMatchesTabName(tabName, tabKey)) {
                return tabKey;
            }
        }

        return null;
    }

    protected boolean processedTabKeyMatchesTabName(String tabName, String candidateTabKey) {
        try {
            String processedCandidateTabKey = BLCMessageUtils.getMessage(candidateTabKey);
            boolean candidateTabKeyWasProcessed = !StringUtils.equals(candidateTabKey, processedCandidateTabKey);

            return candidateTabKeyWasProcessed && StringUtils.equalsIgnoreCase(tabName, processedCandidateTabKey);
        } catch (NoSuchMessageException e) {
            LOG.debug("No such message exists for " + candidateTabKey, e);

            return false;
        }
    }

    protected boolean foundMatchingTab(String unprocessedTabName) {
        return (unprocessedTabName != null);
    }

    protected boolean tabExists(EntityForm ef, String tabKey) {
        return (ef.findTab(tabKey) != null);
    }

    @Override
    public String extractDefaultValueFromFieldData(String fieldType, BasicFieldMetadata fmd) {
        String defaultValue = fmd.getDefaultValue();
        if (fieldType.equals(SupportedFieldType.RULE_SIMPLE.toString())
                || fieldType.equals(SupportedFieldType.RULE_SIMPLE_TIME.toString())
                || fieldType.equals(SupportedFieldType.RULE_WITH_QUANTITY.toString())) {
            return null;
        } else if (fieldType.equals(SupportedFieldType.INTEGER.toString())) {
            try {
                Integer.parseInt(defaultValue);
            } catch (NumberFormatException  e) {
                String msg = buildMsgForDefValException(SupportedFieldType.INTEGER.toString(), fmd, defaultValue);
                LOG.debug(msg);
                return null;
            }
        } else if (fieldType.equals(SupportedFieldType.DECIMAL.toString())
                || fieldType.equals(SupportedFieldType.MONEY.toString())) {
            try {
                BigDecimal val = new BigDecimal(defaultValue);
            } catch (NumberFormatException  e) {
                String msg = buildMsgForDefValException(fieldType.toString(), fmd, defaultValue);
                LOG.debug(msg);
                return null;
            }
        } else if (fieldType.equals(SupportedFieldType.BOOLEAN.toString())) {
            if (!defaultValue.toLowerCase().equals("true") && !defaultValue.toLowerCase().equals("false")
                    && !defaultValue.toUpperCase().equals("Y") && !defaultValue.toUpperCase().equals("N")) {
                String msg = buildMsgForDefValException(SupportedFieldType.BOOLEAN.toString(), fmd, defaultValue);
                LOG.debug(msg);
                return null;
            }
        } else if (fieldType.equals(SupportedFieldType.DATE.toString())) {
            DateFormat format = FormatUtil.getDateFormat();
            if (defaultValue.toLowerCase().contains("today")) {
                defaultValue = format.format(new Date());
            } else {
                try {
                    Date date = format.parse(defaultValue);
                    defaultValue = format.format(date);
                } catch (ParseException e) {
                    String msg = buildMsgForDefValException(SupportedFieldType.DATE.toString(), fmd, defaultValue);
                    LOG.debug(msg);
                    return null;
                }
            }
        }
        return defaultValue;
    }

    protected String buildMsgForDefValException(String type, BasicFieldMetadata fmd, String defaultValue) {
        return StringUtil.sanitize(fmd.getTargetClass()) + " : " + StringUtil.sanitize(fmd.getName()) + " - Failed to parse " 
                + StringUtil.sanitize(type) + " from DefaultValue [ " + StringUtil.sanitize(defaultValue) + " ]";
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

        setEntityFormTabsAndGroups(ef, cmd.getTabAndGroupMetadata());

        setEntityFormFields(cmd, ef, Arrays.asList(cmd.getProperties()));
        
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

    protected void setVisibilityBasedOnShowIfFieldEquals(ClassMetadata cmd, Entity entity, EntityForm ef) {
        for (Property p : cmd.getProperties()) {
            FieldMetadata fmd = p.getMetadata();

            if (shouldHideField(fmd, entity)) {
                if (fmd instanceof CollectionMetadata) {
                    ef.removeListGrid(p.getName());
                } else {
                    ef.removeField(p.getName());
                }
            }
        }
    }

    protected boolean shouldHideField(FieldMetadata fmd, Entity entity) {
        if (fmd == null || fmd.getShowIfFieldEquals() == null) {
            return false;
        }

        for (String property : fmd.getShowIfFieldEquals().keySet()) {
            List<String> values = fmd.getShowIfFieldEquals().get(property);
            Property entityProp = entity.findProperty(property);

            if (entityProp == null || values.contains(entityProp.getValue())) {
                return false;
            }
        }
        return true;
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
                                || basicFM.getFieldType()==SupportedFieldType.RULE_SIMPLE_TIME
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
                        } else if (!SupportedFieldType.PASSWORD_CONFIRM.equals(basicFM.getExplicitFieldType())) {
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
                    Map<String, String> options = new HashMap<>();
                    for (Entity row : rows) {
                        Property prop = row.findProperty(displayProp);
                        if (prop == null) {
                            LOG.warn("Could not find displayProp [" + StringUtil.sanitize(displayProp) + "] on entity [" + 
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
                if (md instanceof BasicCollectionMetadata) {
                    PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md, sectionCrumbs);
                    ClassMetadata collectionCmd = adminEntityService.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
                    if (collectionCmd.getPolymorphicEntities().getChildren().length != 0) {
                        List<ClassTree> entityTypes = collectionCmd.getPolymorphicEntities().getCollapsedClassTrees();
                        ListGridActionGroup actionGroup = new ListGridActionGroup().withName("Add");
                        for (ClassTree entityType : entityTypes) {
                            ListGridAction ADD = new ListGridAction(ListGridAction.ADD)
                                    .withButtonClass(AddMethodType.PERSIST_EMPTY==
                                            ((BasicCollectionMetadata) md).getAddMethodType()?"sub-list-grid-add-empty":"sub-list-grid-add")
                                    .withActionTargetEntity(entityType.getFullyQualifiedClassname())
                                    .withUrlPostfix("/add")
                                    .withIconClass("fa fa-plus")
                                    .withDisplayText(BLCMessageUtils.getMessage(entityType.getFriendlyName()));
                            actionGroup.getListGridActions().add(0, ADD);
                        }
                        listGrid.addToolbarActionGroup(actionGroup);
                    } else {
                        listGrid.getToolbarActions().add(0, AddMethodType.PERSIST_EMPTY==
                                ((BasicCollectionMetadata) md).getAddMethodType()?DefaultListGridActions.ADD_EMPTY:DefaultListGridActions.ADD);
                    }
                } else {
                    listGrid.getToolbarActions().add(0, DefaultListGridActions.ADD);
                }

                if (subCollectionEntities.getUnselectedTabMetadata().get(md.getTab())!=null) {
                    ef.addListGrid(cmd, listGrid, md.getTab(), md.getTabOrder(), md.getGroup(), true);
                } else {
                    ef.addListGrid(cmd, listGrid, md.getTab(), md.getTabOrder(), md.getGroup(), false);
                }
            }
        }

        if (CollectionUtils.isEmpty(ef.getActions())) {
            ef.addAction(DefaultEntityFormActions.SAVE);
        }
        
        addDeleteActionIfAllowed(ef, cmd, entity);
        setReadOnlyState(ef, cmd, entity);

        // check for fields that should be hidden based on annotations
        setVisibilityBasedOnShowIfFieldEquals(cmd, entity, ef);

        extensionManager.getProxy().modifyDetailEntityForm(ef);
    }
    
    /**
     * Adds the {@link DefaultEntityFormActions#DELETE} if the user is allowed to delete the <b>entity</b>. The user can
     * delete an entity for the following cases:
     * <ol>
     *  <li>The user has the security to {@link EntityOperationType#REMOVE} the given class name represented by
     *  the <b>entityForm</b> (determined by {@link #getSecurityClassname(EntityForm, ClassMetadata)})</li>
     *  <li>The user has the security necessary to delete the given <b>entity</b> according to the
     *  {@link RowLevelSecurityService#canRemove(AdminUser, Entity)}</li>
     * </ol>
     * 
     * @param entityForm the form being generated
     * @param cmd the metatadata used to build the <b>entityForm</b> for the <b>entity</b>
     * @param entity the entity being edited
     * @see {@link SecurityVerifier#securityCheck(String, EntityOperationType)}
     * @see {@link #getSecurityClassname(EntityForm, ClassMetadata)}
     * @see {@link RowLevelSecurityService#canRemove(AdminUser, Entity)}
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
     *  {@link RowLevelSecurityService#canUpdate(AdminUser, Entity)}</li>
     * </ol>
     * 
     * @param entityForm the form being generated
     * @param cmd the metatadata used to build the <b>entityForm</b> for the <b>entity</b>
     * @param entity the entity being edited
     * @see {@link SecurityVerifier#securityCheck(String, EntityOperationType)}
     * @see {@link #getSecurityClassname(EntityForm, ClassMetadata)}
     * @see {@link RowLevelSecurityService#canUpdate(AdminUser, Entity)}
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
            //If someone has replaced RowLevelSecurityService, check here to make sure the replacement implements the expected interface
            if (rowLevelSecurityService instanceof ExceptionAwareRowLevelSecurityProvider) {
                EntityFormModifierConfiguration entityFormModifierConfiguration = ((ExceptionAwareRowLevelSecurityProvider) rowLevelSecurityService).getUpdateDenialExceptions();
                for (EntityFormModifierData<EntityFormModifierDataPoint> data : entityFormModifierConfiguration.getData()) {
                    for (EntityFormModifier modifier : entityFormModifierConfiguration.getModifier()) {
                        if (modifier.isQualified(data.getModifierType())) {
                            modifier.modifyEntityForm(new EntityFormModifierRequest()
                                    .withEntityForm(entityForm)
                                    .withConfiguration(data)
                                    .withCurrentUser(adminRemoteSecurityService.getPersistentAdminUser())
                                    .withEntity(entity)
                                    .withRowLevelSecurityService(rowLevelSecurityService));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Obtains the class name suitable for passing along to the {@link SecurityVerifier}
     * @param entityForm
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
        Field field = ef.findField(adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty());
        Property entityProp = entity.findProperty(ef.getIdProperty());
        field.setValue(entityProp.getValue());

        if (StringUtils.isNotBlank(adornedList.getSortField())) {
            field = ef.findField(adornedList.getSortField());
            entityProp = entity.findProperty(adornedList.getSortField());
            if (field != null && entityProp != null) {
                field.setValue(entityProp.getValue());
            }
        }
    }

    @Override
    public void populateMapEntityFormFields(EntityForm ef, Entity entity) {
        Field field = ef.findField("priorKey");
        Property entityProp = entity.findProperty("key");
        if (field != null && entityProp != null) {
            field.setValue(entityProp.getValue());
        }
    }

    @Override
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId, boolean isViewCollectionItem, List<SectionCrumb> sectionCrumbs, boolean isAdd)
            throws ServiceException {
        EntityForm ef = createStandardAdornedEntityForm();
        return buildAdornedListForm(adornedMd, adornedList, parentId, isViewCollectionItem, ef, sectionCrumbs, isAdd);
    }
    
    @Override
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId, boolean isViewCollectionItem, EntityForm ef, List<SectionCrumb> sectionCrumbs, boolean isAdd)
            throws ServiceException {
        ef.setEntityType(adornedList.getAdornedTargetEntityClassname());

        // Get the metadata for this adorned field
        PersistencePackageRequest request = PersistencePackageRequest.adorned()
                .withCeilingEntityClassname(adornedMd.getCollectionCeilingEntity())
                .withAdornedList(adornedList)
                .withSectionCrumbs(sectionCrumbs);
        request.setAddOperationInspect(isAdd);
        ClassMetadata collectionMetadata = adminEntityService.getClassMetadata(request).getDynamicResultSet().getClassMetaData();

        List<Property> entityFormProperties = new ArrayList<>();
        if (isViewCollectionItem) {
            Collections.addAll(entityFormProperties, collectionMetadata.getProperties());
        } else {
            // We want our entity form to only render the maintained adorned target fields
            for (String targetFieldName : adornedMd.getMaintainedAdornedTargetFields()) {
                Property p = collectionMetadata.getPMap().get(targetFieldName);
                if (p.getMetadata() instanceof BasicFieldMetadata && BooleanUtils.isNotTrue( p.getMetadata().getExcluded())) {
                    ((BasicFieldMetadata) p.getMetadata()).setVisibility(VisibilityEnum.VISIBLE_ALL);
                    entityFormProperties.add(p);
                }
            }
        }

        // Set the maintained fields on the form
        setEntityFormFields(collectionMetadata, ef, entityFormProperties);

        // Add these two additional hidden fields that are required for persistence
        Field f = new Field()
                .withName(adornedList.getLinkedObjectPath() + "." + adornedList.getLinkedIdProperty())
                .withFieldType(SupportedFieldType.HIDDEN.toString())
                .withValue(parentId);
        ef.addHiddenField(collectionMetadata, f);

        f = new Field()
                .withName(adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty())
                .withFieldType(SupportedFieldType.HIDDEN.toString())
                .withIdOverride("adornedTargetIdProperty");
        ef.addHiddenField(collectionMetadata, f);

        if (StringUtils.isNotBlank(adornedList.getSortField())) {
            f = new Field()
                    .withName(adornedList.getSortField())
                    .withFieldType(SupportedFieldType.HIDDEN.toString());
            ef.addHiddenField(collectionMetadata, f);
        }

        ef.setParentId(parentId);

        extensionManager.getProxy().addAdditionalAdornedFormActions(ef);

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
                    .withFriendlyName(mapStructure.getKeyPropertyFriendlyName());
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
                                .withFriendlyName(mapStructure.getKeyPropertyFriendlyName());
        }
        keyField.setRequired(true);
        ef.addMapKeyField(cmd, keyField);
        
        // Set the fields for this form
        List<Property> mapFormProperties;
        if (mapMd.isSimpleValue()) {
            ef.setIdProperty("key");
            mapFormProperties = new ArrayList<>();
            Property valueProp = cmd.getPMap().get("value");
            mapFormProperties.add(valueProp);
        } else {
            String valueClassName = mapStructure.getValueClassName();
            List<String> classNames = getValueClassNames(valueClassName);

            mapFormProperties = new ArrayList<>(Arrays.asList(cmd.getProperties()));
            filterMapFormProperties(mapFormProperties, classNames);
        }

        setEntityFormFields(cmd, ef, mapFormProperties);

        Field f = new Field()
                .withName("priorKey")
                .withFieldType(SupportedFieldType.HIDDEN.toString());
        ef.addHiddenField(cmd, f);

        ef.setParentId(parentId);

        return ef;
    }

    protected List<String> getValueClassNames(String valueClassName) {
        PersistenceManager pm = PersistenceManagerFactory.getPersistenceManager(valueClassName);
        List<String> classNames = new ArrayList<>();
        try {
            Class<?>[] mapEntities = pm.getPolymorphicEntities(valueClassName);
            for (Class clazz : mapEntities) {
                classNames.add(clazz.getName());
            }
        } catch (ClassNotFoundException e) {
            classNames.add(valueClassName);
        }
        return classNames;
    }

    protected void filterMapFormProperties(List<Property> mapFormProperties, final List<String> classNames) {
        CollectionUtils.filter(mapFormProperties, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                Property p = (Property) object;
                for (String availType : p.getMetadata().getAvailableToTypes()) {
                    if (classNames.contains(availType)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    protected EntityForm createStandardEntityForm() {
        EntityForm ef = new EntityForm();
        ef.addAction(DefaultEntityFormActions.SAVE);
        return ef;
    }

    protected EntityForm createStandardAdornedEntityForm() {
        EntityForm ef = new EntityForm();
        ef.addAction(DefaultAdornedEntityFormActions.Add);
        return ef;
    }
    
    protected VisibilityEnum[] getGridHiddenVisibilities() {
        return FormBuilderServiceImpl.GRID_HIDDEN_VISIBILITIES;
    }
    
    protected VisibilityEnum[] getFormHiddenVisibilities() {
        return FormBuilderServiceImpl.FORM_HIDDEN_VISIBILITIES;
    }

}
