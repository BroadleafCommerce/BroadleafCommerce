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

package org.broadleafcommerce.openadmin.client.setup;

import com.smartgwt.client.widgets.Canvas;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.BatchManager;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.BatchSuccessHandler;
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
    private List<PresenterSetupItem> supplementalItems = new ArrayList<PresenterSetupItem>();
	private Iterator<PresenterSetupItem> itemsIterator = null;
    private Iterator<PresenterSetupItem> supplementalItemsIterator = null;
	private Canvas canvas;
	private EntityPresenter presenter;
    private Map<String, DynamicEntityDataSource> dataSourceLibrary = new HashMap<String, DynamicEntityDataSource>();
    private boolean stage1 = false;
    private boolean stage2 = false;
	
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
		addOrReplaceItem(item, null);
	}
	
	public void addOrReplaceItem(PresenterSetupItem item, Integer destinationPos) {
        int pos = -1;
        if (itemsIterator == null && !stage1) {
            if (items.contains(item)) {
                pos = items.indexOf(item);
                items.remove(pos);
            }
        } else {
            if (supplementalItems.contains(item) && !stage2) {
                pos = supplementalItems.indexOf(item);
                supplementalItems.remove(pos);
            }
        }
        if (destinationPos != null) {
            pos = destinationPos;
        }
        item.getAdapter().registerDataSourceSetupManager(this);
        if (itemsIterator == null) {
            if (pos >= 0) {
                items.add(pos, item);
            } else {
                items.add(item);
            }
        } else {
            if (!stage2) {
                if (pos >= 0) {
                    supplementalItems.add(pos, item);
                } else {
                    supplementalItems.add(item);
                }
            } else {
                /*
                This must be a late stage addition. This can happen when a AdminPresentationToOneLookup is discovered
                inside an entity managed by an annotated advanced collection, such as AdminPresentationCollection. In these
                cases, we cannot batch handle the call and must one-off handle the datasource
                 */
                item.invoke();
            }
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
            stage1 = true;
            itemsIterator = items.iterator();
            next();
        }
	}

    protected void launchSupplemental() {
        if (!presenter.getLoaded()) {
            supplementalItemsIterator = supplementalItems.iterator();
            next();
        }
    }

	protected void next() {
		if (itemsIterator.hasNext()) {
			itemsIterator.next().invoke();
		} else if (supplementalItemsIterator != null && supplementalItemsIterator.hasNext()) {
            supplementalItemsIterator.next().invoke();
        } else {
            BatchManager batchManager = BatchManager.getInstance();
            if (!batchManager.getBatchPackages().isEmpty()) {
                batchManager.executeBatchRPC(new BatchSuccessHandler() {
                    @Override
                    public void onSuccess() {
                        if (supplementalItemsIterator == null) {
                            stage2 = true;
                            supplementalItemsIterator = supplementalItems.iterator();
                        }
                        if (supplementalItemsIterator.hasNext()) {
                            next();
                        } else {
                            presenter.postSetup(canvas);
                        }
                    }
                });
            } else {
			    presenter.postSetup(canvas);
            }
		}
	}

    protected void addDataSource(DynamicEntityDataSource dataSource) {
        dataSourceLibrary.put(dataSource.getDataURL(), dataSource);
    }

    public DynamicEntityDataSource getDataSource(String dataURL) {
        return dataSourceLibrary.get(dataURL);
    }

    public EntityPresenter getPresenter() {
        return presenter;
    }
}
