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
package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.core.env.Environment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides a few methods for generating default schema objects
 * that are universal to the entire site.
 *
 * @author Jacob Mitash
 */
public class LinkedDataUtil {

    protected final static String DEFAULT_CONTEXT = "http://schema.org/";

    /**
     * Generates an object representing the Schema.org organization
     *
     * @param url The URL of the currently visited page
     * @return JSON representation of Organization from Schema.org
     */
    protected static JSONObject getDefaultOrganization(Environment environment, String url) throws JSONException {
        JSONObject organization = new JSONObject();

        organization.put("@context", DEFAULT_CONTEXT);
        organization.put("@type", "Organization");
        organization.put("name", environment.getProperty("site.name"));
        organization.put("url", getHomepageUrl(url));
        organization.put("logo", getLogoObject(environment));
        organization.put("sameAs", getSocialMediaList());

        return organization;
    }

    /**
     * Generates an object representing the Schema.org WebSite
     *
     * @param url The URL of the currently visited page
     * @return JSON representation of WebSite from Schema.org
     */
    protected static JSONObject getDefaultWebSite(Environment environment, String url) throws JSONException {
        JSONObject webSite = new JSONObject();
        webSite.put("@context", DEFAULT_CONTEXT);
        webSite.put("@type", "WebSite");
        webSite.put("name", environment.getProperty("site.name"));
        webSite.put("url", getHomepageUrl(url));

        return webSite;
    }

    /**
     * Generates an object representing the Schema.org BreadcrumbList
     *
     * @param url The full URL of the requested page
     * @return JSON representation of BreadcrumbList from Schema.org
     */
    protected static JSONObject getDefaultBreadcrumbList(BreadcrumbService breadcrumbService, String url) throws JSONException {
        JSONObject breadcrumbObjects = new JSONObject();

        breadcrumbObjects.put("@context", DEFAULT_CONTEXT);
        breadcrumbObjects.put("@type", "BreadcrumbList");

        Map<String, String[]> params = new HashMap<>();

        if(BroadleafRequestContext.getRequestParameterMap() != null) {
            params = new HashMap<>(BroadleafRequestContext.getRequestParameterMap());
        }

        String homepageUrl = getHomepageUrl(url);
        String homepageNoSlash = homepageUrl.substring(0, homepageUrl.length() - 1);

        List<BreadcrumbDTO> breadcrumbs = breadcrumbService.buildBreadcrumbDTOs(homepageUrl, params);

        JSONArray breadcrumbList = new JSONArray();
        for(int i = 1; i < breadcrumbs.size(); i++) {
            JSONObject listItem = new JSONObject();
            listItem.put("@type", "ListItem");
            listItem.put("position", i);
            JSONObject item = new JSONObject();
            item.put("@id", homepageNoSlash + breadcrumbs.get(i).getLink());
            item.put("name", breadcrumbs.get(i).getText());

            listItem.put("item", item);
            breadcrumbList.put(listItem);
        }

        breadcrumbObjects.put("itemListElement", breadcrumbList);

        return breadcrumbObjects;
    }

    /**
     * Generates an object representing the Schema.org image for the site logo
     *
     * @return JSON representation of Schema.org image for site logo
     */
    protected static JSONObject getLogoObject(Environment environment) throws JSONException {
        JSONObject logoImage = new JSONObject();
        logoImage.put("@type", "ImageObject");
        logoImage.put("url", environment.getProperty("site.logo"));
        return logoImage;
    }

    /**
     * Generates a JSON array of the organization's social media sites
     *
     * @return
     * @throws JSONException
     */
    protected static JSONArray getSocialMediaList() throws JSONException {
        JSONArray socialMedia = new JSONArray();
        //TODO: implement
        return socialMedia;
    }


    /**
     * Given any full page URL, gets the URL of the homepage
     *
     * @param urlFull the full URL of any page on the site
     * @return the URL of the homepage of the site
     */
    protected static String getHomepageUrl(String urlFull) {
        try {
            URL url = new URL(urlFull);
            return url.getProtocol() + "://" + url.getHost() + "/";
        } catch (MalformedURLException e) {
            return "";
        }
    }
}
