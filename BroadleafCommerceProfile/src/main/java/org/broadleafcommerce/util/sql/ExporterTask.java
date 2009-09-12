/*
 * Created on 13-Feb-2005
 *
 */
package org.broadleafcommerce.util.sql;

import java.io.File;
import java.util.Properties;

import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.PropertySet;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.Exporter;

/**
 * @author jfischer
 * 
 * Is not actually a ant task, but simply just a task part of a HibernateToolTask
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
