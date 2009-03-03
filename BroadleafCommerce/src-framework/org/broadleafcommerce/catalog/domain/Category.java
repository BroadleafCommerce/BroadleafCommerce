package org.broadleafcommerce.catalog.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Category {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public Category getDefaultParentCategory();

    public void setDefaultParentCategory(Category defaultParentCategory);

    public String getUrl();

    public void setUrl(String url);

    public String getUrlKey();

    public String getGeneratedUrl();

    public void setUrlKey(String urlKey);

    public String getDescription();

    public void setDescription(String description);

    public Date getActiveStartDate();

    public void setActiveStartDate(Date activeStartDate);

    public Date getActiveEndDate();

    public void setActiveEndDate(Date activeEndDate);

    public boolean isActive();

    public String getDisplayTemplate();

    public void setDisplayTemplate(String displayTemplate);

    public List<Category> getChildCategories();

    public boolean hasChildCategories();

    public void setAllChildCategories(List<Category> allChildCategories);

    public Map<String, String> getCategoryImages();

    public String getCategoryImage(String imageKey);

    public void setCategoryImages(Map<String, String> categoryImages);

    public String getLongDescription();

    public void setLongDescription(String longDescription);
}
