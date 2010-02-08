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
	import flexlib.containers.SuperTabNavigator;

	import mx.collections.ArrayCollection;

	import org.broadleafcommerce.admin.cms.model.contentRoles.ContentRoles;

	[Bindable]
	public class ContentModel
	{
		public function ContentModel()
		{
		}
		public static const SERVICE_ID:String = "blContentService";

		public static const STATE_VIEW_CONTENT_HOME:String = "view_content_home";
		public static const STATE_VIEW_FILE_MANAGER:String = "view_file_manager";

		public static const LANGUAGE_CODES:ArrayCollection = new ArrayCollection(["EN"]);

		public var viewState:String = STATE_VIEW_CONTENT_HOME;

		public var contentRoles:ContentRoles = new ContentRoles();

		//reference to the main content tab navigator;
		public var contentTabNavigator:SuperTabNavigator;

		//TODO get rid of this
		public var currentSandbox:String = "";

		//This var is populated on application load;
		public var loggedInUserSandbox:String = "";

		//ArrayCollection <Content>
		public var contentAwaitingApproval:ArrayCollection = new ArrayCollection();

		//ArrayCollection <Content>
		//TODO get rid of this
		public var contentForSandbox:ArrayCollection = new ArrayCollection();

		//ArrayCollection <Content>
		//TODO get rid of this
		public var contentForBrowser:ArrayCollection = new ArrayCollection();

		//ArrayCollection <ContentItem>
		public var contentTypes:ArrayCollection = new ArrayCollection();

		//ArrayCollection <RenderItem>
		public var renderTypes:ArrayCollection = new ArrayCollection();

		//ArrayCollection <Object> for ComboBox
		public var contentTypeComboBoxOptions:ArrayCollection = new ArrayCollection();

		//ArrayCollection <Object> for ComboBox
		public var renderTemplateComboBoxOptions:ArrayCollection = new ArrayCollection();

	}
}