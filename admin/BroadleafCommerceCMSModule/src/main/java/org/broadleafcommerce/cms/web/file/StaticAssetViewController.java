package org.broadleafcommerce.cms.web.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.openadmin.server.service.artifact.ArtifactService;
import org.broadleafcommerce.openadmin.server.service.artifact.image.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jfischer
 */
@Controller("blStaticAssetViewController")
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

    private List<CleanupOperation> operations = new ArrayList<CleanupOperation>();

    protected Thread cleanupThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                List<CleanupOperation> myList = new ArrayList<CleanupOperation>();
                synchronized (operations) {
                    myList.addAll(operations);
                    operations.clear();
                }
                for (final CleanupOperation operation : myList) {
                    File parentDir = operation.parentDir;
                    if (parentDir.exists()) {
                        File[] obsoleteFiles = parentDir.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File file, String s) {
                                if (s.startsWith(operation.assetName + "---")) {
                                    return true;
                                }
                                return false;
                            }
                        });
                        if (obsoleteFiles != null) {
                            for (File file : obsoleteFiles) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Deleting obsolete asset cache file: " + file.getAbsolutePath());
                                }
                                try {
                                    file.delete();
                                } catch (Throwable e) {
                                    //do nothing
                                }
                            }
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Throwable e) {
                        //do nothing
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10000);
            } catch (Throwable e) {
                //do nothing
            }
        }
    }, "CMSStaticAssetCleanupThread");

    public StaticAssetViewController() {
        cleanupThread.start();
    }

    @RequestMapping(value = "/{staticAssetId}.*", method = {RequestMethod.GET})
    public ModelAndView viewItem(@PathVariable Long staticAssetId, HttpServletRequest request) {
        try {
            StaticAsset staticAsset = (StaticAsset) staticAssetService.findStaticAssetById(staticAssetId);
            String cacheName = constructCacheFileName(staticAsset, request.getParameterMap());
            File cacheFile = new File(cacheDirectory!=null?new File(cacheDirectory):DEFAULTCACHEDIRECTORY, cacheName);
            if (!cacheFile.exists()) {
                clearObsoleteCacheFiles(staticAsset, cacheFile);
                StaticAssetStorage storage = staticAssetStorageService.readStaticAssetStorageByFullURL(staticAsset.getFullUrl());
                if (!request.getParameterMap().isEmpty()) {
                    //there are filter operations to perform on the asset
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    InputStream is = null;
                    try {
                        is = storage.getFileData().getBinaryStream();
                        boolean eof = false;
                        while (!eof) {
                            int temp = is.read();
                            if (temp < 0) {
                                eof = true;
                            } else {
                                baos.write(temp);
                            }
                        }
                        baos.flush();
                    } finally {
                        if (is != null) {
                            try{
                                is.close();
                            } catch (Throwable e) {}
                        }
                    }
                    InputStream original = new ByteArrayInputStream(baos.toByteArray());
                    Operation[] operations = artifactService.buildOperations(request.getParameterMap(), original, staticAsset.getMimeType());
                    InputStream converted = artifactService.convert(original, operations, staticAsset.getMimeType());
                    createCacheFile(converted, cacheFile);
                } else {
                    createCacheFile(storage.getFileData().getBinaryStream(), cacheFile);
                }
            }
            Map<String, String> model = new HashMap<String, String>();
            model.put("cacheFilePath", cacheFile.getAbsolutePath());
            model.put("mimeType", staticAsset.getMimeType());

            return new ModelAndView("blStaticAssetView", model);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected void clearObsoleteCacheFiles(final StaticAsset staticAsset, final File cacheFile) {
        File parentDir = cacheFile.getParentFile();
        if (parentDir.exists()) {
            CleanupOperation operation = new CleanupOperation();
            operation.setAssetName(staticAsset.getName());
            operation.setParentDir(parentDir);
            synchronized (operations) {
                operations.add(operation);
            }
        }
    }

    protected void createCacheFile(InputStream is, File cacheFile) throws SQLException, IOException {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
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

    protected String constructCacheFileName(StaticAsset staticAsset, Map<String, String[]> parameterMap) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        StringBuffer sb = new StringBuffer();
        sb.append(staticAsset.getFullUrl());
        sb.append("---");
        sb.append(format.format(staticAsset.getAuditable().getDateUpdated()==null?staticAsset.getAuditable().getDateCreated():staticAsset.getAuditable().getDateUpdated()));
        for (String key : parameterMap.keySet()) {
            sb.append("-");
            sb.append(key);
            sb.append("-");
            sb.append(parameterMap.get(key)[0]);
        }
        sb.append(".tmp");

        return sb.toString();
    }

    public String getCacheDirectory() {
        return cacheDirectory;
    }

    public void setCacheDirectory(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public class CleanupOperation {

        private String assetName;
        private File parentDir;

        public String getAssetName() {
            return assetName;
        }

        public void setAssetName(String assetName) {
            this.assetName = assetName;
        }

        public File getParentDir() {
            return parentDir;
        }

        public void setParentDir(File parentDir) {
            this.parentDir = parentDir;
        }
    }
}
