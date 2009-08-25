package org.broadleafcommerce.admin.tools.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.remoting.RemoteObject;
	
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;
	
	public class ToolsServiceDelegate
	{
		private var responder:IResponder;
    	private var service:Object;
    	private var toolsService:RemoteObject;		
		
        public function ToolsServiceDelegate(responder:IResponder){
			this.toolsService = ToolsServiceLocator.getInstance().getService();
            this.responder = responder;	
		}

 		public function findAllCodeTypes():void{
			var call:AsyncToken = toolsService.findAllCodeTypes();
			call.addResponder(responder);
		}
		
		public function saveCodeType(codeType:CodeType):void{
			var call:AsyncToken = toolsService.save(codeType);
			call.addResponder(responder);
		}
		
		public function lookupCodeTypeByKey(key:String):void{
			var call:AsyncToken = toolsService.lookupCodeTypeByKey(key);
			call.addResponder(responder);
		}
		
		public function removeCodeTypeById(codeType:CodeType):void{
			var call:AsyncToken = toolsService.deleteCodeType(codeType);
			call.addResponder(responder);
		}
	}
}