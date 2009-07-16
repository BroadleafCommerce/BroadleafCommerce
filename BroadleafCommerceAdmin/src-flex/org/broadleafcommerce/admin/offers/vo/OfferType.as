package org.broadleafcommerce.admin.offers.vo
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