/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.rulebuilder.service;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.DynamicEntityRemoteService;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.TargetModeType;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldWrapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public abstract class AbstractRuleBuilderFieldService implements RuleBuilderFieldService, ApplicationContextAware {

    protected DynamicEntityDao dynamicEntityDao;
    protected ApplicationContext applicationContext;
    protected List<FieldData> fields = new ArrayList<FieldData>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public FieldWrapper buildFields() {
        FieldWrapper wrapper = new FieldWrapper();

        for (FieldData field : getFields()) {
            FieldDTO fieldDTO = new FieldDTO();
            fieldDTO.setLabel(field.getFieldLabel());
            fieldDTO.setName(field.getFieldName());
            fieldDTO.setOperators(field.getOperators());
            fieldDTO.setOptions(field.getOptions());
            wrapper.getFields().add(fieldDTO);
        }

        return wrapper;
    }

    @Override
    public SupportedFieldType getSupportedFieldType(String fieldName) {
        SupportedFieldType type = null;
        if (fieldName != null) {
            for (FieldData field : getFields()) {
                if (fieldName.equals(field.getFieldName())){
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
                if (fieldName.equals(field.getFieldName())){
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
                FieldDTO fieldDTO = new FieldDTO();
                fieldDTO.setLabel(field.getFieldLabel());
                fieldDTO.setName(field.getFieldName());
                fieldDTO.setOperators(field.getOperators());
                fieldDTO.setOptions(field.getOptions());
                return fieldDTO;
            }
        }
        return null;
    }

    @Override
    public List<FieldData> getFields() {
        return fields;
    }

    @Override
    public void setFields(final List<FieldData> fields) {
        List<FieldData> proxyFields = (List<FieldData>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{List.class}, new InvocationHandler() {
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
                if (!StringUtils.isEmpty(fieldData.getFieldName()) && dynamicEntityDao != null) {
                    Class<?>[] dtos = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Class.forName(getDtoClassName()));
                    if (ArrayUtils.isEmpty(dtos)) {
                        dtos = new Class<?>[]{Class.forName(getDtoClassName())};
                    }
                    Field field = null;
                    for (Class<?> dto : dtos) {
                        field = dynamicEntityDao.getFieldManager().getField(dto, fieldData.getFieldName());
                        if (field != null) {
                            break;
                        }
                    }
                    if (field == null) {
                        throw new IllegalArgumentException("Unable to find the field declared in FieldData (" + fieldData.getFieldName() + ") on the target class (" + getDtoClassName() + "), or any registered entity class that derives from it.");
                    }
                }
            }
        });
        this.fields = proxyFields;
    }

    public RuleBuilderFieldService clone() throws CloneNotSupportedException {
        try {
            RuleBuilderFieldService clone = this.getClass().newInstance();
            clone.setFields(this.fields);

            return clone;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract String getDtoClassName();

    public abstract void init();

    @PostConstruct
    private void construct() {
        PersistenceManager persistenceManager = (PersistenceManager) applicationContext.getBean(DynamicEntityRemoteService.DEFAULTPERSISTENCEMANAGERREF);
        persistenceManager.setTargetMode(TargetModeType.SANDBOX);
        dynamicEntityDao = persistenceManager.getDynamicEntityDao();
        setFields(new ArrayList<FieldData>());
        init();
    }
}
