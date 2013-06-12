/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.web.resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.common.util.StopWatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

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
    protected static final String DEFAULT_STORAGE_DIRECTORY = System.getProperty("java.io.tmpdir");
    
    // Map of known versioned bundle names ==> the resources that are part of that bundle
    // ex: "global12345.js" ==> [Resource("/js/BLC.js"), Resource("/js/blc-admin.js")]
    protected Map<String, Collection<Resource>> bundles = new HashMap<String, Collection<Resource>>();
    
    // Map of known bundle names ==> bundle version
    // ex: "global.js" ==> "global12345.js"
    protected Map<String, String> bundleVersions = new HashMap<String, String>();
    
    @Value("${asset.server.file.system.path}")
    protected String assetFileSystemPath;
    
    @javax.annotation.Resource(name = "blResourceMinificationService")
    protected ResourceMinificationService minifyService;
    
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
        return new FileSystemResource(getFilePath(versionedBundleName));
    }
    
    protected void saveBundle(Resource resource) {
        File file = new File(getFilePath(resource.getDescription()));
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new RuntimeException("Unable to create middle directories for file: " + file.getAbsolutePath());
            }
        }
        
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            StreamUtils.copy(resource.getInputStream(), out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected String getFilePath(String name) {
        String base = StringUtils.isBlank(assetFileSystemPath) ? DEFAULT_STORAGE_DIRECTORY : assetFileSystemPath;
        base = StringUtils.removeEnd(base, "/");
        return base + "/bundles/" + name;
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
                    StreamUtils.copy(r.getInputStream(), baos);
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
                    baos.write(";\r\n".getBytes());
                } else {
                    baos.write("\r\n".getBytes());
                }
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
    
    @Override
    public String getVersionedBundleName(String unversionedBundleName) {
        return bundleVersions.get(unversionedBundleName);
    }
    
    @Override
    public boolean hasBundle(String versionedBundle) {
        return bundles.containsKey(versionedBundle);
    }
    
    @Override
    public String registerBundle(String bundleName, List<String> files, BroadleafResourceHttpRequestHandler handler) 
            throws IOException {
        StopWatch s = new StopWatch();
        LinkedHashMap<String, Resource> foundResources = new LinkedHashMap<String, Resource>();
        
        for (String file : files) {
    		for (Resource location : handler.getLocations()) {
    			try {
    				Resource resource = location.createRelative(file);
    				if (resource.exists() && resource.isReadable()) {
    				    foundResources.put(file, resource);
    				    break;
    				}
    			}
    			catch (IOException ex) {
    				LOG.debug("Failed to create relative resource - trying next resource location", ex);
    			}
    		}
    		
    		// We didn't find the resource in any of the configured locations.
    		// Check to see if it's a generated resource
    		if (handler.getHandlers() != null) {
                for (AbstractGeneratedResourceHandler h : handler.getHandlers()) {
                    if (h.getHandledFileName().equals(file)) {
        				foundResources.put(file, h.getResource());
                    }
                }
    		}
        }
        
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Resource> entry : foundResources.entrySet()) {
            sb.append(entry.getKey()).append(entry.getValue().lastModified()).append("\r\n");
        }
        String version = String.valueOf(sb.toString().hashCode());
        
        String bundleWithoutExtension = bundleName.substring(0, bundleName.lastIndexOf('.'));
        String bundleExtension = bundleName.substring(bundleName.lastIndexOf('.'));
        
        String versionedName = bundleWithoutExtension + version + bundleExtension;
        bundles.put(versionedName, foundResources.values());
        bundleVersions.put(bundleName, versionedName);
        
        s.printString("Regitering bundle");
        return versionedName;
    }

}
