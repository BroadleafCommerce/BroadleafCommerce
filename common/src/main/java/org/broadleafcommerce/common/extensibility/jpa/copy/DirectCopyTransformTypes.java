/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.extensibility.jpa.copy;

/**
 * Constants for tagging an entity class to receive byte code transformation upon
 * being loaded by the classloader
 *
 * @author Jeff Fischer
 */
public class DirectCopyTransformTypes {

    //Class should receive sandbox related fields
    public static final String SANDBOX = "sandbox";
    //Class should receive fields describing the site to which the entity belongs
    public static final String MULTITENANT_SITE = "multiTenantSite";
    //Class should receive fields describing the catalog to which the entity belongs
    public static final String MULTITENANT_CATALOG = "multiTenantCatalog";
    //Class should receive the Discriminatable marker interface alone
    public static final String MULTITENANT_SITEMARKER = "multiTenantSiteMarker";
    //Class should receive fields describing whether or not the entity was created during a site preview
    public static final String PREVIEW = "preview";
    //Class should receive fields describing site context information for the permission
    public static final String MULTITENANT_ADMINPERMISSION = "multiTenantAdminPermission";
    //Class should receive fields describing site context information for the role
    public static final String MULTITENANT_ADMINROLE = "multiTenantAdminRole";
    //Class should receive fields describing site context information for the user
    public static final String MULTITENANT_ADMINUSER = "multiTenantAdminUser";
    //Class should receive fields describing ARCHIVE STATUS
    public static final String ARCHIVE_ONLY = "archiveOnly";
    //Class should receive fields describing Admin Auditable
    public static final String AUDITABLE_ONLY = "auditableOnly";
    //Class should receive addition status related fields (these are included automatically with SANDBOX marked entities)
    public static final String MULTI_PHASE_ADD = "multiPhaseAdd";
}
