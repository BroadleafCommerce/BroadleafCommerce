/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.locale.util;

import org.broadleafcommerce.common.locale.domain.Locale;

/**
 * Author: jerryocanas
 * Date: 9/17/12
 */
public final class LocaleUtil {

    public static String findLanguageCode(Locale locale) {
        if (locale != null && locale.getLocaleCode() != null && locale.getLocaleCode().indexOf("_") > 0) {
            int endIndex = locale.getLocaleCode().indexOf("_");
            char[] localeCodeChars = locale.getLocaleCode().toCharArray();
            StringBuffer sb = new StringBuffer();
            for(int i=0; i < endIndex; i++){
                sb.append(localeCodeChars[i]);
            }
            return sb.toString();
        }
        return null;
    }

}
