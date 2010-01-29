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

	import org.broadleafcommerce.admin.cms.control.events.InitializeRenderTypesEvent;
	import org.broadleafcommerce.admin.cms.model.ContentModel;
	import org.broadleafcommerce.admin.cms.model.ContentModelLocator;
	import org.broadleafcommerce.admin.cms.model.dynamicForms.CustomField;
	import org.broadleafcommerce.admin.cms.model.dynamicForms.RenderItem;
	import org.broadleafcommerce.util.StringUtil;

	public class InitializeRenderTypesCommand implements Command
	{
		public function InitializeRenderTypesCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var irt:InitializeRenderTypesEvent = event as InitializeRenderTypesEvent;
			this.buildRenderTypeFormMap(irt.xml);

		}

		private function buildRenderTypeFormMap(xml:XML):void{
			var contentModel:ContentModel = ContentModelLocator.getInstance().contentModel;
			var renderTypes:ArrayCollection = contentModel.renderTypes;

			//loop through all the render items
			var renderItems:XMLList = xml.children();
			for(var i:int = 0; i < renderItems.length(); i++)
			{
				var renderItem:RenderItem = new RenderItem();
				renderItem.type = renderItems[i].@type;

				//loop through all the data
				var renderData:XMLList = renderItems[i].children();
				for(var j:int = 0; j < renderData.length(); j++) {
					if (renderData[j].name() == 'name'){
						renderItem.name = renderData[j];
					} else if (renderData[j].name() == 'jspTemplate'){
						renderItem.jspTemplate = renderData[j];
					} else if (renderData[j].name() == 'renderFields'){
						var renderFieldsArray:ArrayCollection = new ArrayCollection();
						var renderFields:XMLList = renderData[j].children();
						//loop through the renderFields
						for(var k:int = 0; k<renderFields.length(); k++){
							var customField:CustomField = new CustomField();
							//loop through the field data
							var fieldData:XMLList = renderFields[k].children();
							for (var l:int = 0; l < fieldData.length(); l++) {
								if(fieldData[l].name() == 'displayName'){
									customField.displayName = fieldData[l];
								} else if(fieldData[l].name() == 'description'){
									customField.description = fieldData[l];
								} else if(fieldData[l].name() == 'type'){
									customField.type = fieldData[l];
								} else if(fieldData[l].name() == 'tagName'){
									customField.tagName = fieldData[l];
								} else if(fieldData[l].name() == 'required'){
									customField.required = StringUtil.convertStringToBoolean(fieldData[l]);
								} else if(fieldData[l].name() == 'default'){
									customField.defaultValue = fieldData[l];
								}
							}
							renderFieldsArray.addItem(customField);
						}
						renderItem.renderFields = renderFieldsArray;
					}
				}

				renderTypes.addItem(renderItem);
			}

			populateTemplateComboBox();
		}

		private function populateTemplateComboBox():void{
			var contentModel:ContentModel = ContentModelLocator.getInstance().contentModel;
			var renderTypes:ArrayCollection = new ArrayCollection();

			for each(var renderItem:RenderItem in contentModel.renderTypes) {
				var renderType:Object = new Object();
				renderType.label = renderItem.name;
				renderType.data = renderItem.name;
				renderType.type = renderItem.type;

				renderTypes.addItem(renderType);
			}

			contentModel.renderTemplateComboBoxOptions = renderTypes;
		}

	}
}