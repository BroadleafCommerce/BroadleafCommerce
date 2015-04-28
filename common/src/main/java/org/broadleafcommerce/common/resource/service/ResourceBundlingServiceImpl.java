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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.cache.CacheStatType;
import org.broadleafcommerce.common.cache.StatisticsService;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileService;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.common.web.resource.ResourceRequestExtensionHandler;
import org.broadleafcommerce.common.web.resource.ResourceRequestExtensionManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @see ResourceBundlingService
 * @author Andre Azzolini (apazzolini)
 */
@Service("blResourceBundlingService")
public class ResourceBundlingServiceImpl implements ResourceBundlingService {
    protected static final Log LOG = LogFactory.getLog(ResourceBundlingServiceImpl.class);
    
    // Map of known versioned bundle names ==> the resources that are part of that bundle
    // ex: "global12345.js" ==> [Resource("/js/BLC.js"), Resource("/js/blc-admin.js")]
    protected Map<String, Collection<Resource>> bundles = new HashMap<String, Collection<Resource>>();
    
    // Map of known bundle names ==> bundle version
    // ex: "global.js" ==> "global12345.js"
    protected Cache bundleVersionsCache;
    
    // Map of known unversioned bundle names ==> additional files that should be included
    // Configured via XML
    // ex: "global.js" ==> ["classpath:/file1.js", "/js/file2.js"]
    protected Map<String, List<String>> additionalBundleFiles = new HashMap<String, List<String>>();
    
    @javax.annotation.Resource(name = "blFileService")
    protected BroadleafFileService fileService;
    
    @javax.annotation.Resource(name = "blResourceMinificationService")
    protected ResourceMinificationService minifyService;

    @javax.annotation.Resource(name="blStatisticsService")
    protected StatisticsService statisticsService;
    
    @javax.annotation.Resource(name = "blResourceRequestExtensionManager")
    protected ResourceRequestExtensionManager extensionManager;

    @Override
    public Resource getBundle(String versionedBundleName) {
        // If we can find this bundle on the file system, we've already generated it
        // and we don't need to do so again.
        Resource r = readBundle(versionedBundleName);
        if (r != null && r.exists()) {
            return r;
        }
        
        // Otherwise, we'll create the bundle, write it to the file system, and return
        r = createBundle(versionedBundleName);
        saveBundle(r);
        return r;
    }
    
    protected Resource readBundle(String versionedBundleName) {
        File bundleFile = fileService.getResource(getResourcePath(versionedBundleName));
        return bundleFile == null ? null : new FileSystemResource(bundleFile);
    }
    
    /**
     * Returns the resource path for the given <b>name</b> in URL-format (meaning, / separators)
     * @param name
     * @return
     */
    protected String getResourcePath(String name) {
        return "bundles/" + name;
    }
    
    protected Resource createBundle(String versionedBundleName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = null;
        
        // Join all of the resources for this bundle together into a byte[]
        try {
            for (Resource r : bundles.get(versionedBundleName)) {
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
        
        // Minify the resource
        byte[] minifiedBytes = minifyService.minify(versionedBundleName, bytes);
        
        // Create our GenerateResource that holds our combined and (potentially) minified bundle
        GeneratedResource r = new GeneratedResource(minifiedBytes, versionedBundleName);
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
    
    @Override
    public String getVersionedBundleName(String unversionedBundleName, List<String> files) {
        Element e = getBundleVersionsCache().get(unversionedBundleName);
        if (e == null) {
            statisticsService.addCacheStat(CacheStatType.RESOURCE_BUNDLING_CACHE_HIT_RATE.toString(), false);
        } else {
            statisticsService.addCacheStat(CacheStatType.RESOURCE_BUNDLING_CACHE_HIT_RATE.toString(), true);
        }
        return e == null ? null : (String) e.getValue();
    }
    
    @Override
    public boolean hasBundle(String versionedBundle) {
        return bundles.containsKey(versionedBundle);
    }
    
    @Override
    public synchronized String registerBundle(String bundleName, List<String> files, 
            ResourceHttpRequestHandler handler) throws IOException {
        LinkedHashMap<String, Resource> foundResources = new LinkedHashMap<String, Resource>();
        
        // With Themes, this property will never work since the "bundleName" coming in is 
        // a hashed name based on theme files and update time-stamps.   Leaving in place for
        // community use.
        if (additionalBundleFiles.get(bundleName) != null) {
            files.addAll(additionalBundleFiles.get(bundleName));
        }
        
        for (String file : files) {
            boolean match = false;
            
            // Check to see if there is any registered handler that understands how to generate
            // this file.

            // TODO: This approach no longer works
            /**
            if (handler.getHandlers() != null) {
                for (AbstractGeneratedResourceHandler h : handler.getHandlers()) {
                    if (h.canHandle(file)) {
            			foundResources.put(file, h.getResource(file, handler.getLocations()));
            			match = true;
            			break;
                    }
                }
            }
            **/
    		
    		// If we didn't find a generator that could handle this file, let's see if we can 
    		// look it up from our known locations
            if (!match) {
                ExtensionResultHolder erh = new ExtensionResultHolder();
                extensionManager.getProxy().getOverrideResource(file, erh);
                if (erh.getContextMap().get(ResourceRequestExtensionHandler.RESOURCE_ATTR) != null) {
                    foundResources.put(file, (Resource) erh.getContextMap().get(ResourceRequestExtensionHandler.RESOURCE_ATTR));
                    match = true;
                }
            }

    		// If we didn't find an override for this file, let's see if we can 
    		// look it up from our known locations
    		if (!match) {
        		for (Resource location : handler.getLocations()) {
        			try {
        				Resource resource = location.createRelative(file);
        				if (resource.exists() && resource.isReadable()) {
        				    foundResources.put(file, resource);
        				    match = true;
        				    break;
        				}
        			}
        			catch (IOException ex) {
        				LOG.debug("Failed to create relative resource - trying next resource location", ex);
        			}
        		}
    		}
        }
        
        // Create a hash of the contents of the resources in this bundle.
        String resourcesHashedValue = getBundleVersion(foundResources);

        // Use the hash to create a unique bundle name
        String versionedName = getBundleName(bundleName, resourcesHashedValue);
        
        bundles.put(versionedName, foundResources.values());
        getBundleVersionsCache().put(new Element(getCacheKey(bundleName, files), versionedName));
        
        return versionedName;
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

    public Map<String, List<String>> getAdditionalBundleFiles() {
        return additionalBundleFiles;
    }
    
    public void setAdditionalBundleFiles(Map<String, List<String>> additionalBundleFiles) {
        this.additionalBundleFiles = additionalBundleFiles;
    }
    
    @Override
    public Cache getBundleVersionsCache() {
        if (bundleVersionsCache == null) {
            bundleVersionsCache = CacheManager.getInstance().getCache("blBundleElements");
        }
        return bundleVersionsCache;
    }
    
    @Override
    public Map<String, Collection<Resource>> getBundles() {
        return bundles;
    }

}
