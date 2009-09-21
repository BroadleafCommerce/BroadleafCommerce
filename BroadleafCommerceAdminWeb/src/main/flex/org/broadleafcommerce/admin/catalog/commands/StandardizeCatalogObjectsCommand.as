package org.broadleafcommerce.admin.catalog.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.StandardizeCatalogObjectsEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryTreeItem;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class StandardizeCatalogObjectsCommand implements Command
	{
		public function StandardizeCatalogObjectsCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: StandardizeCatalogObjectsCommand.execute()");
			var scoe:StandardizeCatalogObjectsEvent = StandardizeCatalogObjectsEvent(event);
			var categoryArray:ArrayCollection = scoe.categoryArray;
			var productArray:ArrayCollection = scoe.productArray;
			var skuArray:ArrayCollection = scoe.skuArray;
			var catalogModel:CatalogModel = CatalogModelLocator.getInstance().catalogModel;

			var categoryTreeItemArray:ArrayCollection = new ArrayCollection();

			for each(var refCategory:Category in categoryArray){
				var categoryTreeItem:CategoryTreeItem = new CategoryTreeItem(refCategory);
				categoryTreeItemArray.addItem(categoryTreeItem);
			}
			
			catalogModel.catalogTreeItemArray = categoryTreeItemArray;
			

		}
		
	}
}