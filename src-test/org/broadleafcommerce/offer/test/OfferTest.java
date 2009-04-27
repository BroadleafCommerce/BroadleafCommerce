package org.broadleafcommerce.offer.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.broadleafcommerce.offer.domain.OfferImpl;
import org.broadleafcommerce.offer.service.type.OfferType;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.order.domain.OrderItemImpl;
import org.broadleafcommerce.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.util.money.Money;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.testng.annotations.Test;

public class OfferTest extends BaseTest {

    @Test
    public void testOfferAppliesToItemsInCategoryAndOrderValueGreaterThanFifty() {
        //----------------------------------------------------------------------------------------------------
        // Mock up some order data
        OrderImpl order = new OrderImpl();
        CategoryImpl category = new CategoryImpl();
        category.setName("t-shirt");
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
        Serializable domainExp1 = MVEL.compileExpression("result = false; for (cat : item.product.allParentCategories) {if (cat.name == 't-shirt') {result = true;}}; return result and order.subTotal.amount >= 50", context);

        //Add variables to a HashMap that should be passed in to execute the expression
        HashMap<String, Object> domainVars = new HashMap<String, Object>();
        domainVars.put("order", order);
        domainVars.put("item", orderItem);

        //Execute the expression
        Boolean expressionOutcome1 = (Boolean)MVEL.executeExpression(domainExp1, domainVars);
        assert expressionOutcome1 != null && expressionOutcome1;

        //Do the same thing using a different expression.
        Serializable domainExp2 = MVEL.compileExpression("($ in item.product.allParentCategories if $.name == 't-shirt') != empty and order.subTotal.amount >= 50", context);
        Boolean expressionOutcome2 = (Boolean)MVEL.executeExpression(domainExp2, domainVars);
        assert expressionOutcome2 != null && expressionOutcome2;
    }

    @Test
    public void testBasicMVELFunctions() {
        //First, set up out functions
        HashMap<String, Object> functionMap = new HashMap<String, Object>();
        StringBuffer functions = new StringBuffer("def any(x, y) { System.out.println(fg); return x or y } def all(x, y) { return x and y } ");
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

        OrderItemImpl orderItem = new OrderItemImpl();
        orderItem.setSku(new SkuImpl());
        orderItem.getSku().setId(1234L);
        OfferImpl offer = new OfferImpl();
        offer.setType(OfferType.ORDER_ITEM);

        //Set up MVEL Context
        ParserContext context = new ParserContext();

        //Import OfferType into the MVEL context since it may be used
        context.addImport("OfferType", OfferType.class);
        context.addImport("FulfillmentGroupType", FulfillmentGroupType.class);

        //Compile the MVEL Expression
        Serializable domainExp1 = MVEL.compileExpression("offer.type == OfferType.ORDER_ITEM and (item.sku.id in [1234, 2345, 5678])", context);

        //Add variables to a HashMap that should be passed in to execute the expression
        HashMap<String, Object> domainVars = new HashMap<String, Object>();
        domainVars.put("item", orderItem);
        domainVars.put("offer", offer);

        //Execute the expression
        Boolean expressionOutcome1 = (Boolean)MVEL.executeExpression(domainExp1, domainVars);
        assert expressionOutcome1 != null && expressionOutcome1;

    }

    @Test
    public void testBogo() {

    }

    @Test
    public void testOfferAppliesToFulfillmentGroup() {
        OrderImpl order = new OrderImpl();
        order.setSubTotal(new Money(110D));
        FulfillmentGroupImpl group = new FulfillmentGroupImpl();
        group.setType(FulfillmentGroupType.DEFAULT);

        OfferImpl offer = new OfferImpl();
        offer.setType(OfferType.FULFILLMENT_GROUP);
        order.addFulfillmentGroup(group);

        //Set up MVEL Context
        ParserContext context = new ParserContext();
        //Import OfferType into the MVEL context since it may be used
        context.addImport("OfferType", OfferType.class);
        context.addImport("FulfillmentGroupType", FulfillmentGroupType.class);

        //Compile the MVEL Expression
        //This could test SHIPPING, or PICK_UP_AT_STORE, etc.
        //Could also apply to order instead of FULFILLMENT_GROUP
        Serializable domainExp1 = MVEL.compileExpression("offer.type == OfferType.FULFILLMENT_GROUP and (($ in order.fulfillmentGroups if $.type == FulfillmentGroupType.PICK_UP_AT_STORE) != empty)", context);

        //Add variables to a HashMap that should be passed in to execute the expression
        HashMap<String, Object> domainVars = new HashMap<String, Object>();
        domainVars.put("order", order);
        domainVars.put("offer", offer);

        //Execute the expression
        Boolean expressionOutcome1 = (Boolean)MVEL.executeExpression(domainExp1, domainVars);
        assert expressionOutcome1 != null && expressionOutcome1;
    }
}
