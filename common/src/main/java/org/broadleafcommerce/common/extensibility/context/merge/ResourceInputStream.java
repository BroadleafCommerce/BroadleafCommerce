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
package org.broadleafcommerce.common.extensibility.context.merge;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jfischer
 */
public class ResourceInputStream extends InputStream {

    private final InputStream is;
    private List<String> names = new ArrayList<>(20);

    public ResourceInputStream(InputStream is, String name) {
        this.is = is;
        names.add(name);
    }

    public ResourceInputStream(InputStream is, String name, List<String> previousNames) {
        this.is = is;
        names.addAll(previousNames);
        if (!StringUtils.isEmpty(name)) {
            names.add(name);
        }
    }

    public List<String> getNames() {
        return names;
    }

    public String getName() {
        assert names.size() == 1;
        return names.get(0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        int size = names.size();
        for (int j=0;j<size;j++) {
            sb.append(names.get(j));
            if (j < size - 1) {
                sb.append(" : ");
            }
        }

        return sb.toString();
    }

    @Override
    public int available() throws IOException {
        return (is==null)?-1:is.available();
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    @Override
    public void mark(int i) {
        is.mark(i);
    }

    @Override
    public boolean markSupported() {
        return is.markSupported();
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return is.read(bytes);
    }

    @Override
    public int read(byte[] bytes, int i, int i1) throws IOException {
        return is.read(bytes, i, i1);
    }

    @Override
    public void reset() throws IOException {
        is.reset();
    }

    @Override
    public long skip(long l) throws IOException {
        return is.skip(l);
    }
}
