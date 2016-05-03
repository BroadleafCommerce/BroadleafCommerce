/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.structure.dto;

import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scoped as a prototype bean via bl-cms-applicationContext-entity. This bean is used to wrap an {@link StructuredContentImpl}
 * so that modifications and additional properties can be used without worrying about Hibernate's persistence.
 * 
 * @author bpolster.
 * @see {@link StructuredContentServiceImpl#buildStructuredContentDTO};
 * @see {@link StructuredContentServiceImpl#buildFieldValues};
 */
public class StructuredContentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Long id;
    protected String contentName;
    protected String contentType;
    protected String localeCode;
    protected Integer priority;
    protected Map<String, Object> values = new HashMap<String,Object>();
    protected String ruleExpression;
    protected List<ItemCriteriaDTO> itemCriteriaDTOList;

    /**
     * Attempts to obtain the given property value from the dynamic property map first, and then an actual bean property
     * via a getter
     * 
     * @param propertyName
     * @return
     */
    public Object getPropertyValue(String propertyName) {
        try {
            return getValues().containsKey(propertyName) ? getValues().get(propertyName) : BeanUtils.getProperty(this, propertyName);
        } catch (Exception e) {
            return null;
        }
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        values.put("contentName", contentName);
        this.contentName = contentName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        values.put("contentType", contentType);
        this.contentType = contentType;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        values.put("localeCode", localeCode);
        this.localeCode = localeCode;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        values.put("priority", priority);
        this.priority = priority;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }

    public List<ItemCriteriaDTO> getItemCriteriaDTOList() {
        return itemCriteriaDTOList;
    }

    public void setItemCriteriaDTOList(List<ItemCriteriaDTO> itemCriteriaDTOList) {
        this.itemCriteriaDTOList = itemCriteriaDTOList;
    }

    public StructuredContentDTO getClone() {
        StructuredContentDTO clonedDto = new StructuredContentDTO();
        clonedDto.setId(id);
        clonedDto.setContentName(contentName);
        clonedDto.setContentType(contentType);
        clonedDto.setLocaleCode(localeCode);
        clonedDto.setPriority(priority);
        clonedDto.setValues(new HashMap<String, Object>(values));
        clonedDto.setRuleExpression(ruleExpression);
        if (itemCriteriaDTOList != null) {
            List<ItemCriteriaDTO> itemCriteriaDTOs = new ArrayList<ItemCriteriaDTO>();
            for (ItemCriteriaDTO itemCriteriaDto : itemCriteriaDTOList) {
                itemCriteriaDTOs.add(itemCriteriaDto.getClone());
            }
            clonedDto.setItemCriteriaDTOList(itemCriteriaDTOs);
        }
        return clonedDto;
    }
}
