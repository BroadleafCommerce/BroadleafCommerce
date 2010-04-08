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
		//TODO change back to 8080
		public static const SERVER_PORT:String 				= "8080";
		public static const SERVER_CONTEXT:String 			= "broadleafadmin";
		private static const SERVER_MESSAGE_BROKER:String 	= "messagebroker/amf";
		public static const SERVER_IMAGES:String			= "images";
		public static const SERVER_CONFIG:String 			= "modules/modules-config.xml";
		private static const SERVER_FILE_UPLOAD:String 		= "spring/upload";
		private static const SERVER_FILE_LIST:String 		= "spring/ls";
		private static const SERVER_FILE_MKDIR:String 		= "spring/mkDir";
		private static const SERVER_FILE_REMOVE:String 		= "spring/rm";

//		public static const URL_SERVER:String = SERVER_PROTOCOL+"://"+SERVER_HOST+":"+SERVER_PORT+"/"+SERVER_CONTEXT;
//		public static const URL_CONFIG:String = URL_SERVER+"/"+SERVER_CONFIG;
//		public static const URL_ENDPOINT:String = URL_SERVER+"/"+SERVER_MESSAGE_BROKER;
//		public static const URL_FILE_UPLOAD:String = URL_SERVER+"/"+SERVER_FILE_UPLOAD;
//		public static const URL_FILE_LIST:String = URL_SERVER+"/"+SERVER_FILE_LIST;
//		public static const URL_FILE_MKDIR:String = URL_SERVER+"/"+SERVER_FILE_MKDIR;
//		public static const URL_FILE_REMOVE:String = URL_SERVER+"/"+SERVER_FILE_REMOVE;

		public static const CMS_ROOT_DIR:String 			= "\\content\\admin";
//		public static const CMS_SERVER_PROTOCOL:String 		= "http";
//		public static const CMS_SERVER_HOST:String 			= "localhost";
//		public static const CMS_SERVER_PORT:String 			= "8080";
//		public static const CMS_SERVER_CONTEXT:String 		= "";
//		public static const CMS_PREVIEW_URL:String = CMS_SERVER_PROTOCOL+"://"+CMS_SERVER_HOST+":"+CMS_SERVER_PORT+"/"+CMS_SERVER_CONTEXT;

		private static const defaultUrl:String = "";

		public function ConfigModel()
		{
		}

		public function get urlServer():String{
			var url:String = Application.application.loaderInfo.url;
			return url.substr(0,url.lastIndexOf("/"))+"/";

		} 
		 
		public function get urlConfig():String {
			return urlServer+SERVER_CONFIG; 
		} 
		
//		public static const URL_FILE_LIST:String = URL_SERVER+"/"+SERVER_FILE_LIST;
		public function get urlFileList():String {  
			return urlServer+SERVER_FILE_LIST;   
		}

//		public static const URL_FILE_UPLOAD:String = URL_SERVER+"/"+SERVER_FILE_UPLOAD;
		public function get urlFileUpload():String {
			return urlServer+SERVER_FILE_UPLOAD;
		}

//		public static const URL_FILE_MKDIR:String = URL_SERVER+"/"+SERVER_FILE_MKDIR;
		public function get urlFileMkdir():String {
			return urlServer+SERVER_FILE_MKDIR;
		}

//		public static const URL_FILE_REMOVE:String = URL_SERVER+"/"+SERVER_FILE_REMOVE;
		public function get urlFileRemove():String {
			return urlServer+SERVER_FILE_REMOVE;
		}
		

		public var moduleConfigs:ArrayCollection = new ArrayCollection();

		public var codeTypes:ArrayCollection = new ArrayCollection();

		[Bindable]
		public var currentCodeTypes:ArrayCollection = new ArrayCollection();
	}
}