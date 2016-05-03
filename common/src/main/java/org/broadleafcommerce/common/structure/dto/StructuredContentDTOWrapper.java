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

import java.util.List;
import java.util.Map;

/**
 * Provides a wrapper class that can be used to alter the priority of a structuredcontentdto.
 * @author bpolster
 *
 */
public class StructuredContentDTOWrapper extends StructuredContentDTO {

    private static final long serialVersionUID = 1L;
    private StructuredContentDTO structuredContentDTO;
    private Integer priority;

    public StructuredContentDTOWrapper(StructuredContentDTO structuredContentDTO) {
        this.structuredContentDTO = structuredContentDTO;
    }

    public boolean equals(Object arg0) {
        return structuredContentDTO.equals(arg0);
    }

    public Object getPropertyValue(String propertyName) {
        return structuredContentDTO.getPropertyValue(propertyName);
    }

    public Long getId() {
        return structuredContentDTO.getId();
    }

    public String getContentName() {
        return structuredContentDTO.getContentName();
    }

    public String getContentType() {
        return structuredContentDTO.getContentType();
    }

    public String getLocaleCode() {
        return structuredContentDTO.getLocaleCode();
    }

    public Integer getPriority() {
        if (priority != null) {
            return priority;
        } else {
            return structuredContentDTO.getPriority();
        }
    }

    public Map getValues() {
        return structuredContentDTO.getValues();
    }

    public String getRuleExpression() {
        return structuredContentDTO.getRuleExpression();
    }

    public List<ItemCriteriaDTO> getItemCriteriaDTOList() {
        return structuredContentDTO.getItemCriteriaDTOList();
    }

    public int hashCode() {
        return structuredContentDTO.hashCode();
    }

    public void setId(Long id) {
        structuredContentDTO.setId(id);
    }

    public void setContentName(String contentName) {
        structuredContentDTO.setContentName(contentName);
    }

    public void setContentType(String contentType) {
        structuredContentDTO.setContentType(contentType);
    }

    public void setLocaleCode(String localeCode) {
        structuredContentDTO.setLocaleCode(localeCode);
    }

    public void setPriority(Integer priority) {
        structuredContentDTO.setPriority(priority);
    }

    public void setValues(Map values) {
        structuredContentDTO.setValues(values);
    }

    public void setRuleExpression(String ruleExpression) {
        structuredContentDTO.setRuleExpression(ruleExpression);
    }

    public void setItemCriteriaDTOList(List<ItemCriteriaDTO> itemCriteriaDTOList) {
        structuredContentDTO.setItemCriteriaDTOList(itemCriteriaDTOList);
    }

    public String toString() {
        return structuredContentDTO.toString();
    }
}
