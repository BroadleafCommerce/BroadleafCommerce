/*
 * #%L
 * BroadleafCommerce Framework
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
/**
 * @author Austin Rooke (austinrooke)
 */
package org.broadleafcommerce.core.spec.pricing.service.workflow

import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl
import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.core.offer.domain.OrderAdjustment
import org.broadleafcommerce.core.offer.domain.OrderAdjustmentImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroup
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl
import org.broadleafcommerce.core.order.domain.OrderItem
import org.broadleafcommerce.core.order.domain.OrderItemImpl
import org.broadleafcommerce.core.pricing.service.workflow.FulfillmentItemPricingActivity

import spock.lang.IgnoreRest

class FulfillmentItemPricingActivitySpec extends BasePricingActivitySpec {

    /* This activity has a ton of helper methods, so these tests all have large setup sections to mimic the set up of the 
     * state that would have been done by a prior helper method. The main setup() method helps to knock out a bunch of 
     * code coverage when the execute method is run and can be used by a few of the other tests.
     * 
     * Test execute First
     *	one exit case, many many possible valid cases
     *	order will need the following:   1 FulfillmentGroup
     *	                                     1 FulfillmentGroupItem
     *                                          1 OrderItem
     *                                              Quantity - Long
     *	                                            Price - Money
     *                                              Taxable - boolean
     *                                              Quantity - Long
     *                                          TotalItemAmount - Money
     *                                          TotalPrice - Money
     *                                          TotalItemTaxableAmount - Money
     *                                          ProratedOrderAdjustmentAmount - set by distributeOrderSavingsToItems
     *                                  Currency - Money		
     *                                  OrderAdjustmentsValue
     *                                      Amount
     *                                  HasOrderAdjustments(may be handled by filling in OrderAdjustmentsValue)
     *											
     *      Money entries need: Amount
     *                          Currency
     *	
     *  Test Coverage Cases for 100% Coverage:
     *      populateItemTotalAmount
     *          1.  FulfillmentGroupItem.quantity != OrderItem.quantity //Handled by test: "Test populateItemTotalAmount
     *                                                                  //when multiple FulfillmentGroupItems share the 
     *                                                                  //same OrderItem"
     *          2.  FulfillmentGroupItem.quantity == OrderItem.quantity //Handled by test: "Test a valid run with valid 
     *                                                                  //data"
     *
     *      fixItemTotalRoundingIssues
     *          1.  orderItem.getTotalPrice() - (sum of FulfillmentGroupItem's prices) > 0  //Handled by test:"Test 
     *                                                                                      //fixItemTotalRoundingIssues"
     *                                                                                      
     *
     *      fixOrderSavingsRoundingIssues
     *          1.  order.getOrderAdjustmentsValue - (sum of FGI's ProratedOrderAdjustmentAmount's) > 0 //Handled by test:
     *                                                                                                  //"Test 
     *                                                                                                  //fixOrderSavings
     *                                                                                                  //RoundingIssues"
     *
     *      updateTaxableAmountsOnItems
     *          1.  OrderItem.taxable = true
     *              a.  FGI.proratedOrderAdjustmentAmount != null   //Handled by test: "Test a valid run with valid data"
     *              b.  FGI.proratedOrderAdjustmentAmount == null   //Handled by test: "Test updateTaxableAmountsOnItems
     *                                                              //when FulfillmentGroupItem does not provide a 
     *                                                              //proratedOrderAdjustmentAmount with a taxable 
     *                                                              //OrderItem"
     *          2.  OrderItem.taxable = false                       //Handled by test: "Test updateTaxableAmountsOnItems 
     *                                                              //when the OrderItem inside a FulfillmentGroupItem 
     *                                                              //is not taxable"
     *
     *      getUnitAmount
     *          1.  0 < Money.getCurrenct().getDefaultFractionDigits() < 1  //No idea how to get this case to happen
     *
     *Then sumTaxAmount
     *      Has one exit case, do normal test, will need single FulfillmentGroupItem with a TotalItemTaxableAmount
     *      //Handled by Test: "Test sumTaxAmount"
     *
     *Then applyTaxDifference
     *      Has one exit case, do normal test, will need a single FulfillmentGroupItem with a TotalItemTaxableAmount
     *      //Handled by Test: "Test applyTaxDifference"
     *
     *Then getOrderSavingsToDistribute
     *      This method has three exit cases based upon order.getOrderAdjustmentsValue()
     *      If its null, return order's currency
     *      if not, it compares the order's adjustmentvalues vs its subtotal
     *          if the subtotal is less than the av, then a log is added and returns order's currency
     *          if greater, return the adjustment value
     *      Three tests, mock the order, compare against currency and adjustment value
     *          Order will need:
     *                          OrderAdjustmentValue
     *                          SubTotal
     *                          Currency
     *      Code Coverage Cases:
     *              1.  order.orderAdjustmentsValue = null                                  //Impossible Case, will never
     *                                                                                      //execute under any state/setup
     *              2.  subtotal = null || order.subtotal < order.orderAdjustmentValue      //Handled by test: "Test 
     *                                                                                      //getOrderSavingsToDistribute 
     *                                                                                      //with no SubTotal provided"
     *              3.  order.orderAdjustmentValue > order.subtotal                         //Handled by test: "Test 
     *                                                                                      //getOrderSavingsToDistribute
     *                                                                                      //with a provided 
     *                                                                                      //OrderAdjustmentValue and 
     *                                                                                      //SubTotal"
     *						 
     */
    FulfillmentGroupItem mockFulfillmentGroupItem

    def setup() {

        //This setup can be used for most cases. Special code coverage cases will be set up on a per test basis.
        Money money = new Money(1.00)
        OrderItem orderItem = new OrderItemImpl()
        orderItem.id = 1
        orderItem.quantity = 1
        orderItem.salePrice = money
        orderItem.taxable = true
        orderItem.order = context.seedData
        mockFulfillmentGroupItem = new FulfillmentGroupItemImpl()
        mockFulfillmentGroupItem.quantity = 1
        mockFulfillmentGroupItem.totalItemAmount = money
        mockFulfillmentGroupItem.orderItem = orderItem
        FulfillmentGroup fulfillmentGroup = new FulfillmentGroupImpl()
        fulfillmentGroup.fulfillmentGroupItems << mockFulfillmentGroupItem
        context.seedData.fulfillmentGroups = [fulfillmentGroup] as List
        context.seedData.subTotal = money
        OrderAdjustment orderAdjustment = new OrderAdjustmentImpl()
        orderAdjustment.value = new Money(0.01)
        orderAdjustment.order = context.seedData
        context.seedData.orderAdjustments = [orderAdjustment] as List
    }

    def "Test a valid run with valid data"() {

        //Normal run of execute() with data from this.setup()
        activity = new FulfillmentItemPricingActivity()

        when: "I execute FulfillmentItemPricingActivity"
        context = activity.execute(context)

        then: "The FulfillmentGroupItem's ProratedOrderAdjustmentAmount should be 0.01 and its TotalItemTaxableAmount should be 0.99"
        mockFulfillmentGroupItem.getProratedOrderAdjustmentAmount().getAmount() == 0.01
        mockFulfillmentGroupItem.getTotalItemTaxableAmount().getAmount() == 0.99
    }

    def "Test populateItemTotalAmount when multiple FulfillmentGroupItems share the same OrderItem"() {

        //This test is for Code Coverage of the populateItemTotalAmount helper method
        setup: "Prepare two distinct FulfillmentGroupItems that share one OrderItem"
        FulfillmentGroup fulfillmentGroup = new FulfillmentGroupImpl()
        FulfillmentGroupItem fulfillmentGroupItem1 = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem fulfillmentGroupItem2 = new FulfillmentGroupItemImpl()
        OrderItem orderItem = new OrderItemImpl()
        orderItem.quantity = 2
        orderItem.salePrice = new Money('3.00')
        orderItem.order = context.seedData
        fulfillmentGroupItem1.orderItem = orderItem
        fulfillmentGroupItem2.orderItem = orderItem
        fulfillmentGroupItem1.quantity = 1
        fulfillmentGroupItem2.quantity = 1
        fulfillmentGroup.fulfillmentGroupItems << fulfillmentGroupItem1
        fulfillmentGroup.fulfillmentGroupItems << fulfillmentGroupItem2
        context.seedData.fulfillmentGroups = [fulfillmentGroup] as List

        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()
        Map<OrderItem, List<FulfillmentGroupItem>> partialOrderItemMap =
                        new HashMap<OrderItem, List<FulfillmentGroupItem>>()

        when: "I execute populateItemTotalAmount"
        fulfillmentItemPricingActivity.populateItemTotalAmount(context.seedData, partialOrderItemMap)

        then: "The FulfillmentGroupItems will have their totalItemAmounts updated"
        fulfillmentGroupItem1.totalItemAmount.getAmount() == (new Money(3.00)).getAmount()
        fulfillmentGroupItem2.totalItemAmount.getAmount() == (new Money(3.00)).getAmount()

    }
    @IgnoreRest
    def "Test fixItemTotalRoundingIssues"() {

        //This test is for Code Coverage of the fixItemTotalRoundingIssues helper method
        OrderItem orderItem = new OrderItemImpl()
        orderItem.quantity = 3
        orderItem.salePrice = new Money('3.41777')
        orderItem.order = context.seedData
        FulfillmentGroupItem fulfillmentGroupItem1 = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem fulfillmentGroupItem2 = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem fulfillmentGroupItem3 = new FulfillmentGroupItemImpl()
        fulfillmentGroupItem1.orderItem = orderItem
        fulfillmentGroupItem2.orderItem = orderItem
        fulfillmentGroupItem3.orderItem = orderItem
        fulfillmentGroupItem1.quantity = 1
        fulfillmentGroupItem2.quantity = 1
        fulfillmentGroupItem3.quantity = 1
        FulfillmentGroup fulfillmentGroup1 = new FulfillmentGroupImpl()
        FulfillmentGroup fulfillmentGroup2 = new FulfillmentGroupImpl()
        FulfillmentGroup fulfillmentGroup3 = new FulfillmentGroupImpl()
        fulfillmentGroup1.fulfillmentGroupItems << fulfillmentGroupItem1
        fulfillmentGroup2.fulfillmentGroupItems << fulfillmentGroupItem2
        fulfillmentGroup3.fulfillmentGroupItems << fulfillmentGroupItem3
        context.seedData.fulfillmentGroups = [
            fulfillmentGroup1,
            fulfillmentGroup2,
            fulfillmentGroup3] as List
        Map<OrderItem, List<FulfillmentGroupItem>> partialOrderItemMap =
                        new HashMap<OrderItem, List<FulfillmentGroupItem>>()
        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()
        fulfillmentItemPricingActivity.populateItemTotalAmount(context.seedData, partialOrderItemMap)

        when: "I execute fixItemTotalRoundingIssues"
        fulfillmentItemPricingActivity.fixItemTotalRoundingIssues(context.seedData, partialOrderItemMap)

        then: "The sum of the fulfillmentGroupItem's totalItemAmount should equal the orderItem's totalPrice"
        (fulfillmentGroupItem1.getTotalItemAmount().getAmount() +
            fulfillmentGroupItem2.getTotalItemAmount().getAmount() +
            fulfillmentGroupItem3.getTotalItemAmount().getAmount()) == orderItem.getTotalPrice().getAmount()
    }

    def "Test fixOrderSavingsRoundingIssues where the sum of ProratedOrderAdjustmentAmounts is less than the OrderAdjustmentValue"() {

        setup: "Prepare two FulfillmentGroupItems in seperate FulfillmentGroups as well as a prepared OrderAdjustment for the order"
        OrderItem testOrderItem = new OrderItemImpl()
        testOrderItem.quantity = 3
        testOrderItem.salePrice = new Money('5.00')
        testOrderItem.order = context.seedData
        FulfillmentGroupItem fulfillmentGroupItem1 = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem fulfillmentGroupItem2 = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem fulfillmentGroupItem3 = new FulfillmentGroupItemImpl()
        fulfillmentGroupItem1.orderItem = testOrderItem
        fulfillmentGroupItem2.orderItem = testOrderItem
        fulfillmentGroupItem3.orderItem = testOrderItem
        fulfillmentGroupItem1.quantity = 1
        fulfillmentGroupItem1.totalItemAmount = new Money('5.00')
        fulfillmentGroupItem2.quantity = 1
        fulfillmentGroupItem2.totalItemAmount = new Money('5.00')
        fulfillmentGroupItem3.quantity = 1
        fulfillmentGroupItem3.totalItemAmount = new Money('5.00')
        FulfillmentGroup fulfillmentGroup1 = new FulfillmentGroupImpl()
        FulfillmentGroup fulfillmentGroup2 = new FulfillmentGroupImpl()
        FulfillmentGroup fulfillmentGroup3 = new FulfillmentGroupImpl()
        fulfillmentGroup1.fulfillmentGroupItems << fulfillmentGroupItem1
        fulfillmentGroup2.fulfillmentGroupItems << fulfillmentGroupItem2
        fulfillmentGroup3.fulfillmentGroupItems << fulfillmentGroupItem3
        OrderAdjustment orderAdjustment = new OrderAdjustmentImpl()
        orderAdjustment.setValue(new Money(2.50))
        orderAdjustment.setOrder(context.seedData)
        context.seedData.orderAdjustments = [orderAdjustment]
        context.seedData.fulfillmentGroups = [
            fulfillmentGroup1,
            fulfillmentGroup2,
            fulfillmentGroup3
        ]
        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()
        Money totalAllItemsAmount = 
            fulfillmentItemPricingActivity.calculateTotalPriceForAllFulfillmentItems(context.seedData)
        Money totalOrderAdjustmentDistributed = 
            fulfillmentItemPricingActivity.distributeOrderSavingsToItems(context.seedData, totalAllItemsAmount.getAmount())
        //Due to rounding, the sum of the ProratedOrderAdjustmentAmount's in
        // the fulfillmentGroupItem'ss at this point is 2.49, one penny less than what is in the orderAdjustment

        when: "I execute fixOrderSavingsRoundingIssues"
        fulfillmentItemPricingActivity.fixOrderSavingsRoundingIssues(context.seedData, totalOrderAdjustmentDistributed)

        then: "The Sum of the FulfillmentGroupItem's proratedOrderAmountAdjustmentAmount should equal the orderAdjustmentValue"
        (fulfillmentGroupItem1.getProratedOrderAdjustmentAmount().getAmount() +
            fulfillmentGroupItem2.getProratedOrderAdjustmentAmount().getAmount() +
            fulfillmentGroupItem3.getProratedOrderAdjustmentAmount().getAmount()) == orderAdjustment.getValue().getAmount()
    }

    def "Test fixOrderSavingsRoundingIssues where the sum of ProratedOrderAdjustmentAmounts is greater than the OrderAdjustmentValue"() {

        setup: "Prepare two FulfillmentGroupItems in seperate FulfillmentGroups as well as a prepared OrderAdjustment for the order"
        OrderItem testOrderItem = new OrderItemImpl()
        testOrderItem.quantity = 3
        testOrderItem.salePrice = new Money('5.00')
        testOrderItem.order = context.seedData
        FulfillmentGroupItem fulfillmentGroupItem1 = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem fulfillmentGroupItem2 = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem fulfillmentGroupItem3 = new FulfillmentGroupItemImpl()
        fulfillmentGroupItem1.orderItem = testOrderItem
        fulfillmentGroupItem2.orderItem = testOrderItem
        fulfillmentGroupItem3.orderItem = testOrderItem
        fulfillmentGroupItem1.quantity = 1
        fulfillmentGroupItem1.totalItemAmount = new Money('5.00')
        fulfillmentGroupItem2.quantity = 1
        fulfillmentGroupItem2.totalItemAmount = new Money('5.00')
        fulfillmentGroupItem3.quantity = 1
        fulfillmentGroupItem3.totalItemAmount = new Money('5.00')
        FulfillmentGroup fulfillmentGroup1 = new FulfillmentGroupImpl()
        FulfillmentGroup fulfillmentGroup2 = new FulfillmentGroupImpl()
        FulfillmentGroup fulfillmentGroup3 = new FulfillmentGroupImpl()
        fulfillmentGroup1.fulfillmentGroupItems << fulfillmentGroupItem1
        fulfillmentGroup2.fulfillmentGroupItems << fulfillmentGroupItem2
        fulfillmentGroup3.fulfillmentGroupItems << fulfillmentGroupItem3
        OrderAdjustment orderAdjustment = new OrderAdjustmentImpl()
        orderAdjustment.setValue(new Money(2.00))
        orderAdjustment.setOrder(context.seedData)
        context.seedData.orderAdjustments = [orderAdjustment]
        context.seedData.fulfillmentGroups = [
            fulfillmentGroup1,
            fulfillmentGroup2,
            fulfillmentGroup3
        ]
        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()
        Money totalAllItemsAmount = 
            fulfillmentItemPricingActivity.calculateTotalPriceForAllFulfillmentItems(context.seedData)
        Money totalOrderAdjustmentDistributed =
            fulfillmentItemPricingActivity.distributeOrderSavingsToItems(context.seedData, totalAllItemsAmount.getAmount())
        //Due to rounding, the sum of the ProratedOrderAdjustmentAmount's in
        // the fulfillmentGroupItem'ss at this point is 2.01, one penny more than what is in the orderAdjustment

        when: "I execute fixOrderSavingsRoundingIssues"
        fulfillmentItemPricingActivity.fixOrderSavingsRoundingIssues(context.seedData, totalOrderAdjustmentDistributed)

        then: "The Sum of the FulfillmentGroupItem's proratedOrderAmountAdjustmentAmount should equal the orderAdjustmentValue"
        (fulfillmentGroupItem1.getProratedOrderAdjustmentAmount().getAmount() +
            fulfillmentGroupItem2.getProratedOrderAdjustmentAmount().getAmount() +
            fulfillmentGroupItem3.getProratedOrderAdjustmentAmount().getAmount()) == orderAdjustment.getValue().getAmount()
    }

    def "Repeating previous test using Japenese Yen JPY as the currency to further test possible rounding issues"() {

        setup: "Prepare two FulfillmentGroupItems in seperate FulfillmentGroups as well as a prepared OrderAdjustment for the order"
        OrderItem testOrderItem = new OrderItemImpl()
        testOrderItem.quantity = 3
        testOrderItem.salePrice = new Money('5', 'JPY')
        testOrderItem.order = context.seedData
        FulfillmentGroupItem fulfillmentGroupItem1 = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem fulfillmentGroupItem2 = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem fulfillmentGroupItem3 = new FulfillmentGroupItemImpl()
        fulfillmentGroupItem1.orderItem = testOrderItem
        fulfillmentGroupItem2.orderItem = testOrderItem
        fulfillmentGroupItem3.orderItem = testOrderItem
        fulfillmentGroupItem1.quantity = 1
        fulfillmentGroupItem1.totalItemAmount = new Money('5', 'JPY')
        fulfillmentGroupItem2.quantity = 1
        fulfillmentGroupItem2.totalItemAmount = new Money('5', 'JPY')
        fulfillmentGroupItem3.quantity = 1
        fulfillmentGroupItem3.totalItemAmount = new Money('5', 'JPY')
        FulfillmentGroup fulfillmentGroup1 = new FulfillmentGroupImpl()
        FulfillmentGroup fulfillmentGroup2 = new FulfillmentGroupImpl()
        FulfillmentGroup fulfillmentGroup3 = new FulfillmentGroupImpl()
        fulfillmentGroup1.fulfillmentGroupItems << fulfillmentGroupItem1
        fulfillmentGroup2.fulfillmentGroupItems << fulfillmentGroupItem2
        fulfillmentGroup3.fulfillmentGroupItems << fulfillmentGroupItem3
        OrderAdjustment orderAdjustment = new OrderAdjustmentImpl()
        orderAdjustment.setValue(new Money('2', 'JPY'))
        orderAdjustment.setOrder(context.seedData)
        context.seedData.orderAdjustments = [orderAdjustment]
        context.seedData.fulfillmentGroups = [
            fulfillmentGroup1,
            fulfillmentGroup2,
            fulfillmentGroup3
        ]
        context.seedData.currency = new BroadleafCurrencyImpl().with() {
            currencyCode = 'JPY'
            it
        }
        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()
        Money totalAllItemsAmount = 
            fulfillmentItemPricingActivity.calculateTotalPriceForAllFulfillmentItems(context.seedData)
        Money totalOrderAdjustmentDistributed = 
            fulfillmentItemPricingActivity.distributeOrderSavingsToItems(context.seedData, totalAllItemsAmount.getAmount())
        //Due to rounding, the sum of the ProratedOrderAdjustmentAmount's in
        // the fulfillmentGroupItem'ss at this point is 2.01, one penny more than what is in the orderAdjustment

        when: "I execute fixOrderSavingsRoundingIssues"
        fulfillmentItemPricingActivity.fixOrderSavingsRoundingIssues(context.seedData, totalOrderAdjustmentDistributed)

        then: "The Sum of the FulfillmentGroupItem's proratedOrderAmountAdjustmentAmount should equal the orderAdjustmentValue"
        (fulfillmentGroupItem1.getProratedOrderAdjustmentAmount().getAmount() +
            fulfillmentGroupItem2.getProratedOrderAdjustmentAmount().getAmount() +
            fulfillmentGroupItem3.getProratedOrderAdjustmentAmount().getAmount()) == orderAdjustment.getValue().getAmount()
    }

    def "Test updateTaxableAmountsOnItems when FulfillmentGroupItem does not provide a proratedOrderAdjustmentAmount with a taxable OrderItem"() {

        //This test is for Code Coverage of the updateTaxableAmountsOnItems helper method
        setup: "Prepare a taxable OrderItem inside a FulfillmentGroupItem that does not have a proratedOrderAdjustmentAmount"
        OrderItem orderItem = new OrderItemImpl()
        orderItem.quantity = 1
        orderItem.salePrice = new Money(3.00)
        orderItem.taxable = true
        orderItem.order = context.seedData
        FulfillmentGroupItem fulfillmentGroupItem = new FulfillmentGroupItemImpl()
        fulfillmentGroupItem.orderItem = orderItem
        fulfillmentGroupItem.quantity = 1
        fulfillmentGroupItem.totalItemAmount = new Money(3.00)
        FulfillmentGroup fulfillmentGroup = new FulfillmentGroupImpl()
        fulfillmentGroup.addFulfillmentGroupItem(fulfillmentGroupItem)
        context.seedData.fulfillmentGroups = [fulfillmentGroup] as List
        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()

        when: "I execute updateTaxableAmountsOnItems"
        fulfillmentItemPricingActivity.updateTaxableAmountsOnItems(context.seedData)

        then: "The FulfillmentGroupItem's TotalItemTaxableAmount should be equal to its TotalItemAmount"
        fulfillmentGroupItem.totalItemTaxableAmount.getAmount() == fulfillmentGroupItem.totalItemAmount.getAmount()

    }

    def "Test updateTaxableAmountsOnItems when the OrderItem inside a FulfillmentGroupItem is not taxable"() {

        //This test is for Code Coverage of the updateTaxableAmountsOnItems helper method
        setup: "Prepare a non-taxable OrderItem inside a FulfillmentGroupItem"
        OrderItem orderItem = new OrderItemImpl()
        orderItem.taxable = false
        orderItem.order = context.seedData
        FulfillmentGroupItem fulfillmentGroupItem = new FulfillmentGroupItemImpl()
        fulfillmentGroupItem.orderItem = orderItem
        FulfillmentGroup fulfillmentGroup = new FulfillmentGroupImpl()
        fulfillmentGroup.addFulfillmentGroupItem(fulfillmentGroupItem)
        context.seedData.fulfillmentGroups = [fulfillmentGroup] as List
        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()

        when: "I execute updateTaxableAmountsOnItems"
        fulfillmentItemPricingActivity.updateTaxableAmountsOnItems(context.seedData)

        then: "The FulfillmentGroupItem's TotalItemTaxableAmount should be zero"
        fulfillmentGroupItem.totalItemTaxableAmount.getAmount() == 0
    }

    def "Test sumTaxAmount"() {

        //This helper method isn't called by any other method in this activity, so this test just tests it
        setup: "Prepare two FulfillmentGroupItems with varying TaxableAmounts"
        OrderItem orderItem = new OrderItemImpl()
        orderItem.order = context.seedData
        FulfillmentGroupItem fulfillmentGroupItem1 = new FulfillmentGroupItemImpl()
        FulfillmentGroupItem fulfillmentGroupItem2 = new FulfillmentGroupItemImpl()
        fulfillmentGroupItem1.totalItemTaxableAmount = new Money(0.50)
        fulfillmentGroupItem2.totalItemTaxableAmount = new Money(0.25)
        fulfillmentGroupItem1.orderItem = orderItem
        fulfillmentGroupItem2.orderItem = orderItem
        FulfillmentGroup fulfillmentGroup = new FulfillmentGroupImpl()
        fulfillmentGroup.fulfillmentGroupItems << fulfillmentGroupItem1
        fulfillmentGroup.fulfillmentGroupItems << fulfillmentGroupItem2
        context.seedData.fulfillmentGroups = [fulfillmentGroup] as List
        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()

        when: "I execute sumTaxAmount"
        Money money = 
            fulfillmentItemPricingActivity.sumTaxAmount(context.seedData.fulfillmentGroups.get(0).fulfillmentGroupItems, 
                context.seedData)

        then: "The Money object returend should have an amount of 0.75"
        money.getAmount() == 0.75
    }

    def "Test applyTaxDifference"() {

        //This helper method isn't called by any other method in this activity, so this test just tests it
        OrderItem orderItem = new OrderItemImpl()
        orderItem.order = context.seedData
        FulfillmentGroupItem fulfillmentGroupItem = new FulfillmentGroupItemImpl()
        fulfillmentGroupItem.totalItemTaxableAmount = new Money(1.00)
        fulfillmentGroupItem.quantity = 2
        fulfillmentGroupItem.orderItem = orderItem
        Money unitAmount = new Money(0.01)
        FulfillmentGroup fulfillmentGroup = new FulfillmentGroupImpl()
        fulfillmentGroup.fulfillmentGroupItems << fulfillmentGroupItem
        context.seedData.fulfillmentGroups = [fulfillmentGroup] as List
        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()

        when: "I execute applyTaxDifference"
        Long numTimesToApply = fulfillmentItemPricingActivity.applyTaxDifference(fulfillmentGroupItem, 1, unitAmount)

        then: "The Long variable returned should hold a value of 1 and the FulfillmentGroupItem should have a TotalItemTaxableAmount of 1.01"
        numTimesToApply == 1
        fulfillmentGroupItem.getTotalItemTaxableAmount().getAmount() == 1.01
    }

    def "Test getOrderSavingsToDistribute with no SubTotal provided"() {

        //This helper method isn't called by any other method in this activity, so this test just tests it
        context.seedData.subTotal = null
        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()

        when: "I execute getOrderSavingsToDistribute"
        Money money = fulfillmentItemPricingActivity.getOrderSavingsToDistribute(context.seedData)

        then: "The Money object returned should have a default amount with null currency"
        money.getAmount() == 0
        money.getCurrency() == (new Money()).getCurrency()
    }

    def "Test getOrderSavingsToDistribute with a provided OrderAdjustmentValue and SubTotal"() {

        //This helper method isn't called by any other method in this activity, so this test just tests it
        FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity()

        when: "I execute getOrderSavingsToDistribute"
        Money money = fulfillmentItemPricingActivity.getOrderSavingsToDistribute(context.seedData)

        then: "The Money object returned should have an amount of 0.01"
        money.getAmount() == 0.01
    }
}
