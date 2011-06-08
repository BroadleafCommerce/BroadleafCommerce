package org.broadleafcommerce.profile.email.service.message;

import java.util.HashMap;

public interface EmailServiceProducer {

    public void send(@SuppressWarnings("rawtypes") final HashMap props);
	
}
