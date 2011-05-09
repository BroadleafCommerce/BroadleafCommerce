package org.broadleafcommerce.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CategorySelectionChangedEventHandler extends EventHandler {
  void onChangeSelection(CategorySelectionChangedEvent event);
}
