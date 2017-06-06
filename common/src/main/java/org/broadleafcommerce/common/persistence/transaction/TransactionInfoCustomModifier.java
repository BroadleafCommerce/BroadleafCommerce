package org.broadleafcommerce.common.persistence.transaction;

import org.springframework.core.Ordered;

/**
 * Allows further customization of {@link TransactionInfo} instances upon transaction begin. Implementations can
 * modify existing values or add new ones. This is useful for tracking RDBMS specific attributes - see
 * {@link TransactionInfo#additionalParams}.
 *
 * @author Jeff Fischer
 */
public interface TransactionInfoCustomModifier extends Ordered {

    void modify(TransactionInfo info);

}
