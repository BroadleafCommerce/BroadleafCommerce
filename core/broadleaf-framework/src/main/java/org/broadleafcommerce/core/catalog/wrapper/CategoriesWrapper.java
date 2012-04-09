package org.broadleafcommerce.core.catalog.wrapper;

import org.broadleafcommerce.common.api.APIWrapper;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.common.api.BaseWrapper;
import org.broadleafcommerce.core.catalog.domain.Category;

import javax.xml.bind.annotation.*;

/**
 * This is a JAXB wrapper class for wrapping a collection of categories.
 */
@XmlRootElement(name = "categories")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CategoriesWrapper extends BaseWrapper implements APIWrapper<List<Category>> {

    @XmlElement(name = "category")
    protected List<CategoryWrapper> categories = new ArrayList<CategoryWrapper>();

    public void wrap(List<Category> cats) {
        for (Category category : cats) {
            CategoryWrapper wrapper = (CategoryWrapper) entityConfiguration.createEntityInstance(CategoryWrapper.class.getName());
            wrapper.wrap(category);
            categories.add(wrapper);
        }
    }
}
