package org.broadleafcommerce.cms.file.service;

import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/9/11
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public interface StaticAssetStorageService {
    StaticAssetStorage findStaticAssetStorageById(Long id);

    StaticAssetStorage create();

    StaticAssetStorage readStaticAssetStorageByStaticAssetId(Long id);

    StaticAssetStorage save(StaticAssetStorage assetStorage);

    void delete(StaticAssetStorage assetStorage);

    public Blob createBlob(MultipartFile uploadedFile) throws IOException;

}
