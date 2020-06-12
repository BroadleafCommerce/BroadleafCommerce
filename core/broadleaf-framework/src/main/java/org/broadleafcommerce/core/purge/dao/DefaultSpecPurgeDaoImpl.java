/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2020 Broadleaf Commerce
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
package org.broadleafcommerce.core.purge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.purge.PurgeSpecification;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.stereotype.Service;

/**
 * The DefaultPurgeDao that assumes identifiers are of type Long.  This PurgeDao utilized bulk deletes.
 * A transaction should be started prior to calling this class.
 * 
 * @author dcolgrove
 *
 */
@Service("blSpecPurgeDao")
public class DefaultSpecPurgeDaoImpl implements SpecBasedPurgeDao {

    public static final int RESTRICT_IN_CLAUSE_MAX_SIZE = 800;
    private static final Log LOG = LogFactory.getLog(DefaultSpecPurgeDaoImpl.class);
    
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    /**
     * Entry point into the deletion process.  The <code>PurgeSpec</code> is used to traverse and bulk delete
     * the data.
     * 
     */
    @Override
    public void cascadeDeleteList(PurgeSpecification purgeSpec, String fkColumnName, List<Long> parentDeleteList) {
        if (! purgeSpec.getTableRefsToRoot().isEmpty()) {
            for(PurgeSpecification fkToRoot : purgeSpec.getTableRefsToRoot()) {
                cascadeDeleteList(fkToRoot, fkToRoot.getFkColumnName(), parentDeleteList);
            }
        }
        if (! purgeSpec.getTableRefsFromRoot().isEmpty()) {
            for (PurgeSpecification fkFromRoot : purgeSpec.getTableRefsFromRoot()) {
                List<Long> parentPurgeIds = findAssociatedIds(
                        fkFromRoot.getFkColumnName(),
                        purgeSpec.getTableName(), 
                        fkColumnName,
                        parentDeleteList);
                cascadeDeleteList(fkFromRoot, fkFromRoot.getIdColumnName(), parentPurgeIds);
            }            
        }
        bulkDelete(purgeSpec.getTableName(), fkColumnName != null ? fkColumnName : purgeSpec.getIdColumnName(), parentDeleteList);
    }
   
    /**
     * Finds the id's to be purged from a related table
     * 
     * @param parentFkColumn
     * @param specFkTableName
     * @param specFkColumn
     * @param specDeleteList
     * @return
     */
    protected List<Long> findAssociatedIds(String parentFkColumn, String specFkTableName, String specFkColumn, List<Long> specDeleteList) {
        List<Long> parentIds = new ArrayList<Long>();
        String selectIds = String.format("select distinct %s from %s where %s in (%s)",
                parentFkColumn,
                specFkTableName,
                specFkColumn,
                StringUtils.join(specDeleteList, ","));
        LOG.debug(String.format("Finding associated ids: %s", selectIds));
        if (specDeleteList.size() > 0) {
            PreparedStatement preparedStatement;
            try {
                preparedStatement = getConnection().prepareStatement(selectIds);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    parentIds.add(rs.getLong(1));
                }
            } catch (SQLException e) {
                LOG.error(String.format("Purge error finding associated ids %s, %s, %s",  parentFkColumn, specFkTableName, specFkColumn), e);
            }
        }
        return parentIds;
    }

    /**
     * Performs the physical delete of the provides id's in the table
     * 
     * @param tableName
     * @param columnName
     * @param ids
     */
    protected void bulkDelete(String tableName, String columnName, List<Long> ids) {
        if (ids.size() > 0) {
            List<List<Long>> idSubLists = splitList(ids, RESTRICT_IN_CLAUSE_MAX_SIZE);
            for(List<Long> subList: idSubLists) {
                String deleteStmt = String.format("delete from %s where %s in (%s)", 
                    tableName, 
                    columnName, 
                    StringUtils.join(subList, ","));
                LOG.debug(String.format("Purge delete: %s", deleteStmt));
                PreparedStatement preparedStatement;
                try {
                    preparedStatement = getConnection().prepareStatement(deleteStmt);
                    preparedStatement.execute();
                } catch (SQLException e) {
                    LOG.error(String.format("Purge error deleting %s, %s, %s",  tableName, columnName), e);
                }
            }
        }
    }

    protected  List<List<Long>> splitList(List<Long> ids, int size) {
        return ListUtils.partition(ids, size);
    }
    
    
    protected Connection getConnection() {
        SessionImplementor session = em.unwrap(SessionImplementor.class);
        return session.connection();
    }

    /**
     * TODO: required for tests
     */
    @Override
    public void setEm(EntityManager em) {
        this.em = em;
    }
 
}
