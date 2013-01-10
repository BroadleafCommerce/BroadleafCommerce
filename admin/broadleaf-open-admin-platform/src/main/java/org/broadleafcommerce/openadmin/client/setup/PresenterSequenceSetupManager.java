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

package org.broadleafcommerce.openadmin.client.setup;

import com.smartgwt.client.widgets.Canvas;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.EntityPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class PresenterSequenceSetupManager {
    
    private List<PresenterSetupItem> items = new ArrayList<PresenterSetupItem>();
    private Iterator<PresenterSetupItem> itemsIterator;
    private Canvas canvas;
    private EntityPresenter presenter;
    private Map<String, DynamicEntityDataSource> dataSourceLibrary = new HashMap<String, DynamicEntityDataSource>();
    
    public PresenterSequenceSetupManager(EntityPresenter presenter) {
        this.presenter = presenter;
    }
    
    public Canvas getCanvas() {
        return canvas;
    }

    protected void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
    
    public void addOrReplaceItem(PresenterSetupItem item) {
        int pos = -1;
        if (items.contains(item)) {
            pos = items.indexOf(item);
            items.remove(pos);
        }
        item.getAdapter().registerDataSourceSetupManager(this);
        if (pos >= 0) {
            items.add(pos, item);
        } else {
            items.add(item);
        }
    }
    
    public void addOrReplaceItem(PresenterSetupItem item, int destinationPos) {
        int pos = -1;
        if (items.contains(item)) {
            pos = items.indexOf(item);
            items.remove(pos);
        }
        item.getAdapter().registerDataSourceSetupManager(this);
        if (destinationPos >= 0) {
            items.add(pos, item);
        } else {
            items.add(item);
        }
    }
    
    public void moveItem(PresenterSetupItem item, int pos) {
        Boolean removed = items.remove(item);
        if (!removed) {
            throw new RuntimeException("Unable to find the passed in item in the collection of setup items");
        }
        items.add(pos, item);
    }
    
    public PresenterSetupItem getItem(String key) {
        int pos = items.indexOf(new PresenterSetupItem(key, null, null, null, null));
        if (pos >= 0) {
            return items.get(pos);
        }
        return null;
    }
    
    protected void launch() {
        if (!presenter.getLoaded()) {
            itemsIterator = items.iterator();
            next();
        }
    }

    protected void next() {
        if (itemsIterator.hasNext()) {
            itemsIterator.next().invoke();
        } else {
            presenter.postSetup(canvas);
        }
    }

    protected void addDataSource(DynamicEntityDataSource dataSource) {
        dataSourceLibrary.put(dataSource.getDataURL(), dataSource);
    }

    public DynamicEntityDataSource getDataSource(String dataURL) {
        return dataSourceLibrary.get(dataURL);
    }
}
