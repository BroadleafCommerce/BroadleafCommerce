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
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.model.IModelLocator;


	public class AppModelLocator implements IModelLocator
	{
		private static var modelLocator:AppModelLocator;


		public static function getInstance():AppModelLocator
		{
			if(modelLocator == null)
				modelLocator = new AppModelLocator();

			return modelLocator;
		}

		public function AppModelLocator()
		{
			if(modelLocator != null)
				throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "BlcAdminModelLocator");
		}

		public var authModel:AuthenticationModel = new AuthenticationModel();

		public var configModel:ConfigModel = new ConfigModel();

		public var notificationModel:NotificationModel = new NotificationModel();

	}
}