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

package org.broadleafcommerce.common.security.util;


public class PasswordUtils {

    public static final Character[] characters = {
        'a','b','c','d','e','f','g','h','j','k','m','n','p','q','r','s','t','u','v','w','x','y',
        '2','3','4','6','7','8','9'
    };
    
    public static String generateTemporaryPassword(int requiredLength) {
        int length = characters.length;
        StringBuffer sb = new StringBuffer(requiredLength);
        for (int j=0;j<requiredLength;j++) {
            sb.append(characters[(int) Math.round(Math.floor(Math.random() * length))]);
        }
        
        return sb.toString();
    }
}
