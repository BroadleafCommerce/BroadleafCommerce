package org.broadleafcommerce.openadmin.server.domain;

import java.io.Serializable;

import org.broadleafcommerce.openadmin.client.dto.OperationType;

public interface OperationTypes extends Serializable {

	public abstract OperationType getRemoveType();

	public abstract void setRemoveType(OperationType removeType);

	public abstract OperationType getAddType();

	public abstract void setAddType(OperationType addType);

	public abstract OperationType getUpdateType();

	public abstract void setUpdateType(OperationType updateType);

	public abstract OperationType getFetchType();

	public abstract void setFetchType(OperationType fetchTyper);

	public abstract OperationType getInspectType();

	public abstract void setInspectType(OperationType inspectType);

	/**
	 * @return the id
	 */
	public abstract Long getId();

	/**
	 * @param id the id to set
	 */
	public abstract void setId(Long id);

}