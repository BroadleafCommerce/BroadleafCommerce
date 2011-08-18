package org.broadleafcommerce.openadmin.client.dto;

import java.io.Serializable;

public class PersistencePackage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String ceilingEntityFullyQualifiedClassname;
	protected PersistencePerspective persistencePerspective;
	protected SandBoxInfo sandBoxInfo;
	protected String[] customCriteria;
	protected Entity entity;
	
	public PersistencePackage(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, SandBoxInfo sandBoxInfo, String[] customCriteria) {
		this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
		this.persistencePerspective = persistencePerspective;
		this.entity = entity;
		this.sandBoxInfo = sandBoxInfo;
		this.customCriteria = customCriteria;
	}
	
	public PersistencePackage() {
		//do nothing
	}
	
	public String getCeilingEntityFullyQualifiedClassname() {
		return ceilingEntityFullyQualifiedClassname;
	}
	
	public void setCeilingEntityFullyQualifiedClassname(
			String ceilingEntityFullyQualifiedClassname) {
		this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
	}
	
	public PersistencePerspective getPersistencePerspective() {
		return persistencePerspective;
	}
	
	public void setPersistencePerspective(
			PersistencePerspective persistencePerspective) {
		this.persistencePerspective = persistencePerspective;
	}
	
	public SandBoxInfo getSandBoxInfo() {
		return sandBoxInfo;
	}
	
	public void setSandBoxInfo(SandBoxInfo sandBoxInfo) {
		this.sandBoxInfo = sandBoxInfo;
	}
	
	public String[] getCustomCriteria() {
		return customCriteria;
	}
	
	public void setCustomCriteria(String[] customCriteria) {
		this.customCriteria = customCriteria;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
}
