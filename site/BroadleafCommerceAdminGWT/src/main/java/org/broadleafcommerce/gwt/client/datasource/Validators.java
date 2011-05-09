package org.broadleafcommerce.gwt.client.datasource;

import com.smartgwt.client.widgets.form.validator.RegExpValidator;

public class Validators {
	
	public static final RegExpValidator EMAIL = new RegExpValidator("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");  
	{
		EMAIL.setErrorMessage("Invalid email address");  
	}

	public static final RegExpValidator USCURRENCY = new RegExpValidator("^(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$");  
	{
		USCURRENCY.setErrorMessage("Invalid currency amount");
	}
}
