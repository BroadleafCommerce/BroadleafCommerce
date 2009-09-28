package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.RemoveFeaturedProductFromCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.FeaturedProduct;

	public class RemoveFeaturedProductFromCategoryCommand implements Command
	{
		public function RemoveFeaturedProductFromCategoryCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var rfpfce:RemoveFeaturedProductFromCategoryEvent = RemoveFeaturedProductFromCategoryEvent(event);
			var featuredProduct:FeaturedProduct = rfpfce.featuredProduct;
			var category:Category = rfpfce.category;
			for (var index:String in category.featuredProducts){
				var fp:FeaturedProduct = FeaturedProduct(category.featuredProducts[parseInt(index)]); 
				if(fp.id == featuredProduct.id){
					category.featuredProducts.removeItemAt(parseInt(index));
					break;
				}				
			}
			
			var sce:SaveCategoryEvent = new SaveCategoryEvent(category);
			sce.dispatch();
		}
		
	}
}