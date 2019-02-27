/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.security.service.navigation;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminator;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.security.dao.AdminNavigationDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminMenu;
import org.broadleafcommerce.openadmin.server.security.domain.AdminModule;
import org.broadleafcommerce.openadmin.server.security.domain.AdminModuleDTO;
import org.broadleafcommerce.openadmin.server.security.domain.AdminModuleImpl;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.web.controller.AbstractAdminAbstractControllerExtensionHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

/**
 * This service is used to build the left hand navigation for the admin
 * @author elbertbautista
 */
@Service("blAdminNavigationService")
public class AdminNavigationServiceImpl implements AdminNavigationService {

    private static final Log LOG = LogFactory.getLog(AdminNavigationServiceImpl.class);
    private static final String PATTERN = "_";

    private static SectionComparator SECTION_COMPARATOR = new SectionComparator();

    private static class SectionComparator implements Comparator<AdminSection> {

        @Override
        public int compare(AdminSection section, AdminSection section2) {
            if (section.getDisplayOrder() != null) {
                if (section2.getDisplayOrder() != null) {
                    return section.getDisplayOrder().compareTo(section2.getDisplayOrder());
                }
                else
                    return -1;
            } else if (section2.getDisplayOrder() != null) {
                return 1;
            }

            return section.getId().compareTo(section2.getId());
        }

    }

    @Resource(name = "blAdminNavigationDao")
    protected AdminNavigationDao adminNavigationDao;

    @Resource(name="blAdditionalSectionAuthorizations")
    protected List<SectionAuthorization> additionalSectionAuthorizations = new ArrayList<SectionAuthorization>();

    @Resource(name = "blAdminNavigationServiceExtensionManager")
    protected AdminNavigationServiceExtensionManager extensionManager;

    @Override
    @Transactional("blTransactionManager")
    public AdminSection save(AdminSection adminSection) {
        return adminNavigationDao.save(adminSection);
    }

    @Override
    public void remove(AdminSection adminSection) {
        adminNavigationDao.remove(adminSection);
    }

    @Override
    public AdminMenu buildMenu(AdminUser adminUser) {
        AdminMenu adminMenu = new AdminMenu();
        List<AdminModule> modules = adminNavigationDao.readAllAdminModules();
        populateAdminMenu(adminUser, adminMenu, modules);
        return adminMenu;
    }

    @Override
    public boolean isUserAuthorizedToViewModule(AdminUser adminUser, AdminModule module) {
        List<AdminSection> moduleSections = module.getSections();
        if (moduleSections != null && !moduleSections.isEmpty()) {
            for (AdminSection section : moduleSections) {
                if (isUserAuthorizedToViewSection(adminUser, section)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public AdminSection findAdminSectionByURI(String uri) {
        return adminNavigationDao.readAdminSectionByURI(uri);
    }
    
    @Override
    public AdminSection findAdminSectionByClassAndSectionId(String className, String sectionId) {
        try {
            return findAdminSectionByClassAndSectionId(Class.forName(className), sectionId);
        } catch (ClassNotFoundException e) {
            LOG.warn("Invalid classname received. This likely points to a configuration error.");
            return null;
        }
    }
    
    @Override
    public AdminSection findAdminSectionByClassAndSectionId(Class<?> clazz, String sectionId) {
        return adminNavigationDao.readAdminSectionByClassAndSectionId(clazz, sectionId);
    }

    @Override
    public AdminSection findAdminSectionBySectionKey(String sectionKey) {
        return adminNavigationDao.readAdminSectionBySectionKey(sectionKey);
    }

    @Override
    public AdminSection findBaseAdminSectionByClass(String clazz) {
        List<AdminSection> sections = adminNavigationDao.readAdminSectionForClassName(clazz);
        if (org.springframework.util.CollectionUtils.isEmpty(sections) && clazz.endsWith("Impl")) {
            clazz = clazz.substring(0, clazz.length() - 4);
            sections = adminNavigationDao.readAdminSectionForClassName(clazz);
        }
        
        if (sections == null) {
            return null;
        }
        
        for (AdminSection section : sections) {
            //When identifying the "base" section, multiple can be returned.  "Type" sections (e.g. product:addon) will have a ":".
            //  Since we are looking for the base section, the "type" sections should be ignored
            if(!section.getUrl().contains(":")){
                return section;
            }
        }
        return sections.get(0);
    }

    @Override
    public boolean isUserAuthorizedToViewSection(AdminUser adminUser, AdminSection section) {
        List<AdminPermission> authorizedPermissions = section.getPermissions();

        Set<String> authorizedPermissionNames = null;
        if (authorizedPermissions != null) {
            authorizedPermissionNames = new HashSet<>((authorizedPermissions.size() * 2));
            for (AdminPermission authorizedPermission : authorizedPermissions) {
                authorizedPermissionNames.add(authorizedPermission.getName());
                authorizedPermissionNames.add(parseForAllPermission(authorizedPermission.getName()));
            }
        }

        boolean response = false;
        if (!CollectionUtils.isEmpty(adminUser.getAllRoles())) {
            for (AdminRole role : adminUser.getAllRoles()) {
                for (AdminPermission permission : role.getAllPermissions()){
                    if (checkPermissions(authorizedPermissionNames, permission.getName())) {
                        response = true;
                    }
                }
            }
        }
        if (!response && !CollectionUtils.isEmpty(adminUser.getAllPermissions())) {
            for (AdminPermission permission : adminUser.getAllPermissions()){
                if (checkPermissions(authorizedPermissionNames, permission.getName())) {
                    response = true;
                }
            }
        }
        if (!response) {
            for (String defaultPermission : AdminSecurityService.DEFAULT_PERMISSIONS) {
                if (checkPermissions(authorizedPermissionNames, defaultPermission)) {
                    response = true;
                }
            }
        }

        if (response) {
            for (SectionAuthorization sectionAuthorization : additionalSectionAuthorizations) {
                if (!sectionAuthorization.isUserAuthorizedToViewSection(adminUser, section)) {
                    response = false;
                    break;
                }
            }
        }

        return response;
    }
    
    @Override
    public List<AdminSection> findAllAdminSections() {
        List<AdminSection> sections = adminNavigationDao.readAllAdminSections();
        Collections.sort(sections, SECTION_COMPARATOR);
        return sections;
    }

    @Override
    public boolean checkPermissions(Set<String> authorizedPermissionNames, String permissionName) {
        if (authorizedPermissionNames != null) {
            if (authorizedPermissionNames.contains(permissionName)){
                return true;
            }
        }
        return false;
    }

    public List<SectionAuthorization> getAdditionalSectionAuthorizations() {
            return additionalSectionAuthorizations;
        }

    public void setAdditionalSectionAuthorizations(List<SectionAuthorization> additionalSectionAuthorizations) {
        this.additionalSectionAuthorizations = additionalSectionAuthorizations;
    }

    @Override
    public String getClassNameForSection(String sectionKey) {
        AdminSection section = findAdminSectionByURI("/" + sectionKey);

        ExtensionResultHolder erh = new ExtensionResultHolder();
        extensionManager.getProxy().overrideClassNameForSection(erh, sectionKey, section);
        if (erh.getContextMap().get(AbstractAdminAbstractControllerExtensionHandler.NEW_CLASS_NAME) != null) {
            return (String) erh.getContextMap().get(AbstractAdminAbstractControllerExtensionHandler.NEW_CLASS_NAME);
        }

        return (section == null) ? sectionKey : section.getCeilingEntity();
    }

    @Override
    public List<SectionCrumb> getSectionCrumbs(String crumbList) {
        List<SectionCrumb> myCrumbs = new ArrayList<SectionCrumb>();
        if (!StringUtils.isEmpty(crumbList)) {
            String[] crumbParts = crumbList.split(",");
            for (String part : crumbParts) {
                SectionCrumb crumb = new SectionCrumb();
                String[] crumbPieces = part.split("--");
                crumb.setSectionIdentifier(crumbPieces[0]);
                crumb.setSectionId(crumbPieces[1]);
                if (!myCrumbs.contains(crumb)) {
                    myCrumbs.add(crumb);
                }
            }
        }
        return myCrumbs;
    }

    protected String parseForAllPermission(String currentPermission) {
        String[] pieces = currentPermission.split(PATTERN);
        StringBuilder builder = new StringBuilder(50);
        builder.append(pieces[0]);
        builder.append("_ALL_");
        for (int j = 2; j<pieces.length; j++) {
            builder.append(pieces[j]);
            if (j < pieces.length - 1) {
                builder.append('_');
            }
        }
        return builder.toString();
    }

    protected void populateAdminMenu(AdminUser adminUser, AdminMenu adminMenu, List<AdminModule> modules) {
        for (AdminModule module : modules) {
            List<AdminSection> authorizedSections = buildAuthorizedSectionsList(adminUser, module);
            if (authorizedSections != null && authorizedSections.size() > 0) {
                AdminModuleDTO adminModuleDto = ((AdminModuleImpl) module).getAdminModuleDTO();
                adminMenu.getAdminModules().add(adminModuleDto);
                adminModuleDto.setSections(authorizedSections);
            }
        }

        // Sort the authorized modules
        BeanComparator displayComparator = new BeanComparator("displayOrder");
        Collections.sort(adminMenu.getAdminModules(), displayComparator);
    }

    protected List<AdminSection> buildAuthorizedSectionsList(AdminUser adminUser, AdminModule module) {
        List<AdminSection> authorizedSections = new ArrayList<AdminSection>();
        BroadleafRequestContext broadleafRequestContext = BroadleafRequestContext.getBroadleafRequestContext();
        Site site = broadleafRequestContext.getNonPersistentSite();
        Long siteId = site == null ? null : site.getId();
        for (AdminSection section : module.getSections()) {
            if (isUserAuthorizedToViewSection(adminUser, section)) {
                if(section instanceof SiteDiscriminator){
                    Long sectionSiteId = ((SiteDiscriminator)section).getSiteDiscriminator();
                    if(sectionSiteId == null || sectionSiteId.equals(siteId)){
                        authorizedSections.add(section);
                    }
                } else{
                    authorizedSections.add(section);
                }
            }
        }

        Collections.sort(authorizedSections, SECTION_COMPARATOR);
        return authorizedSections;
    }
}
