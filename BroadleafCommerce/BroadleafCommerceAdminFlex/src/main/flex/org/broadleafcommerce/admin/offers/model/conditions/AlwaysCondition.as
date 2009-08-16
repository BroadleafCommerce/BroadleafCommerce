package org.broadleafcommerce.admin.offers.model.conditions
{
	[Bindable]
	public class AlwaysCondition implements Condition
	{
		private static const _operatorLabel:String = "Always";
		private static const _conditionLabelBegin:String = "Always";
		private var _level:int = 0;
		
		
		public function AlwaysCondition()
		{
		}

		public function get level():int{
			return _level;
		}
		
		public function set level(i:int):void{
			_level = i;
		}

		public function get operatorLabel():String
		{
			return _operatorLabel;
		}
		
		public function get conditionLabelBegin():String
		{
			return _conditionLabelBegin;
		}
		
		public function get conditionLabel():String
		{
			return _conditionLabelBegin;
		}
		
		public function set conditionLabel(s:String):void
		{
		}
		
		public function clone():Condition
		{
			return this;
		}
		
	}
}