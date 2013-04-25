/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.catalog.taglib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryBreadCrumbTag extends CategoryLinkTag {

    private static final Log LOG = LogFactory.getLog(CategoryBreadCrumbTag.class);
    private static final long serialVersionUID = 1L;

    private Long categoryId;
    private List<Category> categoryList = new ArrayList<Category>();

    @Override
    public void doTag() throws JspException, IOException {
        if (categoryId == null && categoryList == null) {
            throw new RuntimeException("Either categoryId or categoryList is required for this tag");
        }

        if (categoryId != null) {
            Category category = this.getCatalogService().findCategoryById(categoryId);

            if (category == null && LOG.isDebugEnabled()){
                LOG.debug("The category returned was null for categoryId: " + categoryId);
            }

            while (category != null) {
                categoryList.add(category);
                category = category.getDefaultParentCategory();
            }

            Collections.reverse(categoryList);
        }

        JspWriter out = getJspContext().getOut();
        int count = 0;
        for (Category cat : categoryList) {
            out.println(getUrl(cat));

            if (count < categoryList.size() - 1) {
                out.println(" > ");
            }

            ++count;
        }
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

}
