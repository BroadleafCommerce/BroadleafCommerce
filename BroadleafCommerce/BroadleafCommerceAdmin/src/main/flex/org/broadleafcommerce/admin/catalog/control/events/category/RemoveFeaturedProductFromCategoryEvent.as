package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.FeaturedProduct;

	public class RemoveFeaturedProductFromCategoryEvent extends CairngormEvent
	{
		public static const EVENT_REMOVE_FEATURED_PRODUCT_FROM_CATEGORY:String = "remove_featured_product_from_category_event";
		
		public var featuredProduct:FeaturedProduct;
		public var category:Category;
		
		public function RemoveFeaturedProductFromCategoryEvent(featuredProduct:FeaturedProduct, category:Category)
		{
			super(EVENT_REMOVE_FEATURED_PRODUCT_FROM_CATEGORY);
			this.featuredProduct = featuredProduct;
			this.category = category;
		}
		
	}
}