package org.broadleafcommerce.admin.tools.commands.codetype
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.tools.control.events.codetype.EditCodeTypeEvent;
	import org.broadleafcommerce.admin.tools.model.CodeTypeModel;
	import org.broadleafcommerce.admin.tools.model.ToolsModelLocator;

	public class EditCodeTypeCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			var ecte:EditCodeTypeEvent = EditCodeTypeEvent(event);
			var toolsModel:CodeTypeModel = ToolsModelLocator.getInstance().codeTypeModel;
			toolsModel.currentCodeType = ecte.codeType;
			if(toolsModel.currentCodeType.modifiable == "true"){
				toolsModel.viewState = CodeTypeModel.STATE_EDIT;
			}else{
				toolsModel.viewState = CodeTypeModel.STATE_VIEW;
			}
		}
	}
}