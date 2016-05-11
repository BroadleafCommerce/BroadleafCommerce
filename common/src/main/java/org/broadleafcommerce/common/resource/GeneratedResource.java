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
package org.broadleafcommerce.common.resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.AbstractResource;
import org.springframework.security.util.InMemoryResource;
import org.springframework.util.Assert;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * An in memory generated resource. This class also overrides some parent Spring AbstractResource methods to ensure
 * compatibility with the {@link ResourceHttpRequestHandler}.
 * 
 * Note that this class <i>intentionally</i> does not subclass Spring's {@link InMemoryResource} and instead has copied
 * the fields here because {@link InMemoryResource} does not provide a default constructor. This causes issues when
 * deserializing an instance from disk (such as in a caching scenario that overflows from memory to disk).
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class GeneratedResource extends AbstractResource implements Serializable {
    
    private static final long serialVersionUID = 7044543270746433688L;

    protected long timeGenerated;
    protected String hashRepresentation;
    
    protected final byte[] source;
    protected final String description;

    /**
     * <b>Note: This constructor should not be explicitly used</b> 
     * 
     * To properly allow for serialization, we must provide this no-arg constructor that will 
     * create a "dummy" GeneratedResource. The appropriate fields will be set during deserialization.
     */
    public GeneratedResource()  {
        this(new byte[]{}, null);
    }

    public GeneratedResource(byte[] source, String description) {
        Assert.notNull(source);
        this.source = source;
        this.description = description;
        timeGenerated = System.currentTimeMillis();
    }
    
    @Override
    public String getFilename() {
        return getDescription();
    }
    
    @Override
	public long lastModified() throws IOException {
        return timeGenerated;
    }
    
    public String getHashRepresentation() {
        return StringUtils.isBlank(hashRepresentation) ? String.valueOf(timeGenerated) : hashRepresentation;
    }

    public void setHashRepresentation(String hashRepresentation) {
        this.hashRepresentation = hashRepresentation;
    }
    
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(source);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public byte[] getBytes() {
        return source;
    }

    @Override
    public boolean equals(Object res) {
        if (res == null) return false;
        if (!getClass().isAssignableFrom(res.getClass())) {
            return false;
        }

        return Arrays.equals(source, ((GeneratedResource)res).source);
    }

}
