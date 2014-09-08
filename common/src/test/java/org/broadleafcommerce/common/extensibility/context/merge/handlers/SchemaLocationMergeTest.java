/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.extensibility.context.merge.handlers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.xerces.impl.xs.opti.DefaultNode;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import java.util.Set;

/**
 * Tests for {@link SchemaLocationNodeValueMerge}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class SchemaLocationMergeTest {

    protected static SchemaLocationNodeValueMerge merge;
    
    @BeforeClass
    public static void setup() {
        merge = new SchemaLocationNodeValueMerge();
    }
    
    @Test
    public void testReplacementRegex() {
        String val = "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.1.xsd     http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.1.xsd";
        String replacedVal = merge.getSanitizedValue(val);
        assertEquals(replacedVal, "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd     http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd");
    }
    
    @Test
    public void testNodeAttributes() {
        Node node1 = new DummyNode();
        node1.setNodeValue("http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd"
                + "\nhttp://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-8.4.xsd"
                + "\nhttp://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-9.4.xsd"
                + "\nhttp://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.1.xsd");
        Node node2 = new DummyNode();
        node2.setNodeValue("http://www.springframework.org/schema/beans       http://www.springframework.org/schema/beans/spring-beans.xsd");
        
        Set<String> mergedVals = merge.getMergedNodeValues(node1, node2);
        
        assertArrayEquals(new String[] {"http://www.springframework.org/schema/beans",
                "http://www.springframework.org/schema/beans/spring-beans.xsd",
                "http://www.springframework.org/schema/tx",
                "http://www.springframework.org/schema/tx/spring-tx.xsd"}, mergedVals.toArray());
    }
    
    @Test
    public void testAddedAttributes() {
        Node node1 = new DummyNode();
        node1.setNodeValue("http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd");
        Node node2 = new DummyNode();
        node2.setNodeValue("http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.1.xsd");
        
        Set<String> mergedVals = merge.getMergedNodeValues(node1, node2);
        
        assertArrayEquals(new String[] {"http://www.springframework.org/schema/beans",
                "http://www.springframework.org/schema/beans/spring-beans.xsd",
                "http://www.springframework.org/schema/tx",
                "http://www.springframework.org/schema/tx/spring-tx.xsd"}, mergedVals.toArray());
    }
    
    public class DummyNode extends DefaultNode {
        protected String nodeValue;
        
        @Override
        public String getNodeValue() throws DOMException {
            return nodeValue;
        }
        
        @Override
        public void setNodeValue(String nodeValue) throws DOMException {
            this.nodeValue = nodeValue;
        }
    }
}
