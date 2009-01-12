package com.springcommerce.demo.user.domain;

import java.io.Serializable;

import com.springcommerce.demo.framework.domain.Person;

public class SpecializedPerson extends Person implements Serializable {

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
