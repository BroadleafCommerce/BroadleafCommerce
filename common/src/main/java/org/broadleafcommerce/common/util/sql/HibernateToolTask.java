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

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.broadleafcommerce.common.extensibility.context.MergeFileSystemAndClassPathXMLApplicationContext;
import org.broadleafcommerce.common.extensibility.context.StandardConfigLocations;
import org.broadleafcommerce.common.extensibility.jpa.ConfigurationOnlyState;
import org.hibernate.MappingNotFoundException;
import org.hibernate.tool.ant.ConfigurationTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This is a re-worked version from Hibernate tools
 * 
 * @author jfischer
 *
 */
public class HibernateToolTask extends Task {

    public HibernateToolTask() {
        super();
    }
    @SuppressWarnings("rawtypes")
    protected List configurationTasks = new ArrayList();
    protected File destDir;
    @SuppressWarnings("rawtypes")
    protected List generators = new ArrayList();
    protected List<Task> appContexts = new ArrayList<Task>();
    protected Path classPath;
    protected boolean combinePersistenceUnits = true;
    protected boolean refineFileNames = true;
    
    public ExporterTask createHbm2DDL() {
        ExporterTask generator = new Hbm2DDLExporterTask(this);
        addGenerator( generator );
        return generator;
    }
    
    public ClassPathApplicationContextTask createClassPathApplicationContext() {
        ClassPathApplicationContextTask task = new ClassPathApplicationContextTask();
        appContexts.add(task);
        return task;
    }
    
    public FileSystemApplicationContextTask createFileSystemApplicationContext() {
        FileSystemApplicationContextTask task = new FileSystemApplicationContextTask();
        appContexts.add(task);
        return task;
    }
    
    public JPAConfigurationTask createJPAConfiguration() {
        JPAConfigurationTask task = new JPAConfigurationTask();
        addConfiguration(task);
        return task;
    }
    
    @SuppressWarnings("unchecked")
    protected boolean addConfiguration(ConfigurationTask config) {
        return configurationTasks.add(config);
    }

    @SuppressWarnings("unchecked")
    protected boolean addGenerator(ExporterTask generator) {
        return generators.add(generator);
    }
    
    /**
     * Set the classpath to be used when running the Java class
     *
     * @param s an Ant Path object containing the classpath.
     */
    public void setClasspath(Path s) {
        classPath = s;
    }
    
    
    /**
     * Adds a path to the classpath.
     *
     * @return created classpath
     */
    public Path createClasspath() {
        classPath = new Path(getProject() );
        return classPath;
    }

    @Override
    public void execute() {
        MergeFileSystemAndClassPathXMLApplicationContext mergeContext;
        try {
            ConfigurationOnlyState state = new ConfigurationOnlyState();
            state.setConfigurationOnly(true);
            ConfigurationOnlyState.setState(state);
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            // launch the service merge application context to get the entity configuration for the entire framework
            String[] contexts = StandardConfigLocations.retrieveAll(StandardConfigLocations.TESTCONTEXTTYPE);
            LinkedHashMap<String, MergeFileSystemAndClassPathXMLApplicationContext.ResourceType> locations = new LinkedHashMap<String, MergeFileSystemAndClassPathXMLApplicationContext.ResourceType>();
            for (String context : contexts) {
                locations.put(context, MergeFileSystemAndClassPathXMLApplicationContext.ResourceType.CLASSPATH);
            }
            for (Task task : appContexts) {
                if (task instanceof ClassPathApplicationContextTask) {
                    locations.put(((ClassPathApplicationContextTask) task).getPath(), MergeFileSystemAndClassPathXMLApplicationContext.ResourceType.CLASSPATH);
                } else if (task instanceof FileSystemApplicationContextTask) {
                    locations.put(((FileSystemApplicationContextTask) task).getPath(), MergeFileSystemAndClassPathXMLApplicationContext.ResourceType.FILESYSTEM);
                }
            }
            mergeContext = new MergeFileSystemAndClassPathXMLApplicationContext(locations, null);
        } catch (Exception e) {
            throw new BuildException(e, getLocation());
        } finally {
            ConfigurationOnlyState.setState(null);
        }
        int count = 1;
        ExporterTask generatorTask = null;
        try {
            for (Object configuration : configurationTasks) {
                JPAConfigurationTask configurationTask = (JPAConfigurationTask) configuration;
                log("Executing Hibernate Tool with a " + configurationTask.getDescription());
                @SuppressWarnings("rawtypes")
                Iterator iterator = generators.iterator();
                while (iterator.hasNext()) {
                    generatorTask = (ExporterTask) iterator.next();
                    log(count++ + ". task: " + generatorTask.getName());
                    generatorTask.setOutputFileName(configurationTask.getDialect() + "_" + configurationTask.getPersistenceUnit() + ".sql");
                    generatorTask.setDestdir(destDir);
                    generatorTask.setConfiguration(configurationTask.createConfiguration(mergeContext));
                    generatorTask.execute();
                }
            }
        } catch (RuntimeException re) {
            reportException(re, count, generatorTask);
        }
        try {
            if (combinePersistenceUnits) {
                ArrayList<File> combine = new ArrayList<File>();
                for (Object configuration : configurationTasks) {
                    JPAConfigurationTask configurationTask = (JPAConfigurationTask) configuration;
                    File[] sqlFiles = destDir.listFiles(new SqlFileFilter());
                    for (File file : sqlFiles) {
                        if (file.getName().startsWith(configurationTask.getDialect())){
                            combine.add(file);
                        }
                    }
                    combineFiles(combine);
                    combine.clear();
                }
            }
            if (refineFileNames) {
                File[] sqlFiles = destDir.listFiles(new SqlFileFilter());
                for (File file : sqlFiles) {
                    String filename = file.getName();
                    String[] starters = {"org.hibernate.dialect.", "org.broadleafcommerce.profile.util.sql."};
                    for (String starter : starters) {
                        if (filename.startsWith(starter)) {
                            String newFileName = filename.substring(starter.length(), filename.length());
                            file.renameTo(new File(destDir, newFileName));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new BuildException(e, getLocation());
        }
    }
    
    private void combineFiles(ArrayList<File> combine) throws Exception {
        Iterator<File> itr = combine.iterator();
        File startFile = itr.next();
        while(itr.hasNext()) {
            File nextFile = itr.next();
            BufferedWriter writer = null;
            BufferedReader reader = null;
            try{
                writer = new BufferedWriter(new FileWriter(startFile, true));
                reader = new BufferedReader(new FileReader(nextFile));
                boolean eof = false;
                String temp = null;
                while (!eof) {
                    temp = reader.readLine();
                    if (temp == null) {
                        eof = true;
                    } else {
                        writer.write(temp);
                        writer.write("\n");
                    }
                }
            } finally {
                if (writer != null) {
                    try{ writer.close(); } catch (Throwable e) {};
                }
                if (reader != null) {
                    try{ reader.close(); } catch (Throwable e) {};
                }
            }
            try{
                nextFile.delete();
            } catch (Throwable e) {}
        }
    }

    private void reportException(Throwable re, int count, ExporterTask generatorTask) {
        log("An exception occurred while running exporter #" + count + ":" + generatorTask.getName(), Project.MSG_ERR);
        log("To get the full stack trace run ant with -verbose", Project.MSG_ERR);
        
        log(re.toString(), Project.MSG_ERR);
        String ex = new String();
        Throwable cause = re.getCause();
        while(cause!=null) {
            ex += cause.toString() + "\n";
            if(cause==cause.getCause()) {
                break; // we reached the top.
            } else {
                cause=cause.getCause();
            }
        }
        if(StringUtils.isNotEmpty(ex)) {
            log(ex, Project.MSG_ERR);
        }

        String newbieMessage = getProbableSolutionOrCause(re);
        if(newbieMessage!=null) {
            log(newbieMessage);
        }       
        
        if(re instanceof BuildException) {
            throw (BuildException)re;
        } else {
            throw new BuildException(re, getLocation());
        }
    }

    private String getProbableSolutionOrCause(Throwable re) {
        if(re==null) return null;
        
        if(re instanceof MappingNotFoundException) {
            MappingNotFoundException mnf = (MappingNotFoundException)re;
            if("resource".equals(mnf.getType())) {
                return "A " + mnf.getType() + " located at " + mnf.getPath() + " was not found.\n" +
                        "Check the following:\n" +
                        "\n" +
                        "1) Is the spelling/casing correct ?\n" +
                        "2) Is " + mnf.getPath() + " available via the classpath ?\n" +
                        "3) Does it actually exist ?\n";                        
            } else {
                return "A " + mnf.getType() + " located at " + mnf.getPath() + " was not found.\n" +
                "Check the following:\n" +
                "\n" +
                "1) Is the spelling/casing correct ?\n" +
                "2) Do you permission to access " + mnf.getPath() + " ?\n" +
                "3) Does it actually exist ?\n";    
            }
        }

        if(re instanceof ClassNotFoundException || re instanceof NoClassDefFoundError) {
            
            return "A class were not found in the classpath of the Ant task.\n" +
                    "Ensure that the classpath contains the classes needed for Hibernate and your code are in the classpath.\n";            
            
        }
        
        if(re instanceof UnsupportedClassVersionError) {
            return "You are most likely running the ant task with a JRE that is older than the JRE required to use the classes.\n" +
                    "e.g. running with JRE 1.3 or 1.4 when using JDK 1.5 annotations is not possible.\n" +
                    "Ensure that you are using a correct JRE.";
        }
        
        
        
        if(re.getCause()!=re) {
            return getProbableSolutionOrCause( re.getCause() );                 
        }
        
        return null;
    }

    public File getDestdir() {
        return destDir;
    }

    public void setDestdir(File destDir) {
        this.destDir = destDir;
    }

    public boolean isCombinePersistenceUnits() {
        return combinePersistenceUnits;
    }

    public void setCombinePersistenceUnits(boolean combinePersistenceUnits) {
        this.combinePersistenceUnits = combinePersistenceUnits;
    }

    public boolean isRefineFileNames() {
        return refineFileNames;
    }

    public void setRefineFileNames(boolean refineFileNames) {
        this.refineFileNames = refineFileNames;
    }
    
    private class SqlFileFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".sql");
        }
        
    }
}
