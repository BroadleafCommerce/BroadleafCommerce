package org.broadleafcommerce.core.web.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.linked.data.CategorySearchLinkedDataService;
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
 * Created by jacobmitash on 6/28/17.
 */
@Component("blLinkedDataProcessor")
public class LinkedDataProcessor extends AbstractBroadleafTagReplacementProcessor
{
    private final String TAG_NAME = "linkedData";
    private final Log LOG = LogFactory.getLog(LinkedDataProcessor.class);
    protected enum Destination { PRODUCT, CATEGORY_SEARCH, HOME, DEFAULT }

    @Resource(name = "blProductLinkedDataService")
    ProductLinkedDataService productLinkedDataService;

    @Resource(name = "blCategorySearchLinkedDataService")
    CategorySearchLinkedDataService categorySearchLinkedDataService;

    @Resource(name = "blHomepageLinkedDataService")
    HomepageLinkedDataService homepageLinkedDataService;

    @Override
    public BroadleafTemplateModel getReplacementModel(String s, Map<String, String> map, BroadleafTemplateContext context) {
        Destination destination = resolveDestination(context.getRequest());

        StringBuffer buffer = new StringBuffer();
        buffer.append("<script type=\"application/ld+json\">\n");
        buffer.append(getDataForDestination(context.getRequest(), destination));
        buffer.append("\n</script>");

        BroadleafTemplateModel model = context.createModel();
        BroadleafTemplateElement linkedData = context.createTextElement(buffer.toString());
        model.addElement(linkedData);

        return model;
    }

    protected Destination resolveDestination(HttpServletRequest request) {
        if(request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME) != null) {
            return Destination.PRODUCT;
        } else if(request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME) != null) {
            return Destination.CATEGORY_SEARCH;
        } else if(request.getRequestURI().equals("/")) {
            return Destination.HOME;
        } else {
            return Destination.DEFAULT;
        }
    }

    protected String getDataForDestination(HttpServletRequest request, Destination destination) {
        try {

            if(destination == Destination.PRODUCT) {
                Product product = (Product) request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
                return productLinkedDataService.getLinkedData(product, request.getRequestURL().toString());

            } else if(destination == Destination.CATEGORY_SEARCH) {
                Category category = (Category) request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME);
                List<CategoryProductXref> productXrefs = category.getActiveProductXrefs();
                List<Product> products = new ArrayList<>(productXrefs.size());
                for(CategoryProductXref productXref : productXrefs) {
                    products.add(productXref.getProduct());
                }
                return categorySearchLinkedDataService.getLinkedData(products);
            } else if(destination == Destination.HOME) {
                return homepageLinkedDataService.getLinkedData(request.getRequestURL().toString());
            } else {
                return "";
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
