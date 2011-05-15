/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.other.domain;

import java.util.Date;

import org.broadleafcommerce.core.catalog.domain.ProductSku;

/**
 * 
 * @author jfischer
 *
 */
public interface OtherProduct extends ProductSku {
	
	Long getCompanyNumber();
	void setCompanyNumber(Long companyNumber);
	Date getReleaseDate();
	void setReleaseDate(Date releaseDate);

}
