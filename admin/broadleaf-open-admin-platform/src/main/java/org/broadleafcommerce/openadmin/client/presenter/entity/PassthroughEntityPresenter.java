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

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSequenceSetupManager;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.Display;

/**
 * For views that do not require dynamic entity interaction, this
 * presenter can be used or extended to allow for
 * @author bpolster
 *
 */
public class PassthroughEntityPresenter implements EntityPresenter, Instantiable {

    private Boolean loaded = false;
    private DataSource ds;
    private Display display;
    protected PresenterSequenceSetupManager presenterSequenceSetupManager = new PresenterSequenceSetupManager(this);

    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("passthroughEntityPresenter", null, new AsyncCallbackAdapter() {
            public void onSetupSuccess(DataSource top) {
                getDisplay().build(top);
            }
        }));
    }

    @Override
    public void postSetup(Canvas container) {
        BLCMain.ISNEW = false;
        if (containsDisplay(container)) {
            display.show();
        } else {
            container.addChild(display.asCanvas());
            loaded = true;
        }
        if (BLCMain.MODAL_PROGRESS.isActive()) {
            BLCMain.MODAL_PROGRESS.stopProgress();
        }
        if (BLCMain.SPLASH_PROGRESS.isActive()) {
            BLCMain.SPLASH_PROGRESS.stopProgress();
        }
    }

    protected Boolean containsDisplay(Canvas container) {
        return container.contains(display.asCanvas());
    }

    @Override
    public HandlerManager getEventBus() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setEventBus(HandlerManager eventBus) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Display getDisplay() {
        return display;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setDisplay(Display display) {
        this.display = display;
    }

    @Override
    public PresenterSequenceSetupManager getPresenterSequenceSetupManager() {
       return presenterSequenceSetupManager;
    }

    @Override
    public Boolean getLoaded() {
        return loaded;
    }

    public DataSource getDs() {
        return ds;
    }

    public void setDs(DataSource ds) {
        this.ds = ds;
    }
}
