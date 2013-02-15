package org.broadleafcommerce.core.offer.service.discount.domain;

import java.math.RoundingMode;


public abstract class AbstractPromotionRounding implements PromotionRounding {

    protected boolean roundOfferValues = true;
    protected int roundingScale = 2;
    protected RoundingMode roundingMode = RoundingMode.HALF_EVEN;

    /**
     * It is sometimes problematic to offer percentage-off offers with regards to rounding. For example,
     * consider an item that costs 9.99 and has a 50% promotion. To be precise, the offer value is 4.995,
     * but this may be a strange value to display to the user depending on the currency being used.
     */
    public boolean isRoundOfferValues() {
        return roundOfferValues;
    }

    /**
     * @see #isRoundOfferValues()
     * 
     * @param roundingScale
     */
    public void setRoundingScale(int roundingScale) {
        this.roundingScale = roundingScale;
    }

    /**
     * @see #isRoundOfferValues()
     * 
     * @param roundingMode
     */
    public void setRoundingMode(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }

    @Override
    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    @Override
    public int getRoundingScale() {
        return roundingScale;
    }

}
