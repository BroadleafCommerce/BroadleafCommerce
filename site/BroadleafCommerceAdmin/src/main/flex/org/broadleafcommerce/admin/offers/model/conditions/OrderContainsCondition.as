/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.offers.model.conditions
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