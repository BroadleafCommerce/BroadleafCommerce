package com.broadleafcommerce.customfield.service.provider;

import org.apache.commons.lang3.ArrayUtils;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.service.provider.SiteConfigProvider;
import org.broadleafcommerce.openadmin.server.security.dao.AdminPermissionDao;
import org.broadleafcommerce.openadmin.server.security.dao.AdminRoleDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Andre Azzolini (apazzolini)
 */
public class CustomFieldSiteConfigProvider implements SiteConfigProvider {

    @Resource(name = "blAdminRoleDao")
    protected AdminRoleDao adminRoleDao;
    
    @Resource(name = "blAdminPermissionDao")
    protected AdminPermissionDao adminPermissionDao;

    @Override
    public void configSite(Site site) {
    }

    /**
     * We must add additional template permissions
     */
    @Override
    @SuppressWarnings("unchecked")
    public void init(Map<String, Object> map) {
        List<AdminRole> templateRoles = (List<AdminRole>) map.get("TEMPLATE_ROLES");
        
        String[] targetRoles = { "ROLE_ADMIN" };
        String[] targetPerms = { "PERMISSION_ALL_CUSTOM_FIELD" };
        
        for (AdminRole ar : templateRoles) {
            Map<String, Boolean> alreadyCreatedPerms = new HashMap<String, Boolean>();
            if (ArrayUtils.contains(targetRoles, ar.getName())) {
                for (AdminPermission perm : ar.getAllPermissions()) {
                    if (ArrayUtils.contains(targetPerms, perm.getName())) {
                        alreadyCreatedPerms.put(perm.getName(), true);
                    }
                }
                
                for (String targetPerm : targetPerms) {
                    if (!alreadyCreatedPerms.containsKey(targetPerm) || !alreadyCreatedPerms.get(targetPerm)) {
                        AdminPermission perm = adminPermissionDao.readAdminPermissionByName(targetPerm);
                        ar.getAllPermissions().add(perm);
                    }
                    
                }
            }
        }
    }
}
