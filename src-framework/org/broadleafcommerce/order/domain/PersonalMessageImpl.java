package org.broadleafcommerce.order.domain;

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
@Table(name = "BLC_PERSONAL_MESSAGE")
public class PersonalMessageImpl implements PersonalMessage, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PersonalMessageId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PersonalMessageId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PersonalMessageImpl", allocationSize = 1)
    @Column(name = "PERSONAL_MESSAGE_ID")
    private Long id;

    @Column(name = "MESSAGE_TO")
    private String messageTo;

    @Column(name = "MESSAGE_FROM")
    private String messageFrom;

    @Column(name = "MESSAGE")
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
