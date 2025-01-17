/*-
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.structure.dto;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * Provides a wrapper class that can be used to alter the priority of a structuredcontentdto.
 *
 * @author bpolster
 */
public class StructuredContentDTOWrapper extends StructuredContentDTO {

    @Serial
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

    public void setId(Long id) {
        structuredContentDTO.setId(id);
    }

    public String getContentName() {
        return structuredContentDTO.getContentName();
    }

    public void setContentName(String contentName) {
        structuredContentDTO.setContentName(contentName);
    }

    public String getContentType() {
        return structuredContentDTO.getContentType();
    }

    public void setContentType(String contentType) {
        structuredContentDTO.setContentType(contentType);
    }

    public String getLocaleCode() {
        return structuredContentDTO.getLocaleCode();
    }

    public void setLocaleCode(String localeCode) {
        structuredContentDTO.setLocaleCode(localeCode);
    }

    public Integer getPriority() {
        if (priority != null) {
            return priority;
        } else {
            return structuredContentDTO.getPriority();
        }
    }

    public void setPriority(Integer priority) {
        structuredContentDTO.setPriority(priority);
    }

    public Map getValues() {
        return structuredContentDTO.getValues();
    }

    public void setValues(Map values) {
        structuredContentDTO.setValues(values);
    }

    public String getRuleExpression() {
        return structuredContentDTO.getRuleExpression();
    }

    public void setRuleExpression(String ruleExpression) {
        structuredContentDTO.setRuleExpression(ruleExpression);
    }

    public List<ItemCriteriaDTO> getItemCriteriaDTOList() {
        return structuredContentDTO.getItemCriteriaDTOList();
    }

    public void setItemCriteriaDTOList(List<ItemCriteriaDTO> itemCriteriaDTOList) {
        structuredContentDTO.setItemCriteriaDTOList(itemCriteriaDTOList);
    }

    public int hashCode() {
        return structuredContentDTO.hashCode();
    }

    public String toString() {
        return structuredContentDTO.toString();
    }

}
