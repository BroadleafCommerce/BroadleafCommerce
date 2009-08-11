package org.broadleafcommerce.admin.tools.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	
	import org.broadleafcommerce.admin.tools.vo.CodeType;
	
	public class BroadleafCommerceAdminToolsServiceDelegate
	{
		private var responder:IResponder;
    	private var service:Object;
    	private var toolsService:Object;		
		
        public function BroadleafCommerceAdminToolsServiceDelegate(responder:IResponder){
			this.toolsService = BroadleafCommerceAdminToolsServiceLocator.getInstance().getService();
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
	}
}