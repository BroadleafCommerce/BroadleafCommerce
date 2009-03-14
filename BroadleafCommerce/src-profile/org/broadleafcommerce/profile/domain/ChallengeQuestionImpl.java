package org.broadleafcommerce.profile.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CHALLENGE_QUESTION")
public class ChallengeQuestionImpl implements ChallengeQuestion, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ChallengeQuestionId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ChallengeQuestionId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CustomerImpl", allocationSize = 1)
    @Column(name = "QUESTION_ID")
    private Long id;

    @Column(name = "QUESTION")
    private String question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String toString() {
        return question;
    }
}
