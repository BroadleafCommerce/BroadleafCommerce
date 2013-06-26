/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.util.InMemoryResource;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.io.IOException;

/**
 * An in memory generated resource. This class also overrides some parent Spring AbstractResource methods to ensure
 * compatibility with the {@link ResourceHttpRequestHandler}.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class GeneratedResource extends InMemoryResource {
    
    protected long timeGenerated;
    protected String hashRepresentation;

    public GeneratedResource(byte[] source, String description) {
        super(source, description);
        timeGenerated = System.currentTimeMillis();
    }
    
    @Override
    public String getFilename() {
        return getDescription();
    }
    
    @Override
	public long lastModified() throws IOException {
        return timeGenerated;
    }
    
    public String getHashRepresentation() {
        return StringUtils.isBlank(hashRepresentation) ? String.valueOf(timeGenerated) : hashRepresentation;
    }

    public void setHashRepresentation(String hashRepresentation) {
        this.hashRepresentation = hashRepresentation;
    }

}
