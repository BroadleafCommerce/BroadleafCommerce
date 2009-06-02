package org.broadleafcommerce.profile.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ID_GENERATION")
public class IdGenerationImpl implements IdGeneration, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_TYPE")
    private String type;

    @Column(name = "BATCH_START")
    private Long batchStart;

    @Column(name = "BATCH_SIZE")
    private Long batchSize;

    @Version
    private Integer version;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getBatchStart() {
        return batchStart;
    }

    public void setBatchStart(Long batchStart) {
        this.batchStart = batchStart;
    }

    public Long getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Long batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getVersion() {
        return version;
    }
}