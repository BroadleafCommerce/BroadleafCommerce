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
	import com.adobe.cairngorm.business.ServiceLocator;
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	
	import mx.collections.ArrayCollection;
	import mx.modules.IModuleInfo;
	import mx.rpc.remoting.mxml.RemoteObject;
	
	import org.broadleafcommerce.admin.core.control.events.GetAdminConfigEvent;
	import org.broadleafcommerce.admin.core.control.events.codetype.AdminFindAllCodeTypesEvent;
	

	public class InitializeApplicationCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();

		private var moduleInfo:IModuleInfo;

		public function InitializeApplicationCommand()
		{			
			eventChain.addItem(new GetAdminConfigEvent());
			eventChain.addItem(new AdminFindAllCodeTypesEvent());

		}

		public function execute(event:CairngormEvent):void
		{
			var myService:RemoteObject = mx.rpc.remoting.mxml.RemoteObject(ServiceLocator.getInstance().getRemoteObject("blcAdminService"));
//			myService.endpoint = AppModelLocator.getInstance().configModel.urlPrefix+"/messagebroker/amf";			
			for each(event in eventChain){
				CairngormEventDispatcher.getInstance().dispatchEvent(event);
			}
			
		}
		
	}
}