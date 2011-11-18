package org.broadleafcommerce.profile.core.service.handler;

import org.broadleafcommerce.openadmin.server.security.util.PasswordReset;
import org.broadleafcommerce.profile.core.domain.Customer;

public interface PasswordUpdatedHandler {

	public void passwordChanged(PasswordReset passwordReset, Customer customer, String newPassword);

}