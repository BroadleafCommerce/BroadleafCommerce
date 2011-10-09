package org.broadleafcommerce.cms.file.dao;

import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/9/11
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface StaticAssetStorageDao {
    StaticAssetStorage create();

    StaticAssetStorage readStaticAssetStorageById(Long id);

    public StaticAssetStorage readStaticAssetStorageByStaticAssetId(Long id);

    StaticAssetStorage save(StaticAssetStorage assetStorage);

    void delete(StaticAssetStorage assetStorage);

    public Blob createBlob(MultipartFile uploadedFile) throws IOException;
}
