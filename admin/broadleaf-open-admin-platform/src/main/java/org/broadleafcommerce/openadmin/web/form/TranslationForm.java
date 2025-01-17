/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.web.form;

import org.apache.commons.lang3.BooleanUtils;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;

public class TranslationForm {

    protected String ceilingEntity;
    protected String entityId;
    protected String propertyName;
    protected String localeCode;
    protected String translatedValue;
    protected Long translationId;
    /**
     * Whether the field to translate is rich text
     *
     * @deprecated Prefer using {@link #fieldType} to explicitly specify the field type for more
     *             advanced logic. Note that {@link #getIsRte()} will remain as a shorthand way of
     *             determining if the field type should be rendered as HTML/rich-text
     */
    @Deprecated
    protected Boolean isRte;

    /**
     * The type of field that the translatable property is. By default this should be one of
     * {@link SupportedFieldType SupportedFieldType's} values.
     * <p>
     * Prefer using this field over {@link #isRte}.
     */
    protected String fieldType;

    public String getCeilingEntity() {
        return ceilingEntity;
    }

    public void setCeilingEntity(String ceilingEntity) {
        this.ceilingEntity = ceilingEntity;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getTranslatedValue() {
        return translatedValue;
    }

    public void setTranslatedValue(String translatedValue) {
        this.translatedValue = translatedValue;
    }

    public Long getTranslationId() {
        return translationId;
    }

    public void setTranslationId(Long translationId) {
        this.translationId = translationId;
    }

    /**
     * Determines whether the translatable property is rich-text. This method has been updated to
     * check whether {@link #fieldType} matches such values as {@link SupportedFieldType#HTML} or
     * {@link SupportedFieldType#HTML_BASIC} and thus is rich-text. For backwards compatibility,
     * this will also check the now deprecated {@link #isRte}.
     *
     * @return Whether the translatable property is rich-text.
     */
    public Boolean getIsRte() {
        if (SupportedFieldType.HTML.name().equals(fieldType) ||
                SupportedFieldType.HTML_BASIC.name().equals(fieldType)) {
            return true;
        }

        return BooleanUtils.isTrue(isRte);
    }

    /**
     * @deprecated use {@link #fieldType} instead
     */
    @Deprecated
    public void setIsRte(Boolean isRte) {
        this.isRte = isRte;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
}
