package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.EditProductEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;

	public class NewProductCommand implements Command
	{
		
		public function execute(event:CairngormEvent):void
		{
			trace("execute : ");
			var product:Product = new Product();
			var defaultCategory:Category = CatalogModelLocator.getInstance().categoryModel.currentCategory
			var sku:Sku = new Sku();
			sku.retailPrice.amount = 0;
			sku.salePrice.amount = 0;
			product.allParentCategories.addItem(defaultCategory);
			product.defaultCategory = defaultCategory;
			product.allSkus.addItem(sku);
			var epe:EditProductEvent = new EditProductEvent(product,true);
			epe.dispatch();
//			CatalogModelLocator.getInstance().productModel.currentProduct = product;			
//			CatalogModelLocator.getInstance().skuModel.currentSku = Sku(product.allSkus.getItemAt(0)); 
//			CatalogModelLocator.getInstance().productModel.viewState = ProductModel.STATE_VIEW_EDIT;			
		}
		
	}
}