package org.broadleafcommerce.common.web.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.resource.service.ResourceBundlingService;
import org.broadleafcommerce.common.web.processor.attributes.ResourceTagAttributes;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafTagReplacementProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.core.env.Environment;

/**
 * @author Jacob Mitash
 */
public abstract class AbstractResourceProcessor extends AbstractBroadleafTagReplacementProcessor {

    @Resource
    protected Environment environment;

    @Resource(name = "blResourceBundlingService")
    protected ResourceBundlingService bundlingService;

    /**
     * Tells if bundling is enabled
     * @return true if enabled, false otherwise
     */
    protected boolean getBundleEnabled() {
        return Boolean.parseBoolean(environment.getProperty("bundle.enabled"));
    }

    /**
     * Gets a list of the requested files for bundling
     * @param rawFiles comma separated list of files
     * @return list of requested files with space trimmed
     */
    protected List<String> getRequestedFiles(String rawFiles) {
        final String[] splitFiles = rawFiles.split(",");
        List<String> files = new ArrayList<>(splitFiles.length);

        for (String file : splitFiles) {
            files.add(file.trim());
        }

        return files;
    }

    /**
     * Builds the tag attributes of the bundle tag
     * @param tagAttributes the original attributes of the bundle tag
     * @return a {@link ResourceTagAttributes} containing the original bundle tag attributes
     */
    protected ResourceTagAttributes buildResourceTagAttributes(Map<String, String> tagAttributes) {
        return new ResourceTagAttributes()
            .name(tagAttributes.get("name"))
            .mappingPrefix(tagAttributes.get("mapping-prefix"))
            .async(tagAttributes.containsKey("async"))
            .defer(tagAttributes.containsKey("defer"))
            .includeAsyncDeferUnbundled(tagAttributes.containsKey("includeAsyncDeferUnbundled") && Boolean.parseBoolean(tagAttributes.get("includeAsyncDeferUnbundled")))
            .dependencyEvent(tagAttributes.get("bundle-dependency-event"))
            .files(tagAttributes.get("files"));
    }

    /**
     * Gets the full path of an unbundled file.
     * @param fileName the file name to parse
     * @param resourceTagAttributes the tag attributes of the original bundle tag (<code>fileName</code> will be used instead of <code>src</code>)
     * @param context the template context
     * @return the full path of the unbundled file
     */
    protected String getFullUnbundledFileName(String fileName, ResourceTagAttributes resourceTagAttributes, BroadleafTemplateContext context) {
        return context.parseExpression("@{'" + resourceTagAttributes.mappingPrefix() + fileName.trim() + "'}");
    }

    /**
     * Adds the context path to the bundleUrl.    We don't use the Thymeleaf "@" syntax or any other mechanism to
     * encode this URL as the resolvers could have a conflict.
     *
     * For example, resolving a bundle named "style.css" that has a file also named "style.css" creates problems as
     * the TF or version resolvers both want to version this file.
     *
     * @param bundleName the path of the bundle to add
     * @param context the context of the original bundle tag
     *
     * @return the full bundle URL
     */
    protected String getBundleUrl(String bundleName, BroadleafTemplateContext context) {
        String bundleUrl = bundleName;

        if (!StringUtils.startsWith(bundleUrl, "/")) {
            bundleUrl = "/" + bundleUrl;
        }

        HttpServletRequest request = context.getRequest();
        String contextPath = "";
        if (request != null) {
            contextPath = request.getContextPath();
        }
        if (StringUtils.isNotEmpty(contextPath)) {
            bundleUrl = contextPath + bundleUrl;
        }

        return bundleUrl;
    }
}
