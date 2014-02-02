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
