package org.broadleafcommerce.cms.admin.server.upload;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by jfischer
 */
public class UploadMember {

    private String name;
    private MultipartFile file;
    private String callbackName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getCallbackName() {
        return callbackName;
    }

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }
}
