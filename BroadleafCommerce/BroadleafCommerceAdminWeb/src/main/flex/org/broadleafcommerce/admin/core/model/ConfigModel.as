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
package org.broadleafcommerce.admin.core.model
{
	import mx.collections.ArrayCollection;
	import mx.core.Application;
	
	public class ConfigModel
	{
		public static const SERVER_PROTOCOL:String 			= "http";
		public static const SERVER_HOST:String 				= "localhost";
		public static const SERVER_PORT:String 				= "8080";
		public static const SERVER_CONTEXT:String 			= "broadleafadmin";
		private static const SERVER_MESSAGE_BROKER:String 	= "messagebroker/amf";
		public static const SERVER_IMAGES:String			= "/images";
		private static const SERVER_CONFIG:String 			= "modules/modules-config.xml";
		private static const SERVER_FILE_UPLOAD:String 		= "spring/upload";
		 
		public static const URL_SERVER:String = SERVER_PROTOCOL+"://"+SERVER_HOST+":"+SERVER_PORT+"/"+SERVER_CONTEXT;
		public static const URL_CONFIG:String = URL_SERVER+"/"+SERVER_CONFIG; 
		public static const URL_ENDPOINT:String = URL_SERVER+"/"+SERVER_MESSAGE_BROKER;
		public static const URL_FILE_UPLOAD:String = URL_SERVER+"/"+SERVER_FILE_UPLOAD;
		
		private static const defaultUrl:String = "";
		
		public function ConfigModel()
		{
		}

		public static function get URL_MODULE_SERVER():String{
			var url:String = Application.application.loaderInfo.url;
			return url.substr(0,url.lastIndexOf("/"))+"/";			
			
		}
		
		public var moduleConfigs:ArrayCollection = new ArrayCollection();

		public var codeTypes:ArrayCollection = new ArrayCollection();

		[Bindable]
		public var currentCodeTypes:ArrayCollection = new ArrayCollection();
	}	
}