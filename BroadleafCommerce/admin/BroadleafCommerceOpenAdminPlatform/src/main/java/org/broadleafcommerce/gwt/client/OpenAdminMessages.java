package org.broadleafcommerce.gwt.client;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;

@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
@DefaultLocale("en_US")
public interface OpenAdminMessages extends ConstantsWithLookup {

	public String contactingServerTitle();
	public String currentUser();
	public String logout();
	
}
