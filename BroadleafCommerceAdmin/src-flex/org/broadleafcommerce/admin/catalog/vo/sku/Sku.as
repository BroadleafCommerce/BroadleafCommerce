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
//		private var _skuImages:Object;
		public var allParentProducts:ArrayCollection = new ArrayCollection();
		
		public function Sku(){
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

		public function set skuImages(x:Object):void{
			// do nothing
		}
		
		public function get skuImages():Object{
			return null;
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