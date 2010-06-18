/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.catalog.web.taglib;

import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.time.SystemTime;

/*NOTE:
 * Was referred to as the ProductsTag.  Class name changed to be more descriptive
 */
public class GetProductsByCategoryIdTag extends AbstractCatalogTag {

    private static final Log LOG = LogFactory.getLog(GetProductsByCategoryIdTag.class);
    private static final long serialVersionUID = 1L;
    private String var;
    private long categoryId;

    @Override
    public void doTag() throws JspException {
        catalogService = super.getCatalogService();

        Category c = catalogService.findCategoryById(categoryId);

        if(c == null){
            getJspContext().setAttribute(var, null);

            if(LOG.isDebugEnabled()){
                LOG.debug("The category returned was null for categoryId: " + categoryId);
            }
        }

        List<Product> productList = catalogService.findActiveProductsByCategory(c, SystemTime.asDate());

        if(CollectionUtils.isEmpty(productList) && LOG.isDebugEnabled()){
            LOG.debug("The productList returned was null for categoryId: " + categoryId);
        }

        getJspContext().setAttribute(var, productList);

    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

}
