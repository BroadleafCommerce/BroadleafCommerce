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
	public class AnyAllCondition implements Condition
	{
		private const _operatorLabel:String = "Any/All Condition";
		private const _conditionLabelBegin:String = "If ALL of these conditions are TRUE";
		private var _conditionLabel:String;
		private var _all:Boolean = true;  // _all = true is All, _all=false is Any
		private var _truefalse:Boolean = true;
		private var _level:int = 0;
		
		public function AnyAllCondition()
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
			var aa:String = (_all)?"ALL":"ANY";
			var tf:String = (_truefalse)?"TRUE":"FALSE";
			return "If "+aa+" of these conditions are "+tf;
		}
		
		public function set conditionLabel(s:String):void
		{
			_conditionLabel = s;
		}
		
		public function set truefalse(b:Boolean):void
		{
			_truefalse = b;
		}
		
		public function get truefalse():Boolean
		{
			return _truefalse;
		}
		
		public function set all(b:Boolean):void
		{
			_all = b;
		}
		
		public function get all():Boolean
		{
			return _all;
		}
		
		public function clone():Condition
		{
			var ac:AnyAllCondition = new AnyAllCondition();
			return ac;
		}
		
	}
}