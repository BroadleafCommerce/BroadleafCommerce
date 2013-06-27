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

package org.broadleafcommerce.openadmin.server.util;

import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class LocaleConverter implements Converter<String, Locale> {

    @Override
    public Locale convert(String localeString) {
        if (localeString == null) {
            return null;
        }
        String[] components = localeString.split("_");
        if (components.length == 1) {
            return new Locale(components[0]);
        } else if (components.length == 2) {
            return new Locale(components[0], components[1]);
        } else if (components.length == 3) {
            return new Locale(components[0], components[1], components[2]);
        }
        return null;
    }

}
