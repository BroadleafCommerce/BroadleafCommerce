/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.file.service;

public interface StaticAssetPathService {

    /**
     * This method will take in a content string (e.g. StructuredContentDTO or PageDTO HTML/ASSET_LOOKUP/STRING field value)
     * and replace any instances of "staticAssetUrlPrefix" in the string with the "staticAssetEnvironmentUrlPrefix"
     * or the "staticAssetEnvironmentSecureUrlPrefix" depending on if the request was secure and if it was configured.
     *
     * @param content       - The content string to rewrite if it contains a cms managed asset
     * @param secureRequest - True if the request is being served over https
     */
    String convertAllAssetPathsInContent(String content, boolean secureRequest);

    /**
     * This method will take in an assetPath (think image url) and convert it if
     * the value contains the asseturlprefix.
     *
     * @param assetPath     - The path to rewrite if it is a cms managed asset
     * @param contextPath   - The context path of the web application (if applicable)
     * @param secureRequest - True if the request is being served over https
     * @see StaticAssetService#getStaticAssetUrlPrefix()
     * @see StaticAssetService#getStaticAssetEnvironmentUrlPrefix()
     */
    String convertAssetPath(String assetPath, String contextPath, boolean secureRequest);

    /**
     * Returns the value configured to mark an item as a static URL.
     * <p>
     * OOB BLC maintains this value in common.properties.
     */
    String getStaticAssetUrlPrefix();

    /**
     * Sets the static asset url prefix
     *
     * @param prefix
     */
    void setStaticAssetUrlPrefix(String prefix);

    /**
     * Returns the value configured for the current environment
     * for the static asset url prefix.   If this is different than
     * the common value, then the URLs will get rewritten by the
     * FieldMapWrapper when called from the DisplayContentTag or
     * ProcessURLFilter.
     */
    String getStaticAssetEnvironmentUrlPrefix();

    /**
     * Sets the environment url prefix.
     *
     * @param prefix
     */
    void setStaticAssetEnvironmentUrlPrefix(String prefix);

    /**
     * Returns the secure value of the environment url prefix (e.g. prefixed with https if needed).
     */
    String getStaticAssetEnvironmentSecureUrlPrefix();

}
