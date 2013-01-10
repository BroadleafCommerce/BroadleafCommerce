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
package org.broadleafcommerce.layout.tags;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.taglibs.standard.tag.common.core.Util;
import org.broadleafcommerce.content.domain.Content;
import org.broadleafcommerce.content.service.ContentService;
import org.broadleafcommerce.time.SystemTime;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ContentSpecifiedTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;

    
    private Map<String, Object> parameterMap;
    private String contentType;
    private Object xslt;
    private boolean escapeXml;
    private int rowCount;
    
    public ContentSpecifiedTag(){
        super();
        init();
    }
    
    private void init(){
        escapeXml = true;
        rowCount = -1;
    }
    
    
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the parameterMap
     */
    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }
    /**
     * @param parameterMap the parameterMap to set
     */
    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }
    
    /**
     * @return the xslt
     */
    public Object getXslt() {
        return xslt;
    }
    /**
     * @param xslt the xslt to set
     */
    public void setXslt(Object xslt) {
        this.xslt = xslt;
    }
    
    /**
     * @return the escapeXml
     */
    public boolean isEscapeXml() {
        return escapeXml;
    }
    /**
     * @param escapeXml the escapeXml to set
     */
    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }
    
    /**
     * @return the rowCount
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * @param rowCount the rowCount to set
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public int doStartTag() throws JspException {
//        PageContext pageContext = (PageContext)getJspContext();       
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
        ContentService contentService = (ContentService) applicationContext.getBean("blContentService");
        List<Content> contentObjs;
        Date displayDate = null;
        String sandbox = "PROD";

        HttpSession session = pageContext.getSession();
        if(session != null){
            
            String newSandbox = (String)session.getAttribute("BLC_CONTENT_SANDBOX");
            String displayDateString = (String)session.getAttribute("BLC_CONTENT_DATE_TIME");
            
            if(newSandbox != null && newSandbox != ""){
                sandbox = newSandbox;
            }
            
            if(displayDateString != null && displayDateString != ""){
                try{
                    
                    displayDate = new SimpleDateFormat("MM-dd-yyyy").parse(displayDateString);
                }catch (ParseException exp){
                    throw new JspException();
                }
            }
        }
        
        if(displayDate == null){
            contentObjs = contentService.findContent(sandbox, contentType, parameterMap, SystemTime.asDate());
        }else{
            contentObjs = contentService.findContent(sandbox, contentType, parameterMap, displayDate);
        }

        JspWriter out = pageContext.getOut();
        try{
            String renderedText = contentService.renderedContent((String)xslt, contentObjs, rowCount); 
            if(!escapeXml){
                out.write(renderedText);                            
            }else{
                writeEscapedXml(renderedText.toCharArray(), renderedText.length(), out);
            }
        }catch (Exception e){
            throw new JspException();
        }
        
        return EVAL_PAGE;
        //        pageContext.setAttribute(contentDetailsProperty, contentXmls);
    }
    
    private static void writeEscapedXml(char[] buffer, int length, JspWriter w) throws IOException{
        int start = 0;

        for (int i = 0; i < length; i++) {
            char c = buffer[i];
            if (c <= Util.HIGHEST_SPECIAL) {
                char[] escaped = Util.specialCharactersRepresentation[c];
                if (escaped != null) {
                    // add unescaped portion
                    if (start < i) {
                        w.write(buffer,start,i-start);
                    }
                    // add escaped xml
                    w.write(escaped);
                    start = i + 1;
                }
            }
        }
        // add rest of unescaped portion
        if (start < length) {
            w.write(buffer,start,length-start);
        }
    }
}
