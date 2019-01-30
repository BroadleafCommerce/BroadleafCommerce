/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.common.web.filter;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafRequestFilter;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>
 * Used to validate usages of em.find() when querying for a primary key specifically across sibling Multi-Tenant sites.
 * This Servlet filter should only turned on if you often query an entity by ID. Generally this only happens in
 * API-based use cases since most other use cases rely on querying by name, url, etc and not directly on a primary key.
 * 
 * <p>
 * This is intentionally not activated by default but is included here for convenience within other projects. If you are
 * in Spring Boot, this filter can be activated simply in an @Bean method. If you are not using Spring Boot, this
 * filter must come <i>after</i> the {@link BroadleafRequestFilter} and is generally initialized in {@code applicationContext-filter.xml}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @since 5.2
 * @see BroadleafRequestContext#setInternalIgnoreFilters(Boolean)
 */
@Order(FilterOrdered.POST_SECURITY_MEDIUM)
public class EntityManagerFindValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            BroadleafRequestContext.getBroadleafRequestContext().setInternalValidateFind(true);
            filterChain.doFilter(request, response);
        } finally {
            BroadleafRequestContext.getBroadleafRequestContext().setInternalIgnoreFilters(false);
        }        
    }

}
