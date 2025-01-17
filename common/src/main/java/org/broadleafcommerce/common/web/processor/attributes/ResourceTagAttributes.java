/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.processor.attributes;

/**
 * Builder class that holds attributes relevant to resources, namely those that go on &lt;blc:bundle&gt; and
 * &lt;blc:bundlepreload&gt; tags.
 *
 * @see org.broadleafcommerce.common.web.processor.ResourcePreloadProcessor
 * @see org.broadleafcommerce.common.web.processor.ResourceBundleProcessor
 * @author Jacob Mitash
 */
public class ResourceTagAttributes {
    private String src;
    private String name;
    private String mappingPrefix;
    private boolean async;
    private boolean defer;
    private boolean includeAsyncDeferUnbundled;
    private String bundleDependencyEvent;
    private String files;
    private String bundleCompletedEvent;

    /**
     * Construct a {@link ResourceTagAttributes} with all default values.
     */
    public ResourceTagAttributes() {

    }

    /**
     * Copy constructor for a {@link ResourceTagAttributes}.
     * @param toCopy the attributes to copy from
     */
    public ResourceTagAttributes(final ResourceTagAttributes toCopy) {
        this.src = toCopy.src;
        this.name = toCopy.name;
        this.mappingPrefix = toCopy.mappingPrefix;
        this.async = toCopy.async;
        this.defer = toCopy.defer;
        this.includeAsyncDeferUnbundled = toCopy.includeAsyncDeferUnbundled;
        this.bundleDependencyEvent = toCopy.bundleDependencyEvent;
        this.files = toCopy.files;
        this.bundleCompletedEvent = toCopy.bundleCompletedEvent;
    }

    public String src() {
        return src;
    }

    public ResourceTagAttributes src(String src) {
        this.src = src;
        return this;
    }

    public String name() {
        return name;
    }

    public ResourceTagAttributes name(String name) {
        this.name = name;
        return this;
    }

    public String mappingPrefix() {
        return mappingPrefix;
    }

    public ResourceTagAttributes mappingPrefix(String mappingPrefix) {
        this.mappingPrefix = mappingPrefix;
        return this;
    }

    public boolean async() {
        return async;
    }

    public ResourceTagAttributes async(boolean async) {
        this.async = async;
        return this;
    }


    public boolean defer() {
        return defer;
    }

    public ResourceTagAttributes defer(boolean defer) {
        this.defer = defer;
        return this;
    }

    public boolean includeAsyncDeferUnbundled() {
        return includeAsyncDeferUnbundled;
    }

    public ResourceTagAttributes includeAsyncDeferUnbundled(boolean includeAsyncDeferUnbundled) {
        this.includeAsyncDeferUnbundled = includeAsyncDeferUnbundled;
        return this;
    }

    public String bundleDependencyEvent() {
        return bundleDependencyEvent;
    }

    public ResourceTagAttributes bundleDependencyEvent(String bundleDependencyEvent) {
        this.bundleDependencyEvent = bundleDependencyEvent;
        return this;
    }

    public String files() {
        return files;
    }

    public ResourceTagAttributes files(String files) {
        this.files = files;
        return this;
    }

    public String bundleCompletedEvent() {
        return bundleCompletedEvent;
    }

    public ResourceTagAttributes bundleCompletedEvent(String bundleCompletedEvent) {
        this.bundleCompletedEvent = bundleCompletedEvent;
        return this;
    }
}
