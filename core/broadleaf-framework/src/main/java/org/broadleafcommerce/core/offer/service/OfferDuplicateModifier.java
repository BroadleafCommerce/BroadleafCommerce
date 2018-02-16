package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.persistence.EntityDuplicateModifier;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.springframework.stereotype.Component;

/**
 * Modify new Offer duplicates before persistence
 *
 * @author Jeff Fischer
 */
@Component("blOfferDuplicateModifier")
public class OfferDuplicateModifier implements EntityDuplicateModifier<Offer> {

    @Override
    public void modifyInitialDuplicateState(Offer copy) {
        String currentName = copy.getName();
        copy.setName("Copy Of ("+currentName+")");
        copy.setStartDate(null);
        copy.setEndDate(null);
    }
}
