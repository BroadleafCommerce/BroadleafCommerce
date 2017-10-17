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
package org.broadleafcommerce.core.search.index;

import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.catalog.domain.Product;

import java.util.List;

/**
 * Interface that defines a method to accept an Indexable instance (e.g. Product) and return a search engine-specific input 
 * (e.g. SolrInputDocument).
 * 
 * @author Kelly Tisdell
 *
 * @param <I>
 * @param <D>
 */
public interface DocumentBuilder<I extends Indexable, D> {
    
    /**
     * Generic interface to build a document or input for the index.  For example, this could 
     * accept a {@link Product} and return a SolrInputDocument.  If the Indexable parameter 
     * should not be indexed, for whatever reason, this method should return null.
     * 
     * @param indexable
     * @param locales
     * @return
     */
    public D buildDocument(I indexable, List<Locale> locales);
    
}
