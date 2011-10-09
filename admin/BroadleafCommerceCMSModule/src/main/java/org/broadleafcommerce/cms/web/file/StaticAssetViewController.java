/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.web.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.service.artifact.ArtifactService;
import org.broadleafcommerce.openadmin.server.service.artifact.image.Operation;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jfischer
 */
@Controller("blStaticAssetViewController")
public class StaticAssetViewController {

    private static final String SANDBOX_ADMIN_ID_VAR = "blAdminCurrentSandboxId";
    private static String SANDBOX_ID_VAR = "blSandboxId";

    private static final Log LOG = LogFactory.getLog(StaticAssetViewController.class);
    private static final File DEFAULTCACHEDIRECTORY = new File(System.getProperty("java.io.tmpdir"));

    protected String cacheDirectory;

    @Resource(name="blStaticAssetService")
    protected StaticAssetService staticAssetService;

    @Resource(name="blStaticAssetStorageService")
    protected StaticAssetStorageService staticAssetStorageService;

    @Resource(name="blArtifactService")
    protected ArtifactService artifactService;

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    private List<CleanupOperation> operations = new ArrayList<CleanupOperation>();

    protected Thread cleanupThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    List<CleanupOperation> myList = new ArrayList<CleanupOperation>();
                    synchronized (operations) {
                        myList.addAll(operations);
                        operations.clear();
                    }
                    for (final CleanupOperation operation : myList) {
                        File parentDir = operation.cacheFile.getParentFile();
                        if (parentDir.exists()) {
                            File[] obsoleteFiles = parentDir.listFiles(new FilenameFilter() {
                                @Override
                                public boolean accept(File file, String s) {
                                    if (s.startsWith(operation.assetName + "---") && !operation.getCacheFile().getName().equals(s)) {
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
        }
    }, "CMSStaticAssetCleanupThread");

    public StaticAssetViewController() {
        cleanupThread.start();
    }

    @RequestMapping(value = "/**/{fileName}", method = {RequestMethod.GET})
    public ModelAndView viewItem(@PathVariable String fileName, HttpServletRequest request) {
        try {
            String fullUrl = request.getPathInfo();
            String sandBoxId = (String) request.getSession().getAttribute(SANDBOX_ID_VAR);
            if (sandBoxId == null) {
                sandBoxId = (String) request.getSession().getAttribute(SANDBOX_ADMIN_ID_VAR);
            }
            SandBox sandBox;
            if (sandBoxId != null) {
                sandBox = sandBoxService.retrieveSandboxById(Long.valueOf(sandBoxId));
            } else {
                sandBox = null;
            }
            StaticAsset staticAsset = (StaticAsset) staticAssetService.findStaticAssetByFullUrl(fullUrl, sandBox);
            if (staticAsset == null && sandBox != null) {
                staticAsset = (StaticAsset) staticAssetService.findStaticAssetByFullUrl(fullUrl, null);
            }
            if (staticAsset == null) {
                throw new RuntimeException("Unable to find an asset for the url (" + fullUrl + ") using the sandBox id (" + sandBoxId + "), or the production sandBox.");
            }
            String mimeType = staticAsset.getMimeType();
            String cacheName = constructCacheFileName(staticAsset, request.getParameterMap());
            File cacheFile = new File(cacheDirectory!=null?new File(cacheDirectory):DEFAULTCACHEDIRECTORY, cacheName);
            if (!cacheFile.exists()) {
                clearObsoleteCacheFiles(staticAsset, cacheFile);
                StaticAssetStorage storage = staticAssetStorageService.readStaticAssetStorageByStaticAssetId(staticAsset.getId());
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
                    if (mimeType.equals("image/gif")) {
                        mimeType = "image/png";
                    }
                } else {
                    createCacheFile(storage.getFileData().getBinaryStream(), cacheFile);
                }
            }
            Map<String, String> model = new HashMap<String, String>();
            model.put("cacheFilePath", cacheFile.getAbsolutePath());
            model.put("mimeType", mimeType);

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
            operation.setCacheFile(cacheFile);
            synchronized (operations) {
                operations.add(operation);
            }
        }
    }

    protected void createCacheFile(InputStream is, File cacheFile) throws SQLException, IOException {
        if (!cacheFile.getParentFile().exists()) {
            cacheFile.getParentFile().mkdirs();
        }
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
        StringBuffer sb = new StringBuffer();
        sb.append(staticAsset.getFullUrl().substring(0, staticAsset.getFullUrl().lastIndexOf(".")));
        sb.append("---");

        StringBuffer sb2 = new StringBuffer();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        sb2.append(format.format(staticAsset.getAuditable().getDateUpdated()==null?staticAsset.getAuditable().getDateCreated():staticAsset.getAuditable().getDateUpdated()));
        for (String key : parameterMap.keySet()) {
            sb2.append("-");
            sb2.append(key);
            sb2.append("-");
            sb2.append(parameterMap.get(key)[0]);
        }

        String digest;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(sb2.toString().getBytes());
            BigInteger number = new BigInteger(1,messageDigest);
            digest = number.toString(16);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        sb.append(pad(digest, 32, '0'));
        sb.append(".");
        sb.append(staticAsset.getFileExtension());

        return sb.toString();
    }

    protected String pad(String s, int length, char pad) {
        StringBuffer buffer = new StringBuffer(s);
        while (buffer.length() < length) {
            buffer.insert(0, pad);
        }
        return buffer.toString();
    }

    public String getCacheDirectory() {
        return cacheDirectory;
    }

    public void setCacheDirectory(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public class CleanupOperation {

        private String assetName;
        private File cacheFile;

        public String getAssetName() {
            return assetName;
        }

        public void setAssetName(String assetName) {
            this.assetName = assetName;
        }

        public File getCacheFile() {
            return cacheFile;
        }

        public void setCacheFile(File cacheFile) {
            this.cacheFile = cacheFile;
        }
    }
}
