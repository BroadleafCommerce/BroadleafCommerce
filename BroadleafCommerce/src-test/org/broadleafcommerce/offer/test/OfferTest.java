package org.broadleafcommerce.offer.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.order.domain.OrderItemImpl;
import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.type.OfferType;
import org.broadleafcommerce.util.money.Money;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.testng.annotations.Test;

public class OfferTest extends BaseTest {

    @Test
    public void testBasicMVELExpressions() {
        //----------------------------------------------------------------------------------------------------
        // Mock up some order data
        OrderImpl order = new OrderImpl();
        CategoryImpl category = new CategoryImpl();
        category.setName("elfa");
        OrderItemImpl orderItem = new OrderItemImpl();
        ProductImpl product = new ProductImpl();
        ArrayList<Category> categories = new ArrayList<Category>();
        categories.add(category);
        product.setAllParentCategories(categories);
        orderItem.setProduct(product);
        order.addOrderItem(orderItem);
        order.setSubTotal(new Money(110D));

        //Set up MVEL Context
        ParserContext context = new ParserContext();

        //Import OfferType into the MVEL context since it may be used
        context.addImport("OfferType", OfferType.class);
        //Compile the MVEL Expression
        Serializable domainExp1 = MVEL.compileExpression("result = false; for (cat : item.product.allParentCategories) {if (cat.name == 'elfa') {result = true;}}; return result and order.subTotal.amount >= 50", context);

        //Add variables to a HashMap that should be passed in to execute the expression
        HashMap<String, Object> domainVars = new HashMap<String, Object>();
        domainVars.put("order", order);
        domainVars.put("item", orderItem);

        //Execute the expression
        Boolean expressionOutcome1 = (Boolean)MVEL.executeExpression(domainExp1, domainVars);
        assert expressionOutcome1 != null && expressionOutcome1;

        //Do the same thing using a different expression.
        Serializable domainExp2 = MVEL.compileExpression("($ in item.product.allParentCategories if $.name == 'elfa') != empty and order.subTotal.amount >= 50", context);
        Boolean expressionOutcome2 = (Boolean)MVEL.executeExpression(domainExp2, domainVars);
        assert expressionOutcome2 != null && expressionOutcome2;
    }

    @Test
    public void testTenPercentOffOrder() {

    }

    @Test
    public void testBogo() {

    }

    @Test
    public void testTenPercentOffItem() {

    }

    @Test
    public void testStackableOffer() {

    }
}
