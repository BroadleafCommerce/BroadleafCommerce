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

package org.broadleafcommerce.core.web.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * A Thymeleaf processor that receives a Product object as attribute value:
 * and returns its url as the value of an href attribute.
 * Such url can optionally have the Product id property appended as a parameter, according 
 * to the system property "product.url.use.id" 
 * This is used at the productListItem page in order to facilitate the breadcrumbs implementation 
 * 
 * @author gdiaz
 */
public class ProductUrlIdProcessor extends AbstractAttributeModifierAttrProcessor {

    //private static final Log LOG = LogFactory.getLog(ProductUrlIdProcessor.class);

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public ProductUrlIdProcessor() {
        super("producturlid");
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {

        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue(attributeName));

        Product product = (Product) expression.execute(arguments.getConfiguration(), arguments);
        String url = null;
        Map<String, String> attrs = new HashMap<String, String>();

        boolean usesProductId = BLCSystemProperty.resolveBooleanSystemProperty("product.url.use.id");

        url = product.getUrl();
        if (usesProductId) {
            Map<String, String[]> urlParams = new HashMap<String, String[]>();
            urlParams.put("productId", new String[] { Long.toString(product.getId()) });
            url = ProcessorUtils.getUrl(url, urlParams);
        }

        attrs.put("href", url);
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
