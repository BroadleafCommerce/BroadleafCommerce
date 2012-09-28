/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.lang;

import java.io.Serializable;

/**
 * A simple, GWT compatible version of StringUtils
 * 
 * @author jfischer
 * 
 */
public class StringUtils implements Serializable {

    private static final long serialVersionUID = 1L;

    public static String uncapitalize(String val) {
        return val.toLowerCase();
    }

    public static String join(String[] items, char delimeter) {
        final StringBuffer sb = new StringBuffer();
        for (String item : items) {
            sb.append(item);
            sb.append(delimeter);
        }
        sb.deleteCharAt(sb.length());
        return sb.toString();
    }

    /**
     * Checks a String for null or empty ("").
     * 
     * @param str The String to check.
     * @return Empty String status.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }
}
