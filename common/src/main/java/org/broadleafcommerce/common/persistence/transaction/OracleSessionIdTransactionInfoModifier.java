/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
