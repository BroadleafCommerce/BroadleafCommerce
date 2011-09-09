package org.broadleafcommerce.cms.file.domain;

import java.sql.Blob;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/8/11
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StaticAssetStorage {

    Long getId();

    void setId(Long id);

    String getFullUrl();

    void setFullUrl(String fullUrl);

    Blob getFileData();

    void setFileData(Blob fileData);

}
