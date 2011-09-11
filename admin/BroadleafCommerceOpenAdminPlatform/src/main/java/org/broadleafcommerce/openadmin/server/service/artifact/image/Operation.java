package org.broadleafcommerce.openadmin.server.service.artifact.image;

import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.UnmarshalledParameter;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/10/11
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class Operation {

    protected String name;
    protected Double factor;
    protected UnmarshalledParameter[] parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public UnmarshalledParameter[] getParameters() {
        return parameters;
    }

    public void setParameters(UnmarshalledParameter[] parameters) {
        this.parameters = parameters;
    }
}
