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
package org.broadleafcommerce.admin.catalog.view.product.components
{
	import flash.display.Sprite;
	import flash.events.KeyboardEvent;
	
	import mx.controls.CheckBox;
	import mx.controls.List;
	import mx.controls.listClasses.IListItemRenderer;
	import mx.core.ClassFactory;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class ProductParentsList extends List
	{
		public function ProductParentsList()
		{
			super();
			this.itemRenderer = new ClassFactory(ProductParentListRenderer);
		}

	// fake all mouse interaction as if it had the ctrl key down
	override protected function selectItem(item:IListItemRenderer,
                                  shiftKey:Boolean, ctrlKey:Boolean,
                                  transition:Boolean = true):Boolean
	{
		return super.selectItem(item, false, true, transition);
	}
	
	// turn off selection indicator
    override protected function drawSelectionIndicator(
                                indicator:Sprite, x:Number, y:Number,
                                width:Number, height:Number, color:uint,
                                itemRenderer:IListItemRenderer):void
    {
	}

	// whenever we draw the renderer, make sure we re-eval the checked state
    override protected function drawItem(item:IListItemRenderer,
                                selected:Boolean = false,
                                highlighted:Boolean = false,
                                caret:Boolean = false,
                                transition:Boolean = false):void
    {
		CheckBox(item).invalidateProperties();
		super.drawItem(item, selected, highlighted, caret, transition);
	}

	// fake all keyboard interaction as if it had the ctrl key down
	override protected function keyDownHandler(event:KeyboardEvent):void
	{
		// this is technically illegal, but works
		event.ctrlKey = true;
		event.shiftKey = false;
		super.keyDownHandler(event);
	}

		
	}
}