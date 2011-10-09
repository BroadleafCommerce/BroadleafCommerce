package org.broadleafcommerce.openadmin.server.domain;

import java.io.Serializable;
import java.util.List;

public interface SandBox extends Serializable {

	public Long getId();

	public void setId(Long id);

    /**
	 * The name of the sandbox.
	 * Certain sandbox names are reserved in the system.    User created
	 * sandboxes cannot start with "", "approve_", or "deploy_".
	 *
	 * @return String sandbox name
	 */
	public String getName();

	public void setName(String name);

	public List<SandBoxItem> getSandBoxItems();

	public void setSandBoxItems(List<SandBoxItem> sandBoxItems);

    public SandBoxType getSandBoxType();

    public void setSandBoxType(SandBoxType sandBoxType);

    public Site getSite();

    public void setSite(Site site);

    public Long getAuthor();

	public void setAuthor(Long author);
}


