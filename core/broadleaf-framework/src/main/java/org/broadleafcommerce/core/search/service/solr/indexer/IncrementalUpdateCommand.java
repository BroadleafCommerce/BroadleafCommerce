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
