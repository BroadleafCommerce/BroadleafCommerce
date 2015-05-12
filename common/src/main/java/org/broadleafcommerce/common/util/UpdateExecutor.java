/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.Type;

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
     * @param em The entity manager to use for the persistence operation
     * @param template the overall update sql template. The IN clause parameter should be written using 'IN (%s)'.
     * @param params any other params that are present in the sql template, other than the IN clause. Should be written using '?'. Should be in order. Can be null.
     * @param types the {@link org.hibernate.type.Type} instances that identify the types for the params. Should be in order and match the length of params. Can be null.
     * @param ids the ids to include in the IN clause.
     * @return the total number of records updated in the database
     */
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
            }
            Long[] temp = new Long[arraySize];
            System.arraycopy(all, pos, temp, 0, arraySize);
            pos += arraySize;
            runs.add(temp);
        }
        return runs;
    }
}
