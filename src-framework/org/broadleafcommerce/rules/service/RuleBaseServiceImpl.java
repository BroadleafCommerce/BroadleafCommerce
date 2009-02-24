package org.broadleafcommerce.rules.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.springframework.stereotype.Repository;

@Repository("ruleBaseService")
public class RuleBaseServiceImpl implements RuleBaseService {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private RuleBase ruleBase;

    public RuleBase getRuleBase() {

        if (ruleBase == null) {
            ruleBase = RuleBaseFactory.newRuleBase();
        }

        return ruleBase;
    }
}
