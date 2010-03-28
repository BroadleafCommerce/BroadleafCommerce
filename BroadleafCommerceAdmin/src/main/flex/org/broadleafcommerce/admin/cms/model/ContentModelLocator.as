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
package org.broadleafcommerce.admin.cms.model
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.model.IModelLocator;

	import mx.collections.ArrayCollection;

	public class ContentModelLocator implements IModelLocator{

		private static var modelLocator:ContentModelLocator;

		public static function getInstance():ContentModelLocator{
			if(modelLocator == null){
				modelLocator = new ContentModelLocator();
			}
			return modelLocator;
		}

		public function ContentModelLocator(){
			if(modelLocator != null){
				throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "ContentModelLocator");
			}
		}

		[Bindable]
		public var contentModel:ContentModel = new ContentModel();


	}
}