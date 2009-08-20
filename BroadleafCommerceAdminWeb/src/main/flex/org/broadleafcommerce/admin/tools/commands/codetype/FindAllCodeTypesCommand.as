package org.broadleafcommerce.admin.tools.commands.codetype
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.tools.business.ToolsServiceDelegate;
	import org.broadleafcommerce.admin.tools.model.ToolsModelLocator;
	
	public class FindAllCodeTypesCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void{
			trace("execute : FindAllCodeTypesCommand");
			var delegate:ToolsServiceDelegate = new ToolsServiceDelegate(this);
			delegate.findAllCodeTypes();
		}

		public function result(data:Object):void{
			trace("result : FindAllCodeTypesCommand");
			var event:ResultEvent = ResultEvent(data);
			ToolsModelLocator.getInstance().toolsModel.codeTypes = ArrayCollection(event.result);
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}		
	}
}