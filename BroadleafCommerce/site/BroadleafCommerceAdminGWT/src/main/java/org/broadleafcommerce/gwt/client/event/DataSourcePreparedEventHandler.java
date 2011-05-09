package org.broadleafcommerce.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DataSourcePreparedEventHandler extends EventHandler {
  void onDataSourcePrepared(DataSourcePreparedEvent event);
}
