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
package org.broadleafcommerce.util.components
{
	import flash.display.DisplayObject;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.text.TextField;
	import mx.controls.CheckBox;
	import mx.controls.dataGridClasses.DataGridListData;
	import mx.controls.listClasses.ListBase;

	public class CheckBoxListRenderer extends CheckBox
	{
		public function CheckBoxListRenderer()
		{
			focusEnabled = false;
		}

	override public function set data(value:Object):void
	{
		super.data = value;
		invalidateProperties();
	}

	override protected function commitProperties():void
	{
		super.commitProperties();
		if (owner is ListBase)
			selected = ListBase(owner).isItemSelected(data);
	}

	/* eat keyboard events, the underlying list will handle them */
	override protected function keyDownHandler(event:KeyboardEvent):void
	{
	}

	/* eat keyboard events, the underlying list will handle them */
	override protected function keyUpHandler(event:KeyboardEvent):void
	{
	}

	/* eat mouse events, the underlying list will handle them */
	override protected function clickHandler(event:MouseEvent):void
	{
	}

	/* center the checkbox if we're in a datagrid */
	override protected function updateDisplayList(w:Number, h:Number):void
	{
		super.updateDisplayList(w, h);

		if (listData is DataGridListData)
		{
			var n:int = numChildren;
			for (var i:int = 0; i < n; i++)
			{
				var c:DisplayObject = getChildAt(i);
				if (!(c is TextField))
				{
					c.x = (w - c.width) / 2;
					c.y = 0;
				}
			}
		}
	}



	}
}