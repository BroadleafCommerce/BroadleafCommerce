package org.broadleafcommerce.admin.model.data.conditions
{
	[Bindable]
	public interface AttributeCondition extends Condition
	{
		function get field():String;
		
		function set field(s:String):void;
		
		function get operator():String;
		
		function set operator(s:String):void;
		
		function get value():String;
		
		function set value(s:String):void;
	}
}