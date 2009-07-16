package org.broadleafcommerce.admin.offers.model
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.offers.model.conditions.AnyAllCondition;
	import org.broadleafcommerce.admin.offers.model.conditions.FulfillmentGroupAttributeCondition;
	import org.broadleafcommerce.admin.offers.model.conditions.OrderAttributeCondition;
	import org.broadleafcommerce.admin.offers.model.conditions.OrderContainsCondition;
	import org.broadleafcommerce.admin.offers.vo.Offer;
	
	[Bindable]
	public class OfferModel
	{
		public var currentOffer:Offer = new Offer();

		public var offersList:ArrayCollection = new ArrayCollection();
		
		public var offersListFiltered:ArrayCollection = new ArrayCollection();
		
		public const offerTypes:ArrayCollection = new ArrayCollection(["ORDER",
																  									   "ORDER_ITEM",
																  									   "FULFILLMENT_GROUP"]);
		
		public const discountTypes:ArrayCollection = new ArrayCollection(["PERCENT_OFF",
																  									   "AMOUNT_OFF",
																  									   "FIX_PRICE"]);

		public const deliveryTypes:ArrayCollection = new ArrayCollection(["AUTOMATIC",
																  									   "MANUAL",
																  									   "CODE"]);																  									   

		public var whenOperators:ArrayCollection = new ArrayCollection([new AnyAllCondition(),
																										  new OrderAttributeCondition(),
																										  new FulfillmentGroupAttributeCondition(),
																										  new OrderContainsCondition() ]);


		public var whichOperators:ArrayCollection = new ArrayCollection([new AnyAllCondition(),
																										  new OrderAttributeCondition(),
																										  new FulfillmentGroupAttributeCondition(),
																										  new OrderContainsCondition() ]);
		

	}
}