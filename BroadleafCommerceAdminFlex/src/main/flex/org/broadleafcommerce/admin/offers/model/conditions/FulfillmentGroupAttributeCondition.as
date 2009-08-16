package org.broadleafcommerce.admin.offers.model.conditions
{
	[Bindable]
	public class FulfillmentGroupAttributeCondition implements AttributeCondition
	{
		private const _operatorLabel:String = "Fulfillment Group attribute is. . .";
		private const _conditionLabelBegin:String = "fg.";
		private var _conditionLabel:String;		
		private var _field:String;
		private var _operator:String;
		private var _value:String;
		private var _level:int = 0;		

		public function FulfillmentGroupAttributeCondition(){
			_conditionLabel = _conditionLabelBegin;
			
		}
		
		public function get level():int{
			return _level;
		}
		
		public function set level(i:int):void{
			_level = i;
		}

		public function get operatorLabel():String{
			return _operatorLabel;
		}
		
		public function get conditionLabelBegin():String{
			return _conditionLabelBegin;
		}
		
		public function get conditionLabel():String{
			return _conditionLabel+_field+" "+_operator+" "+_value;
		}
		
		public function set conditionLabel(s:String):void{
			_conditionLabel = s;
		}

		public function get field():String
		{
			return _field;
		}
		
		public function set field(s:String):void
		{
			_field = s;
		}
		
		public function get operator():String
		{
			return _operator;
		}
		
		public function set operator(s:String):void
		{
			_operator = s;
		}
		
		public function get value():String{
			return _value;
		}
		
		public function set value(s:String):void{
			_value = s;
		}
		
		public function clone():Condition
		{
			var fgc:FulfillmentGroupAttributeCondition = new FulfillmentGroupAttributeCondition();
			return fgc;
		}
		
	}
}