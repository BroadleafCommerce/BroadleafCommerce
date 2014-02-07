package org.broadleafcommerce.common.util;

import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Jeff Fischer
 */
@Repository("blDialectHelper")
public class DialectHelper {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    protected Dialect cachedDialect = null;

    public synchronized Dialect getHibernateDialect() {
        if (cachedDialect == null) {
            SessionFactoryImplementor factory = (SessionFactoryImplementor) em.unwrap(Session.class).getSessionFactory();
            cachedDialect = factory.getDialect();
        }
        return cachedDialect;
    }

    public boolean isOracle() {
        //Since should handle other Oracle dialects as well, since they derive from Oracle8iDialect
        return getHibernateDialect() instanceof Oracle8iDialect;
    }

    public boolean isPostgreSql() {
        //Since should handle other Postgres dialects as well, since they derive from PostgreSQL81Dialect
        return getHibernateDialect() instanceof PostgreSQL81Dialect;
    }

    public boolean isSqlServer() {
        return getHibernateDialect() instanceof SQLServerDialect;
    }

    public boolean isMySql() {
        return getHibernateDialect() instanceof MySQLDialect;
    }

    public void clear() {
        cachedDialect = null;
    }
}
