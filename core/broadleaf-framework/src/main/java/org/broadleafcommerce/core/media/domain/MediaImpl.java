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

package org.broadleafcommerce.core.media.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.core.catalog.domain.UpSaleProductImpl;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.hibernate.annotations.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_MEDIA")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class MediaImpl implements Media {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "MediaId")
    @GenericGenerator(
        name="MediaId",
        strategy="org.broadleafcommerce.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="MediaId"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.media.domain.MediaImpl")
        }
    )
    @Column(name = "MEDIA_ID")
    protected Long id;

    @Column(name = "NAME", nullable = false)
    @Index(name="MEDIA_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName="Media Name", order=1, prominent=true)
    protected String name;

    @Column(name = "URL", nullable = false)
    @Index(name="MEDIA_URL_INDEX", columnNames={"URL"})
    @AdminPresentation(friendlyName="Media Url", order=2, prominent=true, fieldType = SupportedFieldType.ASSET_URL)
    protected String url;

    @Column(name = "LABEL", nullable = false)
    @AdminPresentation(friendlyName="Media Label", order=3, prominent=true)
    protected String label;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MediaImpl other = (MediaImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

}
