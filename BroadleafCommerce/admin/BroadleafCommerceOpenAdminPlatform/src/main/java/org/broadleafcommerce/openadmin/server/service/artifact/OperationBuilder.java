package org.broadleafcommerce.openadmin.server.service.artifact;

import org.broadleafcommerce.openadmin.server.service.artifact.image.Operation;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/10/11
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OperationBuilder {

    public Operation buildOperation(Map<String, String[]> parameterMap, InputStream artifactStream, String mimeType);

}
