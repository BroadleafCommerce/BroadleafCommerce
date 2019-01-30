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

package org.broadleafcommerce.openadmin.web.rulebuilder.service;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldWrapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public abstract class AbstractRuleBuilderFieldService implements RuleBuilderFieldService, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    protected DynamicEntityDao dynamicEntityDao;
    protected ApplicationContext applicationContext;
    protected List<FieldData> fields = new ArrayList<FieldData>();

    @Resource(name = "blRuleBuilderFieldServiceExtensionManager")
    protected RuleBuilderFieldServiceExtensionManager extensionManager;

    @Override
    public void setRuleBuilderFieldServiceExtensionManager(RuleBuilderFieldServiceExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public FieldWrapper buildFields() {
        FieldWrapper wrapper = new FieldWrapper();

        for (FieldData field : getFields()) {
            wrapper.getFields().add(constructFieldDTOFromFieldData(field));
        }

        return wrapper;
    }

    protected FieldDTO constructFieldDTOFromFieldData(FieldData field) {
        FieldDTO fieldDTO = new FieldDTO();
        //translate the label to display
        String label = field.getFieldLabel();
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        MessageSource messages = context.getMessageSource();
        if (messages != null) {
            label = messages.getMessage(label, null, label, context.getJavaLocale());
        }
        fieldDTO.setLabel(label);

        fieldDTO.setId(field.getFieldName());
        fieldDTO.setOperators(field.getOperators());
        fieldDTO.setSelectizeSectionKey(field.getSelectizeSectionKey());
        fieldDTO.setValues(field.getOptions());

        if (SupportedFieldType.BROADLEAF_ENUMERATION.equals(field.getFieldType())){
            fieldDTO.setInput("select");
        } else {
            fieldDTO.setInput("text");
        }

        return fieldDTO;
    }

    @Override
    public SupportedFieldType getSupportedFieldType(String fieldName) {
        SupportedFieldType type = null;
        if (fieldName != null) {
            for (FieldData field : getFields()) {
                if (fieldName.equals(field.getFieldName())) {
                    return field.getFieldType();
                }
            }
        }
        return type;
    }

    @Override
    public SupportedFieldType getSecondaryFieldType(String fieldName) {
        SupportedFieldType type = null;
        if (fieldName != null) {
            for (FieldData field : getFields()) {
                if (fieldName.equals(field.getFieldName())) {
                    return field.getSecondaryFieldType();
                }
            }
        }
        return type;
    }

    @Override
    public FieldDTO getField(String fieldName) {
        for (FieldData field : getFields()) {
            if (field.getFieldName().equals(fieldName)) {
                return constructFieldDTOFromFieldData(field);
            }
        }
        return null;
    }

    @Override
    public List<FieldData> getFields() {
        return fields;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFields(final List<FieldData> fields) {
        List<FieldData> proxyFields = (List<FieldData>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { List.class }, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("add")) {
                    FieldData fieldData = (FieldData) args[0];
                    testFieldName(fieldData);
                }
                if (method.getName().equals("addAll")) {
                    Collection<FieldData> addCollection = (Collection<FieldData>) args[0];
                    Iterator<FieldData> itr = addCollection.iterator();
                    while (itr.hasNext()) {
                        FieldData fieldData = itr.next();
                        testFieldName(fieldData);
                    }
                }
                return method.invoke(fields, args);
            }

            private void testFieldName(FieldData fieldData) throws ClassNotFoundException {
                if (!fieldData.getSkipValidation()) {
                    if (!StringUtils.isEmpty(fieldData.getFieldName()) && dynamicEntityDao != null) {
                        String dtoClassName = getDtoClassName();
                        if (fieldData.getOverrideDtoClassName() != null) {
                            dtoClassName = fieldData.getOverrideDtoClassName();
                        }
                        Class<?>[] dtos = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Class.forName(dtoClassName));
                        if (ArrayUtils.isEmpty(dtos)) {
                            dtos = new Class<?>[] { Class.forName(dtoClassName) };
                        }
                        Field field = null;
                        for (Class<?> dto : dtos) {
                            field = dynamicEntityDao.getFieldManager().getField(dto, fieldData.getFieldName());
                            if (field != null) {
                                break;
                            }
                        }
                        if (field == null) {
                            throw new IllegalArgumentException("Unable to find the field declared in FieldData (" + fieldData.getFieldName() + ") on the target class (" + dtoClassName + "), or any registered entity class that derives from it.");
                        }
                    }
                }
            }
        });
        this.fields = proxyFields;
    }

    @Override
    public String getOverrideFieldEntityKey(String fieldName) {
        for (FieldData fieldData : fields) {
            if (StringUtils.equals(fieldData.getFieldName(), fieldName)) {
                return fieldData.getOverrideEntityKey();
            }
        }
        return null;
    }

    @Override
    public RuleBuilderFieldService clone() throws CloneNotSupportedException {
        try {
            RuleBuilderFieldService clone = this.getClass().newInstance();
            clone.setFields(this.fields);
            clone.setRuleBuilderFieldServiceExtensionManager(extensionManager);
            return clone;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract String getDtoClassName();

    public abstract void init();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // This bean only is valid when the following bean is active. (admin)
        if (applicationContext.containsBean(PersistenceManagerFactory.getPersistenceManagerRef()) && applicationContext.containsBean("blPersistenceManagerFactory")) {
            //initialize the factory bean
            applicationContext.getBean("blPersistenceManagerFactory");

            PersistenceManager persistenceManager = PersistenceManagerFactory.getDefaultPersistenceManager();
            dynamicEntityDao = persistenceManager.getDynamicEntityDao();
            setFields(new ArrayList<FieldData>());

            // This cannot be null during startup as we do not want to remove the null safety checks in a multi-tenant env.
            boolean contextWasNull = false;
            if (BroadleafRequestContext.getBroadleafRequestContext() == null) {
                BroadleafRequestContext brc = new BroadleafRequestContext();
                brc.setIgnoreSite(true);
                BroadleafRequestContext.setBroadleafRequestContext(brc);
                contextWasNull = true;
            }

            try {
                init();
                // Initialize additional static fields method for the component.
                if (extensionManager != null) {
                    extensionManager.getProxy().addFields(fields, getName(), getDtoClassName());
                }
                validateRuleBuilderState(this);
            } finally {
                if (contextWasNull) {
                    BroadleafRequestContext.setBroadleafRequestContext(null);
                }
            }
        }
    }

    protected void validateRuleBuilderState(RuleBuilderFieldService fieldService) {
        for (FieldData fieldData : fieldService.getFields()) {
            if (StringUtils.isBlank(fieldData.getOperators()) ||
                    StringUtils.isBlank(fieldData.getFieldLabel()) ||
                    StringUtils.isBlank(fieldData.getFieldName())) {
                throw new IllegalStateException(String.format("Unable to initialize RuleBuilderFieldService[%s] : FieldData[%s] - " +
                        "All RuleBuilders must initialize at least a Label, Name, and Operators",
                        fieldService.getName(), fieldData.getFieldName()));
            }

            if ("blcOperators_Selectize".equals(fieldData.getOperators()) &&
                    StringUtils.isBlank(fieldData.getSelectizeSectionKey())) {

                throw new IllegalStateException(String.format("Unable to initialize RuleBuilderFieldService[%s] : FieldData[%s]- " +
                        "If registering a selectize field, an Admin Section Key must also be defined",
                        fieldService.getName(), fieldData.getFieldName()));
            }
        }
    }

}
