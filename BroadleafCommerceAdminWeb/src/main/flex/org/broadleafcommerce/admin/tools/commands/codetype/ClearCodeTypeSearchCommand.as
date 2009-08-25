package org.broadleafcommerce.admin.tools.commands.codetype
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;
	import org.broadleafcommerce.admin.tools.control.events.codetype.FindAllCodeTypesEvent;
	import org.broadleafcommerce.admin.tools.model.CodeTypeModel;
	import org.broadleafcommerce.admin.tools.model.ToolsModelLocator;

	public class ClearCodeTypeSearchCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			new FindAllCodeTypesEvent().dispatch();
			ToolsModelLocator.getInstance().codeTypeModel.currentCodeType = new CodeType();
			ToolsModelLocator.getInstance().codeTypeModel.viewState = CodeTypeModel.STATE_NONE;
		}
		
	}
}