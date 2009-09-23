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
package org.broadleafcommerce.admin.offers.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.offers.business.OfferServiceDelegate;
	import org.broadleafcommerce.admin.offers.control.events.AddUpdateOfferEvent;
	import org.broadleafcommerce.admin.offers.control.events.FindAllOffersEvent;
	import org.broadleafcommerce.admin.offers.model.OfferModelLocator;
	import org.broadleafcommerce.admin.offers.vo.Offer;

	public class AddUpdateOfferCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: AddUpdateOfferCommand.execute()");
			var auoe:AddUpdateOfferEvent = AddUpdateOfferEvent(event);
			var offer:Offer = auoe.offer;
			var offersList:ArrayCollection = OfferModelLocator.getInstance().offerModel.offersList;
//				var currentOffer:Offer = AppModelLocator.getInstance().offerModel.currentOffer;
//				var index:int = offersList.getItemIndex(currentOffer);
//				offersList.removeItemAt(index);
//				offersList.addItemAt(offer,index);
//			}
			var delegate:OfferServiceDelegate = new OfferServiceDelegate(this);
			delegate.saveOffer(offer);
			
		}
		
		public function result(data:Object):void
		{
			trace("DEBUG: AddUpdateOfferCommand.result()");
			var faoe:FindAllOffersEvent = new FindAllOffersEvent();
			faoe.dispatch();

		}


		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+event);			
		}

	}
}