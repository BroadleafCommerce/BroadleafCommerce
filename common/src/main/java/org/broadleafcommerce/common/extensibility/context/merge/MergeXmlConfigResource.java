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

package org.broadleafcommerce.common.extensibility.context.merge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.context.ResourceInputStream;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeException;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeManagerSetupException;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author jfischer
 *
 */
public class MergeXmlConfigResource {

    private static final Log LOG = LogFactory.getLog(MergeXmlConfigResource.class);

    public Resource getMergedConfigResource(ResourceInputStream[] sources) throws BeansException {
        Resource configResource = null;
        ResourceInputStream merged = null;
        try {
            merged = merge(sources);

            //read the final stream into a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean eof = false;
            while (!eof) {
                int temp = merged.read();
                if (temp == -1) {
                    eof = true;
                } else {
                    baos.write(temp);
                }
            }
            configResource = new ByteArrayResource(baos.toByteArray());

            if (LOG.isDebugEnabled()) {
                LOG.debug("Merged config: \n" + serialize(configResource));
            }
        } catch (MergeException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        } catch (MergeManagerSetupException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        } catch (IOException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        } finally {
            if (merged != null) {
                try{ merged.close(); } catch (Throwable e) {
                    LOG.error("Unable to merge source and patch locations", e);
                }
            }
        }

        return configResource;
    }

    protected ResourceInputStream merge(ResourceInputStream[] sources) throws MergeException, MergeManagerSetupException {
        if (sources.length == 1) return sources[0];

        ResourceInputStream response = null;
        ResourceInputStream[] pair = new ResourceInputStream[2];
        pair[0] = sources[0];
        for (int j=1;j<sources.length;j++){
            pair[1] = sources[j];
            response = mergeItems(pair[0], pair[1]);
            try{
                pair[0].close();
            } catch (Throwable e) {
                LOG.error("Unable to merge source and patch locations", e);
            }
            try{
                pair[1].close();
            } catch (Throwable e) {
                LOG.error("Unable to merge source and patch locations", e);
            }
            pair[0] = response;
        }

        return response;
    }

    protected ResourceInputStream mergeItems(ResourceInputStream sourceLocationFirst, ResourceInputStream sourceLocationSecond) throws MergeException, MergeManagerSetupException {
        ResourceInputStream response = new MergeManager().merge(sourceLocationFirst, sourceLocationSecond);

        return response;
    }

    public String serialize(Resource resource) {
        String response = "";
        try {
            response = serialize(resource.getInputStream());
        } catch (IOException e) {
            LOG.error("Unable to merge source and patch locations", e);
        }

        return response;
    }

    public String serialize(InputStream in) {
        InputStreamReader reader = null;
        int temp;
        StringBuilder item = new StringBuilder();
        boolean eof = false;
        try {
            reader = new InputStreamReader(in);
            while (!eof) {
                temp = reader.read();
                if (temp == -1) {
                    eof = true;
                } else {
                    item.append((char) temp);
                }
            }
        } catch (IOException e) {
            LOG.error("Unable to merge source and patch locations", e);
        } finally {
            if (reader != null) {
                try{ reader.close(); } catch (Throwable e) {
                    LOG.error("Unable to merge source and patch locations", e);
                }
            }
        }

        return item.toString();
    }

    protected byte[] buildArrayFromStream(InputStream source) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean eof = false;
        try{
            while (!eof) {
                int temp = source.read();
                if (temp == -1) {
                    eof = true;
                } else {
                    baos.write(temp);
                }
            }
        } finally {
            try{ source.close(); } catch (Throwable e) {
                LOG.error("Unable to merge source and patch locations", e);
            }
        }

        return baos.toByteArray();
    }
}
