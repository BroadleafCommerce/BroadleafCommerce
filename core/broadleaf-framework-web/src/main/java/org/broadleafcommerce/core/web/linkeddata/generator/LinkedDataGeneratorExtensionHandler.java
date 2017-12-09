package org.broadleafcommerce.core.web.linkeddata.generator;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * Extension handler for extending functionality of {@link org.broadleafcommerce.core.web.linkeddata.generator.LinkedDataGenerator}.
 * Implementors should extend from {@link AbstractLinkedDataGeneratorExtensionHandler} to protect from API changes to this interface
 * 
 * @author Nathan Moore (nathanmoore).
 */
public interface LinkedDataGeneratorExtensionHandler extends ExtensionHandler{
    // default
    ExtensionResultStatusType addDefaultData(final HttpServletRequest request, final JSONArray schemaObjects) throws JSONException;
    ExtensionResultStatusType addBreadcrumbData(final HttpServletRequest request, final JSONObject breadcrumbData) throws JSONException;
    ExtensionResultStatusType addBreadcrumbListItemData(final HttpServletRequest request, final JSONObject breadcrumbData) throws JSONException;
    ExtensionResultStatusType addBreadcrumbItemData(final HttpServletRequest request, final JSONObject breadcrumbData) throws JSONException;
    // homepage
    ExtensionResultStatusType addHomepageData(final HttpServletRequest request, final JSONArray schemaObjects) throws JSONException;
    ExtensionResultStatusType addWebSiteData(final HttpServletRequest request, final JSONObject homepageData) throws JSONException;
    ExtensionResultStatusType addOrganizationData(final HttpServletRequest request, final JSONObject homepageData) throws JSONException;
    ExtensionResultStatusType addContactData(final HttpServletRequest request, final JSONObject homepageData) throws JSONException;
    ExtensionResultStatusType addSocialMediaData(final HttpServletRequest request, final JSONArray homepageData) throws JSONException;
    ExtensionResultStatusType addPotentialActionsData(final HttpServletRequest request, final JSONObject homepageData) throws JSONException;
    // category
    ExtensionResultStatusType addCategoryData(final HttpServletRequest request, final JSONObject categoryData) throws JSONException;
    ExtensionResultStatusType addCategoryProductData(final HttpServletRequest request, final JSONObject categoryData) throws JSONException;
    // product
    ExtensionResultStatusType addReviewData(final HttpServletRequest request, final Product product, final JSONObject reviewData) throws JSONException;
    ExtensionResultStatusType addAggregateReviewData(final HttpServletRequest request, final Product product, final JSONObject reviewData) throws JSONException;
    ExtensionResultStatusType addProductData(final HttpServletRequest request, final Product product, final JSONObject productData) throws JSONException;
    ExtensionResultStatusType addSkuData(final HttpServletRequest request, final Product product, final JSONObject skuData) throws JSONException;
    ExtensionResultStatusType addAggregateSkuData(final HttpServletRequest request, final Product product, final JSONObject skuData) throws JSONException;
}
