package org.broadleafcommerce.admin.model.data.remote
{
	import flash.utils.IDataInput;
	import flash.utils.IDataOutput;
	import flash.utils.IExternalizable;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.offer.service.type.OfferDeliveryType")]	
	public class OfferDeliveryType
	{
		public var type:String;
		
		public function OfferDeliveryType()
		{
		}
		

	}
}