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

package org.broadleafcommerce.common.util;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class DependencyLicenseCopy extends Copy {

    protected File licenseDir = null;
    protected Vector<ResourceCollection> rcs = new Vector<ResourceCollection>();
    
    @SuppressWarnings("unchecked")
    public void execute() throws BuildException {
        super.execute();
        try {
            for (int i = 0; i < rcs.size(); i++) {
                ResourceCollection rc = (ResourceCollection) rcs.elementAt(i);  
                Iterator<Resource> resources = rc.iterator();
                while (resources.hasNext()) {
                    Resource r = (Resource) resources.next();
                    if (!r.isExists()) {
                        continue;
                    }
                    if (r instanceof FileResource) {
                        FileResource fr = (FileResource) r;
                        String baseDir = fr.getBaseDir().getAbsolutePath();
                        String file = fr.getFile().getAbsolutePath();
                        file = file.substring(baseDir.length(), file.length());
                        String[] parts = file.split("/");
                        if (parts.length<=1) {
                            parts = file.split("\\\\");
                        }
                        if (parts.length <= 1) {
                            throw new BuildException("Unable to recognize the path separator for src file: " + file);
                        }
                        String[] specificParts = new String[parts.length-1];
                        System.arraycopy(parts, 0, specificParts, 0, specificParts.length);
                        String specificFilePart = StringUtils.join(specificParts, '/') + "/license.txt";
                        File specificFile = new File(licenseDir, specificFilePart);
                        File specificDestinationFile = new File(destDir, specificFilePart);
                        if (specificFile.exists()) {
                            fileUtils.copyFile(specificFile, specificDestinationFile);
                            continue;
                        }
                        
                        String[] generalParts = new String[3];
                        System.arraycopy(parts, 0, generalParts, 0, 3);
                        String generalFilePart = StringUtils.join(generalParts, '/') + "/license.txt";
                        File generalFile = new File(licenseDir, generalFilePart);
                        if (generalFile.exists()) {
                            fileUtils.copyFile(generalFile, specificDestinationFile);
                            continue;
                        }
                        
                        String[] moreGeneralParts = new String[2];
                        System.arraycopy(parts, 0, moreGeneralParts, 0, 2);
                        String moreGeneralFilePart = StringUtils.join(moreGeneralParts, '/') + "/license.txt";
                        File moreGeneralFile = new File(licenseDir, moreGeneralFilePart);
                        if (moreGeneralFile.exists()) {
                            fileUtils.copyFile(moreGeneralFile, specificDestinationFile);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }
    
    public void add(ResourceCollection res) {
        super.add(res);
        rcs.add(res);
    }

    public File getLicenseDir() {
        return licenseDir;
    }

    public void setLicenseDir(File licenseDir) {
        this.licenseDir = licenseDir;
    }
}
