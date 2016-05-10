/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
