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
package org.broadleafcommerce.admin.catalog.vo.media
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.media.domain.MediaImpl")]	
	public class Media
	{
		public function Media():void{
			id = -1;
		}
		
		public var id:Number;
		public var name:String;
		public var url:String;
//		private var _label:String;
		public var label:String;
		
		
		public var key:String;
		
//		public function set label(newLabel:String):void{
//			_label = newLabel;
//		} 
//		
//		public function get label():String{
//			return _label;
//		}

	}
}