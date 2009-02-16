package org.broadleafcommerce.catalog.domain;

import java.util.Date;
import java.util.Map;

public interface Category {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public Category getParentCategory();

    public void setParentCategory(Category parentCategory);

    public String getUrl();

    public void setUrl(String url);

    public String getUrlKey();

    public void setUrlKey(String urlKey);

    public Integer getDisplayOrder();

    public void setDisplayOrder(Integer displayOrder);

    public Map<String, String> getImageMap();

    public void setImageMap(Map<String, String> imageMap);

    public String getDescription();

    public void setDescription(String description);

    public Date getActiveStartDate();

    public void setActiveStartDate(Date activeStartDate);

    public Date getActiveEndDate();

    public void setActiveEndDate(Date activeEndDate);

    public String getDisplayTemplate();

    public void setDisplayTemplate(String displayTemplate);

    public Map<String, String> getExtendedPropertiesMap();

    public void setExtendedPropertiesMap(Map<String, String> extendedPropertiesMap);
}
