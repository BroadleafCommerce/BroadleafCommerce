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

import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.content.dao.ContentDao;
import org.broadleafcommerce.content.dao.ContentDetailsDao;
import org.broadleafcommerce.content.domain.Content;
import org.broadleafcommerce.content.domain.ContentDetails;
import org.broadleafcommerce.util.DateUtil;
import org.compass.core.util.reader.StringReader;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

/**
 * @author btaylor
 *
 */
@Service("blContentService")
public class ContentServiceImpl implements ContentService {
    private static final Log LOG = LogFactory.getLog(ContentServiceImpl.class);
    private static final LRUMap EXPRESSION_CACHE = new LRUMap(100);
    

    @Resource(name="blContentDao")
	protected ContentDao contentDao;
	
	@Resource(name="blContentDetailsDao")
	protected ContentDetailsDao contentDetailsDao;
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#findContentById(java.lang.Long)
	 */
	public Content findContentById(Long id) {
		return contentDao.readContentById(id);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#findContentDetailsById(java.lang.Long)
	 */
	public Content findContentDetailsById(Long id) {
		return contentDao.readContentById(id);
	}

	public List<ContentDetails> findContentDetails(String sandbox, String contentType, Map<String, Object> mvelParameters){
		return findContentDetails(sandbox, contentType, mvelParameters, new Date(DateUtil.getNow()));
	}
	
	public List<ContentDetails> findContentDetails(String sandbox, String contentType, Map<String, Object> mvelParameters, Date displayDate){
		List<Content> contents = contentDao.readContentSpecified(sandbox, contentType, displayDate);		
		List<Long> contentIds = new ArrayList<Long>();
		
		for (Content content : contents){
			if(mvelParameters != null && content.getMvel() != null && content.getMvel() != ""){
				if(!executeExpression(content.getMvel(), mvelParameters)){
					contentIds.add(content.getId());
				}
			}else{
				contentIds.add(content.getId());
			}			
		}
		if(contentIds.size() > 0){
			return contentDetailsDao.readContentDetailsByOrderedIds(contentIds);							
		}else{
			return new ArrayList<ContentDetails>();
		}
		
	}

	public String renderedContentDetails(String styleSheetString, List<ContentDetails> contentDetails) throws Exception{
		return renderedContentDetails(styleSheetString, contentDetails, -1);
	}
	
	public String renderedContentDetails(String styleSheetString, List<ContentDetails> contentDetails, int rowCount) throws Exception{
		Source xmlSource;
		int maxCount = (rowCount > -1 && contentDetails.size() > 0)? rowCount : contentDetails.size();
		
		Writer resultWriter = new StringWriter();
	    StreamResult result = new StreamResult(resultWriter);
	    Source styleSheetSource = getSource(styleSheetString);
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer(styleSheetSource);
	    
	    for(int i=0; i < maxCount; i++){
	    	ContentDetails contentDetail = contentDetails.get(i);
	    	xmlSource = getSource(contentDetail.getXmlContent());	    	
	    	try{
	    		transformer.transform(xmlSource, result);	    	
	    	}catch (Exception e){
	    		LOG.error("Error during transformation. ",e);
	    		throw e;
	    	}
	    	
	    }
		return StringEscapeUtils.unescapeXml(resultWriter.toString()) ;
	}
	
    protected Boolean executeExpression(String expression, Map<String, Object> vars) {
        Serializable exp = (Serializable)EXPRESSION_CACHE.get(expression);
        if (exp == null) {
            ParserContext context = new ParserContext();
            exp = MVEL.compileExpression(expression.toString(), context);
        }
        EXPRESSION_CACHE.put(expression, exp);

        return (Boolean)MVEL.executeExpression(exp, vars);

    }
    
    private Source getSource(String sourceString){
	    StringReader styleSheetSourceReader = new StringReader(sourceString);
	    InputSource inputStyleSheetSource = new InputSource(styleSheetSourceReader);
	    return new SAXSource(inputStyleSheetSource);
    	
    }
    
}
