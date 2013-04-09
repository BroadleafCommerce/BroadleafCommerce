package org.broadleafcommerce.common.resource;

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

}
