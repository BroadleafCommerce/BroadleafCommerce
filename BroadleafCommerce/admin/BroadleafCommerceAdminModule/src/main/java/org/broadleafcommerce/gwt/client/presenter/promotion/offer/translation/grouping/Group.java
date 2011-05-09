package org.broadleafcommerce.gwt.client.presenter.promotion.offer.translation.grouping;

import java.util.ArrayList;
import java.util.List;

public class Group {
	
	private List<Phrase> phrases = new ArrayList<Phrase>();
	private Group subGroup;

	public List<Phrase> getPhrases() {
		return phrases;
	}

	public Group getSubGroup() {
		return subGroup;
	}
	
	public void setSubGroup(Group subGroup) {
		this.subGroup = subGroup;
	}
	
}
