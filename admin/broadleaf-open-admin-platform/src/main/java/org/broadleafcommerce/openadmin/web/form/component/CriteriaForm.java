/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.openadmin.web.form.component;

import org.broadleafcommerce.openadmin.client.dto.FilterAndSortCriteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This is a worthless object that I am required to create because Spring cannot figure out how to properly bind a List
 * of a complex object directly as a controller parameter.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 *
 */
public class CriteriaForm implements Serializable {

    private static final long serialVersionUID = 643315964682270873L;

    protected List<FilterAndSortCriteria> criteria = new ArrayList<FilterAndSortCriteria>();

    public List<FilterAndSortCriteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<FilterAndSortCriteria> criteria) {
        this.criteria = criteria;
    }

}
