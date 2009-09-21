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
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.offers.business.OfferServiceDelegate;
	import org.broadleafcommerce.admin.offers.model.OfferModel;
	import org.broadleafcommerce.admin.offers.model.OfferModelLocator;
	import org.broadleafcommerce.admin.offers.vo.Offer;

	public class FindAllOffersCommand implements Command, IResponder
	{
		private var offerModel:OfferModel = OfferModelLocator.getInstance().offerModel;
		
		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: FindAllOffersCommand.execute()");
			var delegate:OfferServiceDelegate = new OfferServiceDelegate(this);
			delegate.findAllOffers();
		}
		
		public function result(data:Object):void
		{
			trace("DEBUG: FindAllOffersCommand.result()");
			var event:ResultEvent = ResultEvent(data);
			this.offerModel.offersList = ArrayCollection(event.result);
			// populate array collection of offers to be filtered
			this.offerModel.offersListFiltered.removeAll();
			for each(var offerToBeFiltered:Offer in this.offerModel.offersList){
				this.offerModel.offersListFiltered.addItem(offerToBeFiltered);
			}
		}			
		
		
		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+event);			
		}
		
	}
}