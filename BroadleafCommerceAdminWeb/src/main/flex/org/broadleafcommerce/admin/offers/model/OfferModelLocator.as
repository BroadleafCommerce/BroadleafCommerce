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
package org.broadleafcommerce.admin.offers.model
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	
	public class OfferModelLocator
	{
		private static var modelLocator:OfferModelLocator;


		public static function getInstance():OfferModelLocator
		{
			if(modelLocator == null)
				modelLocator = new OfferModelLocator();
			
			return modelLocator;
		}
		
		public function OfferModelLocator()
		{
			if(modelLocator != null)
				throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "OfferModelLocator");				
		}
		
		public var offerModel:OfferModel = new OfferModel();

	}
}