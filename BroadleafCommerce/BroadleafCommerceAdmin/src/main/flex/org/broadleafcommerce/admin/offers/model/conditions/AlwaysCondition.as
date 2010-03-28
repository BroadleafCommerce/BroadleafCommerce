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