/**
 * 
 */
package com.gwtincubator.security.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Just a sample widget to illustrate how to use SpringLoginHandler.
 * @author David MARTIN
 * 
 */
public class SpringSecurityLoginWidget extends FlexTable {

	private static final String DEFAULT_USERNAME_LABEL = "Login";

	private static final String DEFAULT_PASSWORD_LABEL = "Password";

	private static final String DEFAULT_REMEMBERME_LABEL = "Remember me ?";

	private static final String DEFAULT_SUBMIT_LABEL = "Log in";

	private String usernameLabelText = DEFAULT_USERNAME_LABEL;

	private String passwordLabelText = DEFAULT_PASSWORD_LABEL;

	private String remembermeLabelText = DEFAULT_REMEMBERME_LABEL;

	private String submitButtonText = DEFAULT_SUBMIT_LABEL;

	private TextBox usernameTextbox;

	private PasswordTextBox passwordTextbox;

	private Label errorMessageLabel;

	private CheckBox remembermeCheckbox;

	private Button submitButton;

	private SpringLoginHandler springLoginHandler;

	public SpringSecurityLoginWidget() {
		super();
		init();
	}

	private void init() {
		this.setWidget(0, 0, new Label(getUsernameLabelText()));
		this.setWidget(1, 0, new Label(getPasswordLabelText()));
		this.setWidget(0, 1, getUsernameTextbox());
		this.setWidget(1, 1, getPasswordTextbox());
		this.setWidget(2, 0, getRemembermeCheckbox());
		this.getFlexCellFormatter().setColSpan(3, 0, 2);
		this.getFlexCellFormatter().setAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		this.setWidget(3, 0, getSubmitButton());
		this.getFlexCellFormatter().setColSpan(4, 0, 2);
		this.setWidget(4, 0, getErrorMessageLabel());
	}

	private Button getSubmitButton() {
		if (submitButton == null) {
			submitButton = new Button(getSubmitButtonText());
			submitButton.addClickHandler(getSpringLoginHandler());
		}
		return submitButton;
	}

	private CheckBox getRemembermeCheckbox() {
		if (remembermeCheckbox == null) {
			remembermeCheckbox = new CheckBox();
			remembermeCheckbox.setValue(Boolean.TRUE);
			remembermeCheckbox.setText(getRemembermeLabelText());
		}
		return remembermeCheckbox;
	}

	private SpringLoginHandler getSpringLoginHandler() {
		if (springLoginHandler == null) {
			springLoginHandler = new SpringLoginHandler() {

				@Override
				public String getLogin() {
					return getUsernameTextbox().getText();
				}

				@Override
				public String getPassword() {
					return getPasswordTextbox().getText();
				}

				@Override
				public boolean getRememberMe() {
					return false;
				}

				@Override
				public void onSuccessLogin() {
					Window.alert("Great, I'm logged !");
					getErrorMessageLabel().setVisible(false);
					getErrorMessageLabel().setText("");
				}

				@Override
				public void onFailureLogin(Request request) {
					Window.alert("Oh no, I'm NOT logged !");
					getErrorMessageLabel().setText("Oh no, I'm NOT logged !");
					getErrorMessageLabel().setVisible(true);
				}

				@Override
				public void onErrorLogin(Throwable exception) {
					Window.alert("Trouble to connect to back end server ?!? " + exception.getLocalizedMessage());
					getErrorMessageLabel().setText("Trouble to connect to back end server !");
					getErrorMessageLabel().setVisible(true);
				}
			};
		}
		return springLoginHandler;
	}

	private TextBox getUsernameTextbox() {
		if (this.usernameTextbox == null) {
			usernameTextbox = new TextBox();
			usernameTextbox.setWidth("150px");
		}
		return usernameTextbox;
	}

	private PasswordTextBox getPasswordTextbox() {
		if (this.passwordTextbox == null) {
			passwordTextbox = new PasswordTextBox();
			passwordTextbox.setWidth("150px");
		}
		return passwordTextbox;
	}

	private Label getErrorMessageLabel() {
		if (errorMessageLabel == null) {
			errorMessageLabel = new Label();
			errorMessageLabel.setStyleName("login-error-label");
		}
		return errorMessageLabel;
	}

	public String getUsernameLabelText() {
		return usernameLabelText;
	}

	public void setUsernameLabelText(String usernameLabel) {
		this.usernameLabelText = usernameLabel;
	}

	public String getPasswordLabelText() {
		return passwordLabelText;
	}

	public void setPasswordLabelText(String passwordLabel) {
		this.passwordLabelText = passwordLabel;
	}

	/**
	 * @return the remembermeLabelText
	 */
	public String getRemembermeLabelText() {
		return remembermeLabelText;
	}

	/**
	 * @param remembermeLabelText the remembermeLabelText to set
	 */
	public void setRemembermeLabelText(String remembermeLabelText) {
		this.remembermeLabelText = remembermeLabelText;
	}

	/**
	 * @return the submitButtonText
	 */
	public String getSubmitButtonText() {
		return submitButtonText;
	}

	/**
	 * @param submitButtonText the submitButtonText to set
	 */
	public void setSubmitButtonText(String submitButtonText) {
		this.submitButtonText = submitButtonText;
	}

}
