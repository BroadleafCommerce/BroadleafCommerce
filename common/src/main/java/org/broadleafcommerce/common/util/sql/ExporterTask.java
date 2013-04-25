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

package org.broadleafcommerce.common.util.sql;

import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.PropertySet;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.Exporter;

import java.io.File;
import java.util.Properties;

/**
 * This is a re-worked version from Hibernate tools
 * 
 * @author jfischer
 *
 */
public abstract class ExporterTask {

    // refactor out so not dependent on Ant ?
    protected HibernateToolTask parent;
    Properties properties;
    File destdir;
    Configuration configuration;
    String outputFileName = null;
    
    public ExporterTask(HibernateToolTask parent) {
        this.parent = parent;
        this.properties = new Properties();
    }

    
    /*final*/ public void execute() {
    
        Exporter exporter = configureExporter(createExporter() );
        exporter.start();
        
    }
    
    protected abstract Exporter createExporter();

    public File getDestdir() {
        return destdir;
    }
    
    public void setDestdir(File destdir) {
        this.destdir = destdir;
    }
    
    public void addConfiguredPropertySet(PropertySet ps) {
        properties.putAll(ps.getProperties());
    }
    
    public void addConfiguredProperty(Environment.Variable property) {
        properties.put(property.getKey(), property.getValue());
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * File out put name (default: empty) 
     */
    public void setOutputFileName(String fileName) {
        outputFileName = fileName;
    }
    
    abstract String getName();
    
    abstract Exporter configureExporter(Exporter exporter);
}
