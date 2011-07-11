package org.broadleafcommerce.openadmin.server.domain;

public interface JoinStructure extends PersistencePerspectiveItem {

	public abstract String getName();

	public abstract void setName(String manyToField);

	public abstract String getLinkedObjectPath();

	public abstract void setLinkedObjectPath(String linkedPropertyPath);

	public abstract String getTargetObjectPath();

	public abstract void setTargetObjectPath(String targetObjectPath);

	public abstract String getJoinStructureEntityClassname();

	public abstract void setJoinStructureEntityClassname(
			String joinStructureEntityClassname);

	public abstract String getSortField();

	public abstract void setSortField(String sortField);

	public abstract Boolean getSortAscending();

	public abstract void setSortAscending(Boolean sortAscending);

	public abstract String getLinkedIdProperty();

	public abstract void setLinkedIdProperty(String linkedIdProperty);

	public abstract String getTargetIdProperty();

	public abstract void setTargetIdProperty(String targetIdProperty);

	public abstract Boolean getInverse();

	public abstract void setInverse(Boolean inverse);

}