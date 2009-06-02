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
package org.broadleafcommerce.rules.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.springframework.stereotype.Service;

@Service("blRuleBaseService")
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
