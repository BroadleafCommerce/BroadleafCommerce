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

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Used by BroadleafResourceProcessor to modify files. 
 * 
 * @author bpolster
 */
@Service("blResourceModificationService")
public class ResourceModificationServiceImpl implements ResourceModificationService {

    @Override
    public Resource getModifiedResource(String originalResourceName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Resource getNonModifiedResource(String originalResourceName) {
        // TODO Auto-generated method stub
        return null;
    }
    //    protected static final Log LOG = LogFactory.getLog(ResourceModificationServiceImpl.class);
    //
    //    @javax.annotation.Resource(name = "blFileService")
    //    protected BroadleafFileService fileService;
    //    
    //    @javax.annotation.Resource(name="blStatisticsService")
    //    protected StatisticsService statisticsService;
    //    
    //    @javax.annotation.Resource(name = "blResourceRequestExtensionManager")
    //    protected ResourceRequestExtensionManager extensionManager;
    //
    //    @Override
    //    public Resource getModifiedResource(String originalResourceName) {
    //        ExtensionResultHolder<Resource> erh = new ExtensionResultHolder<Resource>();
    //        extensionManager.getProxy().getModifiedResource(originalResourceName, erh);
    //        if (erh.getResult() != null) {
    //            return erh.getResult();
    //        } else {
    //            return getNonModifiedResource(originalResourceName);
    //        }
    //    }
    //
    //    @Override
    //    public Resource getNonModifiedResource(String originalResourceName) {
    //        ExtensionResultHolder<Resource> erh = new ExtensionResultHolder<Resource>();
    //        extensionManager.getProxy().getModifiedResource(originalResourceName, erh);
    //        if (erh.getResult() != null) {
    //            return erh.getResult();
    //        } else {
    //            for (Resource location : handler.getLocations()) {
    //                try {
    //                    Resource resource = location.createRelative(file);
    //                    if (resource.exists() && resource.isReadable()) {
    //                        foundResources.put(file, resource);
    //                        match = true;
    //                        break;
    //                    }
    //                } catch (IOException ex) {
    //                    LOG.debug("Failed to create relative resource - trying next resource location", ex);
    //                }
    //            }
    //        }
    //    }
    //
    //
    //
    //
    //
    //    @Override
    //    public Resource getBundle(String versionedBundleName) {
    //        // If we can find this bundle on the file system, we've already generated it
    //        // and we don't need to do so again.
    //        Resource r = readBundle(versionedBundleName);
    //        if (r != null && r.exists()) {
    //            return r;
    //        }
    //        
    //        // Otherwise, we'll create the bundle, write it to the file system, and return
    //        r = createBundle(versionedBundleName);
    //        saveBundle(r);
    //        return r;
    //    }
    //    
    //    protected Resource readBundle(String versionedBundleName) {
    //        File bundleFile = fileService.getResource(getResourcePath(versionedBundleName));
    //        return bundleFile == null ? null : new FileSystemResource(bundleFile);
    //    }
    //    
    //    /**
    //     * Returns the resource path for the given <b>name</b> in URL-format (meaning, / separators)
    //     * @param name
    //     * @return
    //     */
    //    protected String getResourcePath(String name) {
    //        return "bundles/" + name;
    //    }
    //    
    //    protected Resource createBundle(String versionedBundleName) {
    //        ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //        byte[] bytes = null;
    //        
    //        // Join all of the resources for this bundle together into a byte[]
    //        try {
    //            for (Resource r : bundles.get(versionedBundleName)) {
    //                InputStream is = null;
    //                
    //                try {
    //                    is = r.getInputStream();
    //                    StreamUtils.copy(is, baos);
    //                } catch (IOException e) {
    //                    throw new RuntimeException(e);
    //                } finally {
    //                    try {
    //                        is.close();
    //                    } catch (IOException e2) {
    //                        throw new RuntimeException(e2);
    //                    }
    //                }
    //                
    //                // If we're creating a JavaScript bundle, we'll put a semicolon between each
    //                // file to ensure it won't fail to compile.
    //                if (versionedBundleName.endsWith(".js")) {
    //                    baos.write(";".getBytes());
    //                }
    //                baos.write(System.getProperty("line.separator").getBytes());
    //            }
    //            bytes = baos.toByteArray();
    //        } catch (IOException e) {
    //            throw new RuntimeException(e);
    //        } finally {
    //            try {
    //                baos.close();
    //            } catch (IOException e2) {
    //                throw new RuntimeException(e2);
    //            }
    //        }
    //        
    //        // Minify the resource
    //        byte[] minifiedBytes = minifyService.minify(versionedBundleName, bytes);
    //        
    //        // Create our GenerateResource that holds our combined and (potentially) minified bundle
    //        GeneratedResource r = new GeneratedResource(minifiedBytes, versionedBundleName);
    //        return r;
    //    }
    //    
    //    
    //
    //    @Override
    //    public String getVersionedBundleName(String unversionedBundleName, List<String> files) {
    //        Element e = getBundleVersionsCache().get(unversionedBundleName);
    //        if (e == null) {
    //            statisticsService.addCacheStat(CacheStatType.RESOURCE_BUNDLING_CACHE_HIT_RATE.toString(), false);
    //        } else {
    //            statisticsService.addCacheStat(CacheStatType.RESOURCE_BUNDLING_CACHE_HIT_RATE.toString(), true);
    //        }
    //        return e == null ? null : (String) e.getValue();
    //    }
    //    
    //
    //    
    //    @Override
    //    public synchronized String registerBundle(String bundleName, List<String> files, 
    //            BroadleafResourceHttpRequestHandler handler) throws IOException {
    //        LinkedHashMap<String, Resource> foundResources = new LinkedHashMap<String, Resource>();
    //        
    //        // With Themes, this property will never work since the "bundleName" coming in is 
    //        // a hashed name based on theme files and update time-stamps.   Leaving in place for
    //        // community use.
    //        if (additionalBundleFiles.get(bundleName) != null) {
    //            files.addAll(additionalBundleFiles.get(bundleName));
    //        }
    //        
    //        for (String file : files) {
    //            boolean match = false;
    //            
    //            // Check to see if there is any registered handler that understands how to generate
    //            // this file.
    //    		if (handler.getHandlers() != null) {
    //                for (AbstractGeneratedResourceHandler h : handler.getHandlers()) {
    //                    if (h.canHandle(file)) {
    //        				foundResources.put(file, h.getResource(file, handler.getLocations()));
    //        				match = true;
    //        				break;
    //                    }
    //                }
    //    		}
    //    		
    //    		// If we didn't find a generator that could handle this file, let's see if we can 
    //    		// look it up from our known locations
    //            if (!match) {
    //                ExtensionResultHolder erh = new ExtensionResultHolder();
    //                extensionManager.getProxy().getOverrideResource(file, erh);
    //                if (erh.getContextMap().get(ResourceRequestExtensionHandler.RESOURCE_ATTR) != null) {
    //                    foundResources.put(file, (Resource) erh.getContextMap().get(ResourceRequestExtensionHandler.RESOURCE_ATTR));
    //                    match = true;
    //                }
    //            }
    //
    //    		// If we didn't find an override for this file, let's see if we can 
    //    		// look it up from our known locations
    //    		if (!match) {
    //        		for (Resource location : handler.getLocations()) {
    //        			try {
    //        				Resource resource = location.createRelative(file);
    //        				if (resource.exists() && resource.isReadable()) {
    //        				    foundResources.put(file, resource);
    //        				    match = true;
    //        				    break;
    //        				}
    //        			}
    //        			catch (IOException ex) {
    //        				LOG.debug("Failed to create relative resource - trying next resource location", ex);
    //        			}
    //        		}
    //    		}
    //        }
    //        
    //        // Create a hash of the contents of the resources in this bundle.
    //        String resourcesHashedValue = getBundleVersion(foundResources);
    //
    //        // Use the hash to create a unique bundle name
    //        String versionedName = getBundleName(bundleName, resourcesHashedValue);
    //        
    //        bundles.put(versionedName, foundResources.values());
    //        getBundleVersionsCache().put(new Element(getCacheKey(bundleName, files), versionedName));
    //        
    //        return versionedName;
    //    }
    //
    //    protected String getCacheKey(String unversionedBundleName, List<String> files) {
    //        return unversionedBundleName;
    //    }
    //    
    //    protected String getBundleName(String bundleName, String version) {
    //        String bundleWithoutExtension = bundleName.substring(0, bundleName.lastIndexOf('.'));
    //        String bundleExtension = bundleName.substring(bundleName.lastIndexOf('.'));
    //        String versionedName = bundleWithoutExtension + version + bundleExtension;
    //        return versionedName;
    //    }
    //    
    //    protected String getBundleVersion(LinkedHashMap<String, Resource> foundResources) throws IOException {
    //        StringBuilder sb = new StringBuilder();
    //        for (Entry<String, Resource> entry : foundResources.entrySet()) {
    //            sb.append(entry.getKey());
    //            
    //            if (entry.getValue() instanceof GeneratedResource) {
    //                sb.append(((GeneratedResource) entry.getValue()).getHashRepresentation());
    //            } else {
    //                sb.append(entry.getValue().lastModified());
    //            }
    //            
    //            sb.append("\r\n");
    //        }
    //        String version = String.valueOf(sb.toString().hashCode());
    //        return version;
    //    }
    //    
    //    @Override
    //    public List<String> getAdditionalBundleFiles(String bundleName) {
    //        return additionalBundleFiles.get(bundleName);
    //    }
    //
    //    public Map<String, List<String>> getAdditionalBundleFiles() {
    //        return additionalBundleFiles;
    //    }
    //    
    //    public void setAdditionalBundleFiles(Map<String, List<String>> additionalBundleFiles) {
    //        this.additionalBundleFiles = additionalBundleFiles;
    //    }
    //    
    //    @Override
    //    public Cache getBundleVersionsCache() {
    //        if (bundleVersionsCache == null) {
    //            bundleVersionsCache = CacheManager.getInstance().getCache("blBundleElements");
    //        }
    //        return bundleVersionsCache;
    //    }
    //    
    //    @Override
    //    public Map<String, Collection<Resource>> getBundles() {
    //        return bundles;
    //    }

}
