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
package org.broadleafcommerce.util
{
	import flash.text.TextFormat;

	import mx.controls.AdvancedDataGrid;
	import mx.controls.DataGrid;
	import mx.controls.advancedDataGridClasses.*;
	import mx.controls.dataGridClasses.*;
	import mx.core.UITextField;

	public class DataGridUtil
	{
		public function DataGridUtil()
		{
		}

		public static function optimizeDataGrid(dg:Object,widthPadding:uint = 0,heightPadding:uint = 0):void {
			if ((!dg is AdvancedDataGrid) && (!dg is DataGrid))
				return;

			var col:uint;
			var tf:TextFormat;
			var renderer:UITextField;
			var widths:Array = new Array(dg.columnCount);
			var height:uint = 0;
			var dgCol:Object;

			if (dg.columnCount > 0 && dg.dataProvider != null) {
				for (col = 0; col < dg.columnCount; ++col)
					widths[col] = -1;
				for each (var item:Object in dg.dataProvider) {
					for (col = 0; col < dg.columnCount; ++col) {
						if (dg is AdvancedDataGrid)
							renderer = new AdvancedDataGridItemRenderer();
						else
							renderer = new DataGridItemRenderer();
						dg.addChild(renderer);
						dgCol = dg.columns[col];
						renderer.text = dgCol.itemToLabel(item);
						widths[col] = Math.max(renderer.measuredWidth, widths[col]);
						height = Math.max(renderer.measuredHeight, height);
						dg.removeChild(renderer);
					}
				}

				for (col = 0; col < dg.columnCount; ++col){
					// Added to take into account header text - thanks modtodd!
					renderer = new DataGridItemRenderer();
					dg.addChild(renderer);
					renderer.text = dg.columns[col].headerText;
					widths[col] = Math.max(renderer.measuredWidth,widths[col]);
					dg.removeChild(renderer);
					if (widths[col] != -1)
						dg.columns[col].width = widths[col] + widthPadding;
				}

				if (height != 0)
					dg.rowHeight = height + heightPadding;
			}
		}
	}
}