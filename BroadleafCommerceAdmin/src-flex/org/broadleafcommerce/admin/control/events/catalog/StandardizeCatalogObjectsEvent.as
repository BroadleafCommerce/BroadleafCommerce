package org.broadleafcommerce.admin.control.events.catalog
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;

	public class StandardizeCatalogObjectsEvent extends CairngormEvent
	{
		public var categoryArray:ArrayCollection;
		public var productArray:ArrayCollection;
		public var skuArray:ArrayCollection;
		
		public static const EVENT_STANDARDIZE_CATALOG_OBJECTS:String = "standardize_catalog_objects_event";
		
		public function StandardizeCatalogObjectsEvent(categoryArray:ArrayCollection, productArray:ArrayCollection, skuArray:ArrayCollection)
		{
			super(EVENT_STANDARDIZE_CATALOG_OBJECTS);
			this.categoryArray = categoryArray;
			this.productArray = productArray;
			this.skuArray = skuArray;
			
			for each(var product:Product in productArray){
				for (var i:String in product.allParentCategories){
					for each(var refCategory:Category in categoryArray){
						if(product.allParentCategories[i].id == refCategory.id){
							product.allParentCategories[i] = refCategory;
						}
					}
				}
			}
		}
		
	}
}