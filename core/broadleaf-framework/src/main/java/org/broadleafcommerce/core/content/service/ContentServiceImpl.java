/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.content.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.content.dao.ContentDao;
import org.broadleafcommerce.core.content.dao.ContentDetailsDao;
import org.broadleafcommerce.core.content.domain.Content;
import org.broadleafcommerce.core.content.domain.ContentDetails;
import org.broadleafcommerce.core.content.domain.ContentDetailsImpl;
import org.broadleafcommerce.core.content.domain.ContentImpl;
import org.broadleafcommerce.core.content.domain.ContentPageInfo;
import org.broadleafcommerce.core.content.domain.ContentXmlData;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dwtalk
 *
 */
@Service("blContentService")
public class ContentServiceImpl implements ContentService {
    private static final Log LOG = LogFactory.getLog(ContentServiceImpl.class);
    private static final LRUMap EXPRESSION_CACHE = new LRUMap(1000);
    
    //To sort content by priority
    public class ContentComparator implements Comparator<org.broadleafcommerce.core.content.domain.Content> {

        public int compare(org.broadleafcommerce.core.content.domain.Content o1, org.broadleafcommerce.core.content.domain.Content o2) {

            if (o1.getPriority() < o2.getPriority()) {
                return -1;

            } else if (o1.getPriority() > o2.getPriority()) {
                return 1;

            } else
                return 0;

        }
    }


    @Resource(name="blContentDao")
    protected ContentDao contentDao;

    @Resource(name="blContentDetailsDao")
    protected ContentDetailsDao contentDetailsDao;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.service.ContentService#findContentById(java.lang.Long)
     */
    public Content findContentById(Integer id) {
        return contentDao.readContentById(id);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.service.ContentService#findContentDetailsById(java.lang.Long)
     */
    public ContentDetails findContentDetailsById(Integer id) {
        return contentDetailsDao.readContentDetailsById(id);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.service.ContentService#findContentDetailsXmlById(java.lang.Long)
     */
    public String findContentDetailsXmlById(Integer id) {
        return findContentDetailsById(id).getXmlContent();
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.service.ContentService#findContentDetailsMapById(java.lang.Long)
     */
    public Map<String, Object> findContentDetailsMapById(Integer id) throws Exception{

        Map<String, Object> root = new HashMap<String, Object>();

        String xmlContent = findContentDetailsXmlById(id);
        StringReader reader = new StringReader(xmlContent);
        InputSource inputSource = new InputSource(reader);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(inputSource);
        }catch (Exception e){
            LOG.error("Parse exception. ",e);
            throw e;
        }

        Element rootElm = doc.getDocumentElement();
        NodeList nodeLst = rootElm.getChildNodes();

        for (int s = 0; s < nodeLst.getLength(); s++) {

            Node fstNode = nodeLst.item(s);

            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fstElmnt = (Element) fstNode;
                String elName = fstNode.getNodeName();
                NodeList nl = fstElmnt.getChildNodes();
                Node dataNode = nl.item(0);
                if (dataNode != null) {
                    String nodeData = dataNode.getNodeValue();
                    if (elName.contains("64")) {
                        try{
                            root.put(elName, new String(Base64.decodeBase64(nodeData.getBytes()), "UTF-8"));
                        }catch (Exception e){
                            LOG.error("Error during decode. ",e);
                            throw e;
                        }
                    }
                    else if(nodeData.contains("<![CDATA["))
                    {

                        nodeData.replace("<![CDATA[", "");
                        nodeData.replace("]]>", "");
                        root.put(elName, nodeData);
                    }
                    else {
                        root.put(elName, nodeData);
                    }
                }
            }
        }

        return root;
    }

    public List<ContentXmlData>  findContentDetailsListById(Integer id) throws Exception{
        Map<String, Object> map = this.findContentDetailsMapById(id);
        List<ContentXmlData> xmlDataList = new ArrayList<ContentXmlData>();

        for (Iterator<String> itr = map.keySet().iterator(); itr.hasNext();){
            ContentXmlData xmlData = new ContentXmlData();
            String name = itr.next();
            xmlData.setName(name);
            xmlData.setData(map.get(name));
            xmlDataList.add(xmlData);
        }

        return xmlDataList;
    }

    public List<ContentDetails> findContentDetails(String sandbox, String contentType, Map<String, Object> mvelParameters){
        return findContentDetails(sandbox, contentType, mvelParameters, SystemTime.asDate());
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

    public List<Content> findContent(String sandbox, String contentType, Map<String, Object> mvelParameters, Date displayDate){
             return contentDao.readContentSpecified(sandbox, contentType, displayDate);
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
    
    public String renderedContent(String styleSheetString, List<Content> contentList, int rowCount) throws Exception{
        Source xmlSource;
        int maxCount = (rowCount > -1 && contentList.size() > 0)? rowCount : contentList.size();

        Writer resultWriter = new StringWriter();
         StreamResult result = new StreamResult(resultWriter);
        Source styleSheetSource = getSource(styleSheetString);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(styleSheetSource);

        //TODO: some code here to group and show random item within priority group
        Comparator<Content> cntntCompare = new ContentComparator();
        Collections.sort(contentList, cntntCompare);
             
        for(int i=0; i < maxCount; i++){
            ContentDetails contentDetail = findContentDetailsById(contentList.get(i).getId());
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
     * @see org.broadleafcommerce.core.content.service.ContentService#approveContent(java.util.List, java.lang.String, java.lang.String)
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
                if (stageContent.getTitle().equals(content.getTitle())
                    && stageContent.getContentType().equals(content.getContentType())) {
                    deleteList.add(stageContent);
                }
            }

            content.setSandbox(null);
            content.setApprovedBy(username);
            content.setApprovedDate(SystemTime.asDate());

            saveList.add(content);
        }

        contentDao.saveContent(saveList);
        contentDao.delete(deleteList);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.service.ContentService#checkoutContentToSandbox(java.util.List, java.lang.String)
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
     * @see org.broadleafcommerce.core.content.service.ContentService#readContentAwaitingApproval()
     */
    public List<Content> readContentAwaitingApproval() {
        // This method retrieves all content headers with sandbox that starts with AwaitingApproval
        return contentDao.readContentAwaitingApproval();
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.service.ContentService#readContentForSandbox(java.lang.String)
     */
    public List<Content> readContentForSandbox(String sandbox) {
        List<Content> list =  contentDao.readContentBySandbox(sandbox);
        return list;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.service.ContentService#readContentForSandboxAndType(java.lang.String, java.lang.String)
     */
    public List<Content> readContentForSandboxAndType(String sandbox, String contentType) {
        List<Content> list = contentDao.readContentBySandboxAndType(sandbox, contentType);
        return list;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.service.ContentService#rejectContent(java.util.List, java.lang.String, java.lang.String)
     */
    public void rejectContent(List<Integer> contentIds, String sandbox, String username) {
         // modifies the sandbox name for the matching content to equal the value in its submitted_by field
         //  (e.g. update content set sandbox = submitted_by where id in (....)
         // updates rejected by and rejected date

        List<Content> contentList = contentDao.readContentByIdsAndSandbox(contentIds, sandbox);

        for (Content content:contentList) {
            content.setRejectedBy(username);
            content.setRejectedDate(SystemTime.asDate());
            content.setSandbox(content.getSubmittedBy());
            content.setSubmittedBy(null);
            content.setSubmittedDate(null);
        }

        contentDao.saveContent(contentList);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.service.ContentService#removeContentFromSandbox(java.util.List, java.lang.String)
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
     * @see org.broadleafcommerce.core.content.service.ContentService#submitContentFromSandbox(java.util.List, java.lang.String, java.lang.String)
     */
    public void submitContentFromSandbox(List<Integer> contentIds, String sandboxName, String username, String note) {
        // updates the sandboxName to AwaitingApproval_$username_$timestamp and sets
        // the submitted by and submitted date values

        List<Content> contentList = contentDao.readContentByIdsAndSandbox(contentIds, sandboxName);

        for (Content content:contentList) {
            content.setSubmittedBy(username);
            content.setSubmittedDate(SystemTime.asDate());
            content.setNote(note);
            content.setSandbox("AwaitingApproval_" + username + "_" + SystemTime.asDate().getTime());
        }

        contentDao.saveContent(contentList);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.service.ContentService#saveContent(org.broadleafcommerce.core.content.domain.Content, org.broadleafcommerce.core.content.domain.ContentDetails)
     */
    public Content saveContent(Content content, List<ContentXmlData> details){

        ContentDetails contentDetails = contentDetailsDao.readContentDetailsById(content.getId());
        if (contentDetails == null){
            contentDetails = new ContentDetailsImpl();
        }

        String xmlContent = constructXmlContent(details);
        if (xmlContent!= null){
            contentDetails.setXmlContent(xmlContent);
            try {
                contentDetails.setContentHash(SHA1(xmlContent));
            } catch (NoSuchAlgorithmException e) {
                LOG.error("NoSuchAlgorithmException", e);
            } catch (UnsupportedEncodingException e) {
                LOG.error("UnsupportedEncodingException", e);
            }

            Content saved = contentDao.saveContent(content);
            contentDetails.setId(saved.getId());
            contentDetailsDao.save(contentDetails);
        }

        return null;
    }

    public List<ContentPageInfo> readAllContentPageInfos(){
        Map<Integer,String> parentMap = constructParentUrlMap();
        if (parentMap != null) {
            List<ContentPageInfo> pageInfoList = new ArrayList<ContentPageInfo>();

            for (Iterator<Integer> itr = parentMap.keySet().iterator(); itr.hasNext();){
                Integer key = itr.next();
                ContentPageInfo pageInfo = new ContentPageInfo();
                pageInfo.setId(key);
                pageInfo.setFullUrl(parentMap.get(key));
                pageInfoList.add(pageInfo);
            }

            return pageInfoList;
        }
        return null;
    }

    public Map<Integer, String> constructParentUrlMap(){
        List<Content> content = contentDao.readAllContent();

        if (content != null){
            Map<Integer, Content> idContentMap = new HashMap<Integer, Content>();
            Map<Integer, String> parentUrlMap = new HashMap<Integer, String>();
            for (Content c : content){
                idContentMap.put(c.getId(), c);
            }

            for (Content c: content) {
                String fullPath = constructFullPath(c.getUrlTitle(), c.getParentContentId(), idContentMap);
                parentUrlMap.put(c.getId(), fullPath);
            }

            return parentUrlMap;
        }

        return null;

    }

    private String constructFullPath(String path, Integer parentId, Map<Integer, Content> idContentMap){

        if (idContentMap.get(parentId) != null && parentId != 0){
            path = idContentMap.get(parentId).getUrlTitle() + "/" + path;
            constructFullPath(path, idContentMap.get(parentId).getParentContentId(), idContentMap);
        }
        return path;
    }


    private String constructXmlContent(List<ContentXmlData> details) {
        if (details != null) {
            String xml = "";
            xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?><content>";

            for (Iterator<ContentXmlData> itr = details.iterator(); itr.hasNext();){
                ContentXmlData data = itr.next();
                xml += "<"+data.getName()+">";
                xml += "<![CDATA[";
                xml += data.getData().toString();
                xml += "]]>";
                xml += "</"+data.getName()+">";
            }

            xml += "</content>";
            return xml;
        }

        return null;

    }

    private String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    private String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

}