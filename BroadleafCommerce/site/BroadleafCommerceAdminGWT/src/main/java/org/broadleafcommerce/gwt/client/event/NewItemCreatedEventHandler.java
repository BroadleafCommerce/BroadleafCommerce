package org.broadleafcommerce.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface NewItemCreatedEventHandler extends EventHandler {
  void onNewItemCreated(NewItemCreatedEvent event);
}
