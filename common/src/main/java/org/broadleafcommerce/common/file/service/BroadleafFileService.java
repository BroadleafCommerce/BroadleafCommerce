package org.broadleafcommerce.common.file.service;

import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.type.FileApplicationType;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Many components in the Broadleaf Framework can benefit from creating and manipulating temporary files as well
 * as storing and accessing files in a remote repository (such as AmazonS3).
 * 
 * This service provides a pluggable way to provide those services.
 * 
 * @author bpolster
 *
 */
public interface BroadleafFileService {

    /**
     * Create a file work area that can be used for further operations. 
     * @return
     */
    public FileWorkArea initializeWorkArea();

    /**
     * Closes the passed in work area.   This method will delete all items contained in the work area.   Future calls
     * using this WorkArea will cause a RuntimeError
     * @param Work Area
     */
    public void closeWorkArea(FileWorkArea workArea);

    /**
     * Returns a File representing the passed in name.  This method will always access the file via the FileProvider
     * which might be a remote operation as opposed to the {@link #getResource(String, FileApplicationType)} method
     * which may access a copy of the file that is stored locally.
     * 
     * @param name - fully qualified path to the resource
     * @param applicationType - The type of file being accessed
     * @return
     */
    public File getResource(String name);

    /**
     * Returns a File representing the passed in name and application type.   The application
     * type provides an opportunity for the provider to cache the file locally for infrequently change files.  
     * 
     * @param name - fully qualified path to the resource
     * @param applicationType - The type of file being accessed
     * @return
     */
    public File getResource(String name, FileApplicationType applicationType);

    /**   
     * Allows assets to be included in the Java classpath.   
     * 
     * This method was designed to support an internal Broadleaf use case and may not have general applicability 
     * beyond that.    For Broadleaf demo sites, many of the product images are shared across the demo sites.   
     * 
     * Rather than copy those images, they are stored in a Jar file and shared by all of the sites.
     * 
     * @param name - fully qualified path to the resource
     * @return
     */
    public InputStream getClasspathResource(String name);

    /**
     * Removes the resource from the configured FileProvider
     * 
     * @param name - fully qualified path to the resource
     * @param applicationType - The type of file being accessed
     */
    public boolean removeResource(String name);

    /**
     * Takes in a temporary work area and a single File and copies that files to 
     * the configured FileProvider's permanent storage.
     * 
     * @param workArea
     * @param fileName
     */
    public void addOrUpdateResource(FileWorkArea workArea, File file);

    /**
     * Takes in a temporary work area and copies all of the files to the configured FileProvider's permanent storage.
     * 
     * @param workArea
     */
    public void addOrUpdateResources(FileWorkArea workArea);

    /**
     * Takes in a temporary work area and a list of Files and copies them to 
     * the configured FileProvider's permanent storage.
     * 
     * @param workArea
     * @param fileNames
     */
    public void addOrUpdateResources(FileWorkArea workArea, List<File> files);

}
