package org.broadleafcommerce.admin.core.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.RetrieveCatalogEvent;
	import org.broadleafcommerce.admin.offers.control.events.FindAllOffersEvent;

	public class InitializeApplicationCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();

		public function InitializeApplicationCommand()
		{			
			eventChain.addItem(new RetrieveCatalogEvent());
			eventChain.addItem(new FindAllOffersEvent());
		}

		public function execute(event:CairngormEvent):void
		{
			for each(event in eventChain){
				CairngormEventDispatcher.getInstance().dispatchEvent(event);
			}
		}
		
	}
}