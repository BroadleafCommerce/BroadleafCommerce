/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
