/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.resource.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.cache.StatisticsService;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileService;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.resource.BroadleafDefaultResourceResolverChain;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import de.jkeylockmanager.manager.LockCallback;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

/**
 * @see ResourceBundlingService
 * @author Andre Azzolini (apazzolini)
 * @author Brian Polster (bpolster)
 */
@Service("blResourceBundlingService")
public class ResourceBundlingServiceImpl implements ResourceBundlingService {
    protected static final Log LOG = LogFactory.getLog(ResourceBundlingServiceImpl.class);

    // Map of known unversioned bundle names ==> additional files that should be included
    // Configured via XML
    // ex: "global.js" ==> ["classpath:/file1.js", "/js/file2.js"]
    protected Map<String, List<String>> additionalBundleFiles = new HashMap<String, List<String>>();
            
    @javax.annotation.Resource(name = "blFileService")
    protected BroadleafFileService fileService;

    @javax.annotation.Resource(name = "blJsResources")
    protected ResourceHttpRequestHandler jsResourceHandler;

    @javax.annotation.Resource(name = "blCssResources")
    protected ResourceHttpRequestHandler cssResourceHandler;

    @javax.annotation.Resource(name="blStatisticsService")
    protected StatisticsService statisticsService;

    private KeyLockManager keyLockManager = KeyLockManagers.newLock();

    // Abundance of caution - a typical site would only have a few active bundles.  
    // Adding an LRU for extreme edge cases.
    private Cache<String, String> createdBundles = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(new CacheLoader<String, String>() {

                public String load(String key) {
                    return key;
                }
            });
    
    @Override
    public String resolveBundleResourceName(String requestedBundleName, String mappingPrefix, List<String> files) {
        if (mappingPrefix == null) {
            mappingPrefix = "";
        }
                
        ResourceHttpRequestHandler resourceRequestHandler = findResourceHttpRequestHandler(requestedBundleName);
        if (resourceRequestHandler != null && CollectionUtils.isNotEmpty(files)) {
            ResourceResolverChain resolverChain = new BroadleafDefaultResourceResolverChain(
                    resourceRequestHandler.getResourceResolvers());
            List<Resource> locations = resourceRequestHandler.getLocations();
                    
            StringBuilder combinedPathString = new StringBuilder();
            List<String> filePaths = new ArrayList<String>();
            for (String file : files) {
                String resourcePath = resolverChain.resolveUrlPath(mappingPrefix + file, locations);                
                filePaths.add(resourcePath);
                combinedPathString.append(resourcePath);
            }

            int version = combinedPathString.toString().hashCode();
            String versionedBundleName = mappingPrefix + addVersion(requestedBundleName, String.valueOf(version));
        
            createBundleIfNeeded(versionedBundleName, filePaths, resolverChain, locations);

            return versionedBundleName;
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn("");
            }
            return null;
        }
    }

    @Override
    public Resource resolveBundleResource(String versionedBundleResourceName) {
        return readBundle(versionedBundleResourceName);
    }

    protected void createBundleIfNeeded(final String versionedBundleName, final List<String> filePaths,
            final ResourceResolverChain resolverChain, final List<Resource> locations) {
        if (createdBundles.getIfPresent(versionedBundleName) == null) {
            keyLockManager.executeLocked(versionedBundleName, new LockCallback() {

                public void doInLock() {
                    Resource bundle = readBundle(versionedBundleName);
                    if (bundle == null || !bundle.exists()) {
                        Resource bundleResource = createBundle(versionedBundleName, filePaths, resolverChain, locations);
                        if (bundleResource != null) {
                            saveBundle(bundleResource);
                        }
                    }
                }
            });
        }
    }
    
    protected Resource createBundle(String versionedBundleName, List<String> filePaths,
            ResourceResolverChain resolverChain, List<Resource> locations) {

        HttpServletRequest req = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = null;
        
        // Join all of the resources for this bundle together into a byte[]
        try {
            for (String fileName : filePaths) {
                Resource r = resolverChain.resolveResource(req, fileName, locations);
                InputStream is = null;
                
                try {
                    is = r.getInputStream();
                    StreamUtils.copy(is, baos);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        is.close();
                    } catch (IOException e2) {
                        throw new RuntimeException(e2);
                    }
                }
                
                // If we're creating a JavaScript bundle, we'll put a semicolon between each
                // file to ensure it won't fail to compile.
                if (versionedBundleName.endsWith(".js")) {
                    baos.write(";".getBytes());
                }
                baos.write(System.getProperty("line.separator").getBytes());
            }
            bytes = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                baos.close();
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }
        
        // Create our GenerateResource that holds our combined bundle
        GeneratedResource r = new GeneratedResource(bytes, versionedBundleName);
        return r;
    }
    
    protected void saveBundle(Resource resource) {
        FileWorkArea tempWorkArea = fileService.initializeWorkArea();
        String tempFilename = FilenameUtils.concat(tempWorkArea.getFilePathLocation(), FilenameUtils.separatorsToSystem(getResourcePath(resource.getDescription())));
        File tempFile = new File(tempFilename);
        if (!tempFile.getParentFile().exists()) {
            if (!tempFile.getParentFile().mkdirs()) {
                if (!tempFile.getParentFile().exists()) {
                    throw new RuntimeException("Unable to create parent directories for file: " + tempFilename);
                }
            }
        }
        
        BufferedOutputStream out = null;
        InputStream ris = null;
        try {
            ris = resource.getInputStream();
            out = new BufferedOutputStream(new FileOutputStream(tempFile));
            StreamUtils.copy(ris, out);
            
            ris.close();
            out.close();
            
            fileService.addOrUpdateResource(tempWorkArea, tempFile, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ris);
            IOUtils.closeQuietly(out);
            fileService.closeWorkArea(tempWorkArea);
        }
    }
    
    protected String getCacheKey(String unversionedBundleName, List<String> files) {
        return unversionedBundleName;
    }
    
    protected String getBundleName(String bundleName, String version) {
        String bundleWithoutExtension = bundleName.substring(0, bundleName.lastIndexOf('.'));
        String bundleExtension = bundleName.substring(bundleName.lastIndexOf('.'));
        String versionedName = bundleWithoutExtension + version + bundleExtension;
        return versionedName;
    }
    
    protected String getBundleVersion(LinkedHashMap<String, Resource> foundResources) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Resource> entry : foundResources.entrySet()) {
            sb.append(entry.getKey());
            
            if (entry.getValue() instanceof GeneratedResource) {
                sb.append(((GeneratedResource) entry.getValue()).getHashRepresentation());
            } else {
                sb.append(entry.getValue().lastModified());
            }
            
            sb.append("\r\n");
        }
        String version = String.valueOf(sb.toString().hashCode());
        return version;
    }
    
    @Override
    public List<String> getAdditionalBundleFiles(String bundleName) {
        return additionalBundleFiles.get(bundleName);
    }

    public void setAdditionalBundleFiles(Map<String, List<String>> additionalBundleFiles) {
        this.additionalBundleFiles = additionalBundleFiles;
    }

    /**
     * Copied from Spring 4.1 AbstractVersionStrategy
     * @param requestPath
     * @param version
     * @return
     */
    protected String addVersion(String requestPath, String version) {
        String baseFilename = StringUtils.stripFilenameExtension(requestPath);
        String extension = StringUtils.getFilenameExtension(requestPath);
        return baseFilename + version + "." + extension;
    }
    
    protected Resource readBundle(String versionedBundleName) {
        File bundleFile = fileService.getResource(getResourcePath(versionedBundleName));
        return bundleFile == null ? null : new FileSystemResource(bundleFile);
    }
    

    protected ResourceHttpRequestHandler findResourceHttpRequestHandler(String resourceName) {
        resourceName = resourceName.toLowerCase();
        if (isJavaScriptResource(resourceName)) {
            return jsResourceHandler;
        } else if (isCSSResource(resourceName)) {
            return cssResourceHandler;
        } else {
            return null;
        }
    }
    
    protected boolean isJavaScriptResource(String resourceName) {
        return (resourceName != null && resourceName.contains(".js"));
    }
    
    protected boolean isCSSResource(String resourceName) {
        return (resourceName != null && resourceName.contains(".css"));
    }

    /**
     * Returns the resource path for the given <b>name</b> in URL-format (meaning, / separators)
     * @param name
     * @return
     */
    protected String getResourcePath(String name) {
        return "bundles/" + name;
    }
}
