package org.broadleafcommerce.admin.offers.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	
	import org.broadleafcommerce.admin.offers.vo.Offer;
	
	
	public class OfferServiceDelegate
	{

        private var responder : IResponder;
        private var service : Object;
        private var offerService:Object;

		public function OfferServiceDelegate(responder:IResponder)
		{
			this.offerService = OfferServiceLocator.getInstance().getService();
            this.responder = responder;	
		}
		
		public function findAllOffers():void{
			var call:AsyncToken = offerService.findAllOffers();
			call.addResponder(responder); 
		}
		
		public function saveOffer(offer:Offer):void{
			var call:AsyncToken = offerService.save(offer);
			call.addResponder(responder);
		}

	}
}