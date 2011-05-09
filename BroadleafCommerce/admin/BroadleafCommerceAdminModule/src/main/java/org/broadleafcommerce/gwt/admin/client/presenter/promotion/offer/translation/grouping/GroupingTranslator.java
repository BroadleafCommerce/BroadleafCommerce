package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.grouping;

import java.util.Stack;

import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.IncompatibleMVELTranslationException;

import com.smartgwt.client.types.OperatorId;

public class GroupingTranslator {

	public static final String GROUPSTARTCHAR = "(";
	public static final String GROUPENDCHAR = ")";
	public static final String STATEMENTENDCHAR = ";";
	public static final String SPACECHAR = " ";
	
	public Group createGroups(String mvel) throws IncompatibleMVELTranslationException {
		mvel = stripWhiteSpace(mvel);
		String[] tokens = mvel.trim().split(STATEMENTENDCHAR);
		if (tokens.length > 1) {
			throw new IncompatibleMVELTranslationException("mvel expressions must resolve to a boolean result. More than one terminated statement has been detected, which does not cumulatively result in a single boolean. Multiple phrases should be strung together into a single expression using standard operators.");
		}
		Group topGroup = new Group();
		parseGroups(topGroup, tokens[0]);
		
		return topGroup;
	}
	
	protected int findGroupStart(String segment, int startPos) {
		int startIndex = -1;
		boolean eof = false;
		while (!eof) {
			startIndex = segment.indexOf(GROUPSTARTCHAR, startPos);
			if (startIndex <= 0) {
				eof = true;
				continue;
			}
			char preChar = segment.charAt(startIndex-1);
			if (preChar == '!' || preChar == '&' || preChar == '|') {
				eof = true;
				continue;
			}
			startPos = startIndex + 1;
		}
		return startIndex;
	}
	
	protected int findGroupEnd(String segment, int subgroupStartIndex) throws IncompatibleMVELTranslationException {
		Stack<Integer> leftParenPos = new Stack<Integer>();
		char[] characters = segment.toCharArray();
		for (int j=subgroupStartIndex;j<characters.length;j++) {
			if (characters[j]=='(') {
				leftParenPos.push(j);
				continue;
			}
			if (characters[j]==')') {
				leftParenPos.pop();
				if(leftParenPos.isEmpty()) {
					return j + 1;
				}
			}
		}
		throw new IncompatibleMVELTranslationException("Unable to find an end parenthesis for the group started at (" + segment.substring(subgroupStartIndex) + ")");
	}
	
	protected String stripWhiteSpace(String mvel) {
		return mvel.replaceAll("[\\t\\n\\r]", "");
	}
	
	protected void parseGroups(Group myGroup, String segment) throws IncompatibleMVELTranslationException {
		boolean eol = false;
		int startPos = 0;
		while (!eol) {
			int subgroupStartIndex = -1;
			boolean isNegation = false;
			
			subgroupStartIndex = findGroupStart(segment, startPos);
			if (subgroupStartIndex == startPos || subgroupStartIndex == startPos + 1) {
				int subgroupEndIndex = findGroupEnd(segment, subgroupStartIndex);
				if (subgroupStartIndex > 0 && segment.charAt(subgroupStartIndex - 1) == '!') {
					myGroup.setOperatorType(OperatorId.NOT);
				}
				Group subGroup = new Group();
				myGroup.getSubGroups().add(subGroup);
				parseGroups(subGroup, segment.substring(subgroupStartIndex+1, subgroupEndIndex-1).trim());
				startPos = subgroupEndIndex;
				if (startPos == segment.length()) {
					eol = true;
				} else {
					boolean isAnd = false;
					boolean isOr = false;
					if (segment.charAt(startPos) == '&') {
						isAnd = true;
					} else if (segment.charAt(startPos) == '|') {
						isOr = true;
					}
					if (myGroup.getOperatorType() == null) {
						setGroupOperator(segment, myGroup, isAnd, isOr, false);
					}
					if (isAnd || isOr) {
						startPos += 2;
					}
				}
				continue;
			} else {
				if (subgroupStartIndex < 0) {
					compilePhrases(segment.substring(startPos, segment.length()).trim(), myGroup, isNegation);
					eol = true;
					continue;
				}
				String temp = segment.substring(startPos, subgroupStartIndex);
				compilePhrases(temp.trim(), myGroup, isNegation);
				startPos = subgroupStartIndex;
				continue;
			}
		}
	}
	
	protected void compilePhrases(String segment, Group myGroup, boolean isNegation) throws IncompatibleMVELTranslationException {
		if (segment.trim().length() == 0) {
			return;
		}
		String[] andTokens = segment.split("&&");
		String[] orTokens = segment.split("\\|\\|");
		if (andTokens.length > 1 && orTokens.length > 1) {
			throw new IncompatibleMVELTranslationException("Segments that mix logical operators are not compatible with the rules builder: (" + segment + ")");
		}
		boolean isAnd = false;
		boolean isOr = false;
		boolean isNot = false;
		if (andTokens.length > 1 || segment.indexOf("&&") >= 0) {
			isAnd = true;
		} else if (orTokens.length > 1 || segment.indexOf("||") >= 0) {
			isOr = true;
		}
		if (isAnd && isNegation) {
			isNot = true;
		}
		if (!isAnd && !isOr && !isNot) {
			isAnd = true;
		}
		setGroupOperator(segment, myGroup, isAnd, isOr, isNot);
		String[] tokens;
		if (isAnd || isNot) {
			tokens = andTokens;
		} else {
			tokens = orTokens;
		}
		for (String token : tokens) {
			if (token.length() > 0) {
				myGroup.getPhrases().add(token);
			}
		}
	}

	protected void setGroupOperator(String segment, Group myGroup, boolean isAnd, boolean isOr, boolean isNot) throws IncompatibleMVELTranslationException {
		if (myGroup.getOperatorType() == null) {
			if (isAnd) {
				myGroup.setOperatorType(OperatorId.AND);
			} else if (isOr) {
				myGroup.setOperatorType(OperatorId.OR);
			} else if (isNot) {
				myGroup.setOperatorType(OperatorId.NOT);
			}
		} else {
			if (
				(isOr && !myGroup.getOperatorType().toString().equals(OperatorId.OR.toString()))
			) {
				throw new IncompatibleMVELTranslationException("Segment logical operator is not compatible with the group logical operator: (" + segment + ")");
			}
		}
	}
	
}
