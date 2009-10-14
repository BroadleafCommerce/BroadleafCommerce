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
package org.broadleafcommerce.admin.core.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.universalmind.cairngorm.commands.Command;
	
	import flash.events.Event;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	
	import mx.collections.ArrayCollection;
	import mx.collections.Sort;
	import mx.collections.SortField;
	
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.vo.ModuleConfig;

	public class GetAdminConfigCommand extends Command
	{
		private	var myXML:XML = new XML();
		private var myLoader:URLLoader;
		
		public function GetAdminConfigCommand()
		{
		}

		override public function execute(event:CairngormEvent):void
		{
			super.execute(event);
			trace("DEBUG: GetAdminConfigCommand.execute()");
			var XML_URL:String = AppModelLocator.getInstance().configModel.urlPrefix+"modules/modules-config.xml";
			var myXMLURL:URLRequest = new URLRequest(XML_URL);
			myLoader = new URLLoader(myXMLURL);
			myLoader.addEventListener("complete", xmlLoaded);
			notifyCaller();			
		}
		
		private function xmlLoaded(event:Event):void{
			trace("DEBUG: GetAdminConfigCommand.xmlLoaded()");
			var modules:ArrayCollection = new ArrayCollection();
			myXML = XML(myLoader.data);
			for each (var module:XML in myXML.menuitem){
				var mc:ModuleConfig = new ModuleConfig();
				mc.name = module.@name;
				mc.label = module.@label;
				mc.swf = module.@swf;
				mc.displayOrder = module.@displayOrder;
				mc.authenticatedRoles = module.@authRoles;
				modules.addItem(mc);
			}		
			var dataSortField:SortField = new SortField();
            dataSortField.name = "displayOrder";
            dataSortField.numeric = true;

            /* Create the Sort object and add the SortField object created earlier to the array of fields to sort on. */
            var numericDataSort:Sort = new Sort();
            numericDataSort.fields = [dataSortField];

			modules.sort = numericDataSort;
			modules.refresh();
			AppModelLocator.getInstance().configModel.moduleConfigs = modules;
		}
		
	}
}