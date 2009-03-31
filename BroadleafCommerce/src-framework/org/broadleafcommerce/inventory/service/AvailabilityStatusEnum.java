package org.broadleafcommerce.inventory.service;

public enum AvailabilityStatusEnum {
	AVAILABLE,
	BACKORDERED,
	OUT_OF_STOCK,
	UNKNOWN,
	NOT_AVAILABLE;

	public boolean isAvailable() {
		return this == AVAILABLE;
	}
}
