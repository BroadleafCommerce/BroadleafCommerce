package org.broadleafcommerce.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ProductSelectionChangedEventHandler extends EventHandler {
  void onChangeSelection(ProductSelectionChangedEvent event);
}
