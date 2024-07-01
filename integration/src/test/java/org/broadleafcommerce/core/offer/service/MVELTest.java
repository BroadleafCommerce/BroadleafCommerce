/*-
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.annotation.Resource;

public class MVELTest extends TestNGSiteIntegrationSetup {

    @Resource
    protected CatalogService catalogService;
    
    private StringBuffer functions = new StringBuffer();

    public MVELTest() {
        InputStream is = this.getClass().getResourceAsStream("/org/broadleafcommerce/core/offer/service/mvelFunctions.mvel");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                functions.append(line);
            }
            functions.append(" ");
        } catch(Exception e){
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e){
                    logger.error("An error has occurred ",e);
                }
            }
        }
    }

    @Test
    @Transactional
    public void testOfferAppliesToItemsInCategoryAndOrderValueGreaterThanFifty() {
        //----------------------------------------------------------------------------------------------------
        // Mock up some order data
        OrderImpl order = new OrderImpl();
        CategoryImpl category = new CategoryImpl();
        category.setName("t-shirt");
        Product product = createProduct();

        DiscreteOrderItemImpl orderItem = new DiscreteOrderItemImpl();
        ArrayList<CategoryProductXref> categories = new ArrayList<>();
        CategoryProductXref categoryXref = new CategoryProductXrefImpl();
        categoryXref.setProduct(product);
        categoryXref.setCategory(category);
        categories.add(categoryXref);
        product.setAllParentCategoryXrefs(categories);
        orderItem.setProduct(product);

        order.getOrderItems().add(orderItem);
        order.setSubTotal(new Money(110D));

        //Set up MVEL Context
        ParserContext context = new ParserContext();

        //Import OfferType into the MVEL context since it may be used
        context.addImport("OfferType", OfferType.class);
        //Compile the MVEL Expression
        Serializable domainExp1 = MVEL.compileExpression("result = false; for (cat : currentItem.product.allParentCategories) {if (cat.name == 't-shirt') {result = true;}}; return result and order.subTotal.amount >= 50", context);

        //Add variables to a HashMap that should be passed in to execute the expression
        HashMap<String, Object> domainVars = new HashMap<>();
        domainVars.put("order", order);
        domainVars.put("currentItem", orderItem);

        //Execute the expression
        Boolean expressionOutcome1 = (Boolean)MVEL.executeExpression(domainExp1, domainVars);
        assert expressionOutcome1 != null && expressionOutcome1;

        //Do the same thing using a different expression.
        Serializable domainExp2 = MVEL.compileExpression("($ in currentItem.product.allParentCategories if $.name == 't-shirt') != empty and order.subTotal.amount >= 50", context);
        Boolean expressionOutcome2 = (Boolean)MVEL.executeExpression(domainExp2, domainVars);
        assert expressionOutcome2 != null && expressionOutcome2;
    }

    private Product createProduct() {
        Category category = new CategoryImpl();
        category.setName("t-shirt");

        category = catalogService.saveCategory(category);

        Product product = new ProductImpl();
        Sku sku = new SkuImpl();
        sku = catalogService.saveSku(sku);
        product.setDefaultSku(sku);
        product.setName("Lavender Soap");

        Calendar activeStartCal = Calendar.getInstance();
        activeStartCal.add(Calendar.DAY_OF_YEAR, -2);
        product.setActiveStartDate(activeStartCal.getTime());

        product.setCategory(category);
        product.getAllParentCategoryXrefs().clear();
        product = catalogService.saveProduct(product);

        CategoryProductXref categoryXref = new CategoryProductXrefImpl();
        categoryXref.setProduct(product);
        categoryXref.setCategory(category);
        product.getAllParentCategoryXrefs().add(categoryXref);

        product = catalogService.saveProduct(product);
        return product;
    }


    @Test
    public void testBasicMVELFunctions() {
        //First, set up out functions
        HashMap<String, Object> functionMap = new HashMap<>();
        StringBuffer functions = new StringBuffer("def any(x, y) { return x or y } def all(x, y) { return x and y } ");
        MVEL.eval(functions.toString(), functionMap); //This stores that functions in the map we pass in.

        HashMap<String, Object> vars = new HashMap<>(functionMap); //Now, we need to pass the functions in to our variable map
        vars.put("fg", "Hello");

        StringBuffer expression = new StringBuffer();
        expression.append("return all(fg == 'Hello', true)");
        Boolean result = (Boolean)MVEL.eval(expression.toString(), vars);
        assert result != null && result;

        expression = new StringBuffer();
        expression.append("return any(fg == 'Goodbye', false)");
        Boolean result2 = (Boolean)MVEL.eval(expression.toString(), vars);
        assert result2 != null && ! result2;
    }

    @Test
    public void testOfferAppliesToSpecificItems() {

        DiscreteOrderItemImpl orderItem = new DiscreteOrderItemImpl();
        Sku sku = new SkuImpl();
        sku.setRetailPrice(new Money("1"));
        sku.setId(1234L);
        orderItem.setSku(sku);
        OfferImpl offer = new OfferImpl();
        offer.setType(OfferType.ORDER_ITEM);

        //Set up MVEL Context
        ParserContext context = new ParserContext();

        //Import OfferType into the MVEL context since it may be used
        context.addImport("OfferType", OfferType.class);
        context.addImport("FulfillmentType", FulfillmentType.class);

        //Compile the MVEL Expression
        Serializable domainExp1 = MVEL.compileExpression("offer.type == OfferType.ORDER_ITEM and (currentItem.sku.id in [1234, 2345, 5678])", context);

        //Add variables to a HashMap that should be passed in to execute the expression
        HashMap<String, Object> domainVars = new HashMap<>();
        domainVars.put("currentItem", orderItem);
        domainVars.put("offer", offer);

        //Execute the expression
        Boolean expressionOutcome1 = (Boolean)MVEL.executeExpression(domainExp1, domainVars);
        assert expressionOutcome1 != null && expressionOutcome1;

    }

    //@Test
    //TODO fix this test
    public void testOfferAppliesToHatsWhenOneLawnmowerIsPurchased() {
        OrderImpl order = new OrderImpl();
        ArrayList<OrderItem> items = new ArrayList<>();
        order.setOrderItems(items);
        DiscreteOrderItemImpl item = new DiscreteOrderItemImpl();
        Money amount = new Money(10D);
        items.add(item);
        item.setSalePrice(amount);
        ProductImpl product = new ProductImpl();
        CategoryImpl category = new CategoryImpl();
        category.setName("hat");
        product.setDefaultCategory(category);
        item.setProduct(product);
        item.setQuantity(3);

        DiscreteOrderItemImpl item2 = new DiscreteOrderItemImpl();
        Money amount2 = new Money(250D);
        items.add(item2);
        item2.setSalePrice(amount2);
        ProductImpl product2 = new ProductImpl();
        CategoryImpl category2 = new CategoryImpl();
        category2.setName("lawnmower");
        product2.setDefaultCategory(category2);
        item2.setProduct(product2);
        item2.setQuantity(1);

        HashMap<String, Object> vars = new HashMap<>();
        vars.put("currentItem", item);
        vars.put("order", order);
        vars.put("doMark", false);

        //This test makes use of the static MVEL function "orderContains(quantity)".
        StringBuffer expression = new StringBuffer(functions);
        expression.append("def evalItemForOrderContains(item) {")
        .append("             return item.product.defaultCategory.name == 'lawnmower'")
        .append("          } ")
        .append("          return (orderContainsPlusMark(1) and currentItem.product.defaultCategory.name == 'hat');");

        Boolean result = (Boolean)MVEL.eval(expression.toString(), vars);
        assert result != null && result;
    }

    //@Test
    //No longer a valid test
//    public void testMarkLawnmowerWhenOfferAppliesToHats() {
//        OrderImpl order = new OrderImpl();
//        ArrayList<OrderItem> items = new ArrayList<OrderItem>();
//        order.setOrderItems(items);
//        DiscreteOrderItemImpl item = new DiscreteOrderItemImpl();
//        Money amount = new Money(10D);
//        items.add(item);
//        item.setSalePrice(amount);
//        ProductImpl product = new ProductImpl();
//        CategoryImpl category = new CategoryImpl();
//        category.setName("hat");
//        product.setDefaultCategory(category);
//        item.setProduct(product);
//        item.setQuantity(3);
//
//        DiscreteOrderItemImpl item2 = new DiscreteOrderItemImpl();
//        Money amount2 = new Money(250D);
//        items.add(item2);
//        item2.setSalePrice(amount2);
//        ProductImpl product2 = new ProductImpl();
//        CategoryImpl category2 = new CategoryImpl();
//        category2.setName("lawnmower");
//        product2.setDefaultCategory(category2);
//        item2.setProduct(product2);
//        item2.setQuantity(1);
//
//        HashMap<String, Object> vars = new HashMap<String, Object>();
//        vars.put("currentItem", item);
//        vars.put("order", order);
//        vars.put("doMark", true);
//
//        //This test makes use of the static MVEL function "orderContains(quantity)".
//        StringBuffer expression = new StringBuffer(functions);
//        expression.append("def evalItemForOrderContains(item) {")
//        .append("             return item.product.defaultCategory.name == 'lawnmower'")
//        .append("          } ")
//        .append("          return (orderContainsPlusMark(1) and currentItem.product.defaultCategory.name == 'hat');");
//
//        Boolean result = (Boolean)MVEL.eval(expression.toString(), vars);
//        assert result != null && result;
//        assert item2.getMarkedForOffer() == 1;
//        assert item.getMarkedForOffer() == 0;
//    }

    @Test
    public void testOfferAppliesToFulfillmentGroup() {
        OrderImpl order = new OrderImpl();
        order.setSubTotal(new Money(110D));
        FulfillmentGroupImpl group = new FulfillmentGroupImpl();
        group.setPrimary(true);

        OfferImpl offer = new OfferImpl();
        offer.setType(OfferType.FULFILLMENT_GROUP);
        order.getFulfillmentGroups().add(group);

        //Set up MVEL Context
        ParserContext context = new ParserContext();
        //Import OfferType into the MVEL context since it may be used
        context.addImport("OfferType", OfferType.class);
        context.addImport("FulfillmentType", FulfillmentType.class);

        //Compile the MVEL Expression
        //This could test SHIPPING, or PICK_UP_AT_STORE, etc.
        //Could also apply to order instead of FULFILLMENT_GROUP
        Serializable domainExp1 = MVEL.compileExpression("offer.type.equals(OfferType.FULFILLMENT_GROUP) and (($ in order.fulfillmentGroups if $.type.equals(FulfillmentType.PHYSICAL)) != empty)", context);

        //Add variables to a HashMap that should be passed in to execute the expression
        HashMap<String, Object> domainVars = new HashMap<>();
        domainVars.put("order", order);
        domainVars.put("offer", offer);

        //Execute the expression
        Boolean expressionOutcome1 = (Boolean)MVEL.executeExpression(domainExp1, domainVars);
        assert expressionOutcome1 != null && expressionOutcome1;
    }
}
