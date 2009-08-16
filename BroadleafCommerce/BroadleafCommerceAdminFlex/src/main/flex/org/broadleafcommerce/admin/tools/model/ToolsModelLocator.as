package org.broadleafcommerce.admin.tools.model
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.model.IModelLocator;
	
	public class ToolsModelLocator implements IModelLocator{
		
		private static var modelLocator:ToolsModelLocator;
		
		public static function getInstance():ToolsModelLocator{
			if(modelLocator == null){
				modelLocator = new ToolsModelLocator();
			}
			return modelLocator;
		}
		
		public function ToolsModelLocator(){
			if(modelLocator != null){
				throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "SecurityModelLocator");
			}
		}
		
		[Bindable]
		public var toolsModel:ToolsModel = new ToolsModel();

	}
}