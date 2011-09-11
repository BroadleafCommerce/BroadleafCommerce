package org.broadleafcommerce.openadmin.server.service.artifact;

import org.broadleafcommerce.openadmin.server.service.artifact.image.Operation;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/10/11
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ArtifactProcessor {

    public boolean isSupported(InputStream artifactStream, String mimeType);

    public InputStream convert(InputStream artifactStream, Operation[] operations, String mimeType) throws Exception;

    public Operation[] buildOperations(Map<String, String[]> parameterMap, InputStream artifactStream, String mimeType);

}
