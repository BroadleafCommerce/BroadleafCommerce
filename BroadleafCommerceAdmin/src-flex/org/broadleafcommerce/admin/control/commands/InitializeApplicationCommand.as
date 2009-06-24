package org.broadleafcommerce.admin.control.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.control.events.catalog.category.FindAllCatalogCategoriesEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.FindAllCatalogProductsEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.FindAllCatalogSkusEvent;
	import org.broadleafcommerce.admin.control.events.offer.FindAllOffersEvent;

	public class InitializeApplicationCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();

		public function InitializeApplicationCommand()
		{			
			eventChain.addItem(new FindAllCatalogCategoriesEvent());
			eventChain.addItem(new FindAllOffersEvent());
			eventChain.addItem(new FindAllCatalogProductsEvent());
			eventChain.addItem(new FindAllCatalogSkusEvent());
		}

		public function execute(event:CairngormEvent):void
		{
			for each(event in eventChain){
				CairngormEventDispatcher.getInstance().dispatchEvent(event);
			}
		}
		
	}
}