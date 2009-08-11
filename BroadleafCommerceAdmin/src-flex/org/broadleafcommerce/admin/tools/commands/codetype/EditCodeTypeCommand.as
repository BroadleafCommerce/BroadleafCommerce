package org.broadleafcommerce.admin.tools.commands.codetype
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.tools.control.events.codetype.EditCodeTypeEvent;
	import org.broadleafcommerce.admin.tools.model.ToolsModel;
	import org.broadleafcommerce.admin.tools.model.ToolsModelLocator;

	public class EditCodeTypeCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			var ecte:EditCodeTypeEvent = EditCodeTypeEvent(event);
			var toolsModel:ToolsModel = ToolsModelLocator.getInstance().toolsModel;
			toolsModel.currentCodeType = ecte.codeType;
			if(toolsModel.currentCodeType.modifiable == "true"){
				toolsModel.viewState = ToolsModel.STATE_EDIT;
			}else{
				toolsModel.viewState = ToolsModel.STATE_VIEW;
			}
		}
	}
}