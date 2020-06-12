/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

/**
 * The purpose for this class is to provide an alternate approach to an HQL UPDATE query for batch updates on Hibernate filtered
 * entities (such as sandboxable and multi-tenant entities).
 * </p>
 * This class takes an interesting approach to the use of update queries. To explain, a bit of background is required.
 * First, Hibernate will create a temporary table and fill it will ids to use in a where clause when it executs an HQL UPDATE
 * query. However, it will only create this temporary table when the target entity has Hibernate filters applied
 * (i.e. sandboxable or multi-tenant entities). When creating this temporary table, a ‘insert into select’ is used to
 * populate the values. It is my understanding that this ends up creating some locks on the original table. Because of
 * these locks, we were seeing some instances of deadlocks during concurrent admin usage. The key was to avoid
 * the temporary table creation. We did this by first selecting for ids (so that the filters were still honored) and then
 * using a simple, native sql statement to execute the update on entities matching those ids. The native sql needs to be basic
 * enough that it’s portable across platforms.
 * </p>
 * This class is responsible for building the native sql based on a template String. It does it in a way using a standard
 * parameterized query (rather than string concatenation) to avoid the possibility of any sql injection exploit.
 * </p>
 * This implementation has the added benefit of breaking up large IN clauses into smaller chunks to avoid maximum
 * IN clause lengths enforced by some database platforms.
 *
 * @author Jeff Fischer
 */
public class UpdateExecutor {

    /**
     * Perform an update query using a String template and params. Note, this is only intended for special
     * usage with update queries that have an IN clause at the end. This implementation uses Hibernate Session
     * directly to avoid a problem with assigning NULL values. The query should be written in native SQL.
     * </p>
     * An example looks like: 'UPDATE BLC_SNDBX_WRKFLW_ITEM SET SCHEDULED_DATE = ? WHERE WRKFLW_SNDBX_ITEM_ID IN (%s)'
     *
     * @deprecated Highly recommended not to use this method. This method results in global L2 cache region clearing. Use {@link #executeUpdateQuery(EntityManager, String, String, Object[], Type[], List)} instead.
     * @param em The entity manager to use for the persistence operation
     * @param template the overall update sql template. The IN clause parameter should be written using 'IN (%s)'.
     * @param params any other params that are present in the sql template, other than the IN clause. Should be written using '?'. Should be in order. Can be null.
     * @param types the {@link org.hibernate.type.Type} instances that identify the types for the params. Should be in order and match the length of params. Can be null.
     * @param ids the ids to include in the IN clause.
     * @return the total number of records updated in the database
     */
    @Deprecated
    public static int executeUpdateQuery(EntityManager em, String template, Object[] params, Type[] types, List<Long> ids) {
        int response = 0;
        List<Long[]> runs = buildRuns(ids);
        for (Long[] run : runs) {
            String queryString = String.format(template, buildInClauseTemplate(run.length));
            SQLQuery query = em.unwrap(Session.class).createSQLQuery(queryString);
            int counter = 0;
            if (!ArrayUtils.isEmpty(params)) {
                for (Object param : params) {
                    query.setParameter(counter, param, types[counter]);
                    counter++;
                }
            }
            for (Long id : run) {
                query.setLong(counter, id);
                counter++;
            }
            response += query.executeUpdate();
        }
        return response;
    }

    /**
     * Perform an update query using a String template and params. Note, this is only intended for special
     * usage with update queries that have an IN clause at the end. This implementation uses Hibernate Session
     * directly to avoid a problem with assigning NULL values. The query should be written in native SQL.
     * </p>
     * An example looks like: 'UPDATE BLC_SNDBX_WRKFLW_ITEM SET SCHEDULED_DATE = ? WHERE WRKFLW_SNDBX_ITEM_ID IN (%s)'
     *
     * @param em The entity manager to use for the persistence operation
     * @param template the overall update sql template. The IN clause parameter should be written using 'IN (%s)'.
     * @param tableSpace optionally provide the table being impacted by this query. This value allows Hibernate to limit the scope of cache region invalidation. Otherwise, if left null, Hibernate will invalidate every cache region, which is generally not desirable. An empty String can be used to signify that no region should be invalidated.
     * @param params any other params that are present in the sql template, other than the IN clause. Should be written using '?'. Should be in order. Can be null.
     * @param types the {@link org.hibernate.type.Type} instances that identify the types for the params. Should be in order and match the length of params. Can be null.
     * @param ids the ids to include in the IN clause.
     * @return the total number of records updated in the database
     */
    public static int executeUpdateQuery(EntityManager em, String template, String tableSpace, Object[] params, Type[] types, List<Long> ids) {
        int response = 0;
        List<Long[]> runs = buildRuns(ids);
        for (Long[] run : runs) {
            String queryString = String.format(template, buildInClauseTemplate(run.length));
            SQLQuery query = em.unwrap(Session.class).createSQLQuery(queryString);
            //only check for null - an empty string is a valid value for tableSpace
            if (tableSpace != null) {
                query.addSynchronizedQuerySpace(tableSpace);
            }
            int counter = 0;
            if (!ArrayUtils.isEmpty(params)) {
                for (Object param : params) {
                    query.setParameter(counter, param, types[counter]);
                    counter++;
                }
            }
            for (Long id : run) {
                query.setLong(counter, id);
                counter++;
            }
            response += query.executeUpdate();
        }
        return response;
    }

    /**
     *
     * @param em
     * @param entityType
     * @param ids
     */
    public static void executeTargetedCacheInvalidation(EntityManager em, Class<?> entityType, List<Long> ids) {
        Session session = em.unwrap(Session.class);
        for (Long id : ids) {
            session.getSessionFactory().getCache().evictEntity(entityType, id);
        }
        //update the timestamp cache for the table so that queries will be refreshed
        ClassMetadata metadata = session.getSessionFactory().getClassMetadata(entityType);
        String tableName = ((AbstractEntityPersister) metadata).getTableName();
        UpdateTimestampsCache timestampsCache = em.unwrap(SessionImplementor.class).getFactory().getUpdateTimestampsCache();
        if (timestampsCache != null) {
            timestampsCache.invalidate(new Serializable[]{tableName});
        }
    }

    /**
     * Quickly build up the sql IN clause template
     *
     * @param length
     * @return
     */
    private static String buildInClauseTemplate(int length) {
        String[] temp = new String[length];
        Arrays.fill(temp, "?");
        return StringUtils.join(temp, ",");
    }

    /**
     * This breaks up our IN clause into multiple runs of 800 or less in order
     * to guarantee compatibility across platforms (i.e. some db platforms will throw a error if there are more
     * than a 1000 entries in an sql IN clause).
     *
     * @param ids
     * @return
     */
    private static List<Long[]> buildRuns(List<Long> ids) {
        List<Long[]> runs = new ArrayList<Long[]>();
        Long[] all = ids.toArray(new Long[ids.size()]);
        int test = all.length;
        int pos = 0;
        boolean eof = false;
        while (!eof) {
            int arraySize;
            if (test < 800) {
                arraySize = test;
                eof = true;
            } else {
                arraySize = 800;
                test -= arraySize;
                if (test == 0) {
                    eof = true;
                }
            }
            Long[] temp = new Long[arraySize];
            System.arraycopy(all, pos, temp, 0, arraySize);
            pos += arraySize;
            runs.add(temp);
        }
        return runs;
    }
}
