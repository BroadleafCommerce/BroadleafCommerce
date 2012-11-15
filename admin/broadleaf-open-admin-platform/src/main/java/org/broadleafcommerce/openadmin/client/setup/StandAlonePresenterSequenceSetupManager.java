/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.setup;

import com.smartgwt.client.widgets.Canvas;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.EntityPresenter;

/**
 * @author Jeff Fischer
 */
public class StandAlonePresenterSequenceSetupManager {

    private final PresenterSequenceSetupManager delegate;

    public StandAlonePresenterSequenceSetupManager(PresenterSequenceSetupManager delegate) {
        this.delegate = delegate;
    }

    public void addDataSource(DynamicEntityDataSource dataSource) {
        delegate.addDataSource(dataSource);
    }

    public void launch() {
        delegate.launch();
    }

    public Canvas getCanvas() {
        return delegate.getCanvas();
    }

    public EntityPresenter getPresenter() {
        return delegate.getPresenter();
    }

    public void setCanvas(Canvas canvas) {
        delegate.setCanvas(canvas);
    }

    public void next() {
        delegate.next();
    }

    public void addOrReplaceItem(PresenterSetupItem item, Integer destinationPos) {
        delegate.addOrReplaceItem(item, destinationPos);
    }

    public void addOrReplaceItem(PresenterSetupItem item) {
        delegate.addOrReplaceItem(item);
    }

    public void launchSupplemental() {
        delegate.launchSupplemental();
    }

    public PresenterSetupItem getItem(String key) {
        return delegate.getItem(key);
    }

    public DynamicEntityDataSource getDataSource(String dataURL) {
        return delegate.getDataSource(dataURL);
    }

    public void moveItem(PresenterSetupItem item, int pos) {
        delegate.moveItem(item, pos);
    }
}
