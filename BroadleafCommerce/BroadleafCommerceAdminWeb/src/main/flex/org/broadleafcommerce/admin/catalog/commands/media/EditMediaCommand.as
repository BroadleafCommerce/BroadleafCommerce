package org.broadleafcommerce.admin.catalog.commands.media
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.media.EditMediaEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;

	public class EditMediaCommand implements Command
	{
		public function EditMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var ecme:EditMediaEvent = EditMediaEvent(event);
			CatalogModelLocator.getInstance().mediaModel.currentMedia = ecme.media;
			
		}
		
	}
}