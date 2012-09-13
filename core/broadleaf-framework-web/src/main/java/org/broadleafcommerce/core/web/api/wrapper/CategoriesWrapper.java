/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.web.api.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.core.catalog.domain.Category;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.*;

/**
 * This is a JAXB wrapper class for wrapping a collection of categories.
 */
@XmlRootElement(name = "categories")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CategoriesWrapper extends BaseWrapper implements APIWrapper<List<Category>> {

    @XmlElement(name = "category")
    protected List<CategoryWrapper> categories = new ArrayList<CategoryWrapper>();

    public void wrap(List<Category> cats, HttpServletRequest request) {
        for (Category category : cats) {
            CategoryWrapper wrapper = (CategoryWrapper) context.getBean(CategoryWrapper.class.getName());
            wrapper.wrap(category, request);
            categories.add(wrapper);
        }
    }
}
