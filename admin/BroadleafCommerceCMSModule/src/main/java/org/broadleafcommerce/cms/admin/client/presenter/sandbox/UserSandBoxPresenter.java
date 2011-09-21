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
package org.broadleafcommerce.cms.admin.client.presenter.sandbox;

import com.smartgwt.client.widgets.Canvas;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.presenter.entity.AbstractEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.PresenterSequenceSetupManager;
import org.broadleafcommerce.openadmin.client.view.Display;

/**
 * 
 * @author bpolster
 *
 */
public class UserSandBoxPresenter extends AbstractEntityPresenter implements Instantiable {

    protected Display display;
    protected Boolean loaded = false;
    protected PresenterSequenceSetupManager presenterSequenceSetupManager = new PresenterSequenceSetupManager(this);


    @Override
    public void setup() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void postSetup(Canvas container) {
        BLCMain.ISNEW = false;
		if (containsDisplay(container)) {
			display.show();
		} else {
			bind();
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

    public void bind() {
        // create handlers for revert / promote  / reject buttons

	}

    protected Boolean containsDisplay(Canvas container) {
		return container.contains(display.asCanvas());
	}

    @Override
    public Display getDisplay() {
        return display;
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
}
