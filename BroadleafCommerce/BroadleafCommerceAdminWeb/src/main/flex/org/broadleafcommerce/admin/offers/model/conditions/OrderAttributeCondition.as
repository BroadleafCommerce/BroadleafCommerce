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
	[Bindable]
	public class OrderAttributeCondition implements AttributeCondition
	{
		private const _operatorLabel:String = "Order attribute is. . .";
		private const _conditionLabelBegin:String = "order.";
		private var _conditionLabel:String;
		private var _field:String;
		private var _operator:String;
		private var _value:String;
		private var _level:int = 0;
		
		public function OrderAttributeCondition()
		{
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
			var oac:OrderAttributeCondition = new OrderAttributeCondition();
			return oac;
		}
		
	}
}