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
	import mx.containers.HBox;
	import mx.controls.CheckBox;
	import mx.controls.ComboBox;
	import mx.controls.DateField;
	import mx.controls.RichTextEditor;
	import mx.controls.TextArea;
	import mx.controls.TextInput;
	import mx.utils.ObjectUtil;

	import org.broadleafcommerce.admin.cms.control.events.SaveContentEvent;
	import org.broadleafcommerce.admin.cms.vo.Content;
	import org.broadleafcommerce.admin.cms.vo.ContentXmlData;

	public class ContentFormPersist
	{
		public function ContentFormPersist()
		{
		}

		public static function persistForms(headerForm:Form, dynamicForm:Form, content:Content, contentDetailsList:ArrayCollection, contentType:String, user:String, sandbox:String, callingTabName:String):void{
			var headerFormItems:Array = headerForm.getChildren();
			var contentCopy:Content = new Content();
			var contentDetailsListCopy:ArrayCollection = new ArrayCollection();

			if (content != null){
				contentCopy = ObjectUtil.copy(content) as Content;
			}

			if (contentDetailsList != null){
				contentDetailsListCopy = ObjectUtil.copy(contentDetailsList) as ArrayCollection;
			}

			//update header information
			for each(var frmItem:FormItem in headerFormItems){
				var input:DisplayObject = frmItem.getChildAt(0);

				if (frmItem.name != 'parentContentId') {
					if (input is TextInput){
						contentCopy[frmItem.name] = TextInput(input).text;
					} else if (input is ComboBox){
						contentCopy[frmItem.name] = ComboBox(input).selectedLabel;
					} else if (input is TextArea){
						contentCopy[frmItem.name] = TextArea(input).text;
					} else if (input is DateField){
						contentCopy[frmItem.name] = DateField(input).selectedDate;
					} else if (input is CheckBox){
						contentCopy[frmItem.name] = CheckBox(input).selected;
					}
				} else {
					var hbox:HBox = input as HBox;
					var hiddenTxt:TextInput = hbox.getChildAt(1) as TextInput;
					contentCopy['parentContentId'] = hiddenTxt.text;
				}
			}
			//update other info on header
			contentCopy.contentType = contentType;
			contentCopy.sandbox = sandbox;
			contentCopy.lastModifiedDate = new Date();
			contentCopy.lastModifiedBy = user;

			//update details
			for each(var detailFrmItem:DisplayObject in dynamicForm.getChildren()){
				if (detailFrmItem is FormItem){
					var inputDetails:DisplayObject =FormItem(detailFrmItem).getChildAt(0);

					if( findXmlDataByTagName(contentDetailsListCopy, detailFrmItem.name) != null ) {
						var xmlData:ContentXmlData = findXmlDataByTagName(contentDetailsListCopy, detailFrmItem.name);
						if (inputDetails is TextInput){
							xmlData.data = TextInput(inputDetails).text;
						} else if (inputDetails is ComboBox){
							xmlData.data = ComboBox(inputDetails).selectedLabel;
						} else if (inputDetails is TextArea){
							xmlData.data = TextArea(inputDetails).text;
						} else if (inputDetails is DateField){
							xmlData.data = DateField(inputDetails).selectedDate.toDateString();
						} else if (inputDetails is CheckBox){
							xmlData.data = CheckBox(inputDetails).selected.toString();
						} else if (inputDetails is RichTextEditor) {
							//TODO change to .htmlText
							xmlData.data = RichTextEditor(inputDetails).text;
						}

					} else {
						var xmlDataNew:ContentXmlData = new ContentXmlData();
						xmlDataNew.name = detailFrmItem.name;
						if (inputDetails is TextInput){
							xmlDataNew.data = TextInput(inputDetails).text;
						} else if (inputDetails is ComboBox){
							xmlDataNew.data = ComboBox(inputDetails).selectedLabel;
						} else if (inputDetails is TextArea){
							xmlDataNew.data = TextArea(inputDetails).text;
						} else if (inputDetails is DateField){
							xmlDataNew.data = DateField(inputDetails).selectedDate.toDateString();
						} else if (inputDetails is CheckBox){
							xmlDataNew.data = CheckBox(inputDetails).selected.toString();
						} else if (inputDetails is RichTextEditor) {
							//TODO change to .htmlText
							xmlDataNew.data = RichTextEditor(inputDetails).text;
						}

						contentDetailsListCopy.addItem(xmlDataNew);
					}
				}
			}

//			Alert.show(contentCopy.toString(), "Content");
//			var t:String = "";
//			for each (var x:ContentXmlData in contentDetailsListCopy){
//				t += x.toString();
//			}
//
//			Alert.show(t, "Content Details");

			new SaveContentEvent(contentCopy, contentDetailsListCopy, sandbox, callingTabName).dispatch();
		}

		private static function findXmlDataByTagName(contentDetailsList:ArrayCollection, tagName:String):ContentXmlData{
			for each(var xmlData:ContentXmlData in contentDetailsList){
				if (xmlData.name == tagName){
					return xmlData;
				}
			}
			return null;
		}

	}
}