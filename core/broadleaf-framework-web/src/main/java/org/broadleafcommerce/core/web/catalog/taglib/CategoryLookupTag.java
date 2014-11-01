/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.catalog.taglib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;

import javax.servlet.jsp.JspException;

public class CategoryLookupTag extends AbstractCatalogTag {

    private static final Log LOG = LogFactory.getLog(CategoryTag.class);
    private static final long serialVersionUID = 1L;
    private String var;

    private String categoryName;

    @Override
    public void doTag() throws JspException {
        catalogService = super.getCatalogService();

        Category category = catalogService.findCategoryByName(categoryName);

        if(category == null && LOG.isDebugEnabled()){
            LOG.debug("The category returned was null for categoryName: " + categoryName);
        }

        getJspContext().setAttribute(var, category);
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}

