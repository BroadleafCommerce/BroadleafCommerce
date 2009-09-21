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
package org.broadleafcommerce.admin.catalog.vo.sku
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.core.vo.Money;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.SkuImpl")]
	public class Sku
	{
		public var id:Number;
		public var salePrice:Money = new Money();
		public var retailPrice:Money = new Money();
//		public var listPrice:Money = new Money();
//		public var salePrice:Number;
//		public var retailPrice:Number;
//		public var listPrice:Number;
		public var name:String;
		public var description:String;
		public var longDescription:String;
		public var taxable:Boolean = true;
		public var discountable:Boolean;
		public var available:Boolean;
		public var activeStartDate:Date = new Date();
		public var activeEndDate:Date = new Date();
		public var active:Boolean;
		public var skuImages:Object = new Object();
		public var skuMedia:Object = new Object();
		public var allParentProducts:ArrayCollection = new ArrayCollection();
		
		public function Sku(){
			id=-1;
		}

		public function get listPrice():Money{
			return retailPrice;
		}		
		
		public function set listPrice(money:Money):void{
			this.retailPrice = money;
		}
		
		public function get isDiscountable():Boolean{
			return discountable;
		}

		public function set isDiscountable(b:Boolean):void{
			this.discountable=b;
		}

//		public function get isTaxable():Boolean{
//			return this.taxable;
//		}
//		
//		public function get isDescountable():Boolean{
//			return this.discountable;
//		}
//		
//		public function get isAvailable():Boolean{
//			return this.available;
//		}
//		
//		public function get isActive():Boolean{
//			return this.active;
//		}
	}
}