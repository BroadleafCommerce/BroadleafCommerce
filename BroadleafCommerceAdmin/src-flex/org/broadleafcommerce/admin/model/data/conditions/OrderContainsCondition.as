package org.broadleafcommerce.admin.model.data.conditions
{
	import mx.collections.ArrayCollection;

	[Bindable]
	public class OrderContainsCondition implements ContainsCondition
	{
		private const _operatorLabel:String = "Order contains. . .";
		private const _conditionLabelBegin:String = "order contains. . .";
		private var _conditionLabel:String;
		private var _containsList:ArrayCollection = new ArrayCollection();
		private var _level:int = 0;
		private var _optionsList:ArrayCollection = new ArrayCollection();
		
		public function OrderContainsCondition()
		{
			_conditionLabel = _conditionLabelBegin;			
			_optionsList.addItem(new ContainsOption("SKU",new ArrayCollection(['12345','54321','098765'])));
			_optionsList.addItem(new ContainsOption("Color",new ArrayCollection(['blue','green','red'])));
			_optionsList.addItem(new ContainsOption("Size",new ArrayCollection(['small','medium','large'])));
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
			return _conditionLabel;
		}
		
		public function set conditionLabel(s:String):void{
			_conditionLabel = s;
		}

		public function get containsList():ArrayCollection
		{
			return _containsList;
		}
		
		public function set containsList(ac:ArrayCollection):void
		{
			_containsList = ac;
		}
		
		public function get optionsList():ArrayCollection
		{
			return _optionsList;
		}
		
		public function set optionsList(ac:ArrayCollection):void
		{
			_optionsList = ac;
		}
		
		public function clone():Condition
		{
			var occ:OrderContainsCondition = new OrderContainsCondition();
			return occ;
		}
	}
}