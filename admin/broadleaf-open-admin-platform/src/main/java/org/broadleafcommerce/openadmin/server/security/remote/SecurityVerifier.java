package org.broadleafcommerce.openadmin.server.security.remote;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;

/**
 * @author Jeff Fischer
 */
public interface SecurityVerifier {
    AdminUser getPersistentAdminUser();

    void securityCheck(String ceilingEntityFullyQualifiedName, EntityOperationType operationType) throws ServiceException;

    boolean isEntitySecurityExplicit();

    void setEntitySecurityExplicit(boolean entitySecurityExplicit);
}
