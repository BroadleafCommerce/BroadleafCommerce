/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.rulebuilder.grouping;

import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;

import java.util.Stack;

/**
 * @author jfischer
 * @author Elbert Bautista (elbertbautista)
 */
public class GroupingTranslator {
    public static final String GROUPSTARTCHAR = "(";
    public static final String GROUPENDCHAR = ")";
    public static final String STATEMENTENDCHAR = ";";
    public static final String SPACECHAR = " ";

    public Group createGroups(String mvel) throws MVELTranslationException {
        mvel = stripWhiteSpace(mvel);
        String[] tokens = mvel.trim().split(STATEMENTENDCHAR);
        if (tokens.length > 1) {
            throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_RULE, "mvel expressions must resolve to a boolean result. " +
                    "More than one terminated statement has been detected, which does not cumulatively result " +
                    "in a single boolean. Multiple phrases should be strung together into a single expression using " +
                    "standard operators.");
        }
        Group topGroup = new Group();
        topGroup.setIsTopGroup(true);
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

    protected int findGroupEnd(String segment, int subgroupStartIndex) throws MVELTranslationException {
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
        throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_RULE, "Unable to find an end parenthesis for the group started at (" +
                segment.substring(subgroupStartIndex) + ")");
    }

    protected String stripWhiteSpace(String mvel) {
        return mvel.replaceAll("[\\t\\n\\r]", "");
    }

    protected void parseGroups(Group myGroup, String segment) throws MVELTranslationException {
        boolean eol = false;
        int startPos = 0;
        while (!eol) {
            int subgroupStartIndex = -1;
            boolean isNegation = false;

            subgroupStartIndex = findGroupStart(segment, startPos);
            if (subgroupStartIndex == startPos || subgroupStartIndex == startPos + 1) {
                Group subGroup = new Group();
                myGroup.getSubGroups().add(subGroup);
                int subgroupEndIndex = findGroupEnd(segment, subgroupStartIndex);
                if (subgroupStartIndex > 0 && segment.charAt(subgroupStartIndex - 1) == '!') {
                    if (myGroup.getIsTopGroup()) {
                        //This is a NOT specified at the topmost level
                        myGroup.setOperatorType(BLCOperator.NOT);
                    } else {
                        //This is a NOT specified on a sub group
                        subGroup.setOperatorType(BLCOperator.NOT);
                    }
                }
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

    protected void compilePhrases(String segment, Group myGroup, boolean isNegation) throws MVELTranslationException {
        if (segment.trim().length() == 0) {
            return;
        }
        String[] andTokens = segment.split("&&");
        String[] orTokens = segment.split("\\|\\|");
        if (andTokens.length > 1 && orTokens.length > 1) {
            throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_RULE, "Segments that mix logical operators are not compatible with " +
                    "the rules builder: (" + segment + ")");
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

    protected void setGroupOperator(String segment, Group myGroup, boolean isAnd, boolean isOr, boolean isNot)
            throws MVELTranslationException {
        if (myGroup.getOperatorType() == null) {
            if (isAnd) {
                myGroup.setOperatorType(BLCOperator.AND);
            } else if (isOr) {
                myGroup.setOperatorType(BLCOperator.OR);
            } else if (isNot) {
                myGroup.setOperatorType(BLCOperator.NOT);
            }
        } else {
            if (
                    (isOr && !myGroup.getOperatorType().toString().equals(BLCOperator.OR.toString()))
                    ) {
                throw new MVELTranslationException(MVELTranslationException.INCOMPATIBLE_RULE, "Segment logical operator is not compatible with the group " +
                        "logical operator: (" + segment + ")");
            }
        }
    }

}
