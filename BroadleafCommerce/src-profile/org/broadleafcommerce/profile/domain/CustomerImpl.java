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
package org.broadleafcommerce.profile.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER", uniqueConstraints = @UniqueConstraint(columnNames = { "USER_NAME" }))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CustomerImpl implements Customer, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CUSTOMER_ID")
    private Long id;

    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;

    @Column(name = "CHALLENGE_QUESTION_ID")
    private Long challengeQuestionId;

    @Column(name = "CHALLENGE_ANSWER")
    private String challengeAnswer;

    @Column(name = "PASSWORD_CHANGE_REQUIRED")
    private boolean passwordChangeRequired = false;

    @Column(name = "RECEIVE_EMAIL")
    private boolean receiveEmail = true;

    @Column(name = "IS_REGISTERED")
    private boolean registered = false;

    @Transient
    private String unencodedPassword;

    @Transient
    private String unencodedChallengeAnswer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordChangeRequired() {
        return passwordChangeRequired;
    }

    public void setPasswordChangeRequired(boolean passwordChangeRequired) {
        this.passwordChangeRequired = passwordChangeRequired;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Long getChallengeQuestionId() {
        return challengeQuestionId;
    }

    public void setChallengeQuestionId(Long challengeQuestionId) {
        this.challengeQuestionId = challengeQuestionId;
    }

    public String getChallengeAnswer() {
        return challengeAnswer;
    }

    public void setChallengeAnswer(String challengeAnswer) {
        this.challengeAnswer = challengeAnswer;
    }

    public String getUnencodedPassword() {
        return unencodedPassword;
    }

    public void setUnencodedPassword(String unencodedPassword) {
        this.unencodedPassword = unencodedPassword;
    }

    public boolean isReceiveEmail() {
        return receiveEmail;
    }

    public void setReceiveEmail(boolean receiveEmail) {
        this.receiveEmail = receiveEmail;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public String getUnencodedChallengeAnswer() {
        return unencodedChallengeAnswer;
    }

    public void setUnencodedChallengeAnswer(String unencodedChallengeAnswer) {
        this.unencodedChallengeAnswer = unencodedChallengeAnswer;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || !(other instanceof CustomerImpl))
            return false;

        CustomerImpl item = (CustomerImpl) other;

        if (username != null ? !username.equals(item.username) : item.username != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result *= 31;

        return result;
    }
}
