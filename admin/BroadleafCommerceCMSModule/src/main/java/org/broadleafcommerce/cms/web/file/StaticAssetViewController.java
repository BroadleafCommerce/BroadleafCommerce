package org.broadleafcommerce.cms.web.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.openadmin.server.service.artifact.ArtifactService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by jfischer
 */
@Controller("blImageViewController")
public class StaticAssetViewController {

    private static final Log LOG = LogFactory.getLog(StaticAssetViewController.class);
    private static final File DEFAULTCACHEDIRECTORY = new File(System.getProperty("java.io.tmpdir"));

    protected String cacheDirectory;

    @Resource(name="blStaticAssetService")
    protected StaticAssetService staticAssetService;

    @Resource(name="blStaticAssetStorageService")
    protected StaticAssetStorageService staticAssetStorageService;

    @Resource(name="blArtifactService")
    protected ArtifactService artifactService;

    @RequestMapping(value = "{staticAssetId}", method = {RequestMethod.GET})
    public String viewItem(@PathVariable Long staticAssetId, ModelMap model, HttpServletRequest request) {
        StaticAsset staticAsset = (StaticAsset) staticAssetService.findStaticAssetById(staticAssetId);
        String cacheName = constructCacheFileName(staticAsset);
        File cacheFile = new File(cacheDirectory!=null?new File(cacheDirectory):DEFAULTCACHEDIRECTORY, cacheName);
        if (!cacheFile.exists()) {
            clearObsoleteCacheFiles(staticAsset, cacheFile);
            //createCacheFile(staticAsset, cacheFile);
        }
        model.addAttribute("cacheFile", cacheFile.getAbsolutePath());

    	return "staticAssetView";
    }

    protected void clearObsoleteCacheFiles(final StaticAsset staticAsset, final File cacheFile) {
        File parentDir = cacheFile.getParentFile();
        if (parentDir.exists()) {
            File[] obsoleteFiles = parentDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    if (s.startsWith(staticAsset.getName() + "---")) {
                        return true;
                    }
                    return false;
                }
            });
            if (obsoleteFiles != null) {
                for (File file : obsoleteFiles) {
                    try {
                        file.delete();
                    } catch (Throwable e) {
                        //do nothing
                    }
                }
            }
        }
    }

    protected void createCacheFile(StaticAsset staticAsset, File cacheFile) throws SQLException, IOException {
        StaticAssetStorage storage = staticAssetStorageService.readStaticAssetStorageByFullURL(staticAsset.getFullUrl());
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
            InputStream is = storage.getFileData().getBinaryStream();
            boolean eof = false;
            int temp = -1;
            while (!eof) {
                temp = is.read();
                if (temp < 0) {
                    eof = true;
                } else {
                    bos.write(temp);
                }
            }
        } finally {
            if (bos != null) {
                try {
                    bos.flush();
                    bos.close();
                } catch (Throwable e) {
                    //do nothing
                }
            }
        }
    }

    protected String constructCacheFileName(StaticAsset staticAsset) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        StringBuffer sb = new StringBuffer();
        sb.append(staticAsset.getFullUrl());
        sb.append("---");
        sb.append(format.format(staticAsset.getAuditable().getDateUpdated()==null?staticAsset.getAuditable().getDateCreated():staticAsset.getAuditable().getDateUpdated()));
        sb.append(".tmp");

        return sb.toString();
    }

    public String getCacheDirectory() {
        return cacheDirectory;
    }

    public void setCacheDirectory(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }
}
