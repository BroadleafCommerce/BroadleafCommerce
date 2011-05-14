package org.broadleafcommerce.gwt.client.setup;

import org.broadleafcommerce.gwt.client.datasource.DataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;

public class PresenterSetupItem {
	
	private String name;
	private OperationTypes operationTypes;
	private Object[] additionalItems;
	private AsyncCallbackAdapter adapter = null;
	private DataSourceFactory factory;
	
	public PresenterSetupItem(String name, DataSourceFactory factory, OperationTypes operationTypes, Object[] additionalItems, AsyncCallbackAdapter adapter) {
		this.name = name;
		this.factory = factory;
		this.operationTypes = operationTypes;
		this.additionalItems = additionalItems;
		this.adapter = adapter;
	}
	
	public AsyncCallbackAdapter getAdapter() {
		return adapter;
	}
	
	public void setAdapter(AsyncCallbackAdapter adapter) {
		this.adapter = adapter;
	}
	
	public DataSourceFactory getFactory() {
		return factory;
	}
	
	public void setFactory(DataSourceFactory factory) {
		this.factory = factory;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OperationTypes getOperationTypes() {
		return operationTypes;
	}

	public void setOperationTypes(OperationTypes operationTypes) {
		this.operationTypes = operationTypes;
	}

	public Object[] getAdditionalItems() {
		return additionalItems;
	}

	public void setAdditionalItems(Object[] additionalItems) {
		this.additionalItems = additionalItems;
	}

	protected void invoke() {
		factory.createDataSource(name, operationTypes, additionalItems, adapter);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PresenterSetupItem other = (PresenterSetupItem) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
