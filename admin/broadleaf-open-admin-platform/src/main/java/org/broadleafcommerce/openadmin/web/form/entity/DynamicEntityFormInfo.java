
package org.broadleafcommerce.openadmin.web.form.entity;

public class DynamicEntityFormInfo {

    protected String criteriaName;
    protected String propertyName;
    protected String propertyValue;
    protected String ceilingClassName;
    
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

    public void setCeilingClassName(String ceilingClassName) {
        this.ceilingClassName = ceilingClassName;
    }

}
