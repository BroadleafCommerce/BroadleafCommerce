package org.broadleafcommerce.common.rule;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public interface ComplexRule extends Serializable {

    public Integer getQuantity();

    public void setQuantity(Integer quantity);

    public String getMatchRule();

    public void setMatchRule(String matchRule);

}
