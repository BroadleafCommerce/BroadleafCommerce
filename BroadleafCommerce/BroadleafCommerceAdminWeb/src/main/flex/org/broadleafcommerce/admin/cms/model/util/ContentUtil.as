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
package org.broadleafcommerce.admin.cms.model.util
{
	import org.broadleafcommerce.admin.cms.model.ContentModel;
	import org.broadleafcommerce.admin.cms.model.ContentModelLocator;
	import org.broadleafcommerce.admin.cms.model.dynamicForms.ContentItem;

	public class ContentUtil
	{
		public function ContentUtil()
		{
		}

		public static function isContentTypePage(contentType:String):Boolean {
			var contentModel:ContentModel = ContentModelLocator.getInstance().contentModel;
			for each (var contentItem:ContentItem in contentModel.contentTypes){
				if (contentItem.type.toLowerCase() == contentType.toLowerCase()){
					if (contentItem.isPage){
						return true;
					}
				}
			}
			return false;
		}

		public static function findFullUrl(id:Number):String {
			var contentModel:ContentModel = ContentModelLocator.getInstance().contentModel;
			for each (var pageInfo:Object in contentModel.parentUrlList){
				if(pageInfo.id == id){
					return String(pageInfo.fullUrl);
				}
			}

			return "/";
		}
	}
}