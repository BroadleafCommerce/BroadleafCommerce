/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.core.search.redirect.domain.SearchRedirectImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * @deprecated Replaced in functionality by {@link SearchRedirectImpl}
 */
@Deprecated
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SearchInterceptImpl implements SearchIntercept {
    
    @Id
    @GeneratedValue(generator = "SearchInterceptId")
    @GenericGenerator(
        name="SearchInterceptId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SearchInterceptImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.SearchInterceptImpl")
        }
    )
    @Column(name = "SEARCH_INTERCEPT_ID")
    protected Long id;
    
    @Column(name = "TERM")
    @Index(name="SEARCHINTERCEPT_TERM_INDEX", columnNames={"TERM"})
    private String term;
    
    @Column(name = "REDIRECT")
    private String redirect;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.search.domain.SearchIntercept#getTerm()
     */
    @Override
    public String getTerm() {
        return term;
    }
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.search.domain.SearchIntercept#setTerm(java.lang.String)
     */
    @Override
    public void setTerm(String term) {
        this.term = term;
    }
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.search.domain.SearchIntercept#getRedirect()
     */
    @Override
    public String getRedirect() {
        return redirect;
    }
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.search.domain.SearchIntercept#setRedirect(java.lang.String)
     */
    @Override
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
