package org.broadleafcommerce.openadmin.server.service.artifact.upload;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/8/11
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadedFile {

	private static final ThreadLocal<Map<String, MultipartFile>> upload = new ThreadLocal<Map<String, MultipartFile>>();

	public static Map<String, MultipartFile> getUpload() {
		Map<String, MultipartFile> response = UploadedFile.upload.get();
        if (response == null) {
            return new HashMap<String, MultipartFile>();
        }
        return response;
	}

	public static void setUpload(Map<String, MultipartFile> upload) {
		UploadedFile.upload.set(upload);
	}

    public static void remove() {
        UploadedFile.upload.remove();
    }

}
