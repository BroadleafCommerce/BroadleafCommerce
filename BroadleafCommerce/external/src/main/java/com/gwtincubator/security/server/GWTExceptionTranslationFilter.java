package com.gwtincubator.security.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.util.AntUrlPathMatcher;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.security.web.util.ThrowableCauseExtractor;
import org.springframework.security.web.util.UrlMatcher;
import org.springframework.util.Assert;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

/**
 * Spring Security custom Exception Translation Filter.
 * <p>
 * This class proposes a new behavior for specific request identified with the help
 * of (ant style) patterns. Thus GWT client can handle security error responses.
 * </p>  
 * @author David MARTIN
 * 
 * BroadleafCommerce
 * Changed to make compatible with Spring 3
 * @author jfischer
 */
public class GWTExceptionTranslationFilter extends ExceptionTranslationFilter {

	//~ Instance fields ================================================================================================

	private UrlMatcher matcher = new AntUrlPathMatcher();
	private Set<String> gwtPaths = new HashSet<String>();
	private boolean forbiddenCodeHttpResponse = false;
	private AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();
	private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();    

	//~ Methods ========================================================================================================

	public void afterPropertiesSet() {
		Assert.notNull(getAuthenticationEntryPoint(), "authenticationEntryPoint must be specified");
		//Assert.notNull(getPortResolver(), "portResolver must be specified");
		Assert.notNull(getAuthenticationTrustResolver(), "authenticationTrustResolver must be specified");
		Assert.notNull(throwableAnalyzer, "throwableAnalyzer must be specified");
		Assert.notNull(matcher, "matcher must be specified");
		Assert.notNull(gwtPaths, "gwtPaths must be specified");
	}

	public void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException,
	ServletException {

		try {
			chain.doFilter(request, response);

			if (logger.isDebugEnabled()) {
				logger.debug("Chain processed normally");
			}
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			// Try to extract a SpringSecurityException from the stacktrace
			Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(ex);
			Throwable ase = this.throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
			if (ase == null) {
				ase = this.throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);
			}

			if (ase != null) {
				handleException(request, response, chain, ase);
			} else {
				// Rethrow ServletExceptions and RuntimeExceptions as-is
				if (ex instanceof ServletException) {
					throw (ServletException) ex;
				}
				else if (ex instanceof RuntimeException) {
					throw (RuntimeException) ex;
				}

				// Wrap other Exceptions. These are not expected to happen
				throw new RuntimeException(ex);
			}
		}
	}

	protected boolean matchGWTPath(String url) {
		int firstQuestionMarkIndex = url.indexOf("?");

		if (firstQuestionMarkIndex != -1) {
			url = url.substring(0, firstQuestionMarkIndex);
		}

		if (matcher.requiresLowerCaseUrl()) {
			url = url.toLowerCase();

			if (logger.isDebugEnabled()) {
				logger.debug("Converted URL to lowercase, from: '" + url + "'; to: '" + url + "'");
			}
		}

		boolean matched = false;
		for (String path : gwtPaths) {
			matched = matcher.pathMatchesUrl(path, url);
			if (matched) {
				return true;
			}
		}
		return false;
	}

	private void handleException(ServletRequest request, ServletResponse response, FilterChain chain, Throwable exception) throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		String url = httpRequest.getRequestURI();
		url = url.substring(httpRequest.getContextPath().length());
		boolean matched = matchGWTPath(url);
		if (matched) {
			final HttpServletResponse httpResponse = (HttpServletResponse) response;
			if (!forbiddenCodeHttpResponse) {
				try {
					final String failureResponse = RPC.encodeResponseForFailure(null,
							SecurityExceptionFactory.get(exception));
					httpResponse.getOutputStream().print(failureResponse);
					httpResponse.getOutputStream().flush();
				} catch (SerializationException e) {
					logger.error("RPC Serialization exception");
				}
			} else {
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
			}
			return;
		}

		if (exception instanceof AuthenticationException) {
			if (logger.isDebugEnabled()) {
				logger.debug("Authentication exception occurred; redirecting to authentication entry point", exception);
			}

			sendStartAuthentication((HttpServletRequest) request, (HttpServletResponse) response, chain, (AuthenticationException) exception);
		} else if (exception instanceof AccessDeniedException) {
			if (getAuthenticationTrustResolver().isAnonymous(SecurityContextHolder.getContext().getAuthentication())) {
				if (logger.isDebugEnabled()) {
					logger.debug("Access is denied (user is anonymous); redirecting to authentication entry point",
							exception);
				}

				sendStartAuthentication((HttpServletRequest) request, (HttpServletResponse) response, chain, new InsufficientAuthenticationException(
				"Full authentication is required to access this resource"));
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Access is denied (user is not anonymous); delegating to AccessDeniedHandler",
							exception);
				}

				accessDeniedHandler.handle((HttpServletRequest) request, (HttpServletResponse) response, (AccessDeniedException) exception);
			}
		}
	}

	public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
		Assert.notNull(accessDeniedHandler, "AccessDeniedHandler required");
		this.accessDeniedHandler = accessDeniedHandler;
	}

	public void setThrowableAnalyzer(ThrowableAnalyzer throwableAnalyzer) {
		this.throwableAnalyzer = throwableAnalyzer;
	}    

	/**
	 * @return the matcher
	 */
	public UrlMatcher getMatcher() {
		return matcher;
	}

	/**
	 * @param matcher the matcher to set
	 */
	public void setMatcher(UrlMatcher matcher) {
		this.matcher = matcher;
	}

	/**
	 * @return the gwtPaths
	 */
	public Set<String> getGwtPaths() {
		return gwtPaths;
	}

	/**
	 * @param gwtPaths the gwtPaths to set
	 */
	public void setGwtPaths(Set<String> gwtPaths) {
		this.gwtPaths = gwtPaths;
	}

	/**
	 * @return the forbiddenCodeHttpResponse
	 */
	public boolean isForbiddenCodeHttpResponse() {
		return forbiddenCodeHttpResponse;
	}

	/**
	 * @param forbiddenCodeHttpResponse the forbiddenCodeHttpResponse to set
	 */
	public void setForbiddenCodeHttpResponse(boolean forbiddenCodeHttpResponse) {
		this.forbiddenCodeHttpResponse = forbiddenCodeHttpResponse;
	}

	/**
	 * Default implementation of <code>ThrowableAnalyzer</code> which is capable of also unwrapping
	 * <code>ServletException</code>s.
	 */
	private static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {
		/**
		 * @see org.springframework.security.util.ThrowableAnalyzer#initExtractorMap()
		 */
		protected void initExtractorMap() {
			super.initExtractorMap();

			registerExtractor(ServletException.class, new ThrowableCauseExtractor() {
				public Throwable extractCause(Throwable throwable) {
					ThrowableAnalyzer.verifyThrowableHierarchy(throwable, ServletException.class);
					return ((ServletException) throwable).getRootCause();
				}
			});
		}

	}

}