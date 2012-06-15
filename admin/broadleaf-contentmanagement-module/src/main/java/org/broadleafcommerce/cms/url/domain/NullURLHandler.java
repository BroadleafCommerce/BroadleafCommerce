/**
 * 
 */
package org.broadleafcommerce.cms.url.domain;

import org.broadleafcommerce.cms.url.type.URLRedirectType;


/**
 * A Null instance of a URLHandler.   Used by the default URLHandlerServiceImpl implementation to 
 * cache misses (e.g. urls  that are not being handled by forwards and redirects.
 * 
 * @author bpolster
 */
public class NullURLHandler implements URLHandler,java.io.Serializable {
	 private static final long serialVersionUID = 1L;

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public void setId(Long id) {
	}

	@Override
	public String getIncomingURL() {
		return null;
	}

	@Override
	public void setIncomingURL(String incomingURL) {
	}

	@Override
	public String getNewURL() {
		return null;
	}

	@Override
	public void setNewURL(String newURL) {
	}

	@Override
	public URLRedirectType getUrlRedirectType() {
		return null;
	}

	@Override
	public void setUrlRedirectType(URLRedirectType redirectType) {
	}

	 
}
