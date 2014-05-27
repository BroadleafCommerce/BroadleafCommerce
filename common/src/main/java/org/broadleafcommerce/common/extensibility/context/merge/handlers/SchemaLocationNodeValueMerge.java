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

import org.w3c.dom.Node;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Designed to specifically handle the merge of schemaLocation references. This takes any of the Spring XSD references
 * for particular Spring versions and replaces them with XSDs without a version reference. This allows the final XSD
 * reference to refer to the latest version of Spring, and reduces the need for modules to be updated with every Spring
 * update.
 * 
 * <p>
 * This will also prevents multiple XSD references that cause parse exceptions when the final XML file is presented to Spring
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class SchemaLocationNodeValueMerge extends SpaceDelimitedNodeValueMerge {

    @Override
    protected Set<String> getMergedNodeValues(Node node1, Node node2) {
        String node1Values = getSanitizedValue(node1.getNodeValue());
        String node2Values = getSanitizedValue(node2.getNodeValue());
        
        Set<String> finalItems = new LinkedHashSet<String>();
        for (String node1Value : node1Values.split(getRegEx())) {
            finalItems.add(node1Value.trim());
        }
        for (String node2Value : node2Values.split(getRegEx())) {
            // Only add in this new attribute value if we haven't seen it yet
            if (!finalItems.contains(node2Value.trim())) {
                finalItems.add(node2Value.trim());
            }
        }
        return finalItems;
    }
    
    /**
     * <p>
     * Sanitizes the given attribute value by stripping out the version number for the Spring XSDs.
     * 
     * <p>
     * For example, given http://www.springframework.org/schema/beans/<b>spring-beans-4.0.xsd</b> this will return
     * http://www.springframework.org/schema/beans/<b>spring-beans.xsd</b>
     * 
     * @param attributeValue the value of an xsi:schemaLocation attribute
     * @return the given string with all of the Spring XSD version numbers stripped out.
     */
    protected String getSanitizedValue(String attributeValue) {
        Pattern springVersionPattern = Pattern.compile("(spring-\\w*-[0-9]\\.[0-9]\\.xsd)");
        Matcher versionMatcher = springVersionPattern.matcher(attributeValue);
        while (versionMatcher.find()) {
            String match = versionMatcher.group();
            String replacement = match.replaceAll("-[0-9]\\.[0-9]", "");
            attributeValue = attributeValue.replaceAll(match, replacement);
        }
        return attributeValue;
    }
    
}
