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
package org.broadleafcommerce.common.util.sql;

import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;

/**
 * This is a re-worked version from Hibernate tools
 * 
 * @author jfischer
 *
 */
public class Hbm2DDLExporterTask extends ExporterTask {

    boolean exportToDatabase = true; 
    boolean scriptToConsole = true;
    boolean schemaUpdate = false;
    String delimiter = ";"; 
    boolean drop = false;
    boolean create = true;
    boolean format = false;
    
    private boolean haltOnError = false;
    
    public Hbm2DDLExporterTask(HibernateToolTask parent) {
        super(parent);
    }
    
    public String getName() {
        return "hbm2ddl (Generates database schema)";
    }

    protected Exporter configureExporter(Exporter exp) {
        Hbm2DDLExporter exporter = (Hbm2DDLExporter) exp;
        exporter.setExport(exportToDatabase);
        exporter.setConsole(scriptToConsole);
        exporter.setUpdate(schemaUpdate);
        exporter.setDelimiter(delimiter);
        exporter.setDrop(drop);
        exporter.setCreate(create);
        exporter.setFormat(format);
        exporter.setOutputFileName(outputFileName);
        exporter.setHaltonerror(haltOnError);       
        return exporter;
    }

    protected Exporter createExporter() {
        Hbm2DDLExporter exporter = new Hbm2DDLExporter(getConfiguration(), getDestdir());
        return exporter;
    }

    
    public void setExport(boolean export) {
        exportToDatabase = export;
    }
    
    /**
     * Run SchemaUpdate instead of SchemaExport
     */
    public void setUpdate(boolean update) {
        this.schemaUpdate = update;
    }
    
    /**
     * Output sql to console ? (default true)
     */
    public void setConsole(boolean console) {
        this.scriptToConsole = console;
    }
    
    /**
     * Format the generated sql
     */
    public void setFormat(boolean format) {
        this.format = format;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }
    
    public void setCreate(boolean create) {
        this.create = create;
    }
    
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
    
    public String getDelimiter() {
        return delimiter;
    }
    
    public void setHaltonerror(boolean haltOnError) {
        this.haltOnError  = haltOnError;
    }

}
