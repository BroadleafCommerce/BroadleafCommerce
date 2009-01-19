package com.springcommerce.demo.user.domain;

import com.springcommerce.demo.framework.domain.AbstractPerson;

public class SpecializedPerson extends AbstractPerson {

	private int ageFactor;
	
	/**
	 * @return the ageFactor
	 */
	public int getAgeFactor() {
		return ageFactor;
	}

	/**
	 * @param ageFactor the ageFactor to set
	 */
	public void setAgeFactor(int ageFactor) {
		this.ageFactor = ageFactor;
	}
}
