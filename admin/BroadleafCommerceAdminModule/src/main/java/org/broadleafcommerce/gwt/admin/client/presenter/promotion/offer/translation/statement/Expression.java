package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.statement;

import com.smartgwt.client.types.OperatorId;

public class Expression {

	protected String field;
	protected OperatorId operator;
	protected String value;
	
	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public OperatorId getOperator() {
		return operator;
	}
	
	public void setOperator(OperatorId operator) {
		this.operator = operator;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
}
