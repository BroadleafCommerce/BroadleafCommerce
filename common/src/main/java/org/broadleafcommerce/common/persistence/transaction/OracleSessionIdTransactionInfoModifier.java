package org.broadleafcommerce.common.persistence.transaction;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import javax.persistence.EntityManager;

/**
 * @author Jeff Fischer
 */
public class OracleSessionIdTransactionInfoModifier implements TransactionInfoCustomModifier {

    @Override
    public void modify(TransactionInfo info) {
        EntityManager em = info.getEntityManager();
        if (em != null) {
            Session session = em.unwrap(Session.class);
            SQLQuery query = session.createSQLQuery("SELECT sys_context('userenv') FROM dual;");
            String sessionId = String.valueOf(query.uniqueResult());
            info.getAdditionalParams().put("sessionid", sessionId);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
