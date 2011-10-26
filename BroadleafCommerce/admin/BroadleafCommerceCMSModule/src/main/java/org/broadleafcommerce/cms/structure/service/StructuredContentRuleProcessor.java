package org.broadleafcommerce.cms.structure.service;

import java.util.Map;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;

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
 * Instead, we've opted to create this interface and allow other components to
 * add rule-processors as needed.
 *
 * @see StructuredContentDefaultRuleProcessor and StructuredContentCartRuleProcessor
 *
 *
 * Created by bpolster.
 */
public interface StructuredContentRuleProcessor {

    /**
     * Returns true if the
     * @param sc
     * @return
     */
    public boolean checkForMatch(StructuredContent sc, Map<String,Object> valueMap);
}
