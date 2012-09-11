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

package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafUserManagementController extends BroadleafAbstractController {

    protected static String userView = "/blcadmin/userAdministration/userManagement";
    protected static String roleView = "/blcadmin/userAdministration/roleManagement";
    protected static String permissionView = "/blcadmin/userAdministration/permissionManagement";

    public String user(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getUserView();
    }

    public String role(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getRoleView();
    }

    public String permission(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getPermissionView();
    }

    public static String getUserView() {
        return userView;
    }

    public static void setUserView(String userView) {
        BroadleafUserManagementController.userView = userView;
    }

    public static String getRoleView() {
        return roleView;
    }

    public static void setRoleView(String roleView) {
        BroadleafUserManagementController.roleView = roleView;
    }

    public static String getPermissionView() {
        return permissionView;
    }

    public static void setPermissionView(String permissionView) {
        BroadleafUserManagementController.permissionView = permissionView;
    }
}
