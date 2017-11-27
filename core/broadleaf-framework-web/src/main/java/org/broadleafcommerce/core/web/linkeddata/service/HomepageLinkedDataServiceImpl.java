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
package org.broadleafcommerce.core.web.linkeddata.service;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * This service generates metadata specialized for the homepage, namely the search action. The search action allows
 * search engines to recognize the site has a search feature and allows users to search the site directly from the
 * search engine.
 *
 * @author Jacob Mitash
 */
@Service(value = "blHomepageLinkedDataServiceImpl")
public class HomepageLinkedDataServiceImpl extends DefaultLinkedDataServiceImpl {

    @Override
    public Boolean canHandle(LinkedDataDestinationType destination) {
        return LinkedDataDestinationType.HOME.equals(destination);
    }

    @Override
    protected JSONArray getLinkedDataJson(String url, List<Product> products) throws JSONException {
        JSONArray schemaObjects = new JSONArray();

        schemaObjects.put(getWebSite());
        schemaObjects.put(getBreadcrumbList());
        schemaObjects.put(getOrganization());

        return schemaObjects;
    }

    /**
     * Generates an object representing the Schema.org organization
     *
     * @return JSON representation of Organization from Schema.org
     */
    protected JSONObject getOrganization() throws JSONException {
        JSONObject organization = new JSONObject();

        organization.put("@context", DEFAULT_CONTEXT);
        organization.put("@type", "Organization");
        organization.put("name", getSiteName());
        organization.put("url", getSiteBaseUrl());
        organization.put("logo", getLogoUrl());

        if (siteHasCustomerServiceNumber()) {
            organization.put("contactPoint", getContactList());
        }

        if (siteHasSocialLinks()) {
            organization.put("sameAs", getSocialMediaList());
        }

        return organization;
    }

    private JSONArray getContactList() throws JSONException {
        JSONArray contactList = new JSONArray();

        JSONObject contact = new JSONObject();
        contact.put("@type", "ContactPoint");
        contact.put("telephone", getSiteCustomerServiceNumber());
        contact.put("contactType", "customerService");
        contactList.put(contact);

        return contactList;
    }

    /**
     * Generates an object representing the Schema.org WebSite
     *
     * @return JSON representation of WebSite from Schema.org
     */
    protected JSONObject getWebSite() throws JSONException {
        JSONObject webSite = new JSONObject();
        webSite.put("@context", DEFAULT_CONTEXT);
        webSite.put("@type", "WebSite");
        webSite.put("name", getSiteName());
        webSite.put("url", getSiteBaseUrl());
        webSite.put("potentialAction", getPotentialAction());

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
    protected JSONArray getSocialMediaList() throws JSONException {
        String siteSocialAccounts = getSiteSocialAccounts();
        String[] socialAccounts = siteSocialAccounts.split(",");

        return new JSONArray(Arrays.asList(socialAccounts));
    }

    protected JSONObject getPotentialAction() throws JSONException {
        JSONObject potentialAction = new JSONObject();

        potentialAction.put("@type", "SearchAction");
        potentialAction.put("target", getSiteBaseUrl() + getSiteSearchUri());
        potentialAction.put("query-input", "required name=query");

        return potentialAction;
    }

    private boolean siteHasCustomerServiceNumber() {
        return StringUtils.isNotBlank(getSiteCustomerServiceNumber());
    }

    private boolean siteHasSocialLinks() {
        return StringUtils.isNotBlank(getSiteSocialAccounts());
    }

    protected String getSiteName() {
        return environment.getProperty("site.name");
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
        return environment.getProperty("site.search");
    }
}
