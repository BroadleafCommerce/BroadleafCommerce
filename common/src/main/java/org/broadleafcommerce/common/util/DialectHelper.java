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
package org.broadleafcommerce.common.util;

import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author Jeff Fischer
 */
@Repository("blDialectHelper")
public class DialectHelper {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager defaultEntityManager;

    public Dialect getHibernateDialect() {
        return getHibernateDialect(defaultEntityManager);
    }

    public Dialect getHibernateDialect(EntityManager em) {
        SessionFactoryImplementor factory = (SessionFactoryImplementor) em.unwrap(Session.class).getSessionFactory();
        return factory.getServiceRegistry().getService(JdbcServices.class).getDialect();
    }

    public boolean isOracle() {
        //This should handle other Oracle dialects as well, since they derive from Oracle8iDialect
        return getHibernateDialect(defaultEntityManager) instanceof OracleDialect;
    }

    public boolean isOracle(EntityManager em) {
        //This should handle other Oracle dialects as well, since they derive from Oracle8iDialect
        return getHibernateDialect(em) instanceof OracleDialect;
    }

    public boolean isPostgreSql() {
        //This should handle other Postgres dialects as well, since they derive from PostgreSQL81Dialect
        return getHibernateDialect(defaultEntityManager) instanceof PostgreSQLDialect;
    }

    public boolean isPostgreSql(EntityManager em) {
        //This should handle other Postgres dialects as well, since they derive from PostgreSQL81Dialect
        return getHibernateDialect(em) instanceof PostgreSQLDialect;
    }

    public boolean isSqlServer() {
        return getHibernateDialect(defaultEntityManager) instanceof SQLServerDialect;
    }

    public boolean isSqlServer(EntityManager em) {
        return getHibernateDialect(em) instanceof SQLServerDialect;
    }

    public boolean isMySql() {
        return getHibernateDialect(defaultEntityManager) instanceof MySQLDialect;
    }

    public boolean isMySql(EntityManager em) {
        return getHibernateDialect(em) instanceof MySQLDialect;
    }

}
