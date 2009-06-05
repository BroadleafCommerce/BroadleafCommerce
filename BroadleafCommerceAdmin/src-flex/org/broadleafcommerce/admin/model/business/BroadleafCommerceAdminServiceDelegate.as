package org.broadleafcommerce.admin.model.business
{
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	
	public class BroadleafCommerceAdminServiceDelegate
	{

        private var responder : IResponder;
        private var service : Object;
        private var catalogService : Object;

		public function BroadleafCommerceAdminServiceDelegate(responder:IResponder)
		{
			this.catalogService = ServiceLocator.getInstance().getRemoteObject("catalogService");
            this.responder = responder;	
		}
		
		public function findAllCategories():void{
			var call:AsyncToken = catalogService.findAllCategories();
			call.addResponder(responder);	
		}
		

	}
}