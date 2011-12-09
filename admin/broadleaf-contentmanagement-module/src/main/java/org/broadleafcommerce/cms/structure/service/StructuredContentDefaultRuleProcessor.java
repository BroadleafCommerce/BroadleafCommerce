/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.structure.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentRule;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * By default, this rule processor combines all of the rules from
 * {@link org.broadleafcommerce.cms.structure.domain.StructuredContent#getStructuredContentMatchRules()}
 * into a single MVEL expression.
 *
 * @author bpolster.
 *
 */
@Service("blContentDefaultRuleProcessor")
public class StructuredContentDefaultRuleProcessor extends AbstractStructuredContentRuleProcessor {
    private static final Log LOG = LogFactory.getLog(StructuredContentDefaultRuleProcessor.class);

    private static String AND = " && ";

    /**
     * Returns true if all of the rules associated with the passed in <code>StructuredContent</code>
     * item match based on the passed in vars.
     *
     * Also returns true if no rules are present for the passed in item.
     *
     * @param sc - a structured content item to test
     * @param vars - a map of objects used by the rule MVEL expressions
     * @return the result of the rule checks
     */
    public boolean checkForMatch(StructuredContent sc, Map<String, Object> vars) {
        StringBuffer ruleExpression = null;
        Map<String, StructuredContentRule> ruleMap = sc.getStructuredContentMatchRules();
        if (ruleMap != null) {
            for (String ruleKey : ruleMap.keySet()) {
                if (ruleExpression == null) {
                    ruleExpression = new StringBuffer(ruleMap.get(ruleKey).getMatchRule());
                } else {
                    ruleExpression.append(AND);
                    ruleExpression.append(ruleMap.get(ruleKey).getMatchRule());
                }
            }
        }

        if (ruleExpression != null) {
            if (LOG.isTraceEnabled())  {
                LOG.trace("Processing content rule for StructuredContent with id " + sc.getId() +".   Value = " + ruleExpression.toString());
            }
            boolean result = executeExpression(ruleExpression.toString(), vars);
            if (! result) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Content failed to pass rule and will not be included for StructuredContent with id " + sc.getId() +".   Value = " + ruleExpression.toString());
                }
            }

            return result;
        } else {
            // If no rule found, then consider this a match.
            return true;
        }
    }
}
