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

package org.broadleafcommerce.common.extensibility.context;

import org.broadleafcommerce.common.extensibility.context.merge.ImportProcessor;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeException;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Standalone XML application context, taking the locations of one or more
 * source applicationContext xml files and one or more patch xml files.
 * 
 * <p>One or more source files merge together in pure override mode. Source
 * files are merged in the order specified. If a bean id is repeated in a subsequent
 * source file, the subsequent bean definition will always win. This is the same behavior
 * as Spring's default mechanism for merging 1 to N applicationContext files.</p>
 * 
 * <p>Each patch file is merged with the combined source, one patch file at a time. This
 * merge is performed in true merge mode. Therefore, if a bean id is delivered in a patch
 * file with the same id as a bean in the source, the patch will merge with the source. This
 * could result in an override of the class definition for the bean, or additional or changed
 * property elements within the bean definition.</p>
 * 
 * @author jfischer
 *
 */
public class MergeFileSystemAndClassPathXMLApplicationContext extends AbstractMergeXMLApplicationContext {

    public MergeFileSystemAndClassPathXMLApplicationContext(ApplicationContext parent) {
        super(parent);
    }
    
    public MergeFileSystemAndClassPathXMLApplicationContext(String[] classPathLocations, String[] fileSystemLocations) throws BeansException {
        this(classPathLocations, fileSystemLocations, null);
    }

    public MergeFileSystemAndClassPathXMLApplicationContext(LinkedHashMap<String, ResourceType> locations, ApplicationContext parent) throws BeansException {
        this(parent);

        ResourceInputStream[] resources = new ResourceInputStream[locations.size()];
        int j = 0;
        for (Map.Entry<String, ResourceType> entry : locations.entrySet()) {
            switch (entry.getValue()) {
                case CLASSPATH:
                    resources[j] = new ResourceInputStream(getClassLoader(parent).getResourceAsStream(entry.getKey()), entry.getKey());
                    break;
                case FILESYSTEM:
                    try {
                        File temp = new File(entry.getKey());
                        resources[j] = new ResourceInputStream(new BufferedInputStream(new FileInputStream(temp)), entry.getKey());
                    } catch (FileNotFoundException e) {
                        throw new FatalBeanException("Unable to merge context files", e);
                    }
                    break;
            }
            j++;
        }

        ImportProcessor importProcessor = new ImportProcessor(this);
        try {
            resources = importProcessor.extract(resources);
        } catch (MergeException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        }

        this.configResources = new MergeApplicationContextXmlConfigResource().getConfigResources(resources, null);
        refresh();
    }

    public MergeFileSystemAndClassPathXMLApplicationContext(String[] classPathLocations, String[] fileSystemLocations, ApplicationContext parent) throws BeansException {
        this(parent);

        ResourceInputStream[] classPathSources;
        ResourceInputStream[] fileSystemSources;
        try {
            classPathSources = new ResourceInputStream[classPathLocations.length];
            for (int j=0;j<classPathLocations.length;j++){
                classPathSources[j] = new ResourceInputStream(getClassLoader(parent).getResourceAsStream(classPathLocations[j]), classPathLocations[j]);
            }

            fileSystemSources = new ResourceInputStream[fileSystemLocations.length];
            for (int j=0;j<fileSystemSources.length;j++){
                File temp = new File(fileSystemLocations[j]);
                fileSystemSources[j] = new ResourceInputStream(new BufferedInputStream(new FileInputStream(temp)), fileSystemLocations[j]);
            }
        } catch (FileNotFoundException e) {
            throw new FatalBeanException("Unable to merge context files", e);
        }

        ImportProcessor importProcessor = new ImportProcessor(this);
        try {
            classPathSources = importProcessor.extract(classPathSources);
            fileSystemSources = importProcessor.extract(fileSystemSources);
        } catch (MergeException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        }

        this.configResources = new MergeApplicationContextXmlConfigResource().getConfigResources(classPathSources, fileSystemSources);
        refresh();
    }
    
    /**
     * This could be advantageous for subclasses to override in order to utilize the parent application context. By default,
     * this utilizes the class loader for the current class.
     */
    protected ClassLoader getClassLoader(ApplicationContext parent) {
        return MergeFileSystemAndClassPathXMLApplicationContext.class.getClassLoader();
    }

    public enum ResourceType {
        FILESYSTEM,CLASSPATH
    }
}
