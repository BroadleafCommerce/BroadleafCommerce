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
package org.broadleafcommerce.content.domain;

import java.util.Date;

/**
* DOCUMENT ME!
*
* @author btaylor
 */
public interface Content {
    public Date getActiveEndDate();

    public Date getActiveStartDate();

    public String getApprovedBy();

    public Date getApprovedDate();

    public String getContentType();

    public String getDisplayRule();

    public String getFilePathName();

    public Long getId();

    public Integer getMaxCount();

    public String getNote();

    public Integer getPriority();

    public String getRejectedBy();

    public Date getRejectedDate();

    public String getSandbox();

    public String getSubmittedBy();

    public Date getSubmittedDate();

    public Boolean isActive();

    public boolean isDeployed();

    public void setActive(Boolean active);

    public void setActiveEndDate(Date activeEndDate);

    public void setActiveStartDate(Date activeStartDate);

    public void setApprovedBy(String approvedBy);

    public void setApprovedDate(Date approvedDate);

    public void setContentType(String contentType);

    public void setDisplayRule(String displayRule);

    public void setFilePathName(String filePathName);

    public void setId(Long id);

    public void setMaxCount(Integer maxCount);

    public void setPriority(Integer priority);

    public void setRejectedBy(String rejectedBy);

    public void setRejectedDate(Date rejectedDate);

    public void setSandbox(String sandbox);

    public void setSubmittedBy(String submimttedBy);

    public void setSubmittedDate(Date submittedDate);
}
