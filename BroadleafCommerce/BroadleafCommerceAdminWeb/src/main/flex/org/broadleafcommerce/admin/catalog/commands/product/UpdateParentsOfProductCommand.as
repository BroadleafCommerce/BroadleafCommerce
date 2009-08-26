package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.UpdateParentsOfProductEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class UpdateParentsOfProductCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: execute : ");
			var aptpe:UpdateParentsOfProductEvent = UpdateParentsOfProductEvent(event);
			aptpe.product.allParentCategories = aptpe.parents;
			CatalogModelLocator.getInstance().productModel.currentProductChanged = true;
			for each(var parent:Object in aptpe.parents){
				if(parent is Category){
					Category(parent).allChildCategories.addItem(aptpe.product);
				}
			}
		}
		
	}
}