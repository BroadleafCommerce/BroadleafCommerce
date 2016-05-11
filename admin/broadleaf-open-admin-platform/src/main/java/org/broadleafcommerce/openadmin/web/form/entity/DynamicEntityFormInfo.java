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

package org.broadleafcommerce.openadmin.web.form.entity;

/**
 * Used to setup what data will be needed to create add dynamic form fields to an entity.
 * 
 * Use the {@link #withCeilingClassName(String)} to indicate the entity which contains the template fields.
 * Use the {@link #withSecurityCeilingClassName(String)} to indicate the entity that should be used to control 
 * the security of the fields.   For example, if a user has access to Page, then they should also have access to the
 * fields introduced by the PageTemplate.
 * 
 * @author bpolster
 *
 */
public class DynamicEntityFormInfo {

    public static final String FIELD_SEPARATOR = "|";
    
    protected String criteriaName;
    protected String propertyName;
    protected String propertyValue;
    protected String ceilingClassName;
    protected String securityCeilingClassName;
    protected String[] customCriteriaOverride;
    
    public DynamicEntityFormInfo withCriteriaName(String criteriaName) {
        setCriteriaName(criteriaName);
        return this;
    }

    public DynamicEntityFormInfo withPropertyName(String propertyName) {
        setPropertyName(propertyName);
        return this;
    }
    
    public DynamicEntityFormInfo withPropertyValue(String propertyValue) {
        setPropertyValue(propertyValue);
        return this;
    }

    public DynamicEntityFormInfo withCeilingClassName(String ceilingClassName) {
        setCeilingClassName(ceilingClassName);
        return this;
    }

    /**
     * The security class name that permissions will be based off of for this entity.    Generally this is different
     * from the "ceilingClassName" which represents the template that will be used to define the fields being used.
     * 
     * @param securityCeilingClassName
     * @return
     */
    public DynamicEntityFormInfo withSecurityCeilingClassName(String securityCeilingClassName) {
        setSecurityCeilingClassName(securityCeilingClassName);
        return this;
    }

    public DynamicEntityFormInfo withCustomCriteriaOverride(String[] customCriteriaOverride) {
        setCustomCriteriaOverride(customCriteriaOverride);
        return this;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public String getPropertyValue() {
        return propertyValue;
    }
    
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getCeilingClassName() {
        return ceilingClassName;
    }

    public String getSecurityCeilingClassName() {
        return securityCeilingClassName;
    }

    public void setCeilingClassName(String ceilingClassName) {
        this.ceilingClassName = ceilingClassName;
    }

    public void setSecurityCeilingClassName(String securityCeilingClassName) {
        this.securityCeilingClassName = securityCeilingClassName;
    }
    
    public String[] getCustomCriteriaOverride() {
        return customCriteriaOverride;
    }

    public void setCustomCriteriaOverride(String[] customCriteriaOverride) {
        this.customCriteriaOverride = customCriteriaOverride;
    }

}
