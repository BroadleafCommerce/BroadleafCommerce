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
package org.broadleafcommerce.admin.cms.commands.contentHome
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.collections.ArrayCollection;

	import org.broadleafcommerce.admin.cms.control.events.InitializeContentTypesEvent;
	import org.broadleafcommerce.admin.cms.model.ContentModel;
	import org.broadleafcommerce.admin.cms.model.ContentModelLocator;
	import org.broadleafcommerce.admin.cms.model.dynamicForms.ContentItem;
	import org.broadleafcommerce.admin.cms.model.dynamicForms.CustomField;
	import org.broadleafcommerce.util.StringUtil;

	public class InitializeContentTypesCommand implements Command
	{
		public function InitializeContentTypesCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var ict:InitializeContentTypesEvent = event as InitializeContentTypesEvent;
			this.buildContentTypeFormMap(ict.xml);

		}

		private function buildContentTypeFormMap(xml:XML):void{
			var contentModel:ContentModel = ContentModelLocator.getInstance().contentModel;
			var contentTypes:ArrayCollection = contentModel.contentTypes;

			//loop through all the content items
			var contentItems:XMLList = xml.children();
			for(var i:int = 0; i < contentItems.length(); i++)
			{
				var contentItem:ContentItem = new ContentItem();
				contentItem.type = contentItems[i].@type;
				contentItem.isPage = StringUtil.convertStringToBoolean(contentItems[i].@isPage);
				var contentFields:ArrayCollection = new ArrayCollection();

				//loop through all the fields
				var fields:XMLList = contentItems[i].children();
				for(var j:int = 0; j < fields.length(); j++) {

					var customField:CustomField = new CustomField();

					//loop through the field data
					var fieldData:XMLList = fields[j].children();
					for (var k:int = 0; k < fieldData.length(); k++) {
						if(fieldData[k].name() == 'displayName'){
							customField.displayName = fieldData[k];
						} else if(fieldData[k].name() == 'description'){
							customField.description = fieldData[k];
						} else if(fieldData[k].name() == 'type'){
							customField.type = fieldData[k];
						} else if(fieldData[k].name() == 'tagName'){
							customField.tagName = fieldData[k];
						} else if(fieldData[k].name() == 'required'){
							customField.required = StringUtil.convertStringToBoolean(fieldData[k]);
						} else if(fieldData[k].name() == 'default'){
							customField.defaultValue = fieldData[k];
						}
					}

					contentFields.addItem(customField);
				}

				contentItem.contentFields = contentFields;
				contentTypes.addItem(contentItem);
			}

			populateCreateContentComboBox();
		}

		private function populateCreateContentComboBox():void{
			var contentModel:ContentModel = ContentModelLocator.getInstance().contentModel;
			var contentTypes:ArrayCollection = new ArrayCollection();

			for each (var contentItem:ContentItem in contentModel.contentTypes) {
				var contentType:Object = new Object();
				contentType.label = contentItem.type;
				contentType.data = contentItem.type;

				contentTypes.addItem(contentType);
			}

			contentModel.contentTypeComboBoxOptions = contentTypes;
		}
	}
}