package org.broadleafcommerce.admin.offers.vo
{
	import flash.utils.IDataInput;
	import flash.utils.IDataOutput;
	import flash.utils.IExternalizable;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.offer.service.type.OfferDiscountType")]	
	public class OfferDiscountType 
	{
		public var type:String;
		
		public function OfferDiscountType()
		{
		}
		
	}
}