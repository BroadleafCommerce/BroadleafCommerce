package org.broadleafcommerce.cms.admin.server.contoller;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Created by jfischer
 */
public class UploadMember {

    private String name;
    private CommonsMultipartFile fileData;
    private String callbackName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommonsMultipartFile getFileData() {
        return fileData;
    }

    public void setFileData(CommonsMultipartFile fileData) {
        this.fileData = fileData;
    }

    public String getCallbackName() {
        return callbackName;
    }

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }
}
