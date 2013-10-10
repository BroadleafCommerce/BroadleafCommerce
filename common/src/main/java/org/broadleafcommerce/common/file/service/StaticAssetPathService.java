/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.file.service;


public interface StaticAssetPathService {

    /**
     * This method will take in an assetPath (think image url) and convert it if
     * the value contains the asseturlprefix.
     * @see StaticAssetService#getStaticAssetUrlPrefix()
     * @see StaticAssetService#getStaticAssetEnvironmentUrlPrefix()
     * 
     * @param assetPath - The path to rewrite if it is a cms managed asset
     * @param contextPath - The context path of the web application (if applicable)
     * @param secureRequest - True if the request is being served over https
     */
    public String convertAssetPath(String assetPath, String contextPath, boolean secureRequest);

    /**
     * Returns the value configured to mark an item as a static URL.
     *
     * OOB BLC maintains this value in common.properties.
     */
    public String getStaticAssetUrlPrefix();

    /**
     * Sets the static asset url prefix
     * @param prefix
     */
    public void setStaticAssetUrlPrefix(String prefix);

    /**
     * Returns the value configured for the current environment
     * for the static asset url prefix.   If this is different than
     * the common value, then the URLs will get rewritten by the
     * FieldMapWrapper when called from the DisplayContentTag or
     * ProcessURLFilter.
     */
    public String getStaticAssetEnvironmentUrlPrefix();

    /**
     * Sets the environment url prefix.
     * @param prefix
     */
    public void setStaticAssetEnvironmentUrlPrefix(String prefix);

    /**
     * Returns the secure value of the environment url prefix (e.g. prefixed with https if needed).
     */
    public String getStaticAssetEnvironmentSecureUrlPrefix();

}
