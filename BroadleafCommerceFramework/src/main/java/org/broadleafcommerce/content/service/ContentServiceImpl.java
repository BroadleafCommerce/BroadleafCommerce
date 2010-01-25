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
import org.broadleafcommerce.content.domain.ContentDetailsImpl;
import org.broadleafcommerce.content.domain.ContentImpl;
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
	public Content findContentById(Integer id) {
		return contentDao.readContentById(id);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#findContentDetailsById(java.lang.Long)
	 */
	public Content findContentDetailsById(Integer id) {
		return contentDao.readContentById(id);
	}

	public List<ContentDetails> findContentDetails(String sandbox, String contentType, Map<String, Object> mvelParameters){
		return findContentDetails(sandbox, contentType, mvelParameters, new Date(DateUtil.getNow()));
	}

	public List<ContentDetails> findContentDetails(String sandbox, String contentType, Map<String, Object> mvelParameters, Date displayDate){
		List<Content> contents = contentDao.readContentSpecified(sandbox, contentType, displayDate);
		List<Integer> contentIds = new ArrayList<Integer>();

		for (Content content : contents){
			if(mvelParameters != null && content.getDisplayRule() != null && content.getDisplayRule() != ""){
				if(!executeExpression(content.getDisplayRule(), mvelParameters)){
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

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#approveContent(java.util.List, java.lang.String, java.lang.String)
	 */
	public void approveContent(List<Integer> contentIds, String sandboxName, String username) {
		// deletes any content and associated contentDetail from the staging sandbox that
	    // matches the passed in contentKey (i.e. type and filename).
	    // Updates the sandBoxName for all matching content to null and sets the
	    // approved by and approved date values

		List<Content> contentList = contentDao.readContentByIdsAndSandbox(contentIds, sandboxName);
		List <Content> stageContentList = contentDao.readStagedContent();

		List<Content> deleteList = new ArrayList<Content>();
		List<Content> saveList = new ArrayList<Content>();

		for (Content content:contentList) {
			for (Content stageContent: stageContentList) {
				if (stageContent.getFilePathName().equals(content.getFilePathName())
				    && stageContent.getContentType().equals(content.getContentType())) {
					deleteList.add(stageContent);
				}
			}

			content.setSandbox(null);
			content.setApprovedBy(username);
			content.setApprovedDate(new Date());

			saveList.add(content);
		}

		contentDao.saveContent(saveList);
		contentDao.delete(deleteList);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#checkoutContentToSandbox(java.util.List, java.lang.String)
	 */
	public List<Content> checkoutContentToSandbox(List<Integer> contentIds, String sandboxName) {
		// copies all matching content items where sandbox = null
		// to the new records where sandbox = the passed in value
		// sets the deployed_flag to false for all items in the new sandbox

		List<Content> contentList = contentDao.readContentByIdsAndSandbox(contentIds, null);
		List<ContentDetails> contentDetailsList = contentDetailsDao.readContentDetailsByOrderedIds(contentIds);

		List<Content> newContentList = new ArrayList<Content>();

		for (Content content:contentList) {
			// copy content and set deployed to false
			Content newContent = new ContentImpl(content, sandboxName, false);
			Content createdContent = contentDao.saveContent(newContent);

			newContentList.add(createdContent);

			// look up the corresponding contact detail

			for (ContentDetails contentDetails:contentDetailsList) {
				if (contentDetails.getId().equals(content.getId())) {
					ContentDetails newContentDetails = new ContentDetailsImpl(contentDetails, createdContent.getId());
					contentDetailsDao.save(newContentDetails);

					break;
				}
			}
		}

		return newContentList;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#readContentAwaitingApproval()
	 */
	public List<Content> readContentAwaitingApproval() {
		// This method retrieves all content headers with sandbox that starts with AwaitingApproval
		return contentDao.readContentAwaitingApproval();
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#readContentForSandbox(java.lang.String)
	 */
	public List<Content> readContentForSandbox(String sandbox) {
		return contentDao.readContentBySandbox(sandbox);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#readContentForSandboxAndType(java.lang.String, java.lang.String)
	 */
	public List<Content> readContentForSandboxAndType(String sandbox, String contentType) {
		return contentDao.readContentBySandboxAndType(sandbox, contentType);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#rejectContent(java.util.List, java.lang.String, java.lang.String)
	 */
	public void rejectContent(List<Integer> contentIds, String sandbox, String username) {
	     // modifies the sandbox name for the matching content to equal the value in its submitted_by field
	     //  (e.g. update content set sandbox = submitted_by where id in (....)
	     // updates rejected by and rejected date

		List<Content> contentList = contentDao.readContentByIdsAndSandbox(contentIds, sandbox);

		for (Content content:contentList) {
			content.setRejectedBy(username);
			content.setRejectedDate(new Date());
			content.setSandbox(content.getSubmittedBy());
			content.setSubmittedBy(null);
			content.setSubmittedDate(null);
		}

		contentDao.saveContent(contentList);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#removeContentFromSandbox(java.util.List, java.lang.String)
	 */
	public void removeContentFromSandbox(List<Integer> contentIds, String sandbox) {
		// if sandbox is not null, deletes the content and associated content detail from the sandbox
	    // otherwise, ignores the request

		if (sandbox != null) {
			List<Content> contentList = contentDao.readContentByIdsAndSandbox(contentIds, sandbox);
			contentDao.delete(contentList);
		}
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#submitContentFromSandbox(java.util.List, java.lang.String, java.lang.String)
	 */
	public void submitContentFromSandbox(List<Integer> contentIds, String sandboxName, String username) {
		// updates the sandboxName to AwaitingApproval_$username_$timestamp and sets
		// the submitted by and submitted date values

		List<Content> contentList = contentDao.readContentByIdsAndSandbox(contentIds, sandboxName);

		for (Content content:contentList) {
			content.setSubmittedBy(username);
			content.setSubmittedDate(new Date());
			content.setSandbox("AwaitingApproval_" + username + "_" + new Date().getTime());
		}

		contentDao.saveContent(contentList);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.content.service.ContentService#saveContent(org.broadleafcommerce.content.domain.Content, org.broadleafcommerce.content.domain.ContentDetails)
	 */
	public Content saveContent(Content content, ContentDetails contentDetails) {
		Content contentFromDB = contentDao.saveContent(content);

		if (content != null) {
			contentDetails.setId(contentFromDB.getId());
			contentDetailsDao.save(contentDetails);
		}

		return null;
	}

}
