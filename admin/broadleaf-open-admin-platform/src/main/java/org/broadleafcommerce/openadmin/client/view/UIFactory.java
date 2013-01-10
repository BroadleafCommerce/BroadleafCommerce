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

package org.broadleafcommerce.openadmin.client.view;

import org.broadleafcommerce.openadmin.client.reflection.AsyncClient;
import org.broadleafcommerce.openadmin.client.reflection.ModuleFactory;

import java.util.HashMap;
import java.util.Stack;

/**
 * 
 * @author jfischer
 *
 */
public class UIFactory extends HashMap<String, Display> {

    private static final long serialVersionUID = 1L;
    
    private Stack<Display> currentView = new Stack<Display>();
    private Stack<String> keyStack = new Stack<String>();

    public void getView(String value, final AsyncClient asyncClient) {
        getView(value, true, true, asyncClient);
    }
    
    public void getView(final String value, boolean clearCurrentView, final boolean storeView, final AsyncClient asyncClient) {
        if (clearCurrentView) {
            clearCurrentView();
        }
        Display view;
        if (!containsKey(value)) {
            ModuleFactory.getInstance().createAsync(value, new AsyncClient() {
                @Override
                public void onSuccess(Object instance) {
                    if (storeView) {
                        put(value, (Display) instance);
                    }
                    currentView.push((Display) instance);
                    keyStack.push(value);
                    asyncClient.onSuccess(instance);
                }

                @Override
                public void onUnavailable() {
                    asyncClient.onUnavailable();
                }
            });

        } else {
            view = get(value);
            currentView.push(view);
            keyStack.push(value);
            asyncClient.onSuccess(view);
        }
    }
    
    public void getPresenter(String value, AsyncClient asyncClient) {
        ModuleFactory.getInstance().createAsync(value, asyncClient);
    }
    
    public void clearCurrentView() {
        for (Display display : currentView) {
            display.destroy();
        }
        currentView.clear();
        keyStack.clear();
    }

    public boolean equalsCurrentView(String key) {
        return !(keyStack.isEmpty() || !keyStack.peek().equals(key));
    }

}
