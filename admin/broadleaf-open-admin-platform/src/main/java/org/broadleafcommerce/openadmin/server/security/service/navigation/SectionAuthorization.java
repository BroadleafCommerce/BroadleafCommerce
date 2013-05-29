package org.broadleafcommerce.openadmin.server.security.service.navigation;

import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;

/**
 * @author Jeff Fischer
 */
public interface SectionAuthorization {
    boolean isUserAuthorizedToViewSection(AdminUser adminUser, AdminSection section);
}
