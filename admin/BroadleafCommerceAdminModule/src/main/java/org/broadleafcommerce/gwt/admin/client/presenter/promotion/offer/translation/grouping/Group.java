package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.grouping;

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.types.OperatorId;

public class Group {
	
	private List<String> phrases = new ArrayList<String>();
	private List<Group> subGroups = new ArrayList<Group>();
	private OperatorId operatorType;
	private Boolean isTopGroup = false;

	public List<String> getPhrases() {
		return phrases;
	}

	public OperatorId getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(OperatorId operatorType) {
		this.operatorType = operatorType;
	}

	public List<Group> getSubGroups() {
		return subGroups;
	}

	public Boolean getIsTopGroup() {
		return isTopGroup;
	}

	public void setIsTopGroup(Boolean isTopGroup) {
		this.isTopGroup = isTopGroup;
	}
	
}
