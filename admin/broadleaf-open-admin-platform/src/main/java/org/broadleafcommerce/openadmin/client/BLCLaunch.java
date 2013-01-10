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

package org.broadleafcommerce.openadmin.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;

/**
 * 
 * @author jfischer
 *
 */
public class BLCLaunch implements EntryPoint {
    private static String PAGE_FRAGMENT = "pageKey=";
    private static String MODULE_FRAGMENT = "moduleKey=";


    public void onModuleLoad() {
        if (BLCMain.SPLASH_PROGRESS != null) {
            BLCMain.SPLASH_PROGRESS.startProgress();
        }

        String currentModulePage = History.getToken();
        BLCMain.drawCurrentState(getSelectedModule(currentModulePage), getSelectedPage(currentModulePage));
    }

    public static String getSelectedModule(String currentModulePage) {
        String moduleParam = null;
        if (currentModulePage != null) {
            int moduleStart = currentModulePage.indexOf(MODULE_FRAGMENT);
            int ampLocation = currentModulePage.indexOf("&");

            if (moduleStart >= 0) {
                moduleStart = moduleStart + MODULE_FRAGMENT.length();
                int moduleEnd = currentModulePage.length();
                if (ampLocation > 0 && ampLocation > moduleStart) {
                    moduleEnd = ampLocation;
                }

                moduleParam = currentModulePage.substring(moduleStart,moduleEnd);
            }
        }
        return moduleParam;
    }

    public static String getSelectedPage(String currentModulePage) {
        String pageParam = null;
        if (currentModulePage != null) {
            int pageStart = currentModulePage.indexOf(PAGE_FRAGMENT);

            if (pageStart >= 0) {
                pageParam = currentModulePage.substring(pageStart + PAGE_FRAGMENT.length());
            }
        }
        return pageParam;
    }

}
