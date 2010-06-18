package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class AddFeaturedProductToCategoryEvent extends CairngormEvent
	{
		public static const EVENT_ADD_FEATURED_PRODUCT_TO_CATEGORY:String = "add_featured_product_to_category";
		
		public var product:Product;
		public var category:Category;
		public var index:int;

		public function AddFeaturedProductToCategoryEvent(product:Product, category:Category, index:int=-1)
		{
			super(EVENT_ADD_FEATURED_PRODUCT_TO_CATEGORY);
			this.product = product;
			this.category = category;
			this.index = index;
		}
		
	}
}