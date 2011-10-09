package org.broadleafcommerce.openadmin.server.service.artifact.upload;

/**
 * Created by jfischer
 */
public class UploadInfoBean {

    private long totalSize = 0;
    private long bytesRead = 0;
    private int percentage = 0;

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }
}
