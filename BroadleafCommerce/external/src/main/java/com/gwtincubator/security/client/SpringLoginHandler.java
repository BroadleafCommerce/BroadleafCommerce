/**
 * 
 */
package com.gwtincubator.security.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

/**
 * LoginHandler that post information to Spring Security to log the user.
 * <br>
 * Use this class in conjunction with <code>GWTAuthenticationProcessingFilter</code> in order to benefit from the 4xx status code returned in case of a failure (wrong credential).
 *
 * @author David MARTIN
 */
public abstract class SpringLoginHandler implements ClickHandler {

	private static final String DEFAULT_SPRING_USERNAME_FIELD = "j_username";

	private static final String DEFAULT_SPRING_PASSWORD_FIELD = "j_password";

	private static final String DEFAULT_SPRING_REMEMBERME_FIELD = "_spring_security_remember_me";

	private static final String DEFAULT_SPRING_LOGIN_URL = "j_spring_security_check";

	private String springUserNameField = DEFAULT_SPRING_USERNAME_FIELD;

	private String springPasswordField = DEFAULT_SPRING_PASSWORD_FIELD;

	private String springRememberMeField = DEFAULT_SPRING_REMEMBERME_FIELD;

	private String springLoginUrl = GWTUtil.getContextUrl() + DEFAULT_SPRING_LOGIN_URL;

	public abstract String getLogin();

	public abstract String getPassword();

	public abstract boolean getRememberMe();

	/**
	 * On success, do something.
	 */
	public void onSuccessLogin() {
		
	}

	/**
	 * On failure, do something.
	 * Default behavior is to display an alert.
	 * @param request request
	 */
	public void onFailureLogin(final Request request) {
		Window.alert("Log in failure");
	}

	/**
	 * On error, do something.
	 * @param exception exception
	 */
	public void onErrorLogin(final Throwable exception) {
		
	}

	public void onClick(final ClickEvent event) {

		final String url = getFullLoginUrl();
		final RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(final Request request, final Throwable exception) {
					onErrorLogin(exception);
				}

				public void onResponseReceived(final Request request, final Response response) {
					if (200 == response.getStatusCode()) {
						onSuccessLogin();
					} else {
						onFailureLogin(request);
					}
				}
			});
		} catch (RequestException e) {
			onErrorLogin(e);
		}
	}

	/**
	 * Return the fully qualified login URL for Spring Security.
	 * Can be overridden if the URL construction is different.
	 * @return the fully qualified login URL for Spring Security
	 */
	public String getFullLoginUrl() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getSpringLoginUrl());
		sb.append("?");
		sb.append(getSpringUserNameField());
		sb.append("=");
		sb.append(getLogin());
		sb.append("&");
		sb.append(getSpringPasswordField());
		sb.append("=");
		sb.append(getPassword());
		if (getRememberMe()) {
			sb.append("&");
			sb.append(getSpringRememberMeField());
			sb.append("=true");
		}
		return sb.toString();
	}

	/**
	 * @return the springUserNameField
	 */
	public String getSpringUserNameField() {
		return springUserNameField;
	}

	/**
	 * @param springUserNameField the springUserNameField to set
	 */
	public void setSpringUserNameField(String springUserNameField) {
		this.springUserNameField = springUserNameField;
	}

	/**
	 * @return the springPasswordField
	 */
	public String getSpringPasswordField() {
		return springPasswordField;
	}

	/**
	 * @param springPasswordField the springPasswordField to set
	 */
	public void setSpringPasswordField(String springPasswordField) {
		this.springPasswordField = springPasswordField;
	}

	/**
	 * @return the springRememberMeField
	 */
	public String getSpringRememberMeField() {
		return springRememberMeField;
	}

	/**
	 * @param springRememberMeField the springRememberMeField to set
	 */
	public void setSpringRememberMeField(String springRememberMeField) {
		this.springRememberMeField = springRememberMeField;
	}

	/**
	 * @return the springLoginUrl
	 */
	public String getSpringLoginUrl() {
		return springLoginUrl;
	}

	/**
	 * @param springLoginUrl the springLoginUrl to set
	 */
	public void setSpringLoginUrl(String springLoginUrl) {
		this.springLoginUrl = springLoginUrl;
	}

}
