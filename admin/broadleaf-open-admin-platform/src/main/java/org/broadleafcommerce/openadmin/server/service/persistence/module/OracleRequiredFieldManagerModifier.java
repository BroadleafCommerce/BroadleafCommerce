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
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.DialectHelper;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.EntityManager;

/**
 * Oracle-only field modifier that is used to ensure that non-nullable fields are not persisted with empty strings and
 * instead use single-space strings.
 *
 * @author Nick Crum ncrum
 */
@Component("blOracleRequiredFieldManagerModifier")
public class OracleRequiredFieldManagerModifier implements FieldManagerModifier {

    private static final String ORACLE_SINGLE_SPACE_DEFAULT = " ";

    protected static final List<String> TYPES_THAT_SUPPORT_SINGLE_SPACE_AS_DEFAULT = Arrays.asList(SupportedFieldType.STRING.toString(),
            SupportedFieldType.HTML_BASIC.toString(),
            SupportedFieldType.HTML.toString(),
            SupportedFieldType.DESCRIPTION.toString(),
            SupportedFieldType.EMAIL.toString(),
            SupportedFieldType.CODE.toString(),
            SupportedFieldType.COLOR.toString());

    @Resource(name = "blDialectHelper")
    protected DialectHelper dialectHelper;

    @Override
    public boolean canHandle(Field field, Object value, EntityManager em) {
        if (!dialectHelper.isOracle(em)) {
            return false;
        }

        Column column = field.getAnnotation(Column.class);
        AdminPresentation adminPresentation = field.getAnnotation(AdminPresentation.class);
        return adminPresentation != null && isRequiredField(adminPresentation, column) && isStringFieldType(field, adminPresentation);
    }

    protected boolean isRequiredField(AdminPresentation adminPresentation, Column column) {
        RequiredOverride requiredOverride = adminPresentation.requiredOverride();
        String defaultValue = adminPresentation.defaultValue();
        return ((column != null && !column.nullable()) || (requiredOverride.equals(RequiredOverride.REQUIRED))) && StringUtils.isEmpty(defaultValue);
    }

    protected boolean isStringFieldType(Field field, AdminPresentation adminPresentation) {
        SupportedFieldType fieldType = adminPresentation.fieldType();
        return TYPES_THAT_SUPPORT_SINGLE_SPACE_AS_DEFAULT.contains(fieldType.toString())
            || (SupportedFieldType.UNKNOWN.toString().equals(fieldType.toString()) && String.class.isAssignableFrom(field.getType()));
    }

    @Override
    public Object getModifiedWriteValue(Field field, Object value, Object newValue, EntityManager em) throws IllegalAccessException {
        String modifierValue = (String) newValue;
        return StringUtils.isEmpty(modifierValue) ? ORACLE_SINGLE_SPACE_DEFAULT : modifierValue;
    }

    @Override
    public Object getModifiedReadValue(Field field, Object value, EntityManager em) throws IllegalAccessException {
        String modifierValue = (String) value;
        return modifierValue != null && modifierValue.equals(ORACLE_SINGLE_SPACE_DEFAULT) ? "" : modifierValue;
    }

    @Override
    public int getOrder() {
        return 1000;
    }
}
