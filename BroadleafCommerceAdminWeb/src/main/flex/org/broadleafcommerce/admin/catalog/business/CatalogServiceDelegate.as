package org.broadleafcommerce.admin.catalog.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.remoting.RemoteObject;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;
	
	
	public class CatalogServiceDelegate
	{

        private var responder : IResponder;
        private var catalogService : RemoteObject;

		public function CatalogServiceDelegate(responder:IResponder)
		{
			trace("DEBUG: new CatalogServiceDelegate()");
			this.catalogService = CatalogServiceLocator.getInstance().getService();
            this.responder = responder;	
		}
		
		public function findAllCategories():void{
			trace("DEBUG: CatalogServiceDelegate.findAllCategories()");			
			var call:AsyncToken = catalogService.findAllCategories();
			call.addResponder(responder);	
		}
		
		public function saveCategory(category:Category):void{
			trace("DEBUG: CatalogServiceDelegate.saveCategory()");			
			var call:AsyncToken = catalogService.saveCategory(category);
			call.addResponder(responder);
		}
		
		public function saveProduct(product:Product):void{
			trace("DEBUG: CatalogServiceDelegate.saveProduct()");			
			var call:AsyncToken = catalogService.saveProduct(product);
			call.addResponder(responder);
		}
		
		public function findAllProducts():void{
			trace("DEBUG: CatalogServiceDelegate.findAllProducts()");			
			var call:AsyncToken = catalogService.findAllProducts();
			call.addResponder(responder);
		}
		
		public function findProductsByCategory(category:Category):void{
			trace("DEBUG: CatalogServiceDelegate.findActiveProductsByCategory()");			
			var call:AsyncToken = catalogService.findProductsByCategory(category);
			call.addResponder(responder);
		}
		
		public function findAllSkus():void{
			trace("DEBUG: CatalogServiceDelegate.findAllSkus()");			
			var call:AsyncToken = catalogService.findAllSkus();
			call.addResponder(responder);
		}
		
		public function saveSku(sku:Sku):void{
			trace("DEBUG: CatalogServiceDelegate.saveSku()");			
			var call:AsyncToken = catalogService.saveSku(sku);
			call.addResponder(responder);
		}
		
	}
}