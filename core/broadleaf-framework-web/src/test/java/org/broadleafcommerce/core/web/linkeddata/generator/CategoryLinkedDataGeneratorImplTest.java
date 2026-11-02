/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchResult;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.broadleafcommerce.core.web.service.SearchFacetDTOService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CategoryLinkedDataGeneratorImplTest {

    private CategoryLinkedDataGeneratorImpl generator;
    private HttpServletRequest request;
    private SearchFacetDTOService facetService;
    private SearchService searchService;
    private Environment environment;
    private RecordingExtensionHandler extensionHandler;

    @Before
    public void setUp() {
        request = EasyMock.createMock(HttpServletRequest.class);
        facetService = EasyMock.createMock(SearchFacetDTOService.class);
        searchService = EasyMock.createMock(SearchService.class);
        environment = EasyMock.createMock(Environment.class);

        extensionHandler = new RecordingExtensionHandler();
        LinkedDataGeneratorExtensionManager extensionManager = new LinkedDataGeneratorExtensionManager();
        extensionManager.registerHandler(extensionHandler);

        generator = new CategoryLinkedDataGeneratorImpl();
        generator.facetService = facetService;
        generator.searchService = searchService;
        generator.environment = environment;
        generator.extensionManager = extensionManager;
    }

    @After
    public void tearDown() {
        EasyMock.verify(request, facetService, searchService, environment);
    }

    @Test
    public void testCanHandleWhenCurrentCategoryPresent() {
        EasyMock.expect(request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME))
                .andReturn(new Object());
        replayAll();

        assertTrue(generator.canHandle(request));
    }

    @Test
    public void testCanHandleWhenCurrentCategoryAbsent() {
        EasyMock.expect(request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME))
                .andReturn(null);
        replayAll();

        assertFalse(generator.canHandle(request));
    }

    @Test
    public void testGetProductsFromSearchResultAttribute() {
        List<Product> products = mockProducts("/a", "/b");
        SearchResult searchResult = new SearchResult();
        searchResult.setProducts(products);
        EasyMock.expect(request.getAttribute(CategoryLinkedDataGeneratorImpl.PRODUCT_SEARCH_RESULT_ATTRIBUTE_NAME))
                .andReturn(searchResult);
        replayAll();

        assertSame(products, generator.getProducts(request));
    }

    @Test
    public void testGetProductsFallsBackToProductsAttributeWhenSearchResultHasNoProducts() {
        SearchResult searchResult = new SearchResult();
        searchResult.setProducts(null);
        List<Product> products = mockProducts("/a");
        EasyMock.expect(request.getAttribute(CategoryLinkedDataGeneratorImpl.PRODUCT_SEARCH_RESULT_ATTRIBUTE_NAME))
                .andReturn(searchResult);
        EasyMock.expect(request.getAttribute(CategoryLinkedDataGeneratorImpl.PRODUCTS_ATTRIBUTE_NAME))
                .andReturn(products);
        replayAll();

        assertSame(products, generator.getProducts(request));
    }

    @Test
    public void testGetProductsFromProductsAttributeWhenNoSearchResult() {
        List<Product> products = mockProducts("/a");
        EasyMock.expect(request.getAttribute(CategoryLinkedDataGeneratorImpl.PRODUCT_SEARCH_RESULT_ATTRIBUTE_NAME))
                .andReturn(null);
        EasyMock.expect(request.getAttribute(CategoryLinkedDataGeneratorImpl.PRODUCTS_ATTRIBUTE_NAME))
                .andReturn(products);
        replayAll();

        assertSame(products, generator.getProducts(request));
    }

    @Test
    public void testGetProductsExecutesSearchWhenNoRequestAttributes() throws Exception {
        List<Product> products = mockProducts("/a", "/b", "/c");
        SearchCriteria criteria = new SearchCriteria();
        SearchResult searchResult = new SearchResult();
        searchResult.setProducts(products);
        expectNoRequestAttributes();
        EasyMock.expect(facetService.buildSearchCriteria(request)).andReturn(criteria);
        EasyMock.expect(searchService.findSearchResults(criteria)).andReturn(searchResult);
        replayAll();

        assertSame(products, generator.getProducts(request));
    }

    @Test
    public void testGetProductsReturnsEmptyListWhenSearchReturnsNull() throws Exception {
        SearchCriteria criteria = new SearchCriteria();
        expectNoRequestAttributes();
        EasyMock.expect(facetService.buildSearchCriteria(request)).andReturn(criteria);
        EasyMock.expect(searchService.findSearchResults(criteria)).andReturn(null);
        replayAll();

        assertTrue(generator.getProducts(request).isEmpty());
    }

    @Test
    public void testGetProductsReturnsEmptyListWhenSearchResultHasNoProducts() throws Exception {
        SearchCriteria criteria = new SearchCriteria();
        SearchResult searchResult = new SearchResult();
        searchResult.setProducts(null);
        expectNoRequestAttributes();
        EasyMock.expect(facetService.buildSearchCriteria(request)).andReturn(criteria);
        EasyMock.expect(searchService.findSearchResults(criteria)).andReturn(searchResult);
        replayAll();

        assertTrue(generator.getProducts(request).isEmpty());
    }

    @Test
    public void testGetProductsReturnsEmptyListWhenSearchThrows() throws Exception {
        expectNoRequestAttributes();
        EasyMock.expect(facetService.buildSearchCriteria(request)).andThrow(new RuntimeException("boom"));
        replayAll();

        assertTrue(generator.getProducts(request).isEmpty());
    }

    @Test
    public void testGetLinkedDataJsonBuildsItemList() throws Exception {
        List<Product> products = mockProducts("/product-1", "/product-2", "/product-3");
        EasyMock.expect(request.getAttribute(CategoryLinkedDataGeneratorImpl.PRODUCT_SEARCH_RESULT_ATTRIBUTE_NAME))
                .andReturn(null);
        EasyMock.expect(request.getAttribute(CategoryLinkedDataGeneratorImpl.PRODUCTS_ATTRIBUTE_NAME))
                .andReturn(products);
        EasyMock.expect(environment.getProperty("structured.data.context",
                AbstractLinkedDataGenerator.DEFAULT_STRUCTURED_CONTENT_CONTEXT))
                .andReturn(AbstractLinkedDataGenerator.DEFAULT_STRUCTURED_CONTENT_CONTEXT);
        replayAll();

        JSONArray schemaObjects = new JSONArray();
        generator.getLinkedDataJSON("/category", request, schemaObjects);

        assertEquals(1, schemaObjects.length());
        JSONObject categoryData = schemaObjects.getJSONObject(0);
        assertEquals(AbstractLinkedDataGenerator.DEFAULT_STRUCTURED_CONTENT_CONTEXT, categoryData.getString("@context"));
        assertEquals("ItemList", categoryData.getString("@type"));

        JSONArray itemList = categoryData.getJSONArray("itemListElement");
        assertEquals(3, itemList.length());
        for (int i = 0; i < itemList.length(); i++) {
            JSONObject item = itemList.getJSONObject(i);
            assertEquals("ListItem", item.getString("@type"));
            assertEquals(i + 1, item.getInt("position"));
            assertEquals("/product-" + (i + 1), item.getString("url"));
        }

        assertEquals(1, extensionHandler.categoryDataCalls);
        assertEquals(3, extensionHandler.categoryProductDataCalls);
        assertSame(categoryData, extensionHandler.lastCategoryData);
    }

    @Test
    public void testGetLinkedDataJsonWithNoProductsBuildsEmptyItemList() throws Exception {
        expectNoRequestAttributes();
        EasyMock.expect(facetService.buildSearchCriteria(request)).andReturn(new SearchCriteria());
        EasyMock.expect(searchService.findSearchResults(EasyMock.anyObject(SearchCriteria.class))).andReturn(null);
        EasyMock.expect(environment.getProperty("structured.data.context",
                AbstractLinkedDataGenerator.DEFAULT_STRUCTURED_CONTENT_CONTEXT))
                .andReturn("https://schema.example/");
        replayAll();

        JSONArray schemaObjects = new JSONArray();
        generator.getLinkedDataJSON("/category", request, schemaObjects);

        JSONObject categoryData = schemaObjects.getJSONObject(0);
        assertEquals("https://schema.example/", categoryData.getString("@context"));
        assertEquals(0, categoryData.getJSONArray("itemListElement").length());
        assertEquals(1, extensionHandler.categoryDataCalls);
        assertEquals(0, extensionHandler.categoryProductDataCalls);
    }

    private void expectNoRequestAttributes() {
        EasyMock.expect(request.getAttribute(CategoryLinkedDataGeneratorImpl.PRODUCT_SEARCH_RESULT_ATTRIBUTE_NAME))
                .andReturn(null);
        EasyMock.expect(request.getAttribute(CategoryLinkedDataGeneratorImpl.PRODUCTS_ATTRIBUTE_NAME))
                .andReturn(null);
    }

    private void replayAll() {
        EasyMock.replay(request, facetService, searchService, environment);
    }

    private List<Product> mockProducts(String... urls) {
        List<Product> products = new ArrayList<>();
        for (String url : urls) {
            Product product = EasyMock.createNiceMock(Product.class);
            EasyMock.expect(product.getUrl()).andReturn(url).anyTimes();
            EasyMock.replay(product);
            products.add(product);
        }
        return products;
    }

    private static class RecordingExtensionHandler extends AbstractLinkedDataGeneratorExtensionHandler {

        int categoryDataCalls;
        int categoryProductDataCalls;
        JSONObject lastCategoryData;

        @Override
        public ExtensionResultStatusType addCategoryData(final HttpServletRequest request, final JSONObject categoryData) throws JSONException {
            categoryDataCalls++;
            lastCategoryData = categoryData;
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        @Override
        public ExtensionResultStatusType addCategoryProductData(final HttpServletRequest request, final JSONObject categoryData) throws JSONException {
            categoryProductDataCalls++;
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }
    }

}
