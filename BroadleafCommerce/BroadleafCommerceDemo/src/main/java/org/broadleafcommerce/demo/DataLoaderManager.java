package org.broadleafcommerce.demo;

public class DataLoaderManager {

	private DataLoader managed;

	public DataLoader getManaged() {
		return managed;
	}

	public void setManaged(DataLoader managed) {
		this.managed = managed;
		managed.init();
	}
	
}
