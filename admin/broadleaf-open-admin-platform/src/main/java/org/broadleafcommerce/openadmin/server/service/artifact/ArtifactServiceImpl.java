/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.artifact;

import org.broadleafcommerce.openadmin.server.service.artifact.image.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/10/11
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("blArtifactService")
public class ArtifactServiceImpl implements ArtifactService {

    @Autowired
    protected List<ArtifactProcessor> artifactProcessors;

    @Override
    public InputStream convert(InputStream artifactStream, Operation[] operations, String mimeType) throws Exception {
        for (ArtifactProcessor artifactProcessor : artifactProcessors) {
            if (artifactProcessor.isSupported(artifactStream, mimeType)) {
                return artifactProcessor.convert(artifactStream, operations, mimeType);
            }
        }

        return artifactStream;
    }

    @Override
    public Operation[] buildOperations(Map<String, String> parameterMap, InputStream artifactStream, String mimeType) {
        for (ArtifactProcessor artifactProcessor : artifactProcessors) {
            if (artifactProcessor.isSupported(artifactStream, mimeType)) {
                return artifactProcessor.buildOperations(parameterMap, artifactStream, mimeType);
            }
        }

        return null;
    }
}
