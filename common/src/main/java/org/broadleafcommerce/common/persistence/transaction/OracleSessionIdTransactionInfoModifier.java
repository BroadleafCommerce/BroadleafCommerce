package org.broadleafcommerce.common.persistence.transaction;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityManager;

/**
 * @author Jeff Fischer
 */
public class OracleSessionIdTransactionInfoModifier implements TransactionInfoCustomModifier {

    @Override
    public void modify(TransactionInfo info) {
        EntityManager em = info.getEntityManager();
        if (em != null) {
            Integer sid = em.unwrap(Session.class).doReturningWork(new ReturningWork<Integer>() {
                @Override
                public Integer execute(Connection connection) throws SQLException {
                    Statement statement = null;
                    ResultSet resultSet = null;
                    try {
                        statement = connection.createStatement();
                        resultSet = statement.executeQuery("SELECT sys_context('userenv','sid') FROM dual");
                        Integer response = null;
                        if (resultSet.next()) {
                            response = resultSet.getInt(1);
                        }
                        return response;
                    } finally {
                        if (resultSet != null) {
                            try{
                                resultSet.close();
                            } catch (Throwable e) {}
                        }
                        if (statement != null) {
                            try {
                                statement.close();
                            } catch (Throwable e) {}
                        }
                    }
                }
            });
            info.getAdditionalParams().put("sessionid", sid==null?"UNKNOWN":String.valueOf(sid));
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
