/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

		public static const SERVICE_ID:String = "blOfferService";

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