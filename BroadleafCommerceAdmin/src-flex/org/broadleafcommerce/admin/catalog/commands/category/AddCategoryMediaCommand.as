package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.Media;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class AddCategoryMediaCommand implements Command
	{
		public function AddCategoryMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var newMedia:Media = new Media();
			CatalogModelLocator.getInstance().categoryModel.categoryMedia.addItem(newMedia);
//			var currentCategory:Category = CatalogModelLocator.getInstance().categoryModel.currentCategory;
//			currentCategory.categoryMedia["new"] = newMedia;
			
		}
		
	}
}