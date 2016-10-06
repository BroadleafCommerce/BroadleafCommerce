/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.search.service.solr;

import org.broadleafcommerce.core.search.domain.SearchCriteria;

/**
 * Convenience methods for converting simple MVEL rules to Solr SearchCriteria
 * 
 * @author Chris Kittrell (ckittrell)
 */
public interface MvelToSearchCriteriaConversionService {
    
    /**
     * Given a simple MVEL rule, convert it to Search Criteria.
     * 
     * @param mvelRule
     * @return the converted MVEL rule in the form of Search Criteria
     */
    public SearchCriteria convert(String mvelRule);

}
