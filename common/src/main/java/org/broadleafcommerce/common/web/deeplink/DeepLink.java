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
package org.broadleafcommerce.common.web.deeplink;

/**
 * DTO Class that contains enough information to allow the client site application to generate
 * the necessary information for a link to an admin screen
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class DeepLink {

    protected String adminBaseUrl;
    protected String urlFragment;
    protected String displayText;
    protected Object sourceObject;

    /* ******************* *
     * WITH FLUID BUILDERS *
     * ******************* */
    
    public DeepLink withAdminBaseUrl(String adminBaseUrl) {
        setAdminBaseUrl(adminBaseUrl);
        return this;
    }

    public DeepLink withUrlFragment(String urlFragment) {
        setUrlFragment(urlFragment);
        return this;
    }

    public DeepLink withDisplayText(String displayText) {
        setDisplayText(displayText);
        return this;
    }
    
    public DeepLink withSourceObject(Object sourceObject) {
        setSourceObject(sourceObject);
        return this;
    }

    /* ************************ *
     * CUSTOM GETTERS / SETTERS *
     * ************************ */

    public void setAdminBaseUrl(String adminBaseUrl) {
        if (adminBaseUrl.charAt(adminBaseUrl.length() - 1) == '/') {
            adminBaseUrl = adminBaseUrl.substring(0, adminBaseUrl.length() - 1);
        }
        this.adminBaseUrl = adminBaseUrl;
    }

    public void setUrlFragment(String urlFragment) {
        if (urlFragment.charAt(0) == '/') {
            urlFragment = urlFragment.substring(1);
        }
        this.urlFragment = urlFragment;
    }
    
    public String getFullUrl() {
        return adminBaseUrl + "/" + urlFragment;
    }

    /* ************************* *
     * GENERIC GETTERS / SETTERS *
     * ************************* */

    public String getAdminBaseUrl() {
        return adminBaseUrl;
    }

    public String getUrlFragment() {
        return urlFragment;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
    
    public Object getSourceObject() {
        return sourceObject;
    }
    
    public void setSourceObject(Object sourceObject) {
        this.sourceObject = sourceObject;
    }
    
}
