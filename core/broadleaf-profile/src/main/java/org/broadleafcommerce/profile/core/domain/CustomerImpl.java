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

package org.broadleafcommerce.profile.core.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.audit.AuditableListener;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

@Entity
@EntityListeners(value = { AuditableListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER", uniqueConstraints = @UniqueConstraint(columnNames = { "USER_NAME" }))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "baseCustomer")
public class CustomerImpl implements Customer {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CUSTOMER_ID")
    @AdminPresentation(friendlyName="Customer Id", group="Primary Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Embedded
    protected Auditable auditable = new Auditable();

    @Column(name = "USER_NAME")
    @AdminPresentation(friendlyName="UserName", order=1, group="Customer", prominent=true)
    protected String username;

    @Column(name = "PASSWORD")
    @AdminPresentation(excluded = true)
    protected String password;

    @Column(name = "FIRST_NAME")
    @AdminPresentation(friendlyName="First Name", order=2, group="Customer", prominent=true)
    protected String firstName;

    @Column(name = "LAST_NAME")
    @AdminPresentation(friendlyName="Last Name", order=3, group="Customer", prominent=true)
    protected String lastName;

    @Column(name = "EMAIL_ADDRESS")
    @Index(name="CUSTOMER_EMAIL_INDEX", columnNames={"EMAIL_ADDRESS"})
    @AdminPresentation(friendlyName="Email Address", order=4, group="Customer")
    protected String emailAddress;

    @ManyToOne(targetEntity = ChallengeQuestionImpl.class)
    @JoinColumn(name = "CHALLENGE_QUESTION_ID")
    @Index(name="CUSTOMER_CHALLENGE_INDEX", columnNames={"CHALLENGE_QUESTION_ID"})
    @AdminPresentation(friendlyName="Challenge Question", group="Customer", excluded = true, visibility = VisibilityEnum.GRID_HIDDEN)
    protected ChallengeQuestion challengeQuestion;

    @Column(name = "CHALLENGE_ANSWER")
    @AdminPresentation(excluded = true)
    protected String challengeAnswer;

    @Column(name = "PASSWORD_CHANGE_REQUIRED")
    @AdminPresentation(excluded = true)
    protected boolean passwordChangeRequired = false;

    @Column(name = "RECEIVE_EMAIL")
    @AdminPresentation(friendlyName="Customer Receive Email", group="Customer")
    protected boolean receiveEmail = true;

    @Column(name = "IS_REGISTERED")
    @AdminPresentation(friendlyName="Customer Registered", group="Customer")
    protected boolean registered = false;

    @ManyToOne(targetEntity = LocaleImpl.class)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName="Customer Locale", group="Customer", excluded = true, visibility = VisibilityEnum.GRID_HIDDEN)
    protected Locale customerLocale;

    @Transient
    protected String unencodedPassword;

    @Transient
    protected String unencodedChallengeAnswer;
    
    @Transient
    protected boolean anonymous;

    @Transient
    protected boolean cookied;

    @Transient
    protected boolean loggedIn;

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

    public ChallengeQuestion getChallengeQuestion() {
        return challengeQuestion;
    }

    public void setChallengeQuestion(ChallengeQuestion challengeQuestion) {
        this.challengeQuestion = challengeQuestion;
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

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public boolean isCookied() {
        return cookied;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
        if (anonymous) {
            cookied = false;
            loggedIn = false;
        }
    }

    public void setCookied(boolean cookied) {
        this.cookied = cookied;
        if (cookied) {
            anonymous = false;
            loggedIn = false;
        }
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
        if (loggedIn) {
            anonymous = false;
            cookied = false;
        }
    }

    public Locale getCustomerLocale() {
        return customerLocale;
    }

    public void setCustomerLocale(Locale customerLocale) {
        this.customerLocale = customerLocale;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomerImpl other = (CustomerImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

}
