package org.broadleafcommerce.admin.core.business
{
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.remoting.RemoteObject;
	
	public class AdminToolsDelegate
	{
		
		private var responder:IResponder;
		private var toolsService:RemoteObject;		
		
		public function AdminToolsDelegate(responder:IResponder)
		{
			this.toolsService = ServiceLocator.getInstance().getRemoteObject("blCodeTypeService");
			this.responder = responder;
		}

 		public function findAllCodeTypes():void{
			var call:AsyncToken = toolsService.findAllCodeTypes();
			call.addResponder(responder);
		}

		public function lookupCodeTypeByKey(key:String):void{
			var call:AsyncToken = toolsService.lookupCodeTypeByKey(key);
			call.addResponder(responder);
		}
		
	}
}