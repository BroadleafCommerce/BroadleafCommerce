package org.broadleafcommerce.cms.admin.server.upload;

import java.text.NumberFormat;

/**
 * Created by jfischer
 */
public class UploadProgressListener implements org.apache.commons.fileupload.ProgressListener {

    private static long bytesTransferred = 0;
    private static long fileSize = -100;
    private long totalBytesRead = 0;
    private long fiveKBRead = -1;
    private UploadInfoBean uploadInfoBean = null;

    public UploadProgressListener() {
        uploadInfoBean = new UploadInfoBean();
    }

    //function called from javascript to retrive the status of the upload
    public UploadInfoBean getStatus() {
        // per looks like 0% - 100%, remove % before submission
        uploadInfoBean.setTotalSize(fileSize / 1024);
        uploadInfoBean.setBytesRead(totalBytesRead / 1024);
        String per = NumberFormat.getPercentInstance().format((double) bytesTransferred / (double) fileSize);
        uploadInfoBean.setPercentage(Integer.parseInt(per.substring(0, per.length() - 1)));
        return uploadInfoBean;
    }
}
