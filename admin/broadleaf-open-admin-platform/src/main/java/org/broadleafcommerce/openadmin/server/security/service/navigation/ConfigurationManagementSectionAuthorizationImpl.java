package org.broadleafcommerce.openadmin.server.security.service.navigation;

import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Jeff Fischer
 */
@Component("blConfigurationManagementSectionAuthorization")
public class ConfigurationManagementSectionAuthorizationImpl implements SectionAuthorization {

    @Resource(name="blDynamicEntityDao")
    protected DynamicEntityDao dynamicEntityDao;

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @PostConstruct
    public void init() {
        dynamicEntityDao.setStandardEntityManager(em);
    }

    @Override
    public boolean isUserAuthorizedToViewSection(AdminUser adminUser, AdminSection section) {
        try {
            //Only show this section if there is an extension of AbstractModuleConfiguration
            return !section.getCeilingEntity().equals("org.broadleafcommerce.common.config.domain.AbstractModuleConfiguration") ||
                    dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Class.forName("org.broadleafcommerce.common" +
                            ".config.domain.AbstractModuleConfiguration")).length > 1;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
