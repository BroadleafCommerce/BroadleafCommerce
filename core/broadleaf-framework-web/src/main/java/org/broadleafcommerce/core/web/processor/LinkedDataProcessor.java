package org.broadleafcommerce.core.web.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.linked.data.CategoryLinkedDataService;
import org.broadleafcommerce.core.linked.data.DefaultLinkedDataService;
import org.broadleafcommerce.core.linked.data.HomepageLinkedDataService;
import org.broadleafcommerce.core.linked.data.ProductLinkedDataService;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.broadleafcommerce.core.web.catalog.ProductHandlerMapping;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafTagReplacementProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.codehaus.jettison.json.JSONException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This processor replaces linkedData tags with metadata for search engine optimization. The
 * data is formatted to Schema.org and Google standards.
 *
 * @author Jacob Mitash
 */
@Component("blLinkedDataProcessor")
public class LinkedDataProcessor extends AbstractBroadleafTagReplacementProcessor
{
    private final String TAG_NAME = "linkedData";
    private final Log LOG = LogFactory.getLog(LinkedDataProcessor.class);
    protected enum Destination { PRODUCT, CATEGORY, HOME, DEFAULT }

    @Resource(name = "blProductLinkedDataService")
    protected ProductLinkedDataService productLinkedDataService;

    @Resource(name = "blCategoryLinkedDataService")
    protected CategoryLinkedDataService categoryLinkedDataService;

    @Resource(name = "blHomepageLinkedDataService")
    protected HomepageLinkedDataService homepageLinkedDataService;

    @Resource(name = "blDefaultLinkedDataService")
    protected DefaultLinkedDataService defaultLinkedDataService;

    @Override
    public BroadleafTemplateModel getReplacementModel(String s, Map<String, String> map, BroadleafTemplateContext context) {
        Destination destination = resolveDestination(context.getRequest());

        String linkedDataText = "<script type=\"application/ld+json\">\n" +
                getDataForDestination(context.getRequest(), destination) +
                "\n</script>";

        BroadleafTemplateModel model = context.createModel();
        BroadleafTemplateElement linkedData = context.createTextElement(linkedDataText);
        model.addElement(linkedData);

        return model;
    }

    /**
     * Find out which page the user has requested
     * @param request the user HttpServletRequest
     * @return the destination page type
     */
    protected Destination resolveDestination(HttpServletRequest request) {
        if(request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME) != null) {
            return Destination.PRODUCT;
        } else if(request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME) != null) {
            return Destination.CATEGORY;
        } else if(request.getRequestURI().equals("/")) {
            return Destination.HOME;
        } else {
            return Destination.DEFAULT;
        }
    }

    /**
     * Get the metadata for the specific page
     * @param request the user request
     * @param destination the type of page trying to be visited
     * @return the JSON string representation of the linked data
     */
    protected String getDataForDestination(HttpServletRequest request, Destination destination) {
        try {

            if(destination == Destination.PRODUCT) {
                Product product = (Product) request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
                return productLinkedDataService.getLinkedData(product, request.getRequestURL().toString());

            } else if(destination == Destination.CATEGORY) {
                Category category = (Category) request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME);
                List<CategoryProductXref> productXrefs = category.getActiveProductXrefs();
                List<Product> products = new ArrayList<>(productXrefs.size());
                for(CategoryProductXref productXref : productXrefs) {
                    products.add(productXref.getProduct());
                }
                return categoryLinkedDataService.getLinkedData(products, request.getRequestURL().toString());
            } else if(destination == Destination.HOME) {
                return homepageLinkedDataService.getLinkedData(request.getRequestURL().toString());
            } else {
                return defaultLinkedDataService.getLinkedData(request.getRequestURL().toString());
            }

        } catch (JSONException e) {
            LOG.error("A JSON exception occurred while generating LinkedData", e);
            return "";
        }
    }

    @Override
    public String getName() {
        return TAG_NAME;
    }
}
