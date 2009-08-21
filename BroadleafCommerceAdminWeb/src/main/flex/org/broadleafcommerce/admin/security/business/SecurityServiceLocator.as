package org.broadleafcommerce.admin.security.business
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.remoting.mxml.RemoteObject;
	
	import org.broadleafcommerce.admin.security.model.SecurityModel;

	public class SecurityServiceLocator
	{
		private static var _instance:SecurityServiceLocator;
		
		private static var myService : RemoteObject;

      /**
       * Return the ServiceLocator instance.
       * @return the instance.
       */
      public static function get instance() : SecurityServiceLocator
      {
         if ( ! _instance )
         {
            _instance = new SecurityServiceLocator();
         }

         return _instance;
      }

      /**
       * Return the ServiceLocator instance.
       * @return the instance.
       */
      public static function getInstance() : SecurityServiceLocator
      {
         return instance;
      }

      // Constructor should be private but current AS3.0 does not allow it
      public function SecurityServiceLocator()
      {
         if ( _instance )
         {
            throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "AdminSecurityServiceLocator" );
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
				myService.destination = SecurityModel.SERVICE_ID;
			}
			return myService;
		}

	}
}