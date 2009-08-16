package org.broadleafcommerce.admin.util.domain;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadBean {
    private MultipartFile file; 
    private String directory;
    
    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
    
    public MultipartFile getFile() {
        return file;
    }
}
