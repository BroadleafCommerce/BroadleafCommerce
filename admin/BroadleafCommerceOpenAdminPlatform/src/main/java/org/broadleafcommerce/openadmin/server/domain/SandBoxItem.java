package org.broadleafcommerce.openadmin.server.domain;

import java.io.Serializable;

public interface SandBoxItem extends Serializable {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Entity getEntity();

	public abstract void setEntity(Entity entity);

	public abstract PersistencePerspective getPersistencePerspective();

	public abstract void setPersistencePerspective(PersistencePerspective persistencePerspective);

}