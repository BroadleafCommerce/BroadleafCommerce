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
	import flash.display.Sprite;
	import flash.events.KeyboardEvent;

	import mx.collections.Sort;
	import mx.collections.SortField;
	import mx.controls.CheckBox;
	import mx.controls.DataGrid;
	import mx.controls.dataGridClasses.DataGridColumn;
	import mx.controls.listClasses.IListItemRenderer;

	/**
	 *  DataGrid that uses checkboxes for multiple selection
	 */
	public class CheckBoxDataGrid extends DataGrid
	{

		public var sortIndex:int = -1;
		public var sortColumn:DataGridColumn;
		public var sortDirection:String;
		public var lastSortIndex:int = -1;

		override protected function selectItem(item:IListItemRenderer,
	                                  shiftKey:Boolean, ctrlKey:Boolean,
	                                  transition:Boolean = true):Boolean
		{
			// only run selection code if a checkbox was hit and always
			// pretend we're using ctrl selection
			if (item is CheckBox)
				return super.selectItem(item, false, true, transition);
			return false;
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

		public function sortByColumn(index:int):void
		{
			var c:DataGridColumn = columns[index];
			var desc:Boolean = c.sortDescending;
			// do the sort if we’re allowed to
			if (c.sortable) {
				var s:Sort = collection.sort;
				var f:SortField;
				if (s) {
					s.compareFunction = null;
					// analyze the current sort to see what we’ve been given
					var sf:Array = s.fields;
					if (sf){
						for (var i:int = 0; i < sf.length; i++) {
							if (sf[i].name == c.dataField){
								// we’re part of the current sort
								f = sf[i]
								// flip the logic so desc is new desired order
								desc = !f.descending;
								break;
							}
						}
					}
				} else
					s = new Sort;
				if (!f)
					f = new SortField(c.dataField);

				c.sortDescending = desc;
				var dir:String = (desc) ? "DESC" : "ASC";
				sortDirection = dir;
				// set the grid’s sortIndex
				lastSortIndex = sortIndex;
				sortIndex = index;
				sortColumn = c;
				placeSortArrow();
				// if you have a labelFunction you must supply a sortCompareFunction
				f.name = c.dataField;
				if (c.sortCompareFunction != null) {
					f.compareFunction = c.sortCompareFunction;
				}
				else {
					f.compareFunction = null;
				}

				f.descending = desc;
				s.fields = [f];
			}

			collection.sort = s;
			collection.refresh();
		}

	}
}