package org.broadleafcommerce.cms.structure.service;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;

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
    public boolean checkForMatch(StructuredContent sc, Map<String,Object> valueMap);
}
