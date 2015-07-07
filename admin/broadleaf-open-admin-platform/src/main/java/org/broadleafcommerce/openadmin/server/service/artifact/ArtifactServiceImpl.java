/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.service.artifact;

import org.broadleafcommerce.openadmin.server.service.artifact.image.Operation;
import org.springframework.stereotype.Service;

import java.io.InputStream;
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

    protected ArtifactProcessor[] artifactProcessors;

    @Override
    public InputStream convert(InputStream artifactStream, Operation[] operations, String mimeType) throws Exception {
        for (ArtifactProcessor artifactProcessor : artifactProcessors) {
            if (artifactProcessor.isSupported(artifactStream, mimeType)) {
                return artifactProcessor.convert(artifactStream, operations, mimeType);
            }
        }

        return artifactStream;
    }

    public Operation[] buildOperations(Map<String, String> parameterMap, InputStream artifactStream, String mimeType) {
        for (ArtifactProcessor artifactProcessor : artifactProcessors) {
            if (artifactProcessor.isSupported(artifactStream, mimeType)) {
                return artifactProcessor.buildOperations(parameterMap, artifactStream, mimeType);
            }
        }

        return null;
    }

    @Override
    public ArtifactProcessor[] getArtifactProcessors() {
        return artifactProcessors;
    }

    @Override
    public void setArtifactProcessors(ArtifactProcessor[] artifactProcessors) {
        this.artifactProcessors = artifactProcessors;
    }
}
