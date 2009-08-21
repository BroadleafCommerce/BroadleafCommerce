package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryMediaEvent;
	import org.broadleafcommerce.admin.catalog.view.components.MediaCanvasViewHelper;

	public class EditCategoryMediaCommand implements Command
	{
		public function EditCategoryMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var ecme:EditCategoryMediaEvent = EditCategoryMediaEvent(event);
			MediaCanvasViewHelper(ViewLocator.getInstance().getViewHelper("categoryMediaCanvasViewHelper")).editMedia(ecme.media);
			
		}
		
	}
}