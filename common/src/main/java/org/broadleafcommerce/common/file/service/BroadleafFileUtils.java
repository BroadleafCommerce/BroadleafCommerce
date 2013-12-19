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
package org.broadleafcommerce.common.file.service;


public class BroadleafFileUtils {

    /**
     * Builds a file path that ensures the directory and filename are separated by a single separator.
     * @param directory
     * @param fileName
     * @return
     */
    public static String buildFilePath(String directory, String fileName) {
        if (directory.endsWith("/")) {
            return directory + removeLeadingSlash(fileName);
        } else {
            return directory + addLeadingSlash(fileName);
        }
    }

    /**
     * Removes the leading slash if found on the passed in filename.
     * @param fileName
     * @return
     */
    public static String removeLeadingSlash(String fileName) {
        if (fileName.startsWith("/")) {
            return fileName.substring(1);
        }
        return fileName;
    }

    /**
     * Adds the leading slash if needed on the beginning of a filename.
     * @param fileName
     * @return
     */

    public static String addLeadingSlash(String fileName) {
        if (fileName.startsWith("/")) {
            return fileName;
        }
        return "/" + fileName;
    }
}
