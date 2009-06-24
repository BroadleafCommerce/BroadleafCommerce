package org.broadleafcommerce.admin.model.business
{
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;
	import org.broadleafcommerce.admin.model.data.remote.catalog.sku.Sku;
	import org.broadleafcommerce.admin.model.data.remote.offer.Offer;
	
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
		
		public function saveCategory(category:Category):void{
			var call:AsyncToken = catalogService.saveCategory(category);
			call.addResponder(responder);
		}
		
		public function saveProduct(product:Product):void{
			var call:AsyncToken = catalogService.saveProduct(product);
			call.addResponder(responder);
		}
		
		public function findAllProducts():void{
			var call:AsyncToken = catalogService.findAllProducts();
			call.addResponder(responder);
		}
		
		public function findAllSkus():void{
			var call:AsyncToken = catalogService.findAllSkus();
			call.addResponder(responder);
		}
		
		public function saveSku(sku:Sku):void{
			var call:AsyncToken = catalogService.saveSku(sku);
			call.addResponder(responder);
		}

	}
}