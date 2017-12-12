/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.linkeddata.generator;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

/**
 * This generator generates structured data specific for a homepage, namely the search action. The search action allows
 * search engines to recognize the site has a search feature and allows users to search the site directly from the
 * search engine.
 * <p>
 * See <a href="http://schema.org/Organization" target="_blank">http://schema.org/Organization</a>, 
 * <a href="http://schema.org/ContactPoint" target="_blank">http://schema.org/ContactPoint</a>, 
 * <a href="http://schema.org/WebSite" target="_blank">http://schema.org/WebSite</a>, 
 * and <a href="http://schema.org/SearchAction" target="_blank">http://schema.org/SearchAction</a>
 * 
 *
 * @author Jacob Mitash
 * @author Nathan Moore (nathanmoore).
 */
@Service(value = "blHomepageLinkedDataGenerator")
public class HomepageLinkedDataGeneratorImpl extends AbstractLinkedDataGenerator {

    @Override
    public boolean canHandle(final HttpServletRequest request) {
        return Objects.equals(request.getRequestURI(), "/");
    }

    @Override
    protected JSONArray getLinkedDataJsonInternal(final String url, final HttpServletRequest request,
                                                  final JSONArray schemaObjects) throws JSONException {
        schemaObjects.put(addWebSiteData(request));
        schemaObjects.put(addOrganizationData(request));

        extensionManager.getProxy().addHomepageData(request, schemaObjects);

        return schemaObjects;
    }

    /**
     * Generates an object representing the Schema.org organization
     *
     * @return JSON representation of Organization from Schema.org
     */
    protected JSONObject addOrganizationData(final HttpServletRequest request) throws JSONException {
        final JSONObject organization = new JSONObject();
        
        organization.put("@context", getStructuredDataContext());
        organization.put("@type", "Organization");
        organization.put("name", getSiteName());
        organization.put("url", getSiteBaseUrl());
        organization.put("logo", getLogoUrl());

        if (siteHasCustomerServiceNumber()) {
            organization.put("contactPoint", addContactData(request));
        }

        if (siteHasSocialLinks()) {
            organization.put("sameAs", addSocialMediaData(request));
        }

        extensionManager.getProxy().addOrganizationData(request, organization);

        return organization;
    }

    protected JSONArray addContactData(final HttpServletRequest request) throws JSONException {
        final JSONArray contactList = new JSONArray();
        final JSONObject contact = new JSONObject();
        
        contact.put("@type", "ContactPoint");
        contact.put("telephone", getSiteCustomerServiceNumber());
        contact.put("contactType", "customerService");

        extensionManager.getProxy().addContactData(request, contact);
        
        contactList.put(contact);
        
        return contactList;
    }

    /**
     * Generates an object representing the Schema.org WebSite
     *
     * @return JSON representation of WebSite from Schema.org
     */
    protected JSONObject addWebSiteData(final HttpServletRequest request) throws JSONException {
        JSONObject webSite = new JSONObject();
        webSite.put("@context", DEFAULT_STRUCTURED_CONTENT_CONTEXT);
        webSite.put("@type", "WebSite");
        webSite.put("name", getSiteName());
        webSite.put("url", getSiteBaseUrl());
        webSite.put("potentialAction", addPotentialActions(request));

        extensionManager.getProxy().addWebSiteData(request, webSite);

        return webSite;
    }

    /**
     * Generates an object representing the Schema.org image for the site logo
     *
     * @return JSON representation of Schema.org image for site logo
     */
    protected String getLogoUrl() throws JSONException {
        return getSiteBaseUrl() + getSiteLogo();
    }

    /**
     * Generates a JSON array of the organization's social media sites
     *
     * @throws JSONException
     */
    protected JSONArray addSocialMediaData(final HttpServletRequest request) throws JSONException {
        final String siteSocialAccounts = getSiteSocialAccounts();

        final JSONArray socialMediaData = new JSONArray(Arrays.asList(siteSocialAccounts.split(",")));
        
        extensionManager.getProxy().addSocialMediaData(request, socialMediaData);
        
        return socialMediaData;
    }

    protected JSONObject addPotentialActions(final HttpServletRequest request) throws JSONException {
        final JSONObject potentialAction = new JSONObject();

        potentialAction.put("@type", "SearchAction");
        potentialAction.put("target", getSiteBaseUrl() + getSiteSearchUri());
        potentialAction.put("query-input", "required name=query");

        extensionManager.getProxy().addPotentialActionsData(request, potentialAction);

        return potentialAction;
    }

    protected boolean siteHasCustomerServiceNumber() {
        return StringUtils.isNotBlank(getSiteCustomerServiceNumber());
    }

    protected boolean siteHasSocialLinks() {
        return StringUtils.isNotBlank(getSiteSocialAccounts());
    }

    protected String getSiteLogo() {
        return environment.getProperty("site.logo");
    }

    protected String getSiteCustomerServiceNumber() {
        return environment.getProperty("site.customerService.number");
    }

    protected String getSiteSocialAccounts() {
        return environment.getProperty("site.social.accounts");
    }

    protected String getSiteSearchUri() {
        return "/" + environment.getProperty("site.search");
    }

    protected String getSiteName() {
        return environment.getProperty("site.name");
    }
}
