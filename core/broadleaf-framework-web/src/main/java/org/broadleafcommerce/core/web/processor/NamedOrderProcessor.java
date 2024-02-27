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

package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will add the desired named order to the model
 *
 * @author elbertbautista
 */
@Component("blNamedOrderProcessor")
public class NamedOrderProcessor extends AbstractModelVariableModifierProcessor {
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     *
     * NOTE: thymeleaf normalizes the attribute names by converting all to lower-case
     * we will use the underscore instead of camel case to avoid confusion
     *
     */
    public NamedOrderProcessor() {
        super(TemplateMode.HTML, "blc", "named_order", true, null, false, 10000);
    }



    @Override
    protected Map<String, Object> populateModelVariables(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        Map<String,Object> result = new HashMap<>();
        Customer customer = CustomerState.getCustomer();
        Map<String, String> attributeMap = tag.getAttributeMap();
        String orderVar = attributeMap.get("orderVar");
        String orderName = attributeMap.get("orderName");

        Order order = orderService.findNamedOrderForCustomer(orderName, customer);
        if (order != null) {
            result.put(orderVar, order);
        } else {
            result.put(orderVar, new NullOrderImpl());
        }
        return result;
    }
}
