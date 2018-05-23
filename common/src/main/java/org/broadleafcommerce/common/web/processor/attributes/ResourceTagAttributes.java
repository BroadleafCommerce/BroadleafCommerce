package org.broadleafcommerce.common.web.processor.attributes;

/**
 * Builder class that holds attributes relevant to resources.
 *
 * @author Jacob Mitash
 */
public class ResourceTagAttributes {
    private String src;
    private String name;
    private String mappingPrefix;
    private boolean async;
    private boolean defer;
    private boolean includeAsyncDeferUnbundled;
    private String dependencyEvent;
    private String files;

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
        this.dependencyEvent = toCopy.dependencyEvent;
        this.files = toCopy.files;
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

    public String dependencyEvent() {
        return dependencyEvent;
    }

    public ResourceTagAttributes dependencyEvent(String dependencyEvent) {
        this.dependencyEvent = dependencyEvent;
        return this;
    }

    public String files() {
        return files;
    }

    public ResourceTagAttributes files(String files) {
        this.files = files;
        return this;
    }
}
