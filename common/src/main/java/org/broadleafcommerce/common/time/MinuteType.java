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

package org.broadleafcommerce.common.time;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of container shape types.
 * 
 * @author jfischer
 */
public class MinuteType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, MinuteType> TYPES = new LinkedHashMap<String, MinuteType>();

    public static final MinuteType ZERO  = new MinuteType("0", "00");
    public static final MinuteType ONE  = new MinuteType("1", "01");
    public static final MinuteType TWO  = new MinuteType("2", "02");
    public static final MinuteType THREE  = new MinuteType("3", "03");
    public static final MinuteType FOUR  = new MinuteType("4", "04");
    public static final MinuteType FIVE  = new MinuteType("5", "05");
    public static final MinuteType SIX  = new MinuteType("6", "06");
    public static final MinuteType SEVEN  = new MinuteType("7", "07");
    public static final MinuteType EIGHT  = new MinuteType("8", "08");
    public static final MinuteType NINE  = new MinuteType("9", "09");
    public static final MinuteType TEN  = new MinuteType("10", "10");
    public static final MinuteType ELEVEN  = new MinuteType("11", "11");
    public static final MinuteType TWELVE  = new MinuteType("12", "12");
    public static final MinuteType THIRTEEN  = new MinuteType("13", "13");
    public static final MinuteType FOURTEEN  = new MinuteType("14", "14");
    public static final MinuteType FIFTEEN  = new MinuteType("15", "15");
    public static final MinuteType SIXTEEN  = new MinuteType("16", "16");
    public static final MinuteType SEVENTEEN  = new MinuteType("17", "17");
    public static final MinuteType EIGHTEEN  = new MinuteType("18", "18");
    public static final MinuteType NINETEEN  = new MinuteType("19", "19");
    public static final MinuteType TWENTY  = new MinuteType("20", "20");
    public static final MinuteType TWENTYONE  = new MinuteType("21", "21");
    public static final MinuteType TWNETYTWO  = new MinuteType("22", "22");
    public static final MinuteType TWENTYTHREE  = new MinuteType("23", "23");
    public static final MinuteType TWENTYFOUR  = new MinuteType("24", "24");
    public static final MinuteType TWENTYFIVE  = new MinuteType("25", "25");
    public static final MinuteType TWENTYSIX  = new MinuteType("26", "26");
    public static final MinuteType TWENTYSEVEN  = new MinuteType("27", "27");
    public static final MinuteType TWENTYEIGHT  = new MinuteType("28", "28");
    public static final MinuteType TWENTYNINE  = new MinuteType("29", "29");
    public static final MinuteType THIRTY  = new MinuteType("30", "30");
    public static final MinuteType THIRTYONE  = new MinuteType("31", "31");
    public static final MinuteType THIRTYTWO  = new MinuteType("32", "32");
    public static final MinuteType THIRTYTHREE  = new MinuteType("33", "33");
    public static final MinuteType THIRTYFOUR  = new MinuteType("34", "34");
    public static final MinuteType THIRTYFIVE  = new MinuteType("35", "35");
    public static final MinuteType THIRTYSIX  = new MinuteType("36", "36");
    public static final MinuteType THIRTYSEVEN  = new MinuteType("37", "37");
    public static final MinuteType THIRTYEIGHT  = new MinuteType("38", "38");
    public static final MinuteType THIRTYNINE  = new MinuteType("39", "39");
    public static final MinuteType FOURTY  = new MinuteType("40", "40");
    public static final MinuteType FOURTYONE  = new MinuteType("41", "41");
    public static final MinuteType FOURTYTWO  = new MinuteType("42", "42");
    public static final MinuteType FOURTYTHREE  = new MinuteType("43", "43");
    public static final MinuteType FOURTYFOUR  = new MinuteType("44", "44");
    public static final MinuteType FOURTYFIVE  = new MinuteType("45", "45");
    public static final MinuteType FOURTYSIX  = new MinuteType("46", "46");
    public static final MinuteType FOURTYSEVEN  = new MinuteType("47", "47");
    public static final MinuteType FOURTYEIGHT  = new MinuteType("48", "48");
    public static final MinuteType FOURTYNINE  = new MinuteType("49", "49");
    public static final MinuteType FIFTY  = new MinuteType("50", "50");
    public static final MinuteType FIFTYONE  = new MinuteType("51", "51");
    public static final MinuteType FIFTYTWO  = new MinuteType("52", "52");
    public static final MinuteType FIFTYTHREE  = new MinuteType("53", "53");
    public static final MinuteType FIFTYFOUR  = new MinuteType("54", "54");
    public static final MinuteType FIFTYFIVE  = new MinuteType("55", "55");
    public static final MinuteType FIFTYSIX  = new MinuteType("56", "56");
    public static final MinuteType FIFTYSEVEN  = new MinuteType("57", "57");
    public static final MinuteType FIFTYEIGHT  = new MinuteType("58", "58");
    public static final MinuteType FIFTYNINE  = new MinuteType("59", "59");
    
    public static MinuteType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public MinuteType() {
        //do nothing
    }

    public MinuteType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        } else {
            throw new RuntimeException("Cannot add the type: (" + type + "). It already exists as a type via " + getInstance(type).getClass().getName());
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MinuteType other = (MinuteType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
