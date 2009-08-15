/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.extensibility.context.merge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.extensibility.context.merge.exceptions.MergeException;
import org.broadleafcommerce.extensibility.context.merge.exceptions.MergeManagerSetupException;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author jfischer
 *
 */
public class MergeXmlConfigResource {

    private static final Log LOG = LogFactory.getLog(MergeXmlConfigResource.class);

    public Resource getMergedConfigResource(InputStream[] sources) throws BeansException {
        Resource configResource = null;
        InputStream merged = null;
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

    protected InputStream merge(InputStream[] sources) throws MergeException, MergeManagerSetupException {
        if (sources.length == 1) return sources[0];

        InputStream response = null;
        InputStream[] pair = new InputStream[2];
        pair[0] = sources[0];
        for (int j=1;j<sources.length;j++){
            pair[1] = sources[j];
            response = mergeItems(pair);
            if (LOG.isDebugEnabled()) {
            	try {
					byte[] itemArray = buildArrayFromStream(response);
					LOG.debug("merged sources - stage["+j+"]" + serialize(new ByteArrayInputStream(itemArray)));
					response = new ByteArrayInputStream(itemArray);
				} catch (IOException e) {
					throw new MergeException(e);
				}
            }
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

    protected InputStream mergeItems(InputStream[] sourceLocations) throws MergeException, MergeManagerSetupException {
        InputStream response = new MergeManager().merge(sourceLocations[0], sourceLocations[1]);

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
        StringBuffer item = new StringBuffer();
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
