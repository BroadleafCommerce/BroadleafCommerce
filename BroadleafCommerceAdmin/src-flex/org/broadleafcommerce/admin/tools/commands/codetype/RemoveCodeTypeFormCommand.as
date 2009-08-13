package org.broadleafcommerce.admin.tools.commands.codetype
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;
	import org.broadleafcommerce.admin.tools.model.ToolsModel;
	import org.broadleafcommerce.admin.tools.model.ToolsModelLocator;
	
	public class RemoveCodeTypeFormCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			var toolsModel:ToolsModel = ToolsModelLocator.getInstance().toolsModel;
			toolsModel.currentCodeType = new CodeType();
			toolsModel.viewState = ToolsModel.STATE_NONE;
		}
	}
}