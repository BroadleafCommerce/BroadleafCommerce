package org.broadleafcommerce.common.file.service;

import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.type.FileApplicationType;

import java.io.File;
import java.util.List;

/**
 * Interface to be implemented by a FileProvider.   This could be a local FileProvider or a remote service like Amazon S3.
 * 
 * @author bpolster
 *
 */
public interface FileServiceProvider {
    
    /**
     * Returns a File representing the passed in name.
     * 
     * @param name - fully qualified path to the resource
     * @return
     */
    public File getResource(String name);

    /**
     * Returns a File representing the passed in name and application type.   Providers may choose to 
     * cache certain FileApplicationType(s) locally rather than retrieve them from a remote source.   
     *  
     * @param name - fully qualified path to the resource
     * @param fileApplicationType - applicationType
     * @return
     */
    public File getResource(String name, FileApplicationType fileApplicationType);

    /**
     * Takes in a work area and application type and moves all of the files to the configured FileProvider.
     * 
     * @param workArea
     * @param applicationType
     */
    public void addOrUpdateResources(FileWorkArea workArea, List<File> files);

    /**
     * Removes the resource from the file service.
     * 
     * @param name - fully qualified path to the resource
     * @return true if the resource was removed
     */
    public boolean removeResource(String name);
}
