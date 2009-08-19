package org.broadleafcommerce.admin.catalog.business
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.remoting.mxml.RemoteObject;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	
	public class CatalogServiceLocator
	{
		
      private static var _instance : CatalogServiceLocator;
      private var myService:RemoteObject = new RemoteObject();
		
      /**
       * Return the ServiceLocator instance.
       * @return the instance.
       */
      public static function get instance() : CatalogServiceLocator 
      {
      	trace("CatalogServiceLocator.get instance()");      	
         if ( ! _instance )
         {
            _instance = new CatalogServiceLocator();
         }
            
         return _instance;
      }
      
      /**
       * Return the ServiceLocator instance.
       * @return the instance.
       */
      public static function getInstance() : CatalogServiceLocator 
      {
      	trace("CatalogServiceLocator.getInstance()");
         return instance;
      }
         
      // Constructor should be private but current AS3.0 does not allow it
      public function CatalogServiceLocator() 
      {   
      	trace("new CatalogServiceLocator()");
         if ( _instance )
         {
            throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "CatalogServiceLocator" );
         }
            
         _instance = this;
      }
		
		public function getService():RemoteObject{
			trace("CatalogServiceLocator.getService()");
//			var myService:RemoteObject = new RemoteObject();
			var defaultService:RemoteObject = mx.rpc.remoting.mxml.RemoteObject(ServiceLocator.getInstance().getRemoteObject("blcAdminService"));
			myService.endpoint = defaultService.endpoint;
			myService.showBusyCursor = true;			
			myService.destination = CatalogModel.SERVICE_ID;				
			return myService;
		}

	}
}