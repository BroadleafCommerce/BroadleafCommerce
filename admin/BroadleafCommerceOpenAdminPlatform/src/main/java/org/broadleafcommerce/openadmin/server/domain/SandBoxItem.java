package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.server.service.type.ChangeType;

import java.io.Serializable;

public interface SandBoxItem extends Serializable {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Entity getEntity();

	public abstract void setEntity(Entity entity);

	public abstract PersistencePerspective getPersistencePerspective();

	public abstract void setPersistencePerspective(PersistencePerspective persistencePerspective);

	public SandBox getSandBox();

	public void setSandBox(SandBox sandBox);

    public String getCeilingEntityFullyQualifiedClassname();

    public void setCeilingEntityFullyQualifiedClassname(String ceilingEntityFullyQualifiedClassname);

    public String getCustomCriteria();

    public void setCustomCriteria(String customCriteria);

    public ChangeType getChangeType();

    public void setChangeType(ChangeType changeType);

    public Long getTemporaryId();

    public void setTemporaryId(Long temporaryId);
}