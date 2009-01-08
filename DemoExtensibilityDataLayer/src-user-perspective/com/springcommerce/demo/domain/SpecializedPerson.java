package com.springcommerce.demo.domain;

import java.io.Serializable;

import com.springcommerce.demo.framework.domain.Person;

public class SpecializedPerson extends Person implements Serializable {

	private String occupation;

	/**
	 * @return the occupation
	 */
	public String getOccupation() {
		return occupation;
	}

	/**
	 * @param occupation the occupation to set
	 */
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	
}
