/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
    //Class should receive fields that describe the original entity from which a sandbox clone was derived
    public static final String SANDBOX_PRECLONE_INFORMATION = "sandboxPreCloneInformation";
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

}
