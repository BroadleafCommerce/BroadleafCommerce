/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.offers.vo
{
	import org.broadleafcommerce.admin.core.vo.Money;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.offer.domain.OfferImpl")]
	public class Offer
	{
		public var id:Number;
		public var name:String;
		public var description:String;
		public var type:OfferType = new OfferType();
		public var discountType:OfferDiscountType = new OfferDiscountType();
		public var deliveryType:OfferDeliveryType = new OfferDeliveryType();
		public var value:Money = new Money();
		public var priority:int;
		public var startDate:Date;
		public var endDate:Date;
		public var stackable:Boolean;
		public var targetSystem:String;
		public var applyDiscountToSalePrice:Boolean;
		public var appliesToOrderRules:String;
		public var appliesToCustomerRules:String;
		public var applyDiscountToMarkedItems:Boolean;
		public var combinableWithOtherOffers:Boolean;
		public var maxUses:int;
		public var uses:int;
		
//		public function get valueNumber():Number{
//			if(value && value.amount){
//				return value.amount
//			}else{
//				value = new Money();
//				value.amount = 0;
//				return value.amount;
//			}
//		}
		
//		public function set valueNumber(newValue:Number):void{
//			value.amount = newValue;
//		}
		
				
		public function set deliveryTypeString(newType:String):void{
			deliveryType.type = newType;
		}
		
		public function get deliveryTypeString():String{
			return deliveryType.type;
		}
		
		public function set typeString(newType:String):void{
			type.type = newType;
		}
		
		public function get typeString():String{
			return type.type;
		}

		public function set discountTypeString(newType:String):void{
			discountType.type = newType;
		}
		
		public function get discountTypeString():String{
			return discountType.type;
		}
		
	}
}