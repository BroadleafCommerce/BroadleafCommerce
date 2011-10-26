package org.broadleafcommerce.cms.structure.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentRule;
import org.springframework.stereotype.Service;

/**
 * By default, this rule processor can handles all map values except for "cart".
 *
 * For BLC out of box implementations, this includes customer, time, and request based
 * rules.
 *
 * Created by bpolster.
 *
 */
@Service("blContentDefaultRuleProcessor")
public class StructuredContentDefaultRuleProcessor extends AbstractStructuredContentRuleProcessor {
    private static final Log LOG = LogFactory.getLog(StructuredContentDefaultRuleProcessor.class);

    private static String AND = " && ";

    /**
     * Returns true if the
     * @param sc
     * @return
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
