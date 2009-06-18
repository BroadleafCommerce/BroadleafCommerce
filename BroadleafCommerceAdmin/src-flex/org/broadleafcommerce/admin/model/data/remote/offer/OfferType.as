package org.broadleafcommerce.admin.model.data.remote.offer
{
	import flash.utils.IDataInput;
	import flash.utils.IDataOutput;
	import flash.utils.IExternalizable;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.offer.service.type.OfferType")]	
	public class OfferType 
	{
		public var type:String;
		
		public function OfferType()
		{
		}
		

	}
}