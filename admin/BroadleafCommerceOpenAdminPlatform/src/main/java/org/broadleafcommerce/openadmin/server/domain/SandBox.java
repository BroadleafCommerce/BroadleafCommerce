package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface SandBox extends Serializable {

	public abstract Long getId();

	public abstract void setId(Long id);

    /**
	 * The name of the sandbox.
	 * Certain sandbox names are reserved in the system.    User created
	 * sandboxes cannot start with "user_", "approve_", or "deploy_".
	 *
	 * @return String sandbox name
	 */
	public abstract String getName();

	public abstract void setName(String name);

	public abstract List<SandBoxItem> getSandBoxItems();

	public abstract void setSandBoxItems(List<SandBoxItem> sandBoxItems);

	public Long getAuthor();

	public void setAuthor(Long author);

    /**
	 * Supports who can access this sandbox.
	 *
	 * Sandboxes can be accessed by convention.
	 *
	 * For example:
	 *     Sandboxes with the "user_" prefix can only be modified by the corresponding user.
	 *     Sandboxes with the "approve_" prefix can only be viewed/modified by users with the
	 *     CONTENT_APPROVER role.
	 *     the sandbox.
	 *     Sandboxes with the "deploy_" prefix can only be viewed/deployed by users with the
	 *     CONTENT_DEPLOYER role.
	 *
	 * @return Set<AdminRole> Set of admin roles that can edit this sandbox.
	 */
	public Set<AdminRole> getAllowedRoles();

	public void setAllowedRoles(Set<AdminRole> allowedRoles);
}


