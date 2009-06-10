package org.broadleafcommerce.admin.model.data.remote
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.offer.domain.OfferImpl")]
	public class Offer
	{
		public var id:int;
		public var name:String;
		public var description:String;
		public var type:Object;
		public var typeString:String;
		public var discountType:Object;
		public var discountTypeString:String;
		public var value:Money;
		public var priority:Number;
		public var startDate:Date;
		public var  endDate:Date;
		public var stackable:Boolean;
		public var targetSystem:String;
		public var applyDiscountToSalePrice:Boolean;
		public var appliesToOrderRules:String;
		public var appliesToCustomerRules:String;
		public var applyDiscountToMarkedItems:Boolean;
		public var combinableWithOtherOffers:Boolean;
		public var deliveryType:Object;
		public var deliveryTypeString:String;
		public var maxUses:Number;
		public var uses:Number;
		
		public function get valueNumber():Number{
			if(value && value.amount){
				return value.amount
			}else{
				value = new Money();
				value.amount = 0;
				return value.amount;
			}
		}
		
		public function set valueNumber(newValue:Number):void{
			value.amount = newValue;
		}
	}
}