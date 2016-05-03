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
package org.broadleafcommerce.openadmin.web.form;

public class TranslationForm {

    protected String ceilingEntity;
    protected String entityId;
    protected String propertyName;
    protected String localeCode;
    protected String translatedValue;
    protected Long translationId;
    protected Boolean isRte;

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
    
    public Boolean getIsRte() {
        return isRte == null ? false : isRte;
    }

    public void setIsRte(Boolean isRte) {
        this.isRte = isRte;
    }

}
