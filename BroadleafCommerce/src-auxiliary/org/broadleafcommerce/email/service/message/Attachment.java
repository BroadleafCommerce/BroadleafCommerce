package org.broadleafcommerce.email.service.message;

import javax.activation.DataSource;

public class Attachment {

    private String filename;
    private DataSource dataSource;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
