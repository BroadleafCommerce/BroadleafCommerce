package org.broadleafcommerce.admin.model.data.remote.catalog.product
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.ProductImpl")]
	public class Product 
//	implements IExternalizable
	{
		public var id:Number;
		public var name:String;
		public var description:String;
		public var longDescription:String;
		public var activeStartDate:Date;
		public var activeEndDate:Date;
		public var model:String;
 		public var manufacturer:String;
 		public var dimension:ProductDimension = new ProductDimension();
		public var width:uint;
		public var height:uint;
		public var depth:uint;
		public var girth:uint;
		public var size:String;
		public var container:String;
		public var weight:ProductWeight = new ProductWeight();
		public var crossSaleProducts:ArrayCollection = new ArrayCollection();
		public var upSaleProducts:ArrayCollection = new ArrayCollection();
		public var allSkus:ArrayCollection = new ArrayCollection();
		public var productImages:Object = new Object();
		public var defaultCategory:Category = new Category();
		public var allParentCategories:ArrayCollection = new ArrayCollection();
		public var isFeaturedProduct:Boolean;
		public var machineSortable:Boolean;
		
		public function get isMachineSortable():Boolean{
			return machineSortable;
		}
		
//		public function get allParentCategoriesArray():Array{
//			return allParentCategories.toArray();
//		}
//
		
//    public function readExternal(input:IDataInput):void {
//		
//    }
//
//    public function writeExternal(output:IDataOutput):void {
//        
//        // output.writeObject(currency);
//    }
		
		
	}
}