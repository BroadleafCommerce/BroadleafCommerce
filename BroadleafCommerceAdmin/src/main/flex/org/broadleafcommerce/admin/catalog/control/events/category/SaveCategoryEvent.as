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
package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	
	public class SaveCategoryEvent extends CairngormEvent{
		public static const EVENT_SAVE_CATALOG_CATEGORY:String = "event_save_catalog_category";
		public var category:Category;
		public var nextEvent:CairngormEvent;		
		public function SaveCategoryEvent(category:Category, nextEvent:CairngormEvent=null){
			super(EVENT_SAVE_CATALOG_CATEGORY);
			this.category = category;
			this.nextEvent = nextEvent;
		}
	}
}