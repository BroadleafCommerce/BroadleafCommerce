package org.broadleafcommerce.admin.offers.business
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.remoting.mxml.RemoteObject;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	
	public class OfferServiceLocator
	{
		
      private static var _instance : OfferServiceLocator;
		
      /**
       * Return the ServiceLocator instance.
       * @return the instance.
       */
      public static function get instance() : OfferServiceLocator 
      {
         if ( ! _instance )
         {
            _instance = new OfferServiceLocator();
         }
            
         return _instance;
      }
      
      /**
       * Return the ServiceLocator instance.
       * @return the instance.
       */
      public static function getInstance() : OfferServiceLocator 
      {
         return instance;
      }
         
      // Constructor should be private but current AS3.0 does not allow it
      public function OfferServiceLocator() 
      {   
         if ( _instance )
         {
            throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "OfferServiceLocator" );
         }
            
         _instance = this;
      }
		
		public function getService():RemoteObject{
			var myService:RemoteObject = mx.rpc.remoting.mxml.RemoteObject(ServiceLocator.getInstance().getRemoteObject("blcOfferService"));
			myService.destination = CatalogModel.SERVICE_ID;
			return myService;
		}

	}
}