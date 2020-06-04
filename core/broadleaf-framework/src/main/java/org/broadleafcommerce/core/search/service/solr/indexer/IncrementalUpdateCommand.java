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

import java.util.List;

public class IncrementalUpdateCommand extends SolrUpdateCommand {

    private static final long serialVersionUID = 1L;
    
    private final List<SolrInputDocument> docs;
    
    private final List<String> deleteQueries;
    
    public IncrementalUpdateCommand(List<SolrInputDocument> docs, List<String> deleteQueries) {
        this.docs = docs;
        this.deleteQueries = deleteQueries;
    }
    
    public List<SolrInputDocument> getSolrInputDocuments() {
        return docs;
    }
    
    public List<String> getDeleteQueries() {
        return deleteQueries;
    }

    @Override
    public String toString() {
        StringBuffer out = new StringBuffer(getClass().getName());
        out.append("\n");
        out.append("  Delete Queries: \n");
        if (getDeleteQueries() != null && !getDeleteQueries().isEmpty()) {
            for (String query : getDeleteQueries()) {
                out.append("    ").append(query).append("\n");
            }
        } else {
            out.append("    ").append("-- No Delete Queries --").append("\n");
        }
        
        out.append("  SolrInputDocuments: \n");
        if (getSolrInputDocuments() != null && !getSolrInputDocuments().isEmpty()) {
            for (SolrInputDocument doc : getSolrInputDocuments()) {
                out.append("    ").append(doc.toString()).append("\n");
            }
        } else {
            out.append("    ").append("-- No SolrInputDocuments --").append("\n");
        }
        
        return out.toString();
    }

    
}
