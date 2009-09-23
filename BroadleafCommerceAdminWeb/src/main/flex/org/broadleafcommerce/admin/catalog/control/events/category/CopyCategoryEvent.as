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

	public class CopyCategoryEvent extends CairngormEvent
	{
		public static const EVENT_COPY_CATEGORY:String = "copy_category_event";
		
		public var droppedIndex:int;
		public var movedCategory:Category;
		public var newParent:Category;
		
		public function CopyCategoryEvent(droppedIndex:int, movedCategory:Category, newParent:Category)
		{
			super(EVENT_COPY_CATEGORY);
			this.droppedIndex = droppedIndex;
			this.movedCategory = movedCategory;
			this.newParent = newParent;
		}
		
	}
}