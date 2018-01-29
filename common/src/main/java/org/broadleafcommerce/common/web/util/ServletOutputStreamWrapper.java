/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import org.springframework.util.FastByteArrayOutputStream;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * Delegate standard {@link ServletOutputStream} write calls to standard buffered IO.
 *
 * @author Jeff Fischer
 */
public class ServletOutputStreamWrapper extends ServletOutputStream {

    private FastByteArrayOutputStream baos = new FastByteArrayOutputStream(8192);
    private OutputStream outputStream;

    public ServletOutputStreamWrapper(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(int b) throws IOException {
        baos.write(b);
        if (baos.size() >= 8192) {
            flushInternalBuffer();
        }
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        flushInternalBuffer();
    }

    protected void flushInternalBuffer() throws IOException {
        outputStream.write(baos.toByteArray());
        baos.reset();
    }
}
