package org.broadleafcommerce.util;

import java.io.Serializable;

import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RegisterCustomer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Transient
    private final Log logger = LogFactory.getLog(getClass());
    private String challengeAnswer;
    private String challengeQuestion;
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String password;
    private String passwordConfirm;
    private String username;

    public String getChallengeAnswer() {
        return challengeAnswer;
    }

    public String getChallengeQuestion() {
        return challengeQuestion;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Log getLogger() {
        return logger;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public String getUsername() {
        return username;
    }

    public void setChallengeAnswer(String challengeAnswer) {
        this.challengeAnswer = challengeAnswer;
    }

    public void setChallengeQuestion(String challengeQuestion) {
        this.challengeQuestion = challengeQuestion;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
