package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.statement;

import com.smartgwt.client.types.OperatorId;

public class Expression {

	protected String field;
	protected OperatorId operator;
	protected Object value;
	
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
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
}
