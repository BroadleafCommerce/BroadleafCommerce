package org.broadleafcommerce.admin.control.commands.catalog.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.product.UpdateParentsOfProductEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;

	public class UpdateParentsOfProductCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			var aptpe:UpdateParentsOfProductEvent = UpdateParentsOfProductEvent(event);
			aptpe.product.allParentCategories = aptpe.parents;
			AppModelLocator.getInstance().productModel.currentProductChanged = true;
			for each(var parent:Object in aptpe.parents){
				if(parent is Category){
					Category(parent).allChildCategories.addItem(aptpe.product);
				}
			}
		}
		
	}
}