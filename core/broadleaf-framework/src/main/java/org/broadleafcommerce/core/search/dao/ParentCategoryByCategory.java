package org.broadleafcommerce.core.search.dao;

/**
 * Container object for the results from a lightweight query that retrieves parent categories
 * for a child category
 *
 * @author Jeff Fischer
 */
public class ParentCategoryByCategory {

    protected Long parent;
    protected Long defaultParent;
    protected Long child;

    public ParentCategoryByCategory(Long parent, Long defaultParent, Long child) {
        this.parent = parent;
        this.defaultParent = defaultParent;
        this.child = child;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getDefaultParent() {
        return defaultParent;
    }

    public void setDefaultParent(Long defaultParent) {
        this.defaultParent = defaultParent;
    }

    public Long getChild() {
        return child;
    }

    public void setChild(Long child) {
        this.child = child;
    }
}
