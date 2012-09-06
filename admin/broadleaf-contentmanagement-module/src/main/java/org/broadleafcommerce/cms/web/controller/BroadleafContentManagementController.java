/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.web.controller;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafContentManagementController extends BroadleafAbstractController {

    protected static String pagesView = "/blcadmin/contentManagement/pages";
    protected static String assetsView = "/blcadmin/contentManagement/assets";
    protected static String structuredContentView = "/blcadmin/contentManagement/structuredContent";
    protected static String redirectUrlView  = "/blcadmin/contentManagement/redirectUrl";


    public String pages(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getPagesView();
    }

    public String assets(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getAssetsView();
    }

    public String structuredContent(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getStructuredContentView();
    }

    public String redirectUrl(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getRedirectUrlView();
    }

    public static String getPagesView() {
        return pagesView;
    }

    public static void setPagesView(String pagesView) {
        BroadleafContentManagementController.pagesView = pagesView;
    }

    public static String getAssetsView() {
        return assetsView;
    }

    public static void setAssetsView(String assetsView) {
        BroadleafContentManagementController.assetsView = assetsView;
    }

    public static String getStructuredContentView() {
        return structuredContentView;
    }

    public static void setStructuredContentView(String structuredContentView) {
        BroadleafContentManagementController.structuredContentView = structuredContentView;
    }

    public static String getRedirectUrlView() {
        return redirectUrlView;
    }

    public static void setRedirectUrlView(String redirectUrlView) {
        BroadleafContentManagementController.redirectUrlView = redirectUrlView;
    }
}
