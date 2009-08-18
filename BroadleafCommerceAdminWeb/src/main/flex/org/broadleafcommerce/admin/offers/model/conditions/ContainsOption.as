package org.broadleafcommerce.admin.offers.model.conditions
{
	import mx.collections.ArrayCollection;
	
	public class ContainsOption
	{
		public function ContainsOption(label:String=null, list:ArrayCollection=null){
			if(label)
				this.label = label;
			if(list)
				this.list = list;
		}

		public var label:String = "Label";
		public var list:ArrayCollection = new ArrayCollection();

	}
}