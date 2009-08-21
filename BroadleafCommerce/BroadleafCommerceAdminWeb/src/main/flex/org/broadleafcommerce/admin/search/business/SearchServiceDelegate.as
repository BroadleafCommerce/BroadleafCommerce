package org.broadleafcommerce.admin.search.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.remoting.RemoteObject;
	
	import org.broadleafcommerce.admin.search.vo.SearchIntercept;

	public class SearchServiceDelegate
	{
        private var responder : IResponder;
        private var searchService:RemoteObject;

		public function SearchServiceDelegate(responder:IResponder)
		{
			this.searchService = SearchServiceLocator.getInstance().getService();
			this.responder = responder;
		}

 		public function findAllSearchIntercepts():void{
			var call:AsyncToken = searchService.getAllSearchIntercepts();
			call.addResponder(responder);
		}
		
		public function createSearchIntercept(intercept:SearchIntercept):void {
			var call:AsyncToken = searchService.createSearchIntercept(intercept);
			call.addResponder(responder);
		}
		
		public function updateSearchIntercept(intercept:SearchIntercept):void {
			var call:AsyncToken = searchService.updateSearchIntercept(intercept);
			call.addResponder(responder);
		}
		
		public function deleteSearchIntercept(intercept:SearchIntercept):void {
			var call:AsyncToken = searchService.deleteSearchIntercept(intercept);
			call.addResponder(responder);
		}
		
	}
}