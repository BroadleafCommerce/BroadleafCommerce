package org.broadleafcommerce.core.media.domain.common;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.hibernate.annotations.Index;

@MappedSuperclass
public abstract class MediaMappedSuperclass implements Media {

	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "MediaId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "MediaId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "MediaId", allocationSize = 50)
    @Column(name = "MEDIA_ID")
    protected Long id;

    @Column(name = "NAME", nullable = false)
    @Index(name="MEDIA_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName="Media Name", order=1, prominent=true)
    protected String name;

    @Column(name = "URL", nullable = false)
    @Index(name="MEDIA_URL_INDEX", columnNames={"URL"})
    @AdminPresentation(friendlyName="Media Url", order=2, prominent=true)
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
}
