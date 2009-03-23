package org.broadleafcommerce.profile.domain;

public interface IdGeneration {

    public String getType();

    public void setType(String type);

    public Long getBatchStart();

    public void setBatchStart(Long batchStart);

    public Long getBatchSize();

    public void setBatchSize(Long batchSize);
}
