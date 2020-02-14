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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.util.GenericOperation;
import org.broadleafcommerce.common.util.GenericOperationUtil;
import org.broadleafcommerce.core.search.service.solr.SolrConfiguration;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

/**
 * Component to provide basic functionality around handling SolrUpdateCommands.
 * @author Kelly Tisdell
 *
 */
public abstract class AbstractSolrIndexUpdateCommandHandlerImpl implements SolrIndexUpdateCommandHandler {
    
    private static final Log LOG = LogFactory.getLog(AbstractSolrIndexUpdateCommandHandlerImpl.class);
    
    private final String commandGroup;
    
    public AbstractSolrIndexUpdateCommandHandlerImpl(String commandGroup) {
        Assert.notNull(commandGroup, "Command group cannot be null.");
        this.commandGroup = commandGroup.trim();
        Assert.hasText(this.commandGroup, "Command group must not be empty and should not contain white spaces.");
    }
    
    @Override
    public String getCommandGroup() {
        return commandGroup;
    }

    /**
     * By default, this will update the foreground collection.  Deletes, if available, will be applied first.  Then, updates. This should be considered an autonomous method.
     * Do not use this to make incremental updates within the scope of a larger update process because this will apply commits, by default.
     * 
     * @param command
     * @throws ServiceException
     */
    protected void executeCommandInternal(IncrementalUpdateCommand command) throws ServiceException {
        executeCommandInternal(command, getForegroundCollectionName());
    }
    
    /**
     * This will apply updates in the specified collection, and will commit, when finished, if no errors occur. 
     * Deletes, if available, will be applied first.  Then, updates.  This should be considered an autonomous method.
     * Do not use this to make incremental updates within the scope of a larger update process because this will apply commits, by default.
     * 
     * @param command
     * @param collectionName
     * @throws ServiceException
     */
    protected void executeCommandInternal(IncrementalUpdateCommand command, String collectionName) throws ServiceException {
        Assert.notNull(command, "The command cannot be null.");
        Assert.notNull(collectionName, "The collection name cannot be null.");
        
        boolean changeMade = false;
        try {
            
            if (command.getDeleteQueries() != null && ! command.getDeleteQueries().isEmpty()) {
                deleteByQueries(collectionName, command.getDeleteQueries());
                changeMade = true;
            }
            
            if (command.getSolrInputDocuments() != null && ! command.getSolrInputDocuments().isEmpty()) {
                addDocuments(collectionName, command.getSolrInputDocuments());
                changeMade = true;
            }
            
            try {
                if (changeMade) {
                    commit(collectionName, true, true, false);
                }
            } catch (Exception e) {
                throw new ServiceException("An error occured during commit while incrementally updating the Solr collection '" + collectionName + "' with: \n" + command.toString(), e);
            }
            
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            if (changeMade) {
                try {
                    rollback(collectionName);
                } catch (Exception se) {
                    throw new ServiceException("An error occured rolling back the changes in Solr after an error occured.", se);
                }
            }
        }
    }
    
    /**
     * Hook point for implementors to handle new command types.
     * @param command
     * @throws Exception
     */
    protected void executeCommandInternalNoDefaultCommandType(SolrUpdateCommand command) throws ServiceException {
        if (command == null) {
            LOG.error("Unable to process SolrUpdateCommand as the command was null.");
        } else {
            LOG.error("Unable to process SolrUpdateCommand of type: " 
                    + command.getClass().getName() + ". Consider overriding the executeCommandInternalNoDefaultCommandType method in " 
                    + this.getClass().getName() + ".");
        }
    }
    
    /**
     * Issues a global commit command to Solr.  Take care as anyone can issue a commit and since it's global it affects all updates. 
     * It is recommended that you off Solr's autoCommit and autoSoftCommit features.
     * 
     * @param collectionName
     * @param waitFlush
     * @param waitSearcher
     * @param softCommit
     * @throws Exception
     * 
     */
    protected synchronized void commit(final String collectionName, final boolean waitFlush, final boolean waitSearcher, final boolean softCommit) throws Exception {
        LOG.info("Issuing commit to Solr index: " + collectionName + " - with waitFlush=" + waitFlush + ", waitSearcher=" + waitSearcher + ", and softCommit=" + softCommit + ".");
        GenericOperationUtil.executeRetryableOperation(new GenericOperation<Void>() {
            @Override
            public Void execute() throws Exception {
                getSolrConfiguration().getReindexServer().commit(collectionName, waitFlush, waitSearcher, softCommit);
                return null;
            }
        });
    }
    
    /**
     * Issues a global rollback of all items that have not yet been committed.  Take care as anyone can issue a commit and since it's global it affects all updates. 
     * It is recommended that you off Solr's autoCommit and autoSoftCommit features.
     * 
     * @param collectionName
     * @throws Exception
     * 
     */
    protected synchronized void rollback(final String collectionName) throws Exception {
        GenericOperationUtil.executeRetryableOperation(new GenericOperation<Void>() {
            @Override
            public Void execute() throws Exception {
                getSolrConfiguration().getReindexServer().rollback(collectionName);
                return null;
            }
        });
    }
    
    /**
     * Adds the document to the specified collection but does not issue a commit.
     * @param collection
     * @param doc
     * @throws Exception
     */
    protected void addDocument(final String collection, final SolrInputDocument doc) throws Exception {
        GenericOperationUtil.executeRetryableOperation(new GenericOperation<Void>() {
            @Override
            public Void execute() throws Exception {
                getSolrConfiguration().getReindexServer().add(doc);
                return null;
            }
        });
    }
    
    /**
     * Adds the documents to the specified collection but does not issue a commit.
     * 
     * @param collection
     * @param docs
     * @throws Exception
     * 
     */
    protected void addDocuments(final String collection, final List<SolrInputDocument> docs) throws Exception {
        GenericOperationUtil.executeRetryableOperation(new GenericOperation<Void>() {
            @Override
            public Void execute() throws Exception {
                if (docs != null && !docs.isEmpty()) {
                    getSolrConfiguration().getReindexServer().add(collection, docs);
                }
                return null;
            }
        });
    }
    
    /**
     * Deletes items for the provided query.  This does not issue a commit.
     * 
     * @param collection
     * @param query
     * @throws Exception
     * 
     */
    protected void deleteByQuery(final String collection, final String query) throws Exception {
        GenericOperationUtil.executeRetryableOperation(new GenericOperation<Void>() {
            @Override
            public Void execute() throws Exception {
                if (query != null) {
                    getSolrConfiguration().getReindexServer().deleteByQuery(collection, query);
                }
                return null;
            }
        });
    }
    
    /**
     * Deletes items for the provided queries.  This does not issue a commit.
     * 
     * @param collection
     * @param queries
     * @throws Exception
     * 
     */
    protected void deleteByQueries(String collection, List<String> queries) throws Exception {
        if (queries != null) {
            for (String query : queries) {
                deleteByQuery(collection, query);
            }
        }
    }
    
    /**
     * Deletes items by ids.  This does not issue a commit.
     * 
     * @param collection
     * @param ids
     * @throws IOException
     * @throws SolrServerException
     */
    protected void deleteByIds(final String collection, final List<String> ids) throws Exception {
        GenericOperationUtil.executeRetryableOperation(new GenericOperation<Void>() {
            @Override
            public Void execute() throws Exception {
                if (ids != null && !ids.isEmpty()) {
                    getSolrConfiguration().getReindexServer().deleteById(collection, ids);
                }
                return null;
            }
        });
    }
    
    @Override
    public String getForegroundCollectionName() {
        return getSolrConfiguration().getPrimaryName();
    }
    
    @Override
    public String getBackgroundCollectionName() {
        return getSolrConfiguration().getReindexName();
    }

    protected abstract SolrConfiguration getSolrConfiguration();
}
