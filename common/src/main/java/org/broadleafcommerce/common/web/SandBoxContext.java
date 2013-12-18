/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.common.web;


import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;

public class SandBoxContext {
    
    private static final ThreadLocal<SandBoxContext> SANDBOXCONTEXT = ThreadLocalManager.createThreadLocal(SandBoxContext.class);
    
    public static SandBoxContext getSandBoxContext() {
        return SANDBOXCONTEXT.get();
    }
    
    public static void setSandBoxContext(SandBoxContext sandBoxContext) {
        SANDBOXCONTEXT.set(sandBoxContext);
    }

    protected Long sandBoxId;
    protected Boolean previewMode = false;

    /**
     * @return the sandBoxName
     */
    public Long getSandBoxId() {
        return sandBoxId;
    }
    
    /**
     * @param sandBoxId the sandBoxName to set
     */
    public void setSandBoxId(Long sandBoxId) {
        this.sandBoxId = sandBoxId;
    }

    public Boolean getPreviewMode() {
        return previewMode;
    }

    public void setPreviewMode(Boolean previewMode) {
        this.previewMode = previewMode;
    }

    public SandBoxContext clone() {
        SandBoxContext myContext = new SandBoxContext();
        myContext.setSandBoxId(getSandBoxId());
        myContext.setPreviewMode(getPreviewMode());

        return myContext;
    }
}
