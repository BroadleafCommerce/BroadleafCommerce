/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.openadmin.client.view.dynamic.dialog;

/**
 * Created by jfischer
 */
public class JavaScriptMethodHelper {

    private static int requestCounter = 0;

    public static String registerCallbackFunction(JavaScriptMethodCallback callback) {
        String callbackName = "callback" + (requestCounter++);
        createCallbackFunction(callback, callbackName);
        return callbackName;
    }

    private native static void createCallbackFunction(JavaScriptMethodCallback obj, String callbackName )/*-{
        tmpcallback = function( j ){
            obj.@org.broadleafcommerce.openadmin.client.view.dynamic.dialog.JavaScriptMethodCallback::execute(Ljava/lang/String;)( j );
        };
        $wnd[callbackName]=tmpcallback;
    }-*/;

}
