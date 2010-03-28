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
	import mx.collections.ArrayCollection;
	import mx.containers.Form;
	import mx.containers.FormItem;
	import mx.controls.DateField;
	import mx.controls.RichTextEditor;
	import mx.controls.Spacer;
	import mx.controls.TextInput;

	import org.broadleafcommerce.admin.cms.model.dynamicForms.CustomField;
	import org.broadleafcommerce.admin.cms.vo.ContentXmlData;

	public class ContentFormCreator
	{
		public function ContentFormCreator()
		{
		}

		public static function createCustomFields(dynamicForm:Form, customFields:ArrayCollection, contentDetailsList:ArrayCollection):void{
			for each(var customField:CustomField in customFields) {
				var frmItem:FormItem = new FormItem();
				frmItem.name = customField.tagName;
				var spacer:Spacer = new Spacer();
				spacer.height=10;
				var data:String = findDataByDisplayName(customField.displayName, contentDetailsList);
				if (customField.type == 'text/html'){
					var rte:RichTextEditor = new RichTextEditor();
					rte.title = customField.displayName;
					rte.width=600;
					rte.height=300;
					if ( data != null){
						//TODO change to .htmlText
						rte.text = data;
					}
					frmItem.addChild(rte);
					dynamicForm.addChild(frmItem);
				} else if (customField.type == 'text'){
					frmItem.label = customField.displayName;
					var ti:TextInput = new TextInput();
					ti.width=220;
					if ( data != null){
						ti.text = data;
					}
					frmItem.addChild(ti);
					dynamicForm.addChild(frmItem);
				} else if (customField.type == 'date'){
					frmItem.label = customField.displayName;
					var df:DateField = new DateField();
					df.width=220;
					if ( data != null){
						df.text = data;
					}
					frmItem.addChild(df);
					dynamicForm.addChild(frmItem);
				}

				dynamicForm.addChild(spacer);
			}
		}


		private static function findDataByDisplayName(displayName:String, contentDetailsList:ArrayCollection):String {
			for each (var contentXmlData:ContentXmlData in contentDetailsList){
				if (contentXmlData.name.toLowerCase() == displayName.toLowerCase()){
					return contentXmlData.data;
				}
			}

			return null;
		}

	}
}