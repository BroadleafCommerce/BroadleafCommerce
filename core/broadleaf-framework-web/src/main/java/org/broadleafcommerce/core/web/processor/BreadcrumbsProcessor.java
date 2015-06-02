/*
 * #%L
 * BroadleafCommerce Menu
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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

package org.broadleafcommerce.core.web.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.web.processor.extension.BreadcrumbsProcessorExtensionManager;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * A Thymeleaf Element processor that will take a Product or a Category object, and 
 * provide a breadcrumbs path, consisting of a collection of BreadcrumDTO's.
 * 
 * @author gdiaz
 */

public class BreadcrumbsProcessor extends AbstractModelVariableModifierProcessor {

    private static final Log LOG = LogFactory.getLog(BreadcrumbsProcessor.class);

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blBreadcrumbsProcessorExtensionManager")
    protected BreadcrumbsProcessorExtensionManager extensionManager;

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public BreadcrumbsProcessor() {
        super("breadcrumbs");
        LOG.debug("breadcrumbs processor instantiated");
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }

    @Override
    /**
     * populates the resultVar with a list of DTO's that will provide the product's or category's breadcrumbs.
     * If the corresponding "use.." system variable is set, then the breadcrumbs will be formed by trying to 
     * retrieve categories for each subset of url tokens.
     * 
     * If the "use.." system property is not set, then only a short URI is given, consisting of only the default category
     */
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        if (element.hasAttribute("entity")) {
            processEntities(arguments, element);
        } else {
            processSearch(arguments, element);
        }
    }

    /**
     * generates the "crumbs" resultVar when there is no "entity" passed as a 
     * parameter, and the only thing we have in order to generate the breadcrumb is the 
     * search parameter itself.
     * @param arguments
     * @param element
     */
    private void processSearch(Arguments arguments, Element element) {
        LOG.debug("the breadcrumbs processor is dealing with a search");
        String resultVar = element.getAttributeValue("resultVar");

        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        Map<String, String[]> parameters = BroadleafRequestContext.getRequestParameterMap();

        String searchString = parameters.get("q")[0];
        List<BreadcrumbDTO> bcDtos = new ArrayList<BreadcrumbDTO>();

        //"home" node 
        String baseUrl = context.getRequestURIWithoutContext();
        addHomeNode(bcDtos, baseUrl);

        //search string node
        StringBuffer sb = new StringBuffer(BLCMessageUtils.getMessage("breadcrumbs.search"));
        sb.append(" \"").append(searchString).append("\"");
        BreadcrumbDTO last = new BreadcrumbDTO(null, sb.toString());
        bcDtos.add(last);

        extensionManager.getProxy().addAdditionalFieldsToModel(arguments, element);
        addToModel(arguments, resultVar, bcDtos);
    }
    
    /**
     * a special case of breadcrumb formation with a category: if it has a "q" request parameter, then 
     * the breadcrumb is just formed this way: [Home] -> [Cateogry Name] -> Search: [search string]
     * @param arguments
     * @param element
     * @param category
     */
    private void processCategoryWithSearch(Arguments arguments, Element element, Category category) {
        LOG.debug("the breadcrumbs processor is dealing with a category having a search parameter");
        String resultVar = element.getAttributeValue("resultVar");

        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        Map<String, String[]> parameters = BroadleafRequestContext.getRequestParameterMap();

        String searchString = parameters.get("q")[0];
        List<BreadcrumbDTO> bcDtos = new ArrayList<BreadcrumbDTO>();

        //"home" node 
        String baseUrl = context.getRequestURIWithoutContext();
        addHomeNode(bcDtos, baseUrl);
        
        //bracketed [Category Name] node
        StringBuffer sb = new StringBuffer("[").append(category.getName()).append("]");
        BreadcrumbDTO bracketedCategory = new BreadcrumbDTO(category.getUrl(), sb.toString());
        bcDtos.add(bracketedCategory);

        //search string node
        sb = new StringBuffer(BLCMessageUtils.getMessage("breadcrumbs.search"));
        sb.append(" \"").append(searchString).append("\"");
        BreadcrumbDTO last = new BreadcrumbDTO(null, sb.toString());
        bcDtos.add(last);

        extensionManager.getProxy().addAdditionalFieldsToModel(arguments, element);
        addToModel(arguments, resultVar, bcDtos);
    }   

    /**
     * creates the "crumbs" resultVar, when there is an "entity" parameter passed by the 
     * tag. That parameter can be a Product or a Category object 
     * @param arguments
     * @param element
     */
    private void processEntities(Arguments arguments, Element element) {
        LOG.debug("the breadcrumbs processor received an \"entity\" object parameter");
        String resultVar = element.getAttributeValue("resultVar");
        String entity = element.getAttributeValue("entity");

        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, entity);

        Object entityObj = expression.execute(arguments.getConfiguration(), arguments);

        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        String baseUrl = context.getRequestURIWithoutContext();

        if (entityObj instanceof Product) {
            Product product = (Product) entityObj;
            List<BreadcrumbDTO> bcDtos = new ArrayList<BreadcrumbDTO>();
            if (getUseCategoryId()) {
                LOG.debug("category.url.use.id=true");
                List<String> subsets = findSubsets(baseUrl);
                for (String string : subsets) {
                    Category category = catalogService.findCategoryByURI(string);
                    if (category != null) {
                        BreadcrumbDTO bcDto = createCategoryBreadcrumbDTO(string, category);
                        bcDtos.add(bcDto);
                    } else {
                        LOG.warn("No category found for URI=" + string);
                    }
                }
            } else {
                LOG.info("building the product URL with its default category only");
                Category parentCategory = product.getDefaultCategory();
                if (parentCategory != null) {
                    BreadcrumbDTO bcDto = new BreadcrumbDTO(parentCategory.getUrl(), parentCategory.getName());
                    bcDtos.add(bcDto);
                }
            }
            BreadcrumbDTO last = new BreadcrumbDTO(null, product.getName());
            bcDtos.add(last);
            addHomeNode(bcDtos, baseUrl);
            extensionManager.getProxy().addAdditionalFieldsToModel(arguments, element);
            addToModel(arguments, resultVar, bcDtos);

        } else if (entityObj instanceof Category) {
            Category category = (Category) entityObj;
            processCategory(arguments, element, category, baseUrl, resultVar);
         }
    }

    /**
     * Finishes processing the request and adding resultVar to the model, when the passed "entity" object is
     * of the type Category. Considers a special case, when the base URL uses search parameters
     * @param argumentss
     * @param element
     * @param category
     * @param baseUrl
     * @param resultVar
     */
    private void processCategory(Arguments arguments, Element element, Category category, String  baseUrl, String resultVar){
        LOG.debug("the breadcrumbs processor is dealing with a category");
        Map<String, String[]> parameters = BroadleafRequestContext.getRequestParameterMap();
        boolean usesCategorySearch = BLCSystemProperty.resolveBooleanSystemProperty("use.category.search.breadcrumbs");
        if (parameters.get("q")!=null && usesCategorySearch){
            processCategoryWithSearch( arguments,  element,  category);
        }else{        
            List<BreadcrumbDTO> bcDtos = new ArrayList<BreadcrumbDTO>();
            if (getUseCategoryId()) {
                LOG.debug("category.url.use.id=true");
                List<String> subsets = findSubsets(baseUrl);
                for (String string : subsets) {
                    Category categorySeg = catalogService.findCategoryByURI(string);
                    if (categorySeg != null) {
                        BreadcrumbDTO bcDto = createCategoryBreadcrumbDTO(string, categorySeg);
                        bcDtos.add(bcDto);
                    } else {
                        LOG.warn("No category found for URI=" + string);
                    }
                }
            } else {
                LOG.info("building the category URL with its default parent category only");
                Category parentCategory = category.getDefaultParentCategory();
                if (parentCategory != null) {
                    BreadcrumbDTO bcDto = new BreadcrumbDTO(parentCategory.getUrl(), parentCategory.getName());
                    bcDtos.add(bcDto);
                }
            }
            BreadcrumbDTO last = new BreadcrumbDTO(null, category.getName());
            bcDtos.add(last);
            addHomeNode(bcDtos, baseUrl);
            extensionManager.getProxy().addAdditionalFieldsToModel(arguments, element);
            addToModel(arguments, resultVar, bcDtos);
        }
    }

    /**
     * adds a "Home" type of node to the breadcrumbs segment. This should occur whatever the type of 
     * the intersected object
     * @param crumbs
     * @param baseUrl
     */
    private void addHomeNode(List<BreadcrumbDTO> crumbs, String baseUrl) {
        BreadcrumbDTO home = new BreadcrumbDTO("/", BLCMessageUtils.getMessage("breadcrumbs.home"));
        crumbs.add(0, home);
    }

    /**
     * gets all possible consecutive token subsets of a URL string, leaving aside the last
     * @param url (i.e. /cat1/cat2/cat3/product1  or /parentCategory1/pCat2/pCat3/category)
     * @return (i.e. a list containing: ["/cat1", "/cat1/cat2", and "/cat1/cat2/cat3"] 
     * or ["pCat1", "pCat1/pCat2", "pCat1/pCat2/pCat3"])
     */
    private List<String> findSubsets(String url) {
        String[] urlSegments = url.split("/");
        //remove the first element (as the URL will always start with /)
        //and the last (assumed to be the product or last category, which should never provide a link)
        List<String> segments = new ArrayList<String>(Arrays.asList(urlSegments));
        segments.remove(0);
        segments.remove(segments.size() - 1);
        //urlSegments = Arrays.copyOfRange(urlSegments, 1, urlSegments.length-1);

        LOG.debug("starting url is " + url);
        List<String> subsets = new ArrayList<String>();
        int offset = 0;
        for (int i = 0; i < segments.size(); i++) {
            StringBuffer sb = new StringBuffer("/");
            for (int e = 0; e < segments.size() - offset; e++) {
                sb.append(segments.get(e));
                if (e < (segments.size() - offset - 1)) {
                    sb.append("/");
                }
            }
            subsets.add(sb.toString());
            LOG.debug("subset " + sb.toString());
            offset++;
        }
        //sort the subsets from shortest to longest
        Collections.sort(subsets);
        return subsets;
    }

    private boolean getUseCategoryId() {
        return BLCSystemProperty.resolveBooleanSystemProperty("category.url.use.id");
    }

    private BreadcrumbDTO createCategoryBreadcrumbDTO(String url, Category category) {
        StringBuffer sb = new StringBuffer(url).append("?categoryId=").append(category.getId());
        return new BreadcrumbDTO(sb.toString(), category.getName());
    }

}
