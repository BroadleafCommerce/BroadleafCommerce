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
package org.broadleafcommerce.extensibility.context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.extensibility.context.merge.MergeXmlConfigResource;
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
public class MergeApplicationContextXmlConfigResource extends MergeXmlConfigResource {

    private static final Log LOG = LogFactory.getLog(MergeApplicationContextXmlConfigResource.class);

    /**
     * Generate a merged configuration resource, loading the definitions from the given streams. Note,
     * all sourceLocation streams will be merged using standard Spring configuration override rules. However, the patch
     * streams are fully merged into the result of the sourceLocations simple merge. Patch merges are first executed according
     * to beans with the same id. Subsequent merges within a bean are executed against tagnames - ignoring any
     * further id attributes.
     *
     * @param sources array of input streams for the source application context files
     * @param patches array of input streams for the patch application context files
     * @throws BeansException
     */
    public Resource[] getConfigResources(InputStream[] sources, InputStream[] patches) throws BeansException {
        Resource[] configResources = null;
        InputStream merged = null;
        try {
            merged = merge(sources);

            byte[] mergedArray = buildArrayFromStream(merged);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Merged Stage1 Sources: \n" + serialize(new ByteArrayInputStream(mergedArray)));
            }

            if (patches != null) {
                InputStream[] patches2 = new InputStream[patches.length+1];
                patches2[0] = new ByteArrayInputStream(mergedArray);
                System.arraycopy(patches, 0, patches2, 1, patches.length);

                merged = merge(patches2);
            }

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
            configResources = new Resource[]{new ByteArrayResource(baos.toByteArray())};

            if (LOG.isDebugEnabled()) {
                LOG.debug("Merged ApplicationContext Including Patches: \n" + serialize(configResources[0]));
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

        return configResources;
    }

}
