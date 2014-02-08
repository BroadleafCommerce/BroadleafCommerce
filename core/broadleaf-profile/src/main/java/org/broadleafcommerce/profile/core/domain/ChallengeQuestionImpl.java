/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CHALLENGE_QUESTION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "ChallengeQuestionImpl_baseChallengeQuestion")
public class ChallengeQuestionImpl implements ChallengeQuestion {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ChallengeQuestionId")
    @GenericGenerator(
        name="ChallengeQuestionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ChallengeQuestionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.profile.core.domain.ChallengeQuestionImpl")
        }
    )
    @Column(name = "QUESTION_ID")
    protected Long id;

    @Column(name = "QUESTION", nullable=false)
    @AdminPresentation(friendlyName = "ChallengeQuestionImpl_Challenge_Question", group = "ChallengeQuestionImpl_Customer")
    protected String question;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return question;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((question == null) ? 0 : question.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChallengeQuestionImpl other = (ChallengeQuestionImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (question == null) {
            if (other.question != null)
                return false;
        } else if (!question.equals(other.question))
            return false;
        return true;
    }
}
