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
package org.broadleafcommerce.admin.offers.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.remoting.RemoteObject;
	
	import org.broadleafcommerce.admin.offers.vo.Offer;
	
	
	public class OfferServiceDelegate
	{

        private var responder : IResponder;
        private var offerService:RemoteObject;

		public function OfferServiceDelegate(responder:IResponder)
		{
			trace("DEBUG: new OfferServiceDelegate()");
			this.offerService = OfferServiceLocator.getInstance().getService();
            this.responder = responder;	
		}
		
		public function findAllOffers():void{
			trace("DEBUG: OfferServiceDelegate.findAllOffers()");
			var call:AsyncToken = offerService.findAllOffers();
			call.addResponder(responder); 
		}
		
		public function saveOffer(offer:Offer):void{
			trace("DEBUG: OfferServiceDelegate.saveOffer()");
			var call:AsyncToken = offerService.save(offer);
			call.addResponder(responder);
		}

	}
}