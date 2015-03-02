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
package org.broadleafcommerce.core.catalog.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

public class CategoryImplTest extends TestCase {
    
    public void testRecursiveLink() throws Exception {
        CategoryImpl c1 = new CategoryImpl();
        c1.setId(1l);
        c1.setName("cat one");
        c1.setActiveStartDate(new Date());
        List<CategoryXref> c1Parents = new ArrayList<CategoryXref>();

        
        
        CategoryImpl c2 = new CategoryImpl();
        c2.setId(2l);
        c2.setName("cat two");
        c2.setActiveStartDate(new Date());
        List<CategoryXref> c2Parents = new ArrayList<CategoryXref>();

        
        CategoryXref cxref1 = new CategoryXrefImpl();
        cxref1.setCategory(c2);
        cxref1.setSubCategory(c1);
        c1Parents.add(cxref1);
        c1.setAllParentCategoryXrefs(c1Parents);
        
        CategoryXref cxref2 = new CategoryXrefImpl();
        cxref2.setCategory(c1);
        cxref2.setSubCategory(c2);
        c2Parents.add(cxref2);
        c2.setAllParentCategoryXrefs(c2Parents);
        
        String c1Url = c1.getGeneratedUrl();
        String c2Url = c2.getGeneratedUrl();
        
        
        assertTrue("cat-two/cat-one".equals(c1Url));
        assertTrue("cat-one/cat-two".equals(c2Url));
    }


}
