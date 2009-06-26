package org.broadleafcommerce.admin.control.commands.catalog
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.events.CollectionEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.BuildCatalogTreeEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.FindAllCatalogCategoriesEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.FindAllCatalogProductsEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.FindAllCatalogSkusEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;

	public class RetrieveCatalogCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();

		private var categoriesLoaded:Boolean = false;
		private var productsLoaded:Boolean = false;
		private var skusLoaded:Boolean = false;
		
		public function RetrieveCatalogCommand()
		{
			eventChain.addItem(new FindAllCatalogCategoriesEvent());
			eventChain.addItem(new FindAllCatalogProductsEvent());
			eventChain.addItem(new FindAllCatalogSkusEvent());
		}

		public function execute(event:CairngormEvent):void
		{
//			AppModelLocator.getInstance().categoryModel.categoryArray.addEventListener(CollectionEvent.COLLECTION_CHANGE, setCategoriesLoaded);
//			AppModelLocator.getInstance().catalogProducts.addEventListener(CollectionEvent.COLLECTION_CHANGE, setProductsLoaded);
//			AppModelLocator.getInstance().catalogSkus.addEventListener(CollectionEvent.COLLECTION_CHANGE, setSkusLoaded);
				for each(var event:CairngormEvent in eventChain){
					event.dispatch();
				}
		}
		
		private function checkCatalogLoaded():void{
				var bcte:BuildCatalogTreeEvent = new BuildCatalogTreeEvent();
				bcte.dispatch();
		}
		
		public function setCategoriesLoaded():void{
			categoriesLoaded = true;
			checkCatalogLoaded();
		}
		
		public function setProductsLoaded():void{
			productsLoaded = true;
			checkCatalogLoaded();
		}
		
		public function setSkusLoaded():void{
			skusLoaded = true;
			checkCatalogLoaded();
		}
		
	}
}