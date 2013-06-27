/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.extensibility.context;

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
    private List<String> names = new ArrayList<String>(20);

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
