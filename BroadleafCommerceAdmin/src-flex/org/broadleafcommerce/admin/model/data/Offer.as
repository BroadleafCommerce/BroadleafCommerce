package org.broadleafcommerce.admin.model.data
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class Offer
	{
		public var id:int;
		public var name:String;
		public var description:String;
		public var type:String;
		public var applyToPrice:String;
		public var active:Boolean;
		public var maxUsages:int;
		public var who:String;
		public var code:String;
		public var customerGroups:ArrayCollection;
		public var customers:ArrayCollection;
		public var codes:ArrayCollection;
		public var fromDate:Date;
		public var toDate:Date;
		public var qualifyConditions:ArrayCollection;
		public var applyToType:String;
		public var applyToConditions:ArrayCollection;
	}
}