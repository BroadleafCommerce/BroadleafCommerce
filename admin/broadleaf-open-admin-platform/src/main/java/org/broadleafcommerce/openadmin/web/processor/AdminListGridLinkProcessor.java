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

package org.broadleafcommerce.openadmin.web.processor;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * A Thymeleaf processor that will generate the appropriate ID for a given admin component.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Component("blAdminListGridLinkProcessor")
public class AdminListGridLinkProcessor extends AbstractAttributeModifierAttrProcessor {

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public AdminListGridLinkProcessor() {
        super("list_grid_link");
    }

    @Override
    public int getPrecedence() {
        return 10002;
    }

    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        String[] expressions = element.getAttributeValue(attributeName).split(",");
        
        String sectionKey = (String) StandardExpressionProcessor.processExpression(arguments, expressions[0]);
        ListGrid listGrid = (ListGrid) StandardExpressionProcessor.processExpression(arguments, expressions[1]);
        ListGridRecord record = (ListGridRecord) StandardExpressionProcessor.processExpression(arguments, expressions[2]);
        String attr = (String) StandardExpressionProcessor.processExpression(arguments, expressions[3]);

        String link = "/" + sectionKey + "/";
        //TODO  apa This is slow. 
        link = (String) StandardExpressionProcessor.processExpression(arguments, "@{" + link + "}");

        if (listGrid.getContainingEntityId() != null && StringUtils.isNotBlank(listGrid.getSubCollectionFieldName())) {
            link += listGrid.getContainingEntityId() + "/" + listGrid.getSubCollectionFieldName() + "/";
        }

        link += record.getId();

        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put(attr, link);
        return attrs;
    }

    @Override
    protected ModificationType getModificationType(Arguments arguments, Element element, String attributeName, String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }

    @Override
    protected boolean removeAttributeIfEmpty(Arguments arguments, Element element, String attributeName, String newAttributeName) {
        return true;
    }

    @Override
    protected boolean recomputeProcessorsAfterExecution(Arguments arguments, Element element, String attributeName) {
        return false;
    }

}
