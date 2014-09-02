package org.broadleafcommerce.core.spec.pricing.service.workflow

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.core.catalog.domain.Sku
import org.broadleafcommerce.core.catalog.domain.SkuFee
import org.broadleafcommerce.core.catalog.domain.SkuFeeImpl
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.catalog.service.type.SkuFeeType
import org.broadleafcommerce.core.order.domain.BundleOrderItem
import org.broadleafcommerce.core.order.domain.BundleOrderItemImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroup
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFeeImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl
import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.service.FulfillmentGroupService
import org.broadleafcommerce.core.pricing.service.workflow.ConsolidateFulfillmentFeesActivity

class ConsolidateFulfillmentFeesActivitySpec extends BasePricingActivitySpec{
	
	FulfillmentGroupService mockFulfillmentGroupService
	Order order
	def setup(){
		//Setup a valid FulfillmentGroup with a FulfillmentItem inside and place it inside the context.seedData order object
		FulfillmentGroup fulfillmentGroup = new FulfillmentGroupImpl()
		FulfillmentGroupItem fulfillmentGroupItem = new FulfillmentGroupItemImpl()
		SkuFee skuFee = new SkuFeeImpl()
		skuFee.feeType = SkuFeeType.FULFILLMENT
		skuFee.name = "Test"
		skuFee.taxable = true
		skuFee.amount = new Money(1.00)
		BundleOrderItem bundleOrderItem = new BundleOrderItemImpl()
		Sku sku = new SkuImpl()
		sku.id = 1
		sku.retailPrice = new Money(1.00)
		sku.fees = new ArrayList()
		sku.fees.add(skuFee)
		bundleOrderItem.sku = sku
		fulfillmentGroupItem.orderItem = bundleOrderItem
		List<FulfillmentGroupItem> fulfillmentGroupItems = new ArrayList()
		fulfillmentGroupItems.add(fulfillmentGroupItem)
		fulfillmentGroup.fulfillmentGroupItems = fulfillmentGroupItems
		context.seedData.fulfillmentGroups = new ArrayList<FulfillmentGroup>()
		context.seedData.fulfillmentGroups.add(fulfillmentGroup)
		order = context.seedData
	}
	
	def"Test a valid run with valid data"(){
		mockFulfillmentGroupService = Mock()
		
		activity = new ConsolidateFulfillmentFeesActivity().with(){
			fulfillmentGroupService = mockFulfillmentGroupService
			it
		}
		
		when:"I execute ConsolidateFulfillmentfeesActivity"
		context = activity.execute(context)
		
		then:"FulfillmentGroupService's createFulfillmentGroupFee and save methods should run once"
		1 * mockFulfillmentGroupService.createFulfillmentGroupFee() >> {new FulfillmentGroupFeeImpl() }
		1 * mockFulfillmentGroupService.save(_)
		order == context.seedData
	}

}
