/*
 * Copyright 2008-2009 the original author or authors.
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
package org.broadleafcommerce.extensibility.context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StandardConfigLocations {

    private static final Log LOG = LogFactory.getLog(StandardConfigLocations.class);
    
    public static final int ALLCONTEXTTYPE = 0;
    public static final int WEBCONTEXTTYPE = 1;
    public static final int SERVICECONTEXTTYPE = 2;
    public static final int TESTCONTEXTTYPE = 3;

    public static String[] retrieveAll(int contextType) throws IOException {
        String[] response;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(StandardConfigLocations.class.getResourceAsStream("StandardConfigLocations.txt")));
            ArrayList<String> items = new ArrayList<String>();
            boolean eof = false;
            while (!eof) {
                String temp = reader.readLine();
                if (temp == null) {
                    eof = true;
                } else {
                    if (!temp.startsWith("#") && temp.trim().length() > 0 && StandardConfigLocations.class.getClassLoader().getResource(temp.trim()) != null) {
                    	if (
                    			contextType == ALLCONTEXTTYPE  ||
                    			(contextType == WEBCONTEXTTYPE && temp.indexOf("-web-") >= 0) ||
                    			((contextType == SERVICECONTEXTTYPE || contextType == TESTCONTEXTTYPE) && temp.indexOf("-web-") < 0) ||
                    			(contextType == TESTCONTEXTTYPE && temp.indexOf("-test") >= 0)
                    	){
                    		items.add(temp.trim());
                    	}
                    }
                }
            }
            response = new String[]{};
            response = items.toArray(response);
        } finally {
            if (reader != null) {
                try{ reader.close(); } catch (Throwable e) {
                    LOG.error("Unable to merge source and patch locations", e);
                }
            }
        }

        return response;
    }

}
