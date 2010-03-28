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

	import flash.display.DisplayObject;

	import mx.containers.Canvas;
	import mx.containers.TabNavigator;
	import mx.containers.VBox;

	import org.broadleafcommerce.admin.cms.control.events.AddContentTabEvent;
	import org.broadleafcommerce.admin.cms.model.ContentModelLocator;

	public class AddContentTabCommand implements Command
	{
		public function AddContentTabCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var ae:AddContentTabEvent = event as AddContentTabEvent;
			this.addTab(ae.lbl, ae.displayObject, ae.icon);
		}

		private function addTab(lbl:String, displayObject:DisplayObject = null, icon:Class=null):void {
			var navigator:TabNavigator = ContentModelLocator.getInstance().contentModel.contentTabNavigator;

			if(lbl=="") lbl = "(Untitled)";
			var curNum:Number = navigator.numChildren + 1;
			var child:VBox = new VBox();
			child.label = lbl;
			child.name = curNum.toString();

			if(icon) {
				child.icon = icon;
			}

			if(displayObject != null){
				child.addChild(displayObject);
			}

			navigator.addChild(child);
			//invalidateNav(navigator);
			navigator.selectedIndex = navigator.numChildren - 1;

		}

		/* From the FlexLib SuperTabNavigator Sample mxml file:
		 * The following function is a bit of a hack to try to get the
		 * tab navigator to refresh the display and resize all the tabs. When
		 * you change stuff like tabWidth (which is a style) then the tab
		 * navigator has a hard time re-laying out the tabs. Adding and
		 * removing a child can sometimes trigger it to re-layout the tabs.
		 * I don't know, but just don't change tabWdith or horizontalGap or whatever
		 * else at runtime, OK? Pick some values and stick with them.
		 */
//		private function invalidateNav(nav:SuperTabNavigator):void {
//			var child:Canvas = new Canvas();
//			nav.addChild(child);
//			nav.removeChild(child);
//			nav.invalidateDisplayList();
//		}
	}
}