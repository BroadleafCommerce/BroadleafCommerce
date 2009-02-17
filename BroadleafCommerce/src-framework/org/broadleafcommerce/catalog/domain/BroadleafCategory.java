package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.validator.GenericValidator;

public class BroadleafCategory implements Category, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String url;

    private String urlKey;

    private Category parentCategory;

    private Integer displayOrder;

    private String description;

    private Date activeStartDate;

    private Date activeEndDate;

    private String displayTemplate;

    private Set<CategoryImage> categoryImages;

    private Map<String, String> categoryImageMap;

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

    public Set<CategoryImage> getCategoryImages() {
        return categoryImages;
    }

    public void setCategoryImages(Set<CategoryImage> categoryImages) {
        this.categoryImages = categoryImages;
    }

    public String getCategoryImage(String key) {
        if (categoryImageMap == null) {
            categoryImageMap = new HashMap<String, String>();
            Set<CategoryImage> images = getCategoryImages();
            if (images != null) {
                for (CategoryImage ci : images) {
                    categoryImageMap.put(ci.getName(), ci.getUrl());
                }
            }
        }
        return categoryImageMap.get(key);
    }
}
