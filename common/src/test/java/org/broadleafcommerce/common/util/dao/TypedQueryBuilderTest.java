/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util.dao;

import junit.framework.TestCase;


public class TypedQueryBuilderTest extends TestCase {
    
    public void testNoParameters() {
        TypedQueryBuilder<String> q = new TypedQueryBuilder<String>(String.class, "test");
        StringBuilder expected = new StringBuilder("SELECT test FROM " + String.class.getName() + " test");
        assertEquals(q.toQueryString(), expected.toString());
    }
    
    public void testSingleParameter() {
        TypedQueryBuilder<String> q = new TypedQueryBuilder<String>(String.class, "test");
        q.addRestriction("test.attr", "=", "sample");
        StringBuilder expected = new StringBuilder("SELECT test FROM " + String.class.getName() + " test")
            .append(" WHERE (test.attr = :p0)");
        assertEquals(q.toQueryString(), expected.toString()); 
        assertEquals(q.getParamMap().get("p0"), "sample");
        assertEquals(q.getParamMap().size(), 1);
    }
    
    public void testTwoParameters() {
        TypedQueryBuilder<String> q = new TypedQueryBuilder<String>(String.class, "test");
        q.addRestriction("test.attr", "=", "sample");
        q.addRestriction("test.attr2", "=", "sample2");
        StringBuilder expected = new StringBuilder("SELECT test FROM " + String.class.getName() + " test")
            .append(" WHERE (test.attr = :p0) AND (test.attr2 = :p1)");
        assertEquals(q.toQueryString(), expected.toString()); 
        assertEquals(q.getParamMap().get("p0"), "sample");
        assertEquals(q.getParamMap().get("p1"), "sample2");
        assertEquals(q.getParamMap().size(), 2);
    }
    
    public void testThreeParameters() {
        TypedQueryBuilder<String> q = new TypedQueryBuilder<String>(String.class, "test");
        q.addRestriction("test.attr", "=", "sample");
        q.addRestriction("test.attr2", "=", "sample2");
        q.addRestriction("test.attr3", "=", "sample3");
        StringBuilder expected = new StringBuilder("SELECT test FROM " + String.class.getName() + " test")
            .append(" WHERE (test.attr = :p0) AND (test.attr2 = :p1) AND (test.attr3 = :p2)");
        assertEquals(q.toQueryString(), expected.toString()); 
        assertEquals(q.getParamMap().get("p0"), "sample");
        assertEquals(q.getParamMap().get("p1"), "sample2");
        assertEquals(q.getParamMap().get("p2"), "sample3");
        assertEquals(q.getParamMap().size(), 3);
    }
    
    public void testOneNested() {
        TypedQueryBuilder<String> q = new TypedQueryBuilder<String>(String.class, "test");
        
        TQRestriction r = new TQRestriction(TQRestriction.Mode.AND)
            .addChildRestriction(new TQRestriction("test.startDate", "&lt;", "123"))
            .addChildRestriction(new TQRestriction(TQRestriction.Mode.OR)
                .addChildRestriction(new TQRestriction("test.endDate", "is null"))
                .addChildRestriction(new TQRestriction("test.endDate", "&gt;", "456")));
        
        q.addRestriction("test.attr", "=", "sample");
        q.addRestriction(r);
        
        StringBuilder expected = new StringBuilder("SELECT test FROM " + String.class.getName() + " test")
            .append(" WHERE (test.attr = :p0)")
            .append(" AND ((test.startDate &lt; :p1_0) AND ((test.endDate is null) OR (test.endDate &gt; :p1_1_1)))");
        assertEquals(q.toQueryString(), expected.toString()); 
        
        assertEquals(q.getParamMap().get("p0"), "sample");
        assertEquals(q.getParamMap().get("p1_0"), "123");
        assertEquals(q.getParamMap().get("p1_1"), null);
        assertEquals(q.getParamMap().get("p1_1_0"), null);
        assertEquals(q.getParamMap().get("p1_1_1"), "456");
        assertEquals(q.getParamMap().size(), 5);
    }
    
    public void testTwoNested() {
        TypedQueryBuilder<String> q = new TypedQueryBuilder<String>(String.class, "test");
        
        TQRestriction r = new TQRestriction(TQRestriction.Mode.AND)
            .addChildRestriction(new TQRestriction("test.startDate", "&lt;", "123"))
            .addChildRestriction(new TQRestriction(TQRestriction.Mode.OR)
                .addChildRestriction(new TQRestriction("test.endDate", "is null"))
                .addChildRestriction(new TQRestriction("test.endDate", "&gt;", "456")));
        
        TQRestriction r2 = new TQRestriction(TQRestriction.Mode.OR)
            .addChildRestriction(new TQRestriction("test.res1", "=", "333"))
            .addChildRestriction(new TQRestriction(TQRestriction.Mode.AND)
                .addChildRestriction(new TQRestriction("test.res2", "is null"))
                .addChildRestriction(new TQRestriction("test.res3", "&gt;", "456")));
        
        q.addRestriction("test.attr", "=", "sample");
        q.addRestriction(r);
        q.addRestriction(r2);
        
        System.out.println(q.toQueryString());
        
        StringBuilder expected = new StringBuilder("SELECT test FROM " + String.class.getName() + " test")
            .append(" WHERE (test.attr = :p0)")
            .append(" AND ((test.startDate &lt; :p1_0) AND ((test.endDate is null) OR (test.endDate &gt; :p1_1_1)))")
            .append(" AND ((test.res1 = :p2_0) OR ((test.res2 is null) AND (test.res3 &gt; :p2_1_1)))");
        assertEquals(q.toQueryString(), expected.toString()); 
        
        assertEquals(q.getParamMap().get("p0"), "sample");
        assertEquals(q.getParamMap().get("p1_0"), "123");
        assertEquals(q.getParamMap().get("p1_1"), null);
        assertEquals(q.getParamMap().get("p1_1_0"), null);
        assertEquals(q.getParamMap().get("p1_1_1"), "456");
        assertEquals(q.getParamMap().get("p2_0"), "333");
        assertEquals(q.getParamMap().get("p2_1"), null);
        assertEquals(q.getParamMap().get("p2_1_0"), null);
        assertEquals(q.getParamMap().get("p2_1_1"), "456");
        assertEquals(q.getParamMap().size(), 9);
    }
    
    public void testCountQuery() {
        TypedQueryBuilder<String> q = new TypedQueryBuilder<String>(String.class, "test");
        StringBuilder expected = new StringBuilder("SELECT COUNT(*) FROM " + String.class.getName() + " test");
        assertEquals(q.toQueryString(true), expected.toString());
    }

}

