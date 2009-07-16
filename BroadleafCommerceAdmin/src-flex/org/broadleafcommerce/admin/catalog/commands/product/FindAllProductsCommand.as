package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.BuildCatalogEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.business.BroadleafCommerceAdminServiceDelegate;
	
	public class FindAllProductsCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var delegate:BroadleafCommerceAdminServiceDelegate = new BroadleafCommerceAdminServiceDelegate(this);
			delegate.findAllProducts();
		}
		
		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			AppModelLocator.getInstance().productModel.catalogProducts = ArrayCollection(event.result);
			var bcte:BuildCatalogEvent = new BuildCatalogEvent();
			var x:ArrayCollection = ArrayCollection(event.result);
			bcte.dispatch();
		}
		
		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}