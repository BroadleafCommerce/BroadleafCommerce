package org.broadleafcommerce.admin.catalog.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.StandardizeCatalogObjectsEvent;

	public class StandardizeCatalogObjectsCommand implements Command
	{
		public function StandardizeCatalogObjectsCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var scoe:StandardizeCatalogObjectsEvent = StandardizeCatalogObjectsEvent(event);
			var categoryArray:ArrayCollection = scoe.categoryArray;
			var productArray:ArrayCollection = scoe.productArray;
			var skuArray:ArrayCollection = scoe.skuArray;
			
		}
		
	}
}