/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.catalog.domain.Category;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper class for wrapping a collection of categories.
 */
@XmlRootElement(name = "categories")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CategoriesWrapper extends BaseWrapper implements APIWrapper<List<Category>> {

    @XmlElement(name = "category")
    protected List<CategoryWrapper> categories = new ArrayList<CategoryWrapper>();

    @Override
    public void wrapDetails(List<Category> cats, HttpServletRequest request) {
        for (Category category : cats) {
            CategoryWrapper wrapper = (CategoryWrapper) context.getBean(CategoryWrapper.class.getName());
            wrapper.wrapSummary(category, request);
            categories.add(wrapper);
        }
    }

    @Override
    public void wrapSummary(List<Category> cats, HttpServletRequest request) {
        wrapDetails(cats, request);
    }

    
    /**
     * @return the categories
     */
    public List<CategoryWrapper> getCategories() {
        return categories;
    }

    
    /**
     * @param categories the categories to set
     */
    public void setCategories(List<CategoryWrapper> categories) {
        this.categories = categories;
    }
}
