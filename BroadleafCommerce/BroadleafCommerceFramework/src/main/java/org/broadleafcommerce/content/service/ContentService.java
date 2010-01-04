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
package org.broadleafcommerce.content.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.content.domain.Content;
import org.broadleafcommerce.content.domain.ContentDetails;

/**
 * @author btaylor
 *
 */
public interface ContentService {

	public Content findContentById(Long id);
	
	public Content findContentDetailsById(Long id);
	
	public List<ContentDetails> findContentDetails(String sandbox, String contentType, Map<String, Object> mvelParameters);
	
	public List<ContentDetails> findContentDetails(String sandbox, String contentType, Map<String, Object> mvelParameters, Date displayDate);
	
	public String renderedContentDetails(String styleSheet, List<ContentDetails> contentDetails) throws Exception;

	public String renderedContentDetails(String styleSheetString, List<ContentDetails> contentDetails, int rowCount) throws Exception;
	
}
