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
import java.util.Set;

public interface User extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getUsername();

    public void setUsername(String username);

    public String getPassword();

    public void setPassword(String password);

    public Set<UserRole> getUserRoles();

    public void setUserRoles(Set<UserRole> userRoles);

    public boolean isPasswordChangeRequired();

    public void setPasswordChangeRequired(boolean passwordChangeRequired);

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public String getEmailAddress();

    public void setEmailAddress(String emailAddress);

    public String getChallengeQuestion();

    public void setChallengeQuestion(String challengeQuestion);

    public String getChallengeAnswer();

    public void setChallengeAnswer(String challengeAnswer);

    public String getUnencodedPassword();

    public void setUnencodedPassword(String unencodedPassword);
}
