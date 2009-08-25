package org.broadleafcommerce.admin.tools.commands.codetype
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.tools.business.ToolsServiceDelegate;
	import org.broadleafcommerce.admin.tools.control.events.codetype.FindAllCodeTypesEvent;
	import org.broadleafcommerce.admin.tools.control.events.codetype.RemoveCodeTypeEvent;

	public class RemoveCodeTypeCommand implements Command, IResponder
	{
		public function RemoveCodeTypeCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var rcte:RemoveCodeTypeEvent = RemoveCodeTypeEvent(event);
			var delegate:ToolsServiceDelegate = new ToolsServiceDelegate(this);
			delegate.removeCodeTypeById(rcte.codeType);
			
		}
		
		public function result(data:Object):void
		{
			var face:FindAllCodeTypesEvent = new FindAllCodeTypesEvent();
			face.dispatch();
		}
		
		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error removing code type: "+event.message);
		}
		
	}
}