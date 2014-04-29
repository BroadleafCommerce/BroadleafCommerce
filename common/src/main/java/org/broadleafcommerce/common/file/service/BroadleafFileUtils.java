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



/**
 * 
 * @author bpolster
 * @author Phillip Verheyden (phillipuniverse)
 */
public class BroadleafFileUtils {

    /**
     * @deprecated this is now just a pass-through to {@link #appendUnixPaths(String, String)}. The original method was a
     * misnomer.
     */
    @Deprecated
    public static String buildFilePath(String directory, String fileName) {
        return appendUnixPaths(directory, fileName);
    }
    
    /**
     * @deprecated this is now just a pass-through to {@link #removeLeadingUnixSlash(String, String)}. The original method was a
     * misnomer.
     */
    @Deprecated
    public static String removeLeadingSlash(String fileName) {
        return removeLeadingUnixSlash(fileName);
    }
    
    /**
     * @deprecated this is now just a pass-through to {@link #addLeadingUnixSlash(String, String)}. The original method was a
     * misnomer.
     */
    @Deprecated
    public static String addLeadingSlash(String fileName) {
        return addLeadingUnixSlash(fileName);
    }
    
    /**
     * Builds a file path that ensures the directory and filename are separated by a single separator. This is only suitable
     * for Unix and URL paths. File paths need special care for the differences between systems (/ on Unix and \ on Windows)
     * @param directory
     * @param fileName
     */
    public static String appendUnixPaths(String directory, String fileName) {
        if (directory.endsWith("/")) {
            return directory + removeLeadingUnixSlash(fileName);
        } else {
            return directory + addLeadingUnixSlash(fileName);
        }
    }

    /**
     * Removes the leading slash if found on the passed in filename.
     * @param fileName
     */
    public static String removeLeadingUnixSlash(String fileName) {
        if (fileName.startsWith("/")) {
            return fileName.substring(1);
        }
        return fileName;
    }

    /**
     * Adds the leading slash if needed on the beginning of a filename.
     * @param fileName
     */
    public static String addLeadingUnixSlash(String fileName) {
        if (fileName.startsWith("/")) {
            return fileName;
        }
        return "/" + fileName;
    }
}
