package org.broadleafcommerce.gwt.client.presenter.promotion.offer.translation;


public class MVEL_AdvancedCriteriaTranslator {
	
	/*public static final String GROUPSTARTCHAR = "(";
	public static final String GROUPENDCHAR = ")";
	public static final String STATEMENTENDCHAR = ";";
	public static final String SPACECHAR = " ";
	
	public static void main(String[] items) {
		MVEL_AdvancedCriteriaTranslator translator = new MVEL_AdvancedCriteriaTranslator();
		try {
			//Map<FilterType, AdvancedCriteria> test = translator.createAdvancedCriteria("one.test && (two.thing || (three.thing && four.thing)) && five.thing");
			Map<FilterType, AdvancedCriteria> test = translator.createAdvancedCriteria("one.test; two.test");
		} catch (IncompatibleMVELTranslationException e) {
			e.printStackTrace();
		}
	}
	
	protected Map<String, FilterType> mvelKeywordMap = new HashMap<String, FilterType>();
	protected Map<String, OperatorType> types = new HashMap<String, OperatorType>();
	
	public MVEL_AdvancedCriteriaTranslator() {
		mvelKeywordMap.put("order", FilterType.ORDER);
		mvelKeywordMap.put("discreteOrderItem", FilterType.ORDER_ITEM);
		mvelKeywordMap.put("fulfillmentGroup", FilterType.FULFILLMENT_GROUP);
		mvelKeywordMap.put("customer", FilterType.CUSTOMER);
		
		types.put("&&", OperatorType.AND);
		types.put("&", OperatorType.AND);
		types.put("||", OperatorType.OR);
		types.put("|", OperatorType.OR);
	}
	
	public Map<FilterType, AdvancedCriteria> createAdvancedCriteria(String mvel) throws IncompatibleMVELTranslationException {
		StringTokenizer tokens = new StringTokenizer(mvel.trim(), STATEMENTENDCHAR);
		if (tokens.countTokens() > 1) {
			throw new IncompatibleMVELTranslationException("mvel expressions must resolve to a boolean result. More than one terminated statement has been detected, which does not cumulatively result in a single boolean. Multiple phrases should be strung together into a single expression using standard comparison operators.");
		}
		Group topGroup = new Group();
		parseGroups(topGroup, tokens.nextToken());
		//TODO finish the translation algorithm
		return null;
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
		StringTokenizer tokens = new StringTokenizer(segment, SPACECHAR);
		Phrase temp = new Phrase(null);
		myGroup.getPhrases().add(temp);
		while (tokens.hasMoreElements()) {
			if (temp.isComplete()) {
				temp = new Phrase(temp);
				myGroup.getPhrases().add(temp);
			}
			temp.setContent(tokens.nextToken());
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
	
	protected OperatorType determineOperatorType(String phrase) {
		for (String type : types.keySet()) {
			if (type.equals(phrase)) {
				return types.get(type);
			}
		}
		return null;
	}

	private class Group {
		
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
	
	private class Phrase {
		
		private String phrase = "";
		private OperatorType operatorType;
		
		public Phrase(Phrase startPhrase) {
			if (startPhrase != null) {
				operatorType = startPhrase.getOperatorType();
			}
		}
		
		public String getPhrase() {
			return phrase;
		}
		
		public OperatorType getOperatorType() {
			return operatorType;
		}
		
		public void setContent(String phrase) {
			String test = new String(phrase);
			test.trim();
			OperatorType temp = determineOperatorType(test);
			if (temp == null) {
				this.phrase = test;
			} else {
				operatorType = temp;
			}
		}
		
		public Boolean isComplete() {
			return !phrase.equals("") && operatorType != null;
		}
	
	}*/
}
