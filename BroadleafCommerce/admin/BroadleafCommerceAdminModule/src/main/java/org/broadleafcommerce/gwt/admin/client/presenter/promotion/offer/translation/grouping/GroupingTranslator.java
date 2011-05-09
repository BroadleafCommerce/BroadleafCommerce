package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.grouping;

import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.IncompatibleMVELTranslationException;

public class GroupingTranslator {

	public static final String GROUPSTARTCHAR = "(";
	public static final String GROUPENDCHAR = ")";
	public static final String STATEMENTENDCHAR = ";";
	public static final String SPACECHAR = " ";
	
	public Group createGroups(String mvel) throws IncompatibleMVELTranslationException {
		String[] tokens = mvel.trim().split(STATEMENTENDCHAR);
		if (tokens.length > 1) {
			throw new IncompatibleMVELTranslationException("mvel expressions must resolve to a boolean result. More than one terminated statement has been detected, which does not cumulatively result in a single boolean. Multiple phrases should be strung together into a single expression using standard comparison operators.");
		}
		Group topGroup = new Group();
		parseGroups(topGroup, tokens[0]);
		
		return topGroup;
	}
	
	protected void parseGroups(Group myGroup, String segment) throws IncompatibleMVELTranslationException {
		boolean eol = false;
		int startPos = 0;
		if (segment.startsWith(GROUPSTARTCHAR)) {
			startPos = 1;
		}
		while (!eol) {
			int subgroupStartIndex = segment.indexOf(GROUPSTARTCHAR, startPos);
			if (subgroupStartIndex >= 0) {
				compilePhrases(segment.substring(startPos, subgroupStartIndex).trim(), myGroup);
				int subgroupEndIndex = findGroupEndIndex(segment, subgroupStartIndex);
				Group subGroup = new Group();
				myGroup.setSubGroup(subGroup);
				parseGroups(subGroup, segment.substring(subgroupStartIndex, subgroupEndIndex).trim());
				if (segment.endsWith(GROUPENDCHAR)) {
					eol = true;
				} else {
					startPos = subgroupEndIndex + 1;
				}
			} else {
				segment = segment.substring(startPos, segment.length());
				if (segment.startsWith(GROUPENDCHAR)) {
					segment = segment.substring(1, segment.length());
				}
				if (segment.endsWith(GROUPENDCHAR)) {
					segment = segment.substring(0, segment.length()-1);
				}
				compilePhrases(segment.trim(), myGroup);
				eol = true;
			}
		}
	}
	
	protected void compilePhrases(String segment, Group myGroup) {
		String[] tokens = segment.split(SPACECHAR);
		Phrase temp = new Phrase(null);
		myGroup.getPhrases().add(temp);
		for (String token : tokens) {
			if (temp.isComplete()) {
				temp = new Phrase(temp);
				myGroup.getPhrases().add(temp);
			}
			temp.setContent(token);
		}
		Phrase lastPhrase = myGroup.getPhrases().get(myGroup.getPhrases().size() - 1);
		if (!lastPhrase.isComplete()) {
			lastPhrase.operatorType = OperatorType.AND;
		}
	}
	
	protected int findGroupEndIndex(String segment, int subgroupStartIndex) throws IncompatibleMVELTranslationException {
		int subgroupEndIndex = subgroupStartIndex;
		boolean eol = false;
		while (!eol) {
			subgroupEndIndex = segment.indexOf(GROUPENDCHAR, subgroupStartIndex);
			if (subgroupEndIndex < 0) {
				throw new IncompatibleMVELTranslationException("Unable to find an end parenthesis for the group started at (" + segment.substring(subgroupStartIndex) + ")");
			} else {
				subgroupStartIndex = segment.indexOf(GROUPSTARTCHAR, subgroupStartIndex + 1);
				if (subgroupStartIndex < 0 || subgroupStartIndex > subgroupEndIndex) {
					eol = true;
				} else {
					subgroupStartIndex = subgroupEndIndex + 1;
				}
			}
		}
		return subgroupEndIndex + 1;
	}
	
}
