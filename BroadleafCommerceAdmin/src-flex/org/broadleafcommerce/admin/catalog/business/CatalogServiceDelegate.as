package org.broadleafcommerce.admin.catalog.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;
	
	
	public class CatalogServiceDelegate
	{

        private var responder : IResponder;
        private var service : Object;
        private var catalogService : Object;

		public function CatalogServiceDelegate(responder:IResponder)
		{
			this.catalogService = CatalogServiceLocator.getInstance().getService();
            this.responder = responder;	
		}
		
		public function findAllCategories():void{
			var call:AsyncToken = catalogService.findAllCategories();
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
		
		public function findActiveProductsByCategory(category:Category):void{
			var call:AsyncToken = catalogService.findActiveProductsByCategory(category);
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