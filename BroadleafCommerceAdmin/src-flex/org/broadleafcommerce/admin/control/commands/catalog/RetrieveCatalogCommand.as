package org.broadleafcommerce.admin.control.commands.catalog
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.control.events.catalog.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.FindAllProductsEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.FindAllSkusEvent;

	public class RetrieveCatalogCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();

		public function RetrieveCatalogCommand()
		{
			eventChain.addItem(new FindAllCategoriesEvent());
			eventChain.addItem(new FindAllProductsEvent());
			eventChain.addItem(new FindAllSkusEvent());
		}

		public function execute(event:CairngormEvent):void
		{
				for each(var event:CairngormEvent in eventChain){
					event.dispatch();
				}
		}
		
	}
}