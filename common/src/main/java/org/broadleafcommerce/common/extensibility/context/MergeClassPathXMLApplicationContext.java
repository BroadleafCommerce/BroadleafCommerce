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
package org.broadleafcommerce.common.extensibility.context;

import org.broadleafcommerce.common.extensibility.context.merge.ImportProcessor;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeException;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;

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
public class MergeClassPathXMLApplicationContext extends AbstractXmlApplicationContext {

    protected Resource[] configResources;
    
    protected Resource[] getConfigResources() {
        return this.configResources;
    }
    
    /**
     * Create a new MergeClassPathXMLApplicationContext, loading the definitions from the given definitions. Note,
     * all sourceLocation files will be merged using standard Spring configuration override rules. However, the patch
     * files are fully merged into the result of the sourceLocations simple merge. Patch merges are first executed according
     * to beans with the same id. Subsequent merges within a bean are executed against tagnames - ignoring any
     * further id attributes.
     * 
     * @param sourceLocations array of relative (or absolute) paths within the class path for the source application context files
     * @param patchLocations array of relative (or absolute) paths within the class path for the patch application context files
     * @throws BeansException
     */
    public MergeClassPathXMLApplicationContext(String[] sourceLocations, String[] patchLocations) throws BeansException {
        this(sourceLocations, patchLocations, null);
    }
    
    /**
     * Create a new MergeClassPathXMLApplicationContext, loading the definitions from the given definitions. Note,
     * all sourceLocation files will be merged using standard Spring configuration override rules. However, the patch
     * files are fully merged into the result of the sourceLocations simple merge. Patch merges are first executed according
     * to beans with the same id. Subsequent merges within a bean are executed against tagnames - ignoring any
     * further id attributes.
     * 
     * @param sourceLocations array of relative (or absolute) paths within the class path for the source application context files
     * @param patchLocations array of relative (or absolute) paths within the class path for the patch application context files
     * @param parent the parent context
     * @throws BeansException
     */
    public MergeClassPathXMLApplicationContext(String[] sourceLocations, String[] patchLocations, ApplicationContext parent) throws BeansException {
        super(parent);
        
        ResourceInputStream[] sources = new ResourceInputStream[sourceLocations.length];
        for (int j=0;j<sourceLocations.length;j++){
            sources[j] = new ResourceInputStream(MergeClassPathXMLApplicationContext.class.getClassLoader().getResourceAsStream(sourceLocations[j]), sourceLocations[j]);
        }
        
        ResourceInputStream[] patches = new ResourceInputStream[patchLocations.length];
        for (int j=0;j<patches.length;j++){
            patches[j] = new ResourceInputStream(MergeClassPathXMLApplicationContext.class.getClassLoader().getResourceAsStream(patchLocations[j]), patchLocations[j]);
        }

        ImportProcessor importProcessor = new ImportProcessor(this);
        try {
            sources = importProcessor.extract(sources);
            patches = importProcessor.extract(patches);
        } catch (MergeException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        }

        this.configResources = new MergeApplicationContextXmlConfigResource().getConfigResources(sources, patches);
        refresh();
    }

}
