/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.page.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.rule.AbstractRuleProcessor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * By default, this rule processor combines all of the rules from
 * {@link org.broadleafcommerce.cms.page.domain.Page#getPageMatchRules()}
 * into a single MVEL expression.
 *
 * @author bpolster.
 *
 */
@Service("blPageDefaultRuleProcessor")
public class PageDefaultRuleProcessor extends AbstractRuleProcessor<PageDTO> {
    private static final Log LOG = LogFactory.getLog(PageDefaultRuleProcessor.class);

    /**
     * Returns true if all of the rules associated with the passed in <code>Page</code>
     * item match based on the passed in vars.
     *
     * Also returns true if no rules are present for the passed in item.
     *
     * @param sc - a page item to test
     * @param vars - a map of objects used by the rule MVEL expressions
     * @return the result of the rule checks
     */
    public boolean checkForMatch(PageDTO page, Map<String, Object> vars) {
        String ruleExpression = page.getRuleExpression();

        if (ruleExpression != null) {
            if (LOG.isTraceEnabled())  {
                LOG.trace("Processing content rule for page with id " + page.getId() +".   Value = " + ruleExpression);
            }
            boolean result = executeExpression(ruleExpression, vars);
            if (! result) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Page failed to pass rule and will not be included for Page with id " + page.getId() +".   Value = " + ruleExpression);
                }
            }

            return result;
        } else {
            // If no rule found, then consider this a match.
            return true;
        }
    }
}