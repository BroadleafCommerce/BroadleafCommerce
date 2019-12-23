/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr.indexer;

import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.domain.IndexField;

import java.util.List;

/**
 * Implementations of this interface do the heavy lifting with respect to executing the provided commands.  This should only be called from within 
 * the {@link AbstractSolrIndexUpdateServiceImpl}, which provides serialization of the commands.
 * 
 * @author Kelly Tisdell
 *
 */
public interface SolrIndexUpdateCommandHandler {

    /**
     * Entry point from which this component can delegate action based on the type of {@link SolrUpdateCommand}.
     * 
     * This should never be called directly.  Rather it will be called from inside the {@link AbstractSolrIndexUpdateServiceImpl}.
     * 
     * @param command
     * @throws ServiceException
     */
    public <C extends SolrUpdateCommand> void executeCommand(C command) throws ServiceException;
    
    /**
     * Command group or identifier for which this component can respond.  E.g. "catalog".  Components that use the same command group 
     * operate on the same Solr collections / aliases (indexes).
     * 
     * Multiple invocations of this method must return the same result.
     * 
     * @return
     */
    public String getCommandGroup();
    
    /**
     * The "live" or customer facing collection (index) name or alias. This should return a non-null, non-empty string.  Every invocation should return the same value. (e.g. "catalog").
     * 
     * @return
     */
    public String getForegroundCollectionName();
    
    /**
     * The background or "offline" collection (index) name or alias. This should return a non-null, non-empty string.  Every invocation should return the same value. (e.g. "catalog_reindex").
     * 
     * @return
     */
    public String getBackgroundCollectionName();
    
    /**
     * Provides an interface for a caller to convert an {@link Indexable} into a {@link SolrInputDocument}.  This may return null if the implementor does not want 
     * the specified {@link Indexable} indexed.
     * @param indexable
     * @return
     * 
     */
    public SolrInputDocument buildDocument(Indexable indexable);
    
    /**
     * Provides an interface for a caller to convert an {@link Indexable} into a {@link SolrInputDocument}.  This may return null if the implementor does not want 
     * the specified {@link Indexable} indexed.
     * 
     * @param indexable
     * @param fields
     * @param locales
     * @return
     * 
     */
    public SolrInputDocument buildDocument(final Indexable indexable, List<IndexField> fields, List<Locale> locales);
    
}
