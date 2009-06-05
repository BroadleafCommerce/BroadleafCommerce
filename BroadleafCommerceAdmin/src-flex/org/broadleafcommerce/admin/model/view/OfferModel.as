package org.broadleafcommerce.admin.model.view
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.model.data.Offer;
	import org.broadleafcommerce.admin.model.data.conditions.AnyAllCondition;
	import org.broadleafcommerce.admin.model.data.conditions.FulfillmentGroupAttributeCondition;
	import org.broadleafcommerce.admin.model.data.conditions.OrderAttributeCondition;
	import org.broadleafcommerce.admin.model.data.conditions.OrderContainsCondition;
	
	[Bindable]
	public class OfferModel
	{
		public var currentOffer:Offer = new Offer();

		public var offersList:ArrayCollection = new ArrayCollection();
		
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