package org.broadleafcommerce.core.spec.pricing.service.workflow

import org.broadleafcommerce.core.offer.domain.OfferCode
import org.broadleafcommerce.core.offer.domain.OfferCodeImpl
import org.broadleafcommerce.core.offer.service.OfferService
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.pricing.service.workflow.OfferActivity

class OfferActivitySpec extends BasePricingActivitySpec {
	
	OfferService mockOfferService
	OrderService mockOrderService
	
	def setup(){
		mockOfferService = Mock()
		mockOrderService = Mock()
	}
	
	def"Test a valid run with valid data"(){
		setup:"Prepare a List of OfferCode's"
		List<OfferCode> offerCodes = new ArrayList<OfferCode>()
		offerCodes.add(new OfferCodeImpl())
		
		activity = new OfferActivity().with{
			offerService = mockOfferService
			orderService = mockOrderService
			it
		}
		
		when:"I execute the OfferActivity"
		context = activity.execute(context)
		
		then:"orderService's addOfferCodes should have run and offerService's buildOfferListForOrder as well as applyAndSaveOffersToOrder should have run"
		1 * mockOfferService.buildOfferCodeListForCustomer(_) >> offerCodes
		1 * mockOfferService.applyAndSaveOffersToOrder(_, _) >> context.seedData
		1 * mockOrderService.addOfferCodes(_, _, _) >> context.seedData
		context.seedData != null
	}

}
