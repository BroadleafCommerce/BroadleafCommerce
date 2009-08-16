package org.broadleafcommerce.admin.tools.business
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.remoting.mxml.RemoteObject;
	
	import org.broadleafcommerce.admin.tools.model.ToolsModel;
	
	public class BroadleafCommerceAdminToolsServiceLocator
	{
		private static var _instance:BroadleafCommerceAdminToolsServiceLocator;

		/**
		 * Return the ServiceLocator instance.
		 * @return the instance.
		*/
		public static function get instance():BroadleafCommerceAdminToolsServiceLocator{
			if(!_instance){
	        	_instance = new BroadleafCommerceAdminToolsServiceLocator();
	        }
	        return _instance;
        }

		/**
		 * Return the ServiceLocator instance.
		 * @return the instance.
		 */
		public static function getInstance():BroadleafCommerceAdminToolsServiceLocator{
			return instance;
		}
		
		// Constructor should be private but current AS3.0 does not allow it
		public function BroadleafCommerceAdminToolsServiceLocator(){
			if( _instance){
			   throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "BroadleafCommerceAdminToolsServiceLocator" );
			}
			_instance = this;
		}
		
		public function getService():RemoteObject{
			var myService:RemoteObject = mx.rpc.remoting.mxml.RemoteObject(ServiceLocator.getInstance().getRemoteObject("blcAdminService"));
			myService.destination = ToolsModel.SERVICE_ID;
			return myService;
		}
	}
}