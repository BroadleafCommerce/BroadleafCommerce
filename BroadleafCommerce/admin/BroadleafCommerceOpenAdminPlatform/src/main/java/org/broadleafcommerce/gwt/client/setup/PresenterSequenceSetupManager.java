package org.broadleafcommerce.gwt.client.setup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.presenter.entity.EntityPresenter;


import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.Canvas;

public class PresenterSequenceSetupManager {
	
	private List<PresenterSetupItem> items = new ArrayList<PresenterSetupItem>();
	private Iterator<PresenterSetupItem> itemsIterator;
	private Canvas canvas;
	private EntityPresenter presenter;
	
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
		BLCMain.MODAL_PROGRESS.startProgress(new Timer() {
			public void run() {
				if (!presenter.getLoaded()) {
					itemsIterator = items.iterator();
					next();
				}
			}
		});
	}

	protected void next() {
		if (itemsIterator.hasNext()) {
			itemsIterator.next().invoke();
		} else {
			presenter.postSetup(canvas);
		}
	}
}
