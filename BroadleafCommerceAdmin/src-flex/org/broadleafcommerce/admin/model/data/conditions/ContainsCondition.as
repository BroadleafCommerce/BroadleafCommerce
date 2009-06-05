package org.broadleafcommerce.admin.model.data.conditions
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public interface ContainsCondition extends Condition
	{
		function get containsList():ArrayCollection;
		
		function set containsList(ac:ArrayCollection):void;
		
		function get optionsList():ArrayCollection;
		
		function set optionsList(ac:ArrayCollection):void; 
	}
}