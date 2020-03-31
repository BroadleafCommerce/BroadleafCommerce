/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
import org.broadleafcommerce.common.resource.BundledResourceInfo;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.resource.BroadleafDefaultResourceResolverChain;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import de.jkeylockmanager.manager.LockCallback;

/**
 * @see ResourceBundlingService
 * @author Andre Azzolini (apazzolini)
 * @author Brian Polster (bpolster)
 */
@Service("blResourceBundlingService")
public class ResourceBundlingServiceImpl implements ResourceBundlingService {
    protected static final Log LOG = LogFactory.getLog(ResourceBundlingServiceImpl.class);

    // Map of known unversioned bundle names ==> additional files that should be included
    // ex: "global.js" ==> ["classpath:/file1.js", "/js/file2.js"]
    /**
     *  This has to use an @Resource annotation because Spring's @Autowired cannot work with the type erasure from the
     *  Map<String, List<String>> type.
     */
    @javax.annotation.Resource(name = "blAdditionalBundleFiles")
    protected Map<String, List<String>> additionalBundleFiles;
            
    @javax.annotation.Resource(name = "blFileService")
    protected BroadleafFileService fileService;

    /**
     * These properties are looked up manually within {@link #initializeResources(ContextRefreshedEvent)}
     */
    protected ResourceHttpRequestHandler jsResourceHandler;
    protected ResourceHttpRequestHandler cssResourceHandler;
    
    @Autowired
    protected ApplicationContext appctx;

    @javax.annotation.Resource(name="blStatisticsService")
    protected StatisticsService statisticsService;

    @Autowired
    protected Environment environment;

    private KeyLockManager keyLockManager = KeyLockManagers.newLock();

    private ConcurrentHashMap<String, BundledResourceInfo> createdBundles = new ConcurrentHashMap<>();
    
    /**
     * Initalize the blJsResources and blCssResources. The reason that we are doing it this way and not via the normal
     * autowiring process is because there is technically a circular dependency here:
     *   ResourceBundlingService
     *      -> blJsResources
     *          -> blSiteResourceResolvers/blAdminResourceResolvers
     *              -> blBundleResourceResolver
     *                  -> ResourceBundlingService
     *                      -> ...
     * We can easily hit an IllegalStateException depending on the order in which things are initialized. This essentially breaks
     * the circular dependency in Spring's auto-initialization and grabs those resources when they are initialized, since this
     * should only be used at runtime in a web request anyway.
     */
    @EventListener
    public void initializeResources(ContextRefreshedEvent event) {
        if (jsResourceHandler == null) {
            try {
                jsResourceHandler = appctx.getBean("blJsResources", ResourceHttpRequestHandler.class);
            } catch (NoSuchBeanDefinitionException e) {
                // do nothing, this bean is optional
            }
        }
        
        if (cssResourceHandler == null) {
            try {
                cssResourceHandler = appctx.getBean("blCssResources", ResourceHttpRequestHandler.class);
            } catch (NoSuchBeanDefinitionException e) {
                // do nothing, this bean is optional
            }
        }
    }
    
    @Override
    public Resource rebuildBundledResource(String requestedBundleName) {
        String resourceName = lookupBundlePath(requestedBundleName);
        BundledResourceInfo bundleInfo = createdBundles.get(resourceName);

        if (bundleInfo != null) {
            createdBundles.remove(resourceName);
            ResourceHttpRequestHandler handler = findResourceHttpRequestHandler(requestedBundleName);
            if (handler != null) {
                ResourceResolverChain resolverChain = new BroadleafDefaultResourceResolverChain(handler.getResourceResolvers());
                List<Resource> locations = handler.getLocations();
                List<String> bundledFilePaths = bundleInfo.getBundledFilePaths();

                createBundleIfNeeded(bundleInfo.getVersionedBundleName(), bundledFilePaths, resolverChain, locations);
            }
        }

        return getBundledResource(resourceName);
    }
    
    @Override
    public String resolveBundleResourceName(String requestedBundleName, String mappingPrefix, List<String> files) {
        return resolveBundleResourceName(requestedBundleName, mappingPrefix, files, null);
    }

    @Override
    public String resolveBundleResourceName(String requestedBundleName, String mappingPrefix, List<String> files, String bundleAppend) {
        ResourceHttpRequestHandler resourceRequestHandler = findResourceHttpRequestHandler(requestedBundleName);
        if (resourceRequestHandler != null && CollectionUtils.isNotEmpty(files)) {
            ResourceResolverChain resolverChain = new BroadleafDefaultResourceResolverChain(
                    resourceRequestHandler.getResourceResolvers());
            List<Resource> locations = resourceRequestHandler.getLocations();
                    
            StringBuilder combinedPathString = new StringBuilder();
            List<String> filePaths = new ArrayList<>();
            for (String file : files) {
                String resourcePath = resolverChain.resolveUrlPath(file, locations);
                if (resourcePath == null) {
                    // can't find the exact name specified in the bundle, try it with the mappingPrefix
                    resourcePath = resolverChain.resolveUrlPath(mappingPrefix + file, locations);
                }
                
                if (resourcePath != null) {
                    filePaths.add(resourcePath);
                    combinedPathString.append(resourcePath);
                } else {
                    LOG.warn("Could not resolve resource path specified in bundle as [" +
                            file +
                            "] or as [" +
                            mappingPrefix + file +
                            "]. Skipping file.");
                }
            }

            int version = Math.abs(combinedPathString.toString().hashCode());
            String versionedBundleName = mappingPrefix + addVersion(requestedBundleName, "-" + String.valueOf(version));
        
            createBundleIfNeeded(versionedBundleName, filePaths, resolverChain, locations, bundleAppend);

            return versionedBundleName;
        } else {
            LOG.warn("No resource request handler could be found for " + requestedBundleName);
        }

        return null;
    }

    @Override
    public Resource resolveBundleResource(String versionedBundleResourceName) {
        return getBundledResource(lookupBundlePath(versionedBundleResourceName));
    }
    
    @Override
    public boolean checkForRegisteredBundleFile(String versionedBundleName) {
        versionedBundleName = lookupBundlePath(versionedBundleName);
        boolean bundleRegistered = createdBundles.containsKey(versionedBundleName);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Checking for registered bundle file, versionedBundleName=\"" + versionedBundleName + "\" bundleRegistered=\"" + bundleRegistered + "\"");
        }
        return bundleRegistered;
    }

    protected Resource getBundledResource(String versionedBundleName) {
        BundledResourceInfo bundledResourceInfo = createdBundles.get(versionedBundleName);
        return bundledResourceInfo != null ? bundledResourceInfo.getResource() : null;
    }
   
    protected String lookupBundlePath(String requestPath) {
        if (requestPath.contains(".css")) {
            if (!requestPath.startsWith("/css/")) {
                requestPath = "/css/" + requestPath;
            }
        } else if (requestPath.contains(".js")) {
            if (!requestPath.startsWith("/js/")) {
                requestPath = "/js/" + requestPath;
            }
        }
        return requestPath;
    }

    protected void createBundleIfNeeded(final String versionedBundleName, final List<String> filePaths,
                                        final ResourceResolverChain resolverChain, final List<Resource> locations) {
        createBundleIfNeeded(versionedBundleName, filePaths, resolverChain, locations, null);
    }

    protected void createBundleIfNeeded(final String versionedBundleName, final List<String> filePaths,
            final ResourceResolverChain resolverChain, final List<Resource> locations, final String bundleAppend) {
        if (!createdBundles.containsKey(versionedBundleName)) {
            keyLockManager.executeLocked(versionedBundleName, new LockCallback() {

                @Override
                public void doInLock() {
                    Resource bundleResource = getBundledResource(versionedBundleName);
                    if (bundleResource == null || !bundleResource.exists()) {
                        bundleResource = createBundle(versionedBundleName, filePaths, resolverChain, locations, bundleAppend);
                        if (bundleResource != null) {
                            saveBundle(bundleResource);
                        }
                        Resource savedResource = readBundle(versionedBundleName);
                        BundledResourceInfo bundledResourceInfo = new BundledResourceInfo(savedResource, versionedBundleName, filePaths);
                        createdBundles.put(versionedBundleName, bundledResourceInfo);
                    }
                }
            });
        }
    }
    protected Resource createBundle(String versionedBundleName, List<String> filePaths,
                                    ResourceResolverChain resolverChain, List<Resource> locations) {
        return createBundle(versionedBundleName, filePaths, resolverChain, locations, null);
    }

    protected Resource createBundle(String versionedBundleName, List<String> filePaths,
            ResourceResolverChain resolverChain, List<Resource> locations, String bundleAppend) {

        HttpServletRequest req = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes;
        
        // Join all of the resources for this bundle together into a byte[]
        try {
            for (String fileName : filePaths) {
                Resource r = resolverChain.resolveResource(req, fileName, locations);
                InputStream is = null;
                
                if (r == null) {
                    LOG.warn("Could not resolve resource specified in bundle as [" +
                            fileName +
                            "]. Turn on trace logging to determine resolution failure. Skipping file.");
                } else {
                    try {
                        is = r.getInputStream();
                        StreamUtils.copy(is, outputStream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        IOUtils.closeQuietly(is);
                    }
                    
                    // If we're creating a JavaScript bundle, we'll put a semicolon between each
                    // file to ensure it won't fail to compile.
                    if (versionedBundleName.endsWith(".js")) {
                        outputStream.write(";".getBytes(getBundleCharSet()));
                    }
                    outputStream.write(System.getProperty("line.separator").getBytes(getBundleCharSet()));
                }
            }

            // Append the requested text to the bundle
            if (bundleAppend != null) {
                if (versionedBundleName.endsWith(".js")) {
                    outputStream.write(";".getBytes(getBundleCharSet()));
                }
                outputStream.write(bundleAppend.getBytes(getBundleCharSet()));
            }

            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        
        // Create our GenerateResource that holds our combined bundle
        return new GeneratedResource(bytes, versionedBundleName);
    }
    
    protected void saveBundle(Resource resource) {
        FileWorkArea tempWorkArea = fileService.initializeWorkArea();
        String fileToSave = FilenameUtils.separatorsToSystem(getResourcePath(resource.getDescription()));
        String tempFilename = FilenameUtils.concat(tempWorkArea.getFilePathLocation(), fileToSave);
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
            
            fileService.addOrUpdateResourceForPath(tempWorkArea, tempFile, true);
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
        return bundleWithoutExtension + version + bundleExtension;
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
        return String.valueOf(sb.toString().hashCode());
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
        File bundleFile = fileService.getResource("/" + getResourcePath(versionedBundleName));
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
        if (name.startsWith("/")) {
            return "bundles" + name;
        } else {
            return "bundles/" + name;
        }
    }

    protected Charset getBundleCharSet() {
        final String charsetProperty = environment.getProperty("bundle.charset");
        if (StringUtils.isEmpty(charsetProperty)) {
            return StandardCharsets.UTF_8;
        } else {
            return Charset.forName(charsetProperty);
        }
    }

    @Override
    public List<String> findBundlesNameByResourceFileName(String fileName){
        List<String> result = new ArrayList<>();
        for (Entry<String, BundledResourceInfo> bundleInfo : createdBundles.entrySet()) {
            BundledResourceInfo value = bundleInfo.getValue();
            for (String s : value.getBundledFilePaths()) {
                String f = FilenameUtils.removeExtension(fileName);
                if(s.startsWith(f)){
                    result.add(bundleInfo.getKey());
                }
            }
        }
        return result;
    }

    @Override
    public boolean removeBundle(String bundleName) {
        String resourceName = lookupBundlePath(bundleName);
        BundledResourceInfo bundleInfo = createdBundles.get(resourceName);

        if (bundleInfo != null) {
            return createdBundles.remove(resourceName) != null;
        }
        return false;
    }
}
