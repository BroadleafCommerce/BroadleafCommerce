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

    public Operation[] buildOperations(Map<String, String[]> parameterMap, InputStream artifactStream, String mimeType) {
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
