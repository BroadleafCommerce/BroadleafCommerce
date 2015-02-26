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
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
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

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public BreadcrumbsProcessor() {
        super("breadcrumbs");
        LOG.info("breadcrumbs processor instantiated");
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
        LOG.info("inside BreadcrumbsProcessor.modifyModelAttributes");
        String resultVar = element.getAttributeValue("resultVar");
        String entity = element.getAttributeValue("entity");

        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, entity);

        Object entityObj = expression.execute(arguments.getConfiguration(), arguments);

        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        String baseUrl = context.getRequestURIWithoutContext();

        if (entityObj instanceof Product) {
            boolean usesProductId = BLCSystemProperty.resolveBooleanSystemProperty("product.url.use.id");
            Product product = (Product) entityObj;
            LOG.info("the object type is Product");
            List<BreadcrumbDTO> bcDtos = new ArrayList<BreadcrumbDTO>();
            if (usesProductId) {
                List<String> subsets = findSubsets(baseUrl);
                for (String string : subsets) {
                    Category category = catalogService.findCategoryByURI(string);
                    if (category != null) {
                        BreadcrumbDTO bcDto = new BreadcrumbDTO();
                        bcDto.setLink(string);
                        bcDto.setText(category.getName());
                        bcDtos.add(bcDto);
                    } else {
                        LOG.error("No category found for URI=" + string);
                    }
                }
            } else {
                LOG.info("building the product URL with its default category only");                
                Category parentCategory = product.getDefaultCategory();
                if (parentCategory!=null){
                  BreadcrumbDTO bcDto = new BreadcrumbDTO(parentCategory.getUrl(), parentCategory.getName());
                  bcDtos.add(bcDto);
                }
            }
            BreadcrumbDTO last=new BreadcrumbDTO(null, product.getName());
            bcDtos.add(last);
            addToModel(arguments, resultVar, bcDtos);
        } else if (entityObj instanceof Category) {
            boolean usesCategoryId = BLCSystemProperty.resolveBooleanSystemProperty("category.url.use.id");
            LOG.info("the object type is Category");
            Category category = (Category) entityObj;
            List<BreadcrumbDTO> bcDtos = new ArrayList<BreadcrumbDTO>();
            if (usesCategoryId) {
                List<String> subsets = findSubsets(baseUrl);
                for (String string : subsets) {
                    Category categorySeg = catalogService.findCategoryByURI(string);
                    if (category != null) {
                        BreadcrumbDTO bcDto = new BreadcrumbDTO(string, categorySeg.getName());
                        bcDtos.add(bcDto);
                    } else {
                        LOG.error("No category found for URI=" + string);
                    }
                }
            } else {
                LOG.info("building the category URL with its default parent category only");
                Category parentCategory = category.getDefaultParentCategory();
                if (parentCategory!=null){
                  BreadcrumbDTO bcDto = new BreadcrumbDTO(parentCategory.getUrl(), parentCategory.getName());
                  bcDtos.add(bcDto);
                }
            }
            BreadcrumbDTO last=new BreadcrumbDTO(null, category.getName());
            bcDtos.add(last);
            addToModel(arguments, resultVar, bcDtos);
        }
    }

    /**
     * gets all possible consecutive token subsets of a URL string, leaving aside the last
     * @param url (i.e. /cat1/cat2/cat3/product1  or /parentCategory1/pCat2/pCat3/category)
     * @return (i.e. a list containing: ["/cat1/cat2/cat3", "/cat1/cat2", and "/cat1"] or ["pCat1/pCat2/pCat3", "pCat1/pCat2", "pCat1])
     */
    private List<String> findSubsets(String url) {
        String[] urlSegments = url.split("/");
        //remove the first element (as the URL will always start with /)
        //and the last (assumed to be the product or last category, which should never provide a link)
        List<String> segments = new ArrayList<String>(Arrays.asList(urlSegments));
        segments.remove(0);
        segments.remove(segments.size() - 1);
        //urlSegments = Arrays.copyOfRange(urlSegments, 1, urlSegments.length-1);

        LOG.info("starting url is " + url);
        List<String> subsets = new ArrayList<String>();
        int offset = 0;
        for (int i = 0; i < segments.size(); i++) {
            StringBuffer sb = new StringBuffer("/");
            for (int e = 0; e < segments.size() - offset; e++) {
                sb.append(segments.get(e));
                if (e < (segments.size() - offset-1)) {
                    sb.append("/");
                }
            }
            subsets.add(sb.toString());
            LOG.info("subset " + sb.toString());
            offset++;
        }
        return subsets;
    }

}
