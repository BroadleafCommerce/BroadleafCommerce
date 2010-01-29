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
package org.broadleafcommerce.admin.cms.view.contentCreation.dynamicForms
{
	import flash.display.DisplayObject;

	import mx.collections.ArrayCollection;
	import mx.containers.Form;
	import mx.containers.FormItem;
	import mx.controls.Alert;
	import mx.controls.CheckBox;
	import mx.controls.ComboBox;
	import mx.controls.DateField;
	import mx.controls.TextArea;
	import mx.controls.TextInput;

	import org.broadleafcommerce.admin.cms.vo.Content;
	import org.broadleafcommerce.admin.cms.vo.ContentDetails;

	public class ContentFormPersist
	{
		public function ContentFormPersist()
		{
		}

		public static function persistForms(headerForm:Form, dynamicForm:Form, content:Content, contentDetails:ContentDetails, contentDetailsList:ArrayCollection):void{
			var headerFormItems:Array = headerForm.getChildren();

			for each(var frmItem:FormItem in headerFormItems){
				var input:DisplayObject = frmItem.getChildAt(0);

				if (input is TextInput){
					content[frmItem.name] = TextInput(input).text;
				} else if (input is ComboBox){
					content[frmItem.name] = ComboBox(input).selectedLabel;
				} else if (input is TextArea){
					content[frmItem.name] = TextArea(input).text;
				} else if (input is DateField){
					content[frmItem.name] = DateField(input).selectedDate;
				} else if (input is CheckBox){
					content[frmItem.name] = CheckBox(input).selected;
				}
			}

			Alert.show(content.toString());
		}

	}
}