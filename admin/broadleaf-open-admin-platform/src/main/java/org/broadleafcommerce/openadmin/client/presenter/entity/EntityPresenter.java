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

package org.broadleafcommerce.openadmin.client.presenter.entity;

import org.broadleafcommerce.openadmin.client.setup.PresenterSequenceSetupManager;
import org.broadleafcommerce.openadmin.client.view.Display;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.Canvas;

/**
 * 
 * @author jfischer
 *
 */
public interface EntityPresenter {

    public void setup();
    
    public void postSetup(Canvas container);

    public HandlerManager getEventBus();

    public void setEventBus(HandlerManager eventBus);

    public Display getDisplay();

    public void setDisplay(Display display);

    public PresenterSequenceSetupManager getPresenterSequenceSetupManager();
    
    public Boolean getLoaded();
    
}
