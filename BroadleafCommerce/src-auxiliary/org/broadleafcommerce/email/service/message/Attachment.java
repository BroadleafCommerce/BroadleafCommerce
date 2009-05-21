package org.broadleafcommerce.email.service.message;

import java.io.Serializable;

public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String filename;
    private byte[] data;
    private String mimeType;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
