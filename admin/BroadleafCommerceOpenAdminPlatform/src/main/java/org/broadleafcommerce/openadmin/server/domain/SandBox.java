package org.broadleafcommerce.openadmin.server.domain;

import java.io.Serializable;
import java.util.List;

public interface SandBox extends Serializable {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract List<SandBoxItem> getSandBoxItems();

	public abstract void setSandBoxItems(List<SandBoxItem> sandBoxItems);

}