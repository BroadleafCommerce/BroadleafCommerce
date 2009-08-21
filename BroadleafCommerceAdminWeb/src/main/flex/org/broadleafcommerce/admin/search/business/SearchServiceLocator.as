package org.broadleafcommerce.admin.search.business
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.remoting.mxml.RemoteObject;
	
	import org.broadleafcommerce.admin.search.model.SearchModel;
	
	public class SearchServiceLocator
	{
		
      private static var _instance : SearchServiceLocator;

	  private static var myService : RemoteObject;
		
      /**
       * Return the ServiceLocator instance.
       * @return the instance.
       */
      public static function get instance() : SearchServiceLocator 
      {
         if ( ! _instance )
         {
            _instance = new SearchServiceLocator();
         }
            
         return _instance;
      }
      
      /**
       * Return the ServiceLocator instance.
       * @return the instance.
       */
      public static function getInstance() : SearchServiceLocator 
      {
         return instance;
      }
         
      // Constructor should be private but current AS3.0 does not allow it
      public function SearchServiceLocator() 
      {   
         if ( _instance )
         {
            throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "SearchServiceLocator" );
         }
            
         _instance = this;
      }
		
		public function getService():RemoteObject{
			if(myService == null){				
				myService = new mx.rpc.remoting.mxml.RemoteObject(); 
				var adminService:RemoteObject = mx.rpc.remoting.mxml.RemoteObject((ServiceLocator.getInstance().getRemoteObject("blcAdminService")));
				myService.concurrency = "multiple";
				myService.endpoint = adminService.endpoint;
				myService.showBusyCursor = true; 
				myService.destination = SearchModel.SERVICE_ID;
			}
			return myService;
		}

	}
}