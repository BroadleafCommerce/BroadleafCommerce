package org.broadleafcommerce.gwt.client.datasource;

import com.smartgwt.client.widgets.form.validator.RegExpValidator;

public class Validators {
	
	public static final RegExpValidator EMAIL = new RegExpValidator();  
	{
		EMAIL.setErrorMessage("Invalid email address");  
		EMAIL.setExpression("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");
	}

	public static final RegExpValidator USCURRENCY = new RegExpValidator();  
	{
		USCURRENCY.setErrorMessage("Invalid currency amount");  
		USCURRENCY.setExpression("^(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$");
	}
}
