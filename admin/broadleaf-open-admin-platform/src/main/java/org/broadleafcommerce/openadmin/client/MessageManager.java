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

package org.broadleafcommerce.openadmin.client;

import com.google.gwt.i18n.client.ConstantsWithLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/5/11
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageManager implements ConstantsWithLookup {

    List<ConstantsWithLookup> constants = new ArrayList<ConstantsWithLookup>();

    public void addConstants(ConstantsWithLookup constants) {
        this.constants.add(constants);
    }

    @Override
    public boolean getBoolean(String methodName) throws MissingResourceException {
        MissingResourceException backup = null;
        for (int j=constants.size()-1;j>=0;j--) {
            ConstantsWithLookup constant = constants.get(j);
            try {
                boolean temp = constant.getBoolean(methodName);
                return temp;
            } catch (MissingResourceException e) {
                backup = e;
            }
        }
        throw backup;
    }

    @Override
    public double getDouble(String methodName) throws MissingResourceException {
        MissingResourceException backup = null;
        for (int j=constants.size()-1;j>=0;j--) {
            ConstantsWithLookup constant = constants.get(j);
            try {
                double temp = constant.getDouble(methodName);
                return temp;
            } catch (MissingResourceException e) {
                backup = e;
            }
        }
        throw backup;
    }

    @Override
    public float getFloat(String methodName) throws MissingResourceException {
        MissingResourceException backup = null;
        for (int j=constants.size()-1;j>=0;j--) {
            ConstantsWithLookup constant = constants.get(j);
            try {
                float temp = constant.getFloat(methodName);
                return temp;
            } catch (MissingResourceException e) {
                backup = e;
            }
        }
        throw backup;
    }

    @Override
    public int getInt(String methodName) throws MissingResourceException {
        MissingResourceException backup = null;
        for (int j=constants.size()-1;j>=0;j--) {
            ConstantsWithLookup constant = constants.get(j);
            try {
                int temp = constant.getInt(methodName);
                return temp;
            } catch (MissingResourceException e) {
                backup = e;
            }
        }
        throw backup;
    }

    @Override
    public Map<String, String> getMap(String methodName) throws MissingResourceException {
        MissingResourceException backup = null;
        for (int j=constants.size()-1;j>=0;j--) {
            ConstantsWithLookup constant = constants.get(j);
            try {
                Map<String, String> temp = constant.getMap(methodName);
                return temp;
            } catch (MissingResourceException e) {
                backup = e;
            }
        }
        throw backup;
    }

    @Override
    public String getString(String methodName) throws MissingResourceException {
        MissingResourceException backup = null;
        for (int j=constants.size()-1;j>=0;j--) {
            ConstantsWithLookup constant = constants.get(j);
            try {
                String temp = constant.getString(methodName);
                return temp;
            } catch (MissingResourceException e) {
                backup = e;
            }
        }
        throw backup;
    }

    @Override
    public String[] getStringArray(String methodName) throws MissingResourceException {
        MissingResourceException backup = null;
        for (int j=constants.size()-1;j>=0;j--) {
            ConstantsWithLookup constant = constants.get(j);
            try {
                String[] temp = constant.getStringArray(methodName);
                return temp;
            } catch (MissingResourceException e) {
                backup = e;
            }
        }
        throw backup;
    }

    public String replaceKeys(String templateProperty, String[] keyNames, String[] values) {
        for (int j=0;j<keyNames.length;j++) {
            templateProperty = templateProperty.replaceAll("\\$\\{"+keyNames[j]+"}", values[j]);
        }

        return templateProperty;
    }
}
