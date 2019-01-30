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

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Buffer an HTTP response to the file system, rather than streaming directly to the HTTP caller. This is useful for
 * intermediary transformations without utilizing much heap, at the cost of some file IO.
 *
 * @author Jeff Fischer
 */
public class FileSystemResponseWrapper extends HttpServletResponseWrapper {

    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private File targetFile;

    public FileSystemResponseWrapper(HttpServletResponse response, File targetFile) throws IOException {
        super(response);
        this.targetFile = targetFile;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = new ServletOutputStreamWrapper(new FileOutputStream(targetFile));
        }

        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            outputStream = new ServletOutputStreamWrapper(new FileOutputStream(targetFile));
            writer = new PrintWriter(new OutputStreamWriter(outputStream, getResponse().getCharacterEncoding()), true);
        }

        return writer;
    }

    public void closeFileOutputStream() {
        if (writer != null) {
            IOUtils.closeQuietly(writer);
        } else if (outputStream != null) {
            IOUtils.closeQuietly(outputStream);
        }
    }

}
