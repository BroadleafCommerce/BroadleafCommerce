package org.broadleafcommerce.core.linked.data;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class provides a few methods for generating default schema objects
 * that are universal to the entire site.
 *
 * @author Jacob Mitash
 */
public abstract class AbstractLinkedDataService {

    protected final static String DEFAULT_CONTEXT = "http://schema.org/";

    @Autowired
    protected Environment environment;

    /**
     * Generates an object representing the Schema.org organization
     *
     * @param url The URL of the currently visited page
     * @return JSON representation of Organization from Schema.org
     * @throws JSONException
     */
    protected JSONObject getDefaultOrganization(String url) throws JSONException {
        JSONObject organization = new JSONObject();

        organization.put("@context", DEFAULT_CONTEXT);
        organization.put("@type", "Organization");
        organization.put("name", environment.getProperty("site.name"));
        organization.put("url", getHomepageUrl(url));
        organization.put("logo", getLogoObject());
        organization.put("sameAs", getSocialMediaList());

        return organization;
    }

    /**
     * Generates an object representing the Schema.org WebSite
     *
     * @param url The URL of the currently visited page
     * @return JSON representation of WebSite from Schema.org
     */
    protected JSONObject getDefaultWebSite(String url) throws JSONException {
        JSONObject webSite = new JSONObject();
        webSite.put("@context", DEFAULT_CONTEXT);
        webSite.put("@type", "WebSite");
        webSite.put("name", environment.getProperty("site.name"));
        webSite.put("url", getHomepageUrl(url));

        return webSite;
    }

    /**
     * Generates an object representing the Schema.org BreadcrumbList
     * TODO: implement
     * @return JSON representation of BreadcrumbList from Schema.org
     */
    protected JSONObject getDefaultBreadcrumbList() throws JSONException {
        JSONObject breadcrumb = new JSONObject();

        breadcrumb.put("@context", DEFAULT_CONTEXT);
        breadcrumb.put("@type", "BreadcrumbList");

        JSONArray breadcrumbList = new JSONArray();
//        breadcrumbList.put();

        breadcrumb.put("itemListElement", breadcrumbList);

        return breadcrumb;
    }

    /**
     * Generates an object representing the Schema.org image for the site logo
     *
     * @return JSON representation of Schema.org image for site logo
     */
    protected JSONObject getLogoObject() throws JSONException {
        JSONObject logoImage = new JSONObject();
        logoImage.put("@type", "ImageObject");
        logoImage.put("url", environment.getProperty("site.logo"));
        return logoImage;
    }

    /**
     * Generates a JSON array of the organization's social media sites
     * TODO: implement
     * @return
     * @throws JSONException
     */
    protected JSONArray getSocialMediaList() throws JSONException {
        JSONArray socialMedia = new JSONArray();

        return socialMedia;
    }


    /**
     * Given any full page URL, gets the URL of the homepage
     *
     * @param urlFull the full URL of any page on the site
     * @return the URL of the homepage of the site
     */
    protected String getHomepageUrl(String urlFull) {
        try {
            URL url = new URL(urlFull);
            return url.getProtocol() + "://" + url.getHost() + "/";
        } catch (MalformedURLException e) {
            return "";
        }
    }
}
