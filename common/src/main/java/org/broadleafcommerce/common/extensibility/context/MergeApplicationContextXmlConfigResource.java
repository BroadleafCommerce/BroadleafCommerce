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
package org.broadleafcommerce.common.extensibility.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.context.merge.MergeXmlConfigResource;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeException;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeManagerSetupException;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    public Resource[] getConfigResources(ResourceInputStream[] sources, ResourceInputStream[] patches) throws BeansException {
        Resource[] configResources = null;
        ResourceInputStream merged = null;
        try {
            merged = merge(sources);

            if (patches != null) {
                ResourceInputStream[] patches2 = new ResourceInputStream[patches.length+1];
                patches2[0] = merged;
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
