package org.broadleafcommerce.gwt.client.datasource.relations.operations;

import java.io.Serializable;

public class OperationTypes implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private OperationType fetchType = OperationType.ENTITY;
	private OperationType removeType = OperationType.ENTITY;
	private OperationType addType = OperationType.ENTITY;
	private OperationType updateType = OperationType.ENTITY;
	private OperationType inspectType = OperationType.ENTITY;
	private OperationType miscType = OperationType.ENTITY;
	
	public OperationTypes() {
		//do nothing
	}
	
	public OperationTypes(OperationType fetchType, OperationType removeType, OperationType addType, OperationType updateType, OperationType inspectType) {
		this.removeType = removeType;
		this.addType = addType;
		this.updateType = updateType;
		this.fetchType = fetchType;
		this.inspectType = inspectType;
	}
	
	public OperationType getRemoveType() {
		return removeType;
	}
	
	public void setRemoveType(OperationType removeType) {
		this.removeType = removeType;
	}
	
	public OperationType getAddType() {
		return addType;
	}
	
	public void setAddType(OperationType addType) {
		this.addType = addType;
	}
	
	public OperationType getUpdateType() {
		return updateType;
	}
	
	public void setUpdateType(OperationType updateType) {
		this.updateType = updateType;
	}

	public OperationType getFetchType() {
		return fetchType;
	}

	public void setFetchType(OperationType fetchTyper) {
		this.fetchType = fetchTyper;
	}

	public OperationType getInspectType() {
		return inspectType;
	}

	public void setInspectType(OperationType inspectType) {
		this.inspectType = inspectType;
	}

	public OperationType getMiscType() {
		return miscType;
	}

	public void setMiscType(OperationType miscType) {
		this.miscType = miscType;
	}
	
}
