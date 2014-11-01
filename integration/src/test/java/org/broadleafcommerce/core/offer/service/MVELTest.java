/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.test.BaseTest;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MVELTest extends BaseTest {

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
                    logger.error(e);
                }
            }
        }
    }

    @Test
    public void testOfferAppliesToItemsInCategoryAndOrderValueGreaterThanFifty() {
        //----------------------------------------------------------------------------------------------------
        // Mock up some order data
        OrderImpl order = new OrderImpl();
        CategoryImpl category = new CategoryImpl();
        category.setName("t-shirt");
        DiscreteOrderItemImpl orderItem = new DiscreteOrderItemImpl();
        ProductImpl product = new ProductImpl();
        ArrayList<CategoryProductXref> categories = new ArrayList<CategoryProductXref>();
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
        HashMap<String, Object> domainVars = new HashMap<String, Object>();
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

    @Test
    public void testBasicMVELFunctions() {
        //First, set up out functions
        HashMap<String, Object> functionMap = new HashMap<String, Object>();
        StringBuffer functions = new StringBuffer("def any(x, y) { return x or y } def all(x, y) { return x and y } ");
        MVEL.eval(functions.toString(), functionMap); //This stores that functions in the map we pass in.

        HashMap<String, Object> vars = new HashMap<String, Object>(functionMap); //Now, we need to pass the functions in to our variable map
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
        HashMap<String, Object> domainVars = new HashMap<String, Object>();
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
        ArrayList<OrderItem> items = new ArrayList<OrderItem>();
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

        HashMap<String, Object> vars = new HashMap<String, Object>();
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
        HashMap<String, Object> domainVars = new HashMap<String, Object>();
        domainVars.put("order", order);
        domainVars.put("offer", offer);

        //Execute the expression
        Boolean expressionOutcome1 = (Boolean)MVEL.executeExpression(domainExp1, domainVars);
        assert expressionOutcome1 != null && expressionOutcome1;
    }
}
