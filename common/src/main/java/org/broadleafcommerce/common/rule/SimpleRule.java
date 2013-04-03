package org.broadleafcommerce.common.rule;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public interface SimpleRule extends Serializable {

    /**
     *
     * @return the rule as an MVEL string
     */
    @Nonnull
    public String getMatchRule();

    /**
     * Sets the match rule used to test this item.
     *
     * @param matchRule
     */
    public void setMatchRule(@Nonnull String matchRule);

}
