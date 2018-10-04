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

import org.broadleafcommerce.common.domain.Auditable;

public interface Customer extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getUsername();

    public void setUsername(String username);

    public String getPassword();

    public void setPassword(String password);

    public boolean isPasswordChangeRequired();

    public void setPasswordChangeRequired(boolean passwordChangeRequired);

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public String getEmailAddress();

    public void setEmailAddress(String emailAddress);

    public Long getChallengeQuestionId();

    public void setChallengeQuestionId(Long challengeQuestionId);

    public String getChallengeAnswer();

    public void setChallengeAnswer(String challengeAnswer);

    public String getUnencodedPassword();

    public void setUnencodedPassword(String unencodedPassword);

    public boolean isReceiveEmail();

    public void setReceiveEmail(boolean receiveEmail);

    public boolean isRegistered();

    public void setRegistered(boolean registered);

    public String getUnencodedChallengeAnswer();

    public void setUnencodedChallengeAnswer(String unencodedChallengeAnswer);

    public Auditable getAuditable();

    public void setAuditable(Auditable auditable);

}
