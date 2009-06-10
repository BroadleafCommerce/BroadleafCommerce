package org.broadleafcommerce.admin.control.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.control.events.FindAllCatalogCategoriesEvent;
	import org.broadleafcommerce.admin.control.events.FindAllOffersEvent;

	public class InitializeApplicationCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();

		public function InitializeApplicationCommand()
		{			
			eventChain.addItem(new FindAllCatalogCategoriesEvent());
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