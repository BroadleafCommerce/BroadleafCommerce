/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.util;

import org.apache.commons.lang3.StringUtils;


public class TableCreator {
    
    protected Col[] cols;
    protected int rowWidth;
    protected StringBuilder sb;
    protected int globalRowHeaderWidth = 20;
    
    public TableCreator(Col[] cols) {
        this.cols = cols;
        this.rowWidth = calculateRowWidth();
        this.sb = new StringBuilder("\r\n");
        
        addSeparator();
        addRow(cols);
        addSeparator();
    }
    
    protected int calculateRowWidth() {
        int length = 1;
        for (Col col : cols) {
            length += col.width + 3;
        }
        return length;
    }
    
    public TableCreator addSeparator() {
        sb.append(StringUtils.leftPad("", rowWidth, '-')).append("\r\n");
        return this;
    }

    public TableCreator addRow(Col[] cols) {
        String[] data = new String[cols.length];
        for (int i = 0; i < cols.length; i++) {
            data[i] = cols[i].title;
        }
        return addRow(data);
    }
    
    public TableCreator addRow(Object[] data) {
        if (data.length != cols.length) {
            throw new IllegalArgumentException("Wrong number of data elements. Needed [" + cols.length + "] " +
            		"but received [" + data.length + "]");
        }
        
        sb.append('|');
        
        for (int i = 0; i < data.length; i++) {
            String trimmed = StringUtils.left(String.valueOf(data[i]), cols[i].width);
            sb.append(' ').append(StringUtils.rightPad(trimmed, cols[i].width)).append(" |");
        }
        
        sb.append("\r\n");
        return this;
    }

    
    public TableCreator addRow(String rowHeader, Object rowData) {
        String trimmed = StringUtils.left(rowHeader, globalRowHeaderWidth);
        sb.append("| ")
            .append(StringUtils.rightPad(trimmed, globalRowHeaderWidth))
            .append(StringUtils.rightPad(String.valueOf(rowData), rowWidth - globalRowHeaderWidth - 3))
            .append("|\r\n");
        return this;
    }
    
    public TableCreator withGlobalRowHeaderWidth(int width) {
        this.globalRowHeaderWidth = width;
        return this;
    }
    
    public String toString() {
        return sb.toString();
    }
    
    public static class Col {
        
        String title;
        int width;
        
        public Col(String title) {
            this.title = title;
            this.width = title.length();
        }

        public Col(String title, int width) {
            this.title = title;
            this.width = width;
        }

    }

}
