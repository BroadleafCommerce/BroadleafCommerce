/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import java.util.HashMap;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;

/**
 * While it is fairly easy to display numeric and currency formats in SmartGWT
 * (SimpleType has setShort/NormalDisplayFormatter()),
 * I haven't been able to provide full locale format support including editing.
 * Although FormItem class has setEditorValueFormatter() and setEditorValueParser() methods,
 * and SimpleType class has setEditorType(FormItem editorType) method,
 * that way goes nowhere because simpleType.setEditorType(editorType) fires exception - something is broken or not yet done here.
 *
 * Desperately seeking a workaround I finally wrote the following code
 * putting together info I got from SmartGWT and SmartClient forums
 * and other sources.
 * This helper creates and registers custom numeric SimpleType
 * based on GWT i18n NumericFormat.
 * Such a SimpleType could be then used just as build-in types.
 *
 * @author michalg
 * @author Jeff Fischer
 */

public final class NumericTypeFactory {

    private static HashMap<String, NumberFormat> formatMap = new HashMap<String, NumberFormat>();
    private static HashMap<String, SupportedFieldType> fieldTypeMap = new HashMap<String, SupportedFieldType>();

    private NumericTypeFactory(){}

    public static void registerNumericSimpleType(String name, NumberFormat format, SupportedFieldType supportedFieldType) {
        formatMap.put(name, format);
        fieldTypeMap.put(name, supportedFieldType);
        createNumericSimpleType(name);
    }

    private static native void createNumericSimpleType(String name) /*-{
        $wnd.isc.ClassFactory.defineClass(name, "TextItem");

        $wnd.isc.ClassFactory.getClass(name).addProperties( {
            mapDisplayToValue : function(value) {
                retVal = @org.broadleafcommerce.openadmin.client.view.dynamic.form.NumericTypeFactory::parseNumericValue(Ljava/lang/String;Ljava/lang/String;)(name,value);
                if (isNaN(retVal))
                    return value;
                return retVal;
            },
            mapValueToDisplay : function(value) {
                if (value == null)
                    return "";
                else if (isNaN(value))
                    return value;
                else {
                    return @org.broadleafcommerce.openadmin.client.view.dynamic.form.NumericTypeFactory::formatNumericValue(Ljava/lang/String;D)(name,value);
                }
            }
        });

        $wnd.isc.SimpleType.create({name:name,
            inheritsFrom:"float",
            editorType:name,
            normalDisplayFormatter:function(internalValue){
                return this.shortDisplayFormatter(internalValue);
            },
            shortDisplayFormatter:function(value){
                if (value == null)
                    return "";
                else if (isNaN(value))
                    return value;
                else {
                    return @org.broadleafcommerce.openadmin.client.view.dynamic.form.NumericTypeFactory::formatNumericValue(Ljava/lang/String;D)(name,value);
                }
            }
        });
    }-*/;

    private static String formatNumericValue(String name, double value) {
        String response = formatMap.get(name).format(value);
        if (fieldTypeMap.get(name).name().equals(SupportedFieldType.MONEY.name())) {
            String decimalSeparator = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();
            int pos = response.indexOf(decimalSeparator);
            if (pos >= 0) {
                String decimal = response.substring(pos + 1, response.length());
                for (int j = decimal.length(); j < 2; j++) {
                    response += "0";
                }
            } else {
                response += decimalSeparator + "00";
            }
        }

        return response;
    }

    private static double parseNumericValue(String name, String value) {
        try {
            //System.out.println("parsing \"" + value + "\"");
            double result = formatMap.get(name).parse(value);
            //System.out.println("result: " + result);
            return result;
        } catch (Exception e) {
            return Double.NaN;
        }
    }

}
