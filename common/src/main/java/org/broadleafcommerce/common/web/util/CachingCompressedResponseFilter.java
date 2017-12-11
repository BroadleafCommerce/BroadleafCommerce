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
package org.broadleafcommerce.common.web.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.io.AtomicMove;
import org.broadleafcommerce.common.io.AtomicMoveImpl;
import org.broadleafcommerce.common.web.filter.AbstractIgnorableOncePerRequestFilter;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides either dynamic, or cached static compression of response data. The 'filter.compression.enabled' property must
 * be set to true to enable this filter.
 * </p>
 * Cached static file compression generally refers to compression requests for static artifacts, such as css, js and similar files. To
 * allow static file caching, the 'filter.compression.allow.static.file.cache' property must be true and the file
 * must be recognized in the list of extension to mimetype mapping represented in the 'filter.compression.extension.mime.mappings'
 * property. Should a static file fail to be found in the mapping list, the system will fallback to dynamic compression for
 * the artifact. The 'filter.compression.extension.mime.mappings' property is a comma delimited list of request URI matching
 * regular expressions to mime types. For example, the following property setting would limit static compressed file caching
 * to only files that end in the '.css' and '.js' extensions: {@code filter.compression.extension.mime.mappings=.*\\.css:text/css,.*\\.js}.
 * See the javadoc for {@link #compressionExtensionToMimeMappings} for the default values. 'filter.compression.allow.static.file.cache'
 * is false by default, which makes the most sense when a CDN is in place, since the CDN will be the primary source of
 * asset caching.
 * </p>
 * The directory in which cached static files should be stored is denoted via the 'filter.compression.file.temp.directory'
 * property. By default, this property is set to 'none', which signifies that the standard java temp directory should be used.
 * If using static file compression caching, make sure you have enough free hard disk space to accommodate compressed versions
 * of all the interesting static files.
 * </p>
 * Dynamic compression generally refers to compression requests for dynamic web pages, such a standard HTML page requests,
 * or JSON responses for RESTful endpoints. There is no caching of the compressed response and the response is compressed
 * on every request.
 * </p>
 * There is an additional sendfile optimization for static/cached files. For larger files, the OS can use the 'sendfile'
 * optimization to increase network throughput and reduce CPU overhead. By default, this is enabled for files over 49152 bytes.
 * This threshold can be changed via the 'filter.compression.sendfile.size' property. While the sendfile feature is enabled
 * by default, it can be turned off altogether via the 'filter.compression.use.sendfile' property (set to false).
 * </p>
 * Certain request URI can be blacklisted for compression altogether via the 'filter.compression.blacklist.uri.regex'
 * property. This is a comma delimited list of regular expression that, when matched against a request URI, will cause
 * that URI to not be compressed.
 *
 * @author Jeff Fischer
 */
@Component("blCachingCompressedResponseFilter")
@ConditionalOnProperty("filter.compression.enabled")
public class CachingCompressedResponseFilter extends AbstractIgnorableOncePerRequestFilter {

    private static final Log LOG = LogFactory.getLog(CachingCompressedResponseFilter.class);

    @Configuration
    static class BroadleafSpringResourceConfig {

        @Bean
        public CacheAwareResponseHandler blCacheAwareReponseHandler(@Value("${staticResourceBrowserCacheSeconds}") int cacheSeconds) {
            CacheAwareResponseHandler handler = new CacheAwareResponseHandler();
            handler.setCacheSeconds(cacheSeconds);
            return handler;
        }
    }

    /**
     * Whether or not compression is enabled in the default Spring environment. Used to turn off during development. True
     * by default (compression enabled).
     */
    @Value("${filter.compression.use.default.environment:true}")
    protected Boolean useWhileInDefaultEnvironment = true;

    /**
     * It is likely resource versioning will be disabled in the default, development environment. This setting will tell
     * the system to not cache static resources in the local filesystem in the default environment to avoid static
     * asset caching issues where a versioned name change is not involved.
     */
    @Value("${filter.compression.cache.default.environment:false}")
    protected Boolean cacheWhileInDefaultEnvironment = false;

    /**
     * Specify a filesystem directory in which to store compressed static files. 'none' by default, which means use
     * the java temp directory.
     */
    @Value("${filter.compression.file.temp.directory:none}")
    protected String compressedFileTempDirectory;

    /**
     * The minimum size of a cached,compressed file (in bytes) for which to use the OS sendfile feature. 49152 bytes
     * by default.
     */
    @Value("${filter.compression.sendfile.size:49152}")
    protected long sendFileSize;

    /**
     * Whether or not to ever use the OS sendfile feature. True by default (use the feature).
     */
    @Value("${filter.compression.use.sendfile:true}")
    protected Boolean useSendFile;

    /**
     * Comma delimited URI matching regular expression to mime type mapping. The default value is
     * {@code .*\\.svg:image/svg+xml,.*\\.png:image/png,.*\\.xml:text/xml,.*\\.css:text/css,.*\\.js:application/javascript,.*\\.otf:application/x-font-opentype,.*\\.json:application/json,.*\\.css\.map:application/json,.*\\.js\.map:application/json}
     */
    @Value("${filter.compression.extension.mime.mappings:.*\\.svg:image/svg+xml,.*\\.png:image/png,.*\\.xml:text/xml,.*\\.css:text/css,.*\\.js:application/javascript,.*\\.otf:application/x-font-opentype,.*\\.json:application/json,.*\\.css\\.map:application/json,.*\\.js\\.map:application/json}")
    protected String compressionExtensionToMimeMappings;

    /**
     * Whether or not to cache static file compression results. The default value is false (don't use the cache).
     */
    @Value("${filter.compression.allow.static.file.cache:false}")
    protected Boolean allowStaticFileCache;

    /**
     * A comma delimited list of URI matching regular expressions for requests that should be ignored for any compression.
     * The default value is '.*\\.jpg,.*\\.jpeg,.*\\.gif,.*\\.png'.
     */
    @Value("${filter.compression.blacklist.uri.regex:.*\\.jpg,.*\\.jpeg,.*\\.gif,.*\\.png}")
    protected String blackListURIs;

    @Value("${resource.versioning.enabled:true}")
    protected Boolean resourceVersioningEnabled;

    protected Boolean isDefaultEnvironment = false;

    protected Boolean initialized = false;

    @Autowired
    protected Environment environment;

    @Autowired
    protected CacheAwareResponseHandler cacheAwareResponseHandler;

    protected AtomicMove atomicMove = new AtomicMoveImpl();

    protected Map<Pattern, String> extensionToMime = new HashMap<>();

    protected List<Pattern> blackListPatterns = new ArrayList<>();

    @Override
    protected void doFilterInternalUnlessIgnored(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!isDefaultEnvironment || useWhileInDefaultEnvironment) {
            if (useGzipCompression(request, response)) {
                String mimeType = getMimeType(request);
                if (mimeType != null && shouldUseStaticCache()) {
                    boolean isValid = processStatic(request, response, chain, mimeType);
                    if (!isValid) {
                        processDynamic(request, response, chain);
                    }
                } else {
                    processDynamic(request, response, chain);
                }
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public int getOrder() {
            return FilterOrdered.PRE_SECURITY_HIGH - 1200;
        }

    @Override
    protected void initFilterBean() throws ServletException {
        if (!initialized) {
            String[] pairs = compressionExtensionToMimeMappings.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                extensionToMime.put(Pattern.compile(keyValue[0]), keyValue[1]);
            }
            if (!"none".equals(blackListURIs)) {
                String[] rawPatterns = blackListURIs.split(",");
                for (String rawPattern : rawPatterns) {
                    blackListPatterns.add(Pattern.compile(rawPattern));
                }
            }
            isDefaultEnvironment = !(ArrayUtils.isNotEmpty(environment.getActiveProfiles()) && Arrays.binarySearch(environment.getActiveProfiles(), "default") < 0);

            if (!resourceVersioningEnabled && shouldUseStaticCache()) {
                LOG.warn("Static file compression cache is enabled, but resource versioning is not enabled. This can lead " +
                        "to unversioned resources being cached in the filesystem. If these resources are updated, you will " +
                        "not see the changes because of the unversioned file cache of the same name. It is recommended to " +
                        "not use static file compression cache when resource versioning is not enabled. Static file " +
                        "compression cache can be controlled with the 'filter.compression.allow.static.file.cache' property.");
            }
            initialized = true;
        }
    }

    protected boolean shouldUseStaticCache() {
        return allowStaticFileCache && (!isDefaultEnvironment || cacheWhileInDefaultEnvironment);
    }

    protected String getMimeType(HttpServletRequest request) {
        String response = null;
        String uri = request.getRequestURI().toLowerCase();
        for (Map.Entry<Pattern, String> entry : extensionToMime.entrySet()) {
            if (entry.getKey().matcher(uri).matches()) {
                response = entry.getValue();
                break;
            }
        }
        return response;
    }

    protected void processDynamic(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        chain.doFilter(request, wrapper);
        FastByteArrayOutputStream baos = new FastByteArrayOutputStream(1024);
        GZIPOutputStream zip = new GZIPOutputStream(baos);
        StreamUtils.copy(wrapper.getContentInputStream(), zip);
        zip.close();
        response.addHeader("Content-Encoding", "gzip");
        response.setContentLength(baos.size());
        StreamUtils.copy(new ByteArrayInputStream(baos.toByteArray()), response.getOutputStream());
    }

    protected boolean processStatic(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String mimeType) throws IOException, ServletException {
        boolean success = true;
        try {
            File targetFile = prepareTargetFile(request);
            if (targetFile.exists() && targetFile.length() == 0L) {
                //TODO Do something to check the validity of the cached file before returning. This would be an extra layer
                // of protection against any corruption. I would not expect any corruption given the cache file creation code.
                // We should not make it into here. Ignoring anything other than non-empty file validation for now.
                targetFile.delete();
                success = false;
            } else {
                if (!targetFile.exists()) {
                    cacheStaticCompressedFileInFileSystem(request, response, chain, targetFile);
                } else {
                    cacheAwareResponseHandler.setHeaders(response, targetFile, mimeType);
                }
                emitStaticFileToResponse(request, response, targetFile);
            }
        } catch (Exception e) {
            LOG.error(String.format("Unable to send cached static/compressed version of resource %s. Falling back to dynamic compression.", request.getRequestURI()), e);
            success = false;
        }
        return success;
    }

    protected void emitStaticFileToResponse(HttpServletRequest request, HttpServletResponse response, File targetFile) throws IOException {
        long byteSize = targetFile.length();
        response.addHeader("Content-Encoding", "gzip");
        response.setContentLength(Long.valueOf(byteSize).intValue());
        if (sendFileSize > byteSize || !useSendFile) {
            //don't use sendFile for small files
            StreamUtils.copy(new FileInputStream(targetFile), response.getOutputStream());
        } else {
            request.setAttribute("org.apache.tomcat.sendfile.filename", targetFile.getAbsolutePath());
            request.setAttribute("org.apache.tomcat.sendfile.start", 0L);
            request.setAttribute("org.apache.tomcat.sendfile.end", byteSize);
        }
    }

    protected File prepareTargetFile(HttpServletRequest request) {
        String uriHash = DigestUtils.md5Hex(request.getRequestURI());
        File targetDir;
        if ("none".equals(compressedFileTempDirectory)) {
            targetDir = new File(System.getProperty("java.io.tmpdir"));
        } else {
            targetDir = new File(compressedFileTempDirectory);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
        }
        return new File(targetDir, uriHash + ".gz");
    }

    protected void cacheStaticCompressedFileInFileSystem(HttpServletRequest request, HttpServletResponse response, FilterChain chain, File targetFile) throws IOException, ServletException {
        String tempRoot = UUID.randomUUID().toString();
        File tempFile = File.createTempFile(tempRoot, ".tmp");
        FileSystemResponseWrapper wrapper = new FileSystemResponseWrapper(response, tempFile);
        chain.doFilter(request, wrapper);
        wrapper.closeFileOutputStream();
        File compressedFile = File.createTempFile(tempRoot, ".tmpgz");
        OutputStream compressedOut = new GZIPOutputStream(new FileOutputStream(compressedFile));
        StreamUtils.copy(new BufferedInputStream(new FileInputStream(tempFile)), compressedOut);
        IOUtils.closeQuietly(compressedOut);
        tempFile.delete();
        atomicMove.replaceExisting(compressedFile, targetFile);
    }

    protected boolean useGzipCompression(HttpServletRequest request, HttpServletResponse response) throws MalformedURLException {
        for (Pattern pattern : blackListPatterns) {
            String uri = request.getRequestURI().toLowerCase();
            if (pattern.matcher(uri).matches()) {
                return false;
            }
        }

        // If Content-Encoding header is already set on response, skip compression
        if (response.containsHeader("Content-Encoding")) {
            return false;
        }

        // Are we allowed to compress ?
        String s = request.getParameter("gzip");
        if ("false".equals(s)) {
            return false;
        }

        Enumeration<?> e = request.getHeaders("Accept-Encoding");

        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            if (name.contains("gzip")) {
                return true;
            }
        }

        return false;
    }

}
