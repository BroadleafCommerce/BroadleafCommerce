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

package org.broadleafcommerce.core.search.service.solr;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.StreamingResponseCallback;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper implementation of Solr that delegates to an {@link HttpSolrClient}.  With older versions of SolrJ, there was no ability to specify a 
 * a collection on an {@link HttpSolrClient}.  As a result, you needed two different clients, one for each collection.  For example, you might have 
 * a client with the base URL: http://localhost:8983/solr/catalog and another with the base URL of http://localhost:8983/solr/catalog_reindex.
 * <p/>
 * What this class allows for is a "defaultCollection".  If the base URL is http://localhost:8983/solr for example, and the default collection is "catalog", then a call 
 * to {@link DelegatingHttpSolrClient#query(new SolrQuery("foo:bar")} will search the "catalog" index, or http://localhost:8983/solr/catalog.  Alternatively, a call to 
 * {@link DelegatingHttpSolrClient#query("catalog_reindex", new SolrQuery("foo:bar"))} will search the "catalog_reindex" index, or http://localhost:8983/solr/catalog_reindex.
 * 
 * The same thing goes for writes.  This class simply delegates to the delegate passed into the constructor.
 * 
 * @author Kelly Tisdell
 *
 */
public class DelegatingHttpSolrClient extends SolrClient {
    
    private static final long serialVersionUID = 1L;
    
    protected final HttpSolrClient delegate;
    protected final String defaultCollection;
    protected final String defaultCollectionPath;
    
    public DelegatingHttpSolrClient(HttpSolrClient delegate) {
        Assert.notNull(delegate, "SolrClient cannot be null.");
        this.delegate = delegate;
        this.defaultCollection = null;
        defaultCollectionPath = null;
    }
    
    public DelegatingHttpSolrClient(HttpSolrClient delegate, String defaultCollection) {
        Assert.notNull(delegate, "SolrClient cannot be null.");
        this.delegate = delegate;
        if (StringUtils.isNotBlank(defaultCollection)) {
            this.defaultCollection = defaultCollection;
            this.defaultCollectionPath = '/' + defaultCollection;
        } else {
            this.defaultCollection = null;
            this.defaultCollectionPath = null;
        }
    }

    @Override
    public NamedList<Object> request(@SuppressWarnings("rawtypes") SolrRequest request, String collection) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.request(request);
        } else {
            return delegate.request(request, collection);
        }
    }

    @Override
    public UpdateResponse add(String collection, Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.add(docs);
        } else {
            return delegate.add(collection, docs);
        }
    }

    @Override
    public UpdateResponse add(Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.add(docs);
        } else {
            return delegate.add(defaultCollection, docs);
        }
    }

    @Override
    public UpdateResponse add(String collection, Collection<SolrInputDocument> docs, int commitWithinMs) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.add(docs, commitWithinMs);
        } else {
            return delegate.add(collection, docs, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse add(Collection<SolrInputDocument> docs, int commitWithinMs) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.add(docs, commitWithinMs);
        } else {
            return delegate.add(defaultCollection, docs, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse add(String collection, SolrInputDocument doc) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.add(doc);
        } else {
            return delegate.add(collection, doc);
        }
    }

    @Override
    public UpdateResponse add(SolrInputDocument doc) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.add(doc);
        } else {
            return delegate.add(defaultCollection, doc);
        }
    }

    @Override
    public UpdateResponse add(String collection, SolrInputDocument doc, int commitWithinMs) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.add(doc, commitWithinMs);
        } else {
            return delegate.add(collection, doc, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse add(SolrInputDocument doc, int commitWithinMs) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.add(doc, commitWithinMs);
        } else {
            return delegate.add(defaultCollection, doc, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse add(String collection, Iterator<SolrInputDocument> docIterator) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.add(docIterator);
        } else {
            return delegate.add(collection, docIterator);
        }
    }

    @Override
    public UpdateResponse add(Iterator<SolrInputDocument> docIterator) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.add(docIterator);
        } else {
            return delegate.add(defaultCollection, docIterator);
        }
    }

    @Override
    public UpdateResponse addBean(String collection, Object obj) throws IOException, SolrServerException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.addBean(obj);
        } else {
            return delegate.addBean(collection, obj);
        }
    }

    @Override
    public UpdateResponse addBean(Object obj) throws IOException, SolrServerException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.addBean(obj);
        } else {
            return delegate.addBean(defaultCollection, obj);
        }
    }

    @Override
    public UpdateResponse addBean(String collection, Object obj, int commitWithinMs) throws IOException, SolrServerException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.addBean(obj, commitWithinMs);
        } else {
            return delegate.addBean(collection, obj, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse addBean(Object obj, int commitWithinMs) throws IOException, SolrServerException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.addBean(obj, commitWithinMs);
        } else {
            return delegate.addBean(defaultCollection, obj, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse addBeans(String collection, Collection<?> beans) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.addBeans(beans);
        } else {
            return delegate.addBeans(collection, beans);
        }
    }

    @Override
    public UpdateResponse addBeans(Collection<?> beans) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.addBeans(beans);
        } else {
            return delegate.addBeans(defaultCollection, beans);
        }
    }

    @Override
    public UpdateResponse addBeans(String collection, Collection<?> beans, int commitWithinMs) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.addBeans(beans, commitWithinMs);
        } else {
            return delegate.addBeans(collection, beans, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse addBeans(Collection<?> beans, int commitWithinMs) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.addBeans(beans, commitWithinMs);
        } else {
            return delegate.addBeans(defaultCollection, beans, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse addBeans(String collection, Iterator<?> beanIterator) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.addBeans(beanIterator);
        } else {
            return delegate.addBeans(collection, beanIterator);
        }
    }

    @Override
    public UpdateResponse addBeans(Iterator<?> beanIterator) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.addBeans(beanIterator);
        } else {
            return delegate.addBeans(defaultCollection, beanIterator);
        }
    }

    @Override
    public UpdateResponse commit(String collection) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.commit();
        } else {
            return delegate.commit(collection);
        }
    }

    @Override
    public UpdateResponse commit() throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.commit();
        } else {
            return delegate.commit(defaultCollection);
        }
    }

    @Override
    public UpdateResponse commit(String collection, boolean waitFlush, boolean waitSearcher) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.commit(waitFlush, waitSearcher);
        } else {
            return delegate.commit(collection, waitFlush, waitSearcher);
        }
    }

    @Override
    public UpdateResponse commit(boolean waitFlush, boolean waitSearcher) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.commit(waitFlush, waitSearcher);
        } else {
            return delegate.commit(defaultCollection, waitFlush, waitSearcher);
        }
    }

    @Override
    public UpdateResponse commit(String collection, boolean waitFlush, boolean waitSearcher, boolean softCommit) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.commit(waitFlush, waitSearcher, softCommit);
        } else {
            return delegate.commit(collection, waitFlush, waitSearcher, softCommit);
        }
    }

    @Override
    public UpdateResponse commit(boolean waitFlush, boolean waitSearcher, boolean softCommit) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.commit(waitFlush, waitSearcher, softCommit);
        } else {
            return delegate.commit(defaultCollection, waitFlush, waitSearcher, softCommit);
        }
    }

    @Override
    public UpdateResponse optimize(String collection) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.optimize();
        } else {
            return delegate.optimize(collection);
        }
    }

    @Override
    public UpdateResponse optimize() throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.optimize();
        } else {
            return delegate.optimize(defaultCollection);
        }
    }

    @Override
    public UpdateResponse optimize(String collection, boolean waitFlush, boolean waitSearcher) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.optimize(waitFlush, waitSearcher);
        } else {
            return delegate.optimize(collection, waitFlush, waitSearcher);
        }
    }

    @Override
    public UpdateResponse optimize(boolean waitFlush, boolean waitSearcher) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.optimize(waitFlush, waitSearcher);
        } else {
            return delegate.optimize(defaultCollection, waitFlush, waitSearcher);
        }
    }

    @Override
    public UpdateResponse optimize(String collection, boolean waitFlush, boolean waitSearcher, int maxSegments) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.optimize(waitFlush, waitSearcher, maxSegments);
        } else {
            return delegate.optimize(collection, waitFlush, waitSearcher, maxSegments);
        }
    }

    @Override
    public UpdateResponse optimize(boolean waitFlush, boolean waitSearcher, int maxSegments) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.optimize(waitFlush, waitSearcher, maxSegments);
        } else {
            return delegate.optimize(defaultCollection, waitFlush, waitSearcher, maxSegments);
        }
    }

    @Override
    public UpdateResponse rollback(String collection) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.rollback();
        } else {
            return delegate.rollback(collection);
        }
    }

    @Override
    public UpdateResponse rollback() throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.rollback();
        } else {
            return delegate.rollback(defaultCollection);
        }
    }

    @Override
    public UpdateResponse deleteById(String collection, String id) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.deleteById(id);
        } else {
            return delegate.deleteById(collection, id);
        }
    }

    @Override
    public UpdateResponse deleteById(String id) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.deleteById(id);
        } else {
            return delegate.deleteById(defaultCollection, id);
        }
    }

    @Override
    public UpdateResponse deleteById(String collection, String id, int commitWithinMs) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.deleteById(id, commitWithinMs);
        } else {
            return delegate.deleteById(collection, id, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse deleteById(String id, int commitWithinMs) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.deleteById(id, commitWithinMs);
        } else {
            return delegate.deleteById(defaultCollection, id, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse deleteById(String collection, List<String> ids) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.deleteById(ids);
        } else {
            return delegate.deleteById(collection, ids);
        }
    }

    @Override
    public UpdateResponse deleteById(List<String> ids) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.deleteById(ids);
        } else {
            return delegate.deleteById(defaultCollection, ids);
        }
    }

    @Override
    public UpdateResponse deleteById(String collection, List<String> ids, int commitWithinMs) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.deleteById(ids, commitWithinMs);
        } else {
            return delegate.deleteById(collection, ids, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse deleteById(List<String> ids, int commitWithinMs) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.deleteById(ids, commitWithinMs);
        } else {
            return delegate.deleteById(defaultCollection, ids, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse deleteByQuery(String collection, String query) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.deleteByQuery(query);
        } else {
            return delegate.deleteByQuery(collection, query);
        }
    }

    @Override
    public UpdateResponse deleteByQuery(String query) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.deleteByQuery(query);
        } else {
            return delegate.deleteByQuery(defaultCollection, query);
        }
    }

    @Override
    public UpdateResponse deleteByQuery(String collection, String query, int commitWithinMs) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.deleteByQuery(query, commitWithinMs);
        } else {
            return delegate.deleteByQuery(collection, query, commitWithinMs);
        }
    }

    @Override
    public UpdateResponse deleteByQuery(String query, int commitWithinMs) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.deleteByQuery(query, commitWithinMs);
        } else {
            return delegate.deleteByQuery(defaultCollection, query, commitWithinMs);
        }
    }

    @Override
    public SolrPingResponse ping() throws SolrServerException, IOException {
        return delegate.ping();
    }

    @Override
    public QueryResponse query(String collection, SolrParams params) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.query(params);
        } else {
            return delegate.query(collection, params);
        }
    }

    @Override
    public QueryResponse query(SolrParams params) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.query(params);
        } else {
            return delegate.query(defaultCollection, params);
        }
    }

    @Override
    public QueryResponse query(String collection, SolrParams params, METHOD method) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.query(params, method);
        } else {
            return delegate.query(collection, params, method);
        }
    }

    @Override
    public QueryResponse query(SolrParams params, METHOD method) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.query(params, method);
        } else {
            return delegate.query(defaultCollection, params, method);
        }
    }

    @Override
    public QueryResponse queryAndStreamResponse(String collection, SolrParams params, StreamingResponseCallback callback) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.queryAndStreamResponse(params, callback);
        } else {
            return delegate.queryAndStreamResponse(collection, params, callback);
        }
    }

    @Override
    public QueryResponse queryAndStreamResponse(SolrParams params, StreamingResponseCallback callback) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.queryAndStreamResponse(params, callback);
        } else {
            return delegate.queryAndStreamResponse(defaultCollection, params, callback);
        }
    }

    @Override
    public SolrDocument getById(String collection, String id) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.getById(id);
        } else {
            return delegate.getById(collection, id);
        }
    }

    @Override
    public SolrDocument getById(String id) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.getById(id);
        } else {
            return delegate.getById(defaultCollection, id);
        }
    }

    @Override
    public SolrDocument getById(String collection, String id, SolrParams params) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.getById(id, params);
        } else {
            return delegate.getById(collection, id, params);
        }
    }

    @Override
    public SolrDocument getById(String id, SolrParams params) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.getById(id, params);
        } else {
            return delegate.getById(defaultCollection, id, params);
        }
    }

    @Override
    public SolrDocumentList getById(String collection, Collection<String> ids) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.getById(ids);
        } else {
            return delegate.getById(collection, ids);
        }
    }

    @Override
    public SolrDocumentList getById(Collection<String> ids) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.getById(ids);
        } else {
            return delegate.getById(defaultCollection, ids);
        }
    }

    @Override
    public SolrDocumentList getById(String collection, Collection<String> ids, SolrParams params) throws SolrServerException, IOException {
        if (StringUtils.isBlank(collection) || delegate.getBaseURL().endsWith('/' + collection)) {
            return delegate.getById(ids, params);
        } else {
            return delegate.getById(collection, ids, params);
        }
    }

    @Override
    public SolrDocumentList getById(Collection<String> ids, SolrParams params) throws SolrServerException, IOException {
        if (defaultCollection == null || delegate.getBaseURL().endsWith(defaultCollectionPath)) {
            return delegate.getById(ids, params);
        } else {
            return delegate.getById(defaultCollection, ids, params);
        }
    }

    @Override
    public DocumentObjectBinder getBinder() {
        return delegate.getBinder();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
    
    public HttpSolrClient getDelegate() {
        return delegate;
    }
    
    public String getDefaultCollection() {
        return defaultCollection;
    }
}
