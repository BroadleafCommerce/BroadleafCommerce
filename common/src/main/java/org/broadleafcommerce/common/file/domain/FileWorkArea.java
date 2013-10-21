package org.broadleafcommerce.common.file.domain;

/**
 * Represents a temporary location on the fileSystem.
 * 
 * Used by the file-service as a reference point when managing files for a user.
 * 
 * @author bpolster
 *
 */
public class FileWorkArea {

    public String filePathLocation;
    
    public String getFilePathLocation() {
        return filePathLocation;
    }
    
    public void setFilePathLocation(String filePathLocation) {
        this.filePathLocation = filePathLocation;
    }
    
}
