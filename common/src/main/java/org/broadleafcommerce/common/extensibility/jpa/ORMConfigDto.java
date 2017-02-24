/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.extensibility.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * <p>
 * Holder for mapping files (orm.xml) and entity classes to be contributed to the {@link MergePersistenceUnitManager} without being specified directly
 * in a persistence.xml file. Use cases this is designed to support:
 *
 * <ul>
 *  <li>Prividing a fully-qualified-classname to add to the managed classes of the persistence unit (like an additional {@literal @}Entity class)</li>
 *  <li>Providing a classpath-relative location to an orm.xml file to add to the mapping files for additional queries</li>
 * </ul>
 * 
 * <p>
 * Generally, this will be registered conditionally within an {@literal @}Conditional. Example:
 * 
 * <pre>
 * {@literal @}Bean
 * {@literal @}ConditionalOnClass("com.broadleafcommerce.somemodule.domain.DomainClass")
 * public ORMConfigDto newManagedDomainClass() {
 *     return new ORMConfigDto("blPU")
 *         .addClassName("com.broadleafcommerce.somemodule.domain.DomainClass");
 * }
 * </pre>
 * 
 * <p>
 * Note that this functionality can also be achieved by creating multiple {@code persistence.xml} files and conditionally adding it to
 * {@code blMergedPersistenceXmlLocations} like so:
 * 
 * <pre>
 * {@literal @}Merge("blMergedPersistenceXmlLocations")
 * {@literal @}ConditionalOnClass("com.broadleafcommerce.somemodule.domain.DomainClass")
 * public ORMConfigDto newManagedDomainClass() {
 *     return Arrays.asList("persistence-domainclass.xml");
 * }
 * </pre>
 * 
 * <p>
 * This can be attractive if you have a large set of classes or mapping files to dynamically add to a persistence unit.
 *  
 * @author Nick Crum ncrum
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link ORMConfigPersistenceUnitPostProcessor}
 */
public class ORMConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String puName;
    protected List<String> classNames = new ArrayList<>();
    protected List<String> mappingFiles = new ArrayList<>();
    
    /**
     * 
     * @param puName the persistence unit that this should be apart of (usually {@code "blPU"}
     */
    public ORMConfigDto(String puName) {
        this.puName = puName;
    }
    
    /**
     * @return the persistence unit that this should be apart of
     */
    public String getPuName() {
        return puName;
    }
    
    /**
     * Adds the given class name o the persistence unit. Note that this should generally <i>not</i> reference the class name by {@code DomainClass.class.getName()}.
     * Doing so will trigger the class to be loaded which can cause it to skip class transformation
     * @param className a fully-qualfied classname that should be added to this persistence unit's managed classes. This is equivalent to specifying an
     * additional {@code <class></class>} entry in a {@code persistence.xml}
     * @return {@code this}
     */
    public ORMConfigDto addClassName(String className) {
        this.classNames.add(className);
        return this;
    }
    
    /**
     * @see #addClassName(String)
     */
    public ORMConfigDto setClassNames(List<String> classNames) {
        this.classNames = classNames;
        return this;
    }
    
    @Nullable
    public List<String> getClassNames() {
        return classNames;
    }
    
    /**
     * Adds the given mapping file to the persistence unit
     * @param mappingFile a classpath-relative location to a .orm.xml file that contains additional HQL queries or other XML configuration. This is
     * equivalent to using the {@code <mapping-file></mapping-file>} entry in a persistence.xml
     * @return {@code this}
     */
    public ORMConfigDto addMappingFile(String mappingFile) {
        this.mappingFiles.add(mappingFile);
        return this;
    }
    
    /**
     * @see #addMappingFile(String)
     */
    public ORMConfigDto setMappingFiles(List<String> mappingFiles) {
        this.mappingFiles = mappingFiles;
        return this;
    }
    
    public List<String> getMappingFiles() {
        return mappingFiles;
    }

}
