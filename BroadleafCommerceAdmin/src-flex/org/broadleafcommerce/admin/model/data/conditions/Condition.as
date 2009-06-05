package org.broadleafcommerce.admin.model.data.conditions
{
	[Bindable]
	public interface Condition
	{
		function get level():int;
		
		function set level(i:int):void;
		
		function get operatorLabel():String;
			
		function get conditionLabelBegin():String;
		
		function get conditionLabel():String;
		
		function set conditionLabel(s:String):void;
		
		function clone():Condition;
		
		
	}
}