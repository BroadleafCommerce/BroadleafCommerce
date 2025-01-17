/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence;

import org.hibernate.engine.spi.QueryParameters;

/**
 * <p>
 * Serves as a bridge class between Hibernate 4.1 and Hibernate 5.2. Designed to map
 * directly to the org.hibernate.annotations.QueryHints static fields that appear in both Hibernate 4.1 and 5.2
 * but are named differently.
 *
 * <p>
 * This class was duplicated directly from {@link org.hibernate.annotations.QueryHints} as it contains
 * all of the values from Hibernate 4.1 as well as additional values from Hibernate 5.2.
 *
 * <p>
 * This class should only be used if you need Hibernate 4.1 (Broadleaf 5.2 and below) and Hibernate 5.2 (Broadleaf 6.0+)
 * within the same artifact. This is a stop-gap solution to prevent hard-dependency upgrades within the Broadleaf platform
 * and is not intended to be consumed by Broadleaf users.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class HibernateBridgingQueryHints {

    /**
     * Disallow instantiation.
     */
    private HibernateBridgingQueryHints() {
    }

    /**
     * The cache mode to use.
     *
     * @see org.hibernate.Query#setCacheMode
     * @see org.hibernate.SQLQuery#setCacheMode
     */
    public static final String CACHE_MODE = "org.hibernate.cacheMode";

    /**
     * The cache region to use.
     *
     * @see org.hibernate.Query#setCacheRegion
     * @see org.hibernate.SQLQuery#setCacheRegion
     */
    public static final String CACHE_REGION = "org.hibernate.cacheRegion";

    /**
     * Are the query results cacheable?
     *
     * @see org.hibernate.Query#setCacheable
     * @see org.hibernate.SQLQuery#setCacheable
     */
    public static final String CACHEABLE = "org.hibernate.cacheable";

    /**
     * Is the query callable?  Note: only valid for named native sql queries.
     */
    public static final String CALLABLE = "org.hibernate.callable";

    /**
     * Defines a comment to be applied to the SQL sent to the database.
     *
     * @see org.hibernate.Query#setComment
     * @see org.hibernate.SQLQuery#setComment
     */
    public static final String COMMENT = "org.hibernate.comment";

    /**
     * Defines the JDBC fetch size to use.
     *
     * @see org.hibernate.Query#setFetchSize
     * @see org.hibernate.SQLQuery#setFetchSize
     */
    public static final String FETCH_SIZE = "org.hibernate.fetchSize";

    /**
     * The flush mode to associate with the execution of the query.
     *
     * @see org.hibernate.Query#setFlushMode
     * @see org.hibernate.SQLQuery#setFlushMode
     * @see org.hibernate.Session#setFlushMode
     */
    public static final String FLUSH_MODE = "org.hibernate.flushMode";

    /**
     * Should entities returned from the query be set in read only mode?
     *
     * @see org.hibernate.Query#setReadOnly
     * @see org.hibernate.SQLQuery#setReadOnly
     * @see org.hibernate.Session#setReadOnly
     */
    public static final String READ_ONLY = "org.hibernate.readOnly";

    /**
     * Apply a Hibernate query timeout, which is defined in <b>seconds</b>.
     *
     * @see org.hibernate.Query#setTimeout
     * @see org.hibernate.SQLQuery#setTimeout
     */
    public static final String TIMEOUT_HIBERNATE = "org.hibernate.timeout";

    /**
     * Apply a JPA query timeout, which is defined in <b>milliseconds</b>.
     */
    public static final String TIMEOUT_JPA = "javax.persistence.query.timeout";

    /**
     * All constants within this inner class only appear in Hibernate 5.2+ (Broadleaf 6.0+) and
     * are not guaranteed to work when running Hibernate versions earlier than 5.2.
     *
     * @author Phillip Verheyden (phillipuniverse)
     */
    public static class Hibernate5Hints {

        /**
         * Available to apply lock mode to a native SQL query since JPA requires that
         * {@link javax.persistence.Query#setLockMode} throw an IllegalStateException if called for a native query.
         * <p/>
         * Accepts a {@link javax.persistence.LockModeType} or a {@link org.hibernate.LockMode}
         */
        public static final String NATIVE_LOCKMODE = "org.hibernate.lockMode";

        /**
         * Hint providing a "fetchgraph" EntityGraph.  Attributes explicitly specified as AttributeNodes are treated as
         * FetchType.EAGER (via join fetch or subsequent select).
         *
         * Note: Currently, attributes that are not specified are treated as FetchType.LAZY or FetchType.EAGER depending
         * on the attribute's definition in metadata, rather than forcing FetchType.LAZY.
         */
        public static final String FETCHGRAPH = "javax.persistence.fetchgraph";

        /**
         * Hint providing a "loadgraph" EntityGraph.  Attributes explicitly specified as AttributeNodes are treated as
         * FetchType.EAGER (via join fetch or subsequent select).  Attributes that are not specified are treated as
         * FetchType.LAZY or FetchType.EAGER depending on the attribute's definition in metadata
         */
        public static final String LOADGRAPH = "javax.persistence.loadgraph";

        /**
         * Hint to enable/disable the follow-on-locking mechanism provided by {@link org.hibernate.dialect.Dialect#useFollowOnLocking(QueryParameters)}.
         * A value of {@code true} enables follow-on-locking, whereas a value of {@code false} disables it.
         * If the value is {@code null}, the the {@code Dialect} strategy is going to be used instead.
         *
         * @since 5.2
         */
        public static final String FOLLOW_ON_LOCKING = "hibernate.query.followOnLocking";

        /**
         * Hint to enable/disable the pass-distinct-through mechanism.
         * A value of {@code true} enables pass-distinct-through, whereas a value of {@code false} disables it.
         * When the pass-distinct-through is disabled, the HQL and JPQL distinct clause is no longer passed to the SQL statement.
         *
         * @since 5.2
         */
        public static final String PASS_DISTINCT_THROUGH = "hibernate.query.passDistinctThrough";
    }

}
