package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.AddFeaturedProductToCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.FeaturedProduct;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class AddFeaturedProductToCategoryCommand implements Command
	{
		public function AddFeaturedProductToCategoryCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var afptce:AddFeaturedProductToCategoryEvent = AddFeaturedProductToCategoryEvent(event);
			var product:Product = afptce.product;
			var category:Category = afptce.category;
			var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel;
			
			
			var featuredProduct:FeaturedProduct = new FeaturedProduct();
			featuredProduct.promotionMessage = "";
			featuredProduct.category = category;
			featuredProduct.product = product;
			if(afptce.index > -1){
				featuredProduct.sequence = afptce.index;	
				category.featuredProducts.addItemAt(featuredProduct, afptce.index);				
			}else{
				featuredProduct.sequence = categoryModel.currentCategory.featuredProducts.length;
				category.featuredProducts.addItem(featuredProduct);			
			}
			
			var sce:SaveCategoryEvent = new SaveCategoryEvent(category);
			sce.dispatch();
		}
		
	}
}