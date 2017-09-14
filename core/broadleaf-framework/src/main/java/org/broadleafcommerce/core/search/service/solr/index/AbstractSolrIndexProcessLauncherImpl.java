package org.broadleafcommerce.core.search.service.solr.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.index.service.AbstractGenericSearchIndexProcessLauncher;

import java.util.ArrayList;

public abstract class AbstractSolrIndexProcessLauncherImpl<I extends Indexable> extends AbstractGenericSearchIndexProcessLauncher<I> {
    
    private static final Log LOG = LogFactory.getLog(AbstractSolrIndexProcessLauncherImpl.class);
    
    /**
     * Returns the instance of the SolrUtil.  This will typically be injected into a subclass and can simply be returned.
     * @return
     */
    protected abstract SolrUtil getSolrUtil();
    
    /**
     * SolrCloud provides the concept of aliases for collections.  An alias is an arbitrary name for a collection.  Aliases 
     * are assumed to be stable, while collections (or collection names) may be quite variable.  For example, you could 
     * have an alias called "catalog_main" that maps to a collection called "catalog_09_15_2017".  Assume you create a 
     * new collection called "catalog_09_20_2017" and load it with fresh data.  You will want to re-alias collection so that 
     * "catalog_main" is now an alias for "catalog_09_20_2017".  There are a number of strategies for naming collections 
     * and aliases.  They most important thing to note is that an alias must remain stable, like a constant, (as it is 
     * being used/referenced by various processes (such as a live site).
     * 
     * If you are not using SolrCloud or aliases, then this method should return the collection name (which, in lieu of 
     * an alias, should be a stable, constant string).
     * 
     * This method must not return null.
     *  
     * @return
     */
    protected abstract String getPrimaryAliasName();
    
    /**
     * This is the name of the Solr alias that is associated with the an "offline" Solr collection.  By "offline" it means 
     * that this collection exists, but is not referenced by a "live" site.  Any changes to the collection referenced by 
     * this alias will not be seen by a live site until a re-alias occurs.
     * 
     * Note that incremental changes should happen in the primary alias.  This alias is for offline collections 
     * or cores that may be completely emptied (or destroyed and recreated) and re-indexed.
     * 
     * This method may return null if no secondary collection exists.
     * 
     * @return
     */
    protected abstract String getSecondaryAliasName();
    
    /**
     * Given a primary alias name, this returns the associated collection name that is aliased.
     * @return
     */
    protected String getPrimaryCollectionName() {
        if (getSolrUtil().isSolrCloudMode()) {
            return getSolrUtil().getCollectionNameForAlias(getPrimaryAliasName());
        } else {
            return getPrimaryAliasName();
        }
    }
    
    /**
     * Given a secondary alias name, this returns the associated collection name that is aliased.
     * @return
     */
    protected String getSecondaryCollectionName() {
        if (getSolrUtil().isSolrCloudMode()) {
            return getSolrUtil().getCollectionNameForAlias(getSecondaryAliasName());
        } else {
            return getSecondaryAliasName();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.AbstractGenericSearchIndexService#preProcess(java.lang.String)
     * 
     * This can be re-implemented to handle any Solr pre-processing.  Some examples of this might be:
     * - Programmatically update configuration files in Zookeeper
     * - Delete all conents of a collection prior to re-indexing
     * - Create (or re-create) an collection for a given alias
     * 
     */
    @Override
    protected void preProcess(String processId) throws ServiceException {
        reinitializeIndex(processId);
    }
    
    /**
     * This clears the index (collection) so that it can be reindexed.  The default behavior of this method is that it only deletes 
     * data in the collection associated with the secondary alias.  
     * If the secondary alias name is null, then this method does nothing. 
     * By default, this method issues a delete query (*:*).  However, another strategy is to programmatically destroy and 
     * recreate the entire collection associated with the secondary alias.  Destroying and recreating the collection is 
     * more complicated, but can be safer and more memory efficient.  Some implementors prefer to use a date format for 
     * the actual collection name so that it's clear when it was created.  For example, "product_10_20_2017".
     * 
     * @param processId
     * @throws ServiceException
     */
    protected void reinitializeIndex(String processId) throws ServiceException {
        if (getSecondaryAliasName() != null) {
            UpdateRequest deleteRequest = new UpdateRequest();
            ArrayList<String> deleteQueries = new ArrayList<>();
            deleteQueries.add("*:*");
            deleteRequest.setDeleteQuery(deleteQueries);
            getSolrUtil().process(deleteRequest, getSecondaryAliasName());
            getSolrUtil().commit(getSecondaryAliasName());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.AbstractGenericSearchIndexService#postProcessSuccess(java.lang.String)
     * This should only be called for a full reindex.
     */
    @Override
    protected void postProcessSuccess(String processId) throws ServiceException {
        commitAfterReindex(processId);
        swap(processId);
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.AbstractGenericSearchIndexProcessLauncher#postProcessFailure(java.lang.String)
     */
    @Override
    protected void postProcessFailure(String processId) throws ServiceException {
        //Nothing to do here.
    }
    
    /**
     * Method to control commit logic after a reindex.  This is not a general commit method.  Use 
     * SolrUtil.commit() for general commits.
     * 
     * @param processId
     */
    protected void commitAfterReindex(String processId) throws ServiceException {
        if (shouldCommitAfterReindex(processId)) {
            if (getSecondaryAliasName() != null) {
                //Commit the index we just aliased.
                getSolrUtil().commit(getSecondaryAliasName());
            } else {
                getSolrUtil().commit(getPrimaryAliasName());
            }
            
            return;
        }
        LOG.info("Not committing the changes to the Solr index for processId " + processId);
    }
    
    /**
     * Method to control swap logic
     * @param processId
     */
    protected void swap(String processId) throws ServiceException {
        if (shouldSwapAliasesAfterReindex(processId)) {
            if (getSolrUtil().isSolrCloudMode() 
                    && getSecondaryAliasName() != null 
                    && !getSecondaryAliasName().equals(getPrimaryAliasName())) {
                //Swap the indexes.
                getSolrUtil().swap(getPrimaryAliasName(), getSecondaryAliasName());
                return;
            }
        }
        LOG.info("Not swapping aliases for processId " + processId);
    }
    
    /**
     * Provides a hint as to whether primary and secondary aliases should be swapped after a successful reindex process. This is 
     * a hint because even if it is true, the swap will not happen if the alias names are the same, if one of them is null,
     * or if we are not in SolrCloud mode.
     * 
     * @return
     */
    protected boolean shouldSwapAliasesAfterReindex(String processId) {
        return true;
    }
    
    /**
     * Indicates if a commit should be issued.  Solr commits are not like database commits.  They are global to the 
     * index (not to the connection or session).  Too many commits issued can cause performance problems.  Solr has several 
     * mechanisms to address this.  Solr has autoCommit and autoSoftCommit configured in the solrconfig.xml file.  These 
     * properties will allow commits to happen on their own if they are configured.  Soft commits are not durable but make 
     * documents searcheable.  Hard commits are durable.  Soft commits are lightweight, and hard commits are heavy.
     * 
     * Issuing a commit from the client is still possible, but should be done with caution. Typically this should 
     * ONLY be done for a bulk load and not for incremental updates.  Note that SolrInputDocuments have a commitWithin 
     * flag, indicating that Solr should make the visible to the index, but does not issue a hard commit.
     * 
     * See https://cwiki.apache.org/confluence/display/solr/UpdateHandlers+in+SolrConfig
     * 
     * As a general rule, Solr should have autoCommit set to a reasonable value (e.g. 30 seconds).  Soft commits (autoSoftCommit)  
     * should also be set to a reasonable value (e.g. 5 seconds).  However, these values will vary with specific 
     * requirements, volume, etc.
     * 
     * @return
     */
    protected boolean shouldCommitAfterReindex(String processId) {
        return true;
    }
}
