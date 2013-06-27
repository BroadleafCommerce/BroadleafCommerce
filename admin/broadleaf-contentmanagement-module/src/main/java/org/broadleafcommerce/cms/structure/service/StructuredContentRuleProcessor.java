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

package org.broadleafcommerce.cms.structure.service;

import org.broadleafcommerce.cms.structure.dto.StructuredContentDTO;

import java.util.Map;

/**
 * StructuredContent rule processors check each content item to see if it qualifies
 * for inclusion in the result set.
 *
 * This is called by StructuredContentService to determine if a given content item
 * should be returned to the caller.
 *
 * BLC created rule processors to solve a dependency issue.    Some variables to be used
 * in rule processing are within the scope of the CMS module while others are not.
 *
 * For example, checking for cart rules would tie the CMS rules engine with a direct
 * dependency on the cart.
 *
 * Instead, we've opted to create this interface which allows other components to
 * add rule-processors as needed.
 *
 * @see {@link StructuredContentDefaultRuleProcessor}
 *
 *
 * @author bpolster.
 */
public interface StructuredContentRuleProcessor {

    /**
     * Returns true if the passed in <code>StructuredContent</code> is valid according
     * to this rule processor.
     *
     * @param sc
     * @return
     */
    public boolean checkForMatch(StructuredContentDTO sc, Map<String,Object> valueMap);
}
