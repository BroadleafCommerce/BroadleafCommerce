package org.broadleafcommerce.admin.model.business
{
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	
	import org.broadleafcommerce.admin.model.data.remote.Offer;
	
	public class BroadleafCommerceAdminServiceDelegate
	{

        private var responder : IResponder;
        private var service : Object;
        private var catalogService : Object;
        private var offerService:Object;

		public function BroadleafCommerceAdminServiceDelegate(responder:IResponder)
		{
			this.catalogService = ServiceLocator.getInstance().getRemoteObject("catalogService");
			this.offerService = ServiceLocator.getInstance().getRemoteObject("offerService");
            this.responder = responder;	
		}
		
		public function findAllCategories():void{
			var call:AsyncToken = catalogService.findAllCategories();
			call.addResponder(responder);	
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