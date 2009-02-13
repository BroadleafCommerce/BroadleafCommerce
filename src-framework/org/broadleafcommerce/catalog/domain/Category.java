package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.commons.validator.GenericValidator;

public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String url;

    private String urlKey;

    private Category parentCategory;

    private Integer displayOrder;

    private Map<String, String> imageMap;

    private String description;

    private Date activeStartDate;

    private Date activeEndDate;

    private String displayTemplate;

    private Map<String, String> extendedPropertiesMap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlKey() {
        if (GenericValidator.isBlankOrNull(urlKey)) {
            return String.valueOf(getId());
        }
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Map<String, String> getImageMap() {
        return imageMap;
    }

    public void setImageMap(Map<String, String> imageMap) {
        this.imageMap = imageMap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getActiveStartDate() {
        return activeStartDate;
    }

    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    public Date getActiveEndDate() {
        return activeEndDate;
    }

    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    public String getDisplayTemplate() {
        return displayTemplate;
    }

    public void setDisplayTemplate(String displayTemplate) {
        this.displayTemplate = displayTemplate;
    }

    public Map<String, String> getExtendedPropertiesMap() {
        return extendedPropertiesMap;
    }

    public void setExtendedPropertiesMap(Map<String, String> extendedPropertiesMap) {
        this.extendedPropertiesMap = extendedPropertiesMap;
    }
}
