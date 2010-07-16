/*
 * An XML document type.
 * Localname: RateV2Response
 * Namespace: 
 * Java type: noNamespace.RateV2ResponseDocument
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * A document containing one RateV2Response(@) element.
 *
 * This is a complex type.
 */
public class RateV2ResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.RateV2ResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public RateV2ResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName RATEV2RESPONSE$0 = 
        new javax.xml.namespace.QName("", "RateV2Response");
    
    
    /**
     * Gets the "RateV2Response" element
     */
    public noNamespace.RateV2ResponseType getRateV2Response()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV2ResponseType target = null;
            target = (noNamespace.RateV2ResponseType)get_store().find_element_user(RATEV2RESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "RateV2Response" element
     */
    public boolean isNilRateV2Response()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV2ResponseType target = null;
            target = (noNamespace.RateV2ResponseType)get_store().find_element_user(RATEV2RESPONSE$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "RateV2Response" element
     */
    public void setRateV2Response(noNamespace.RateV2ResponseType rateV2Response)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV2ResponseType target = null;
            target = (noNamespace.RateV2ResponseType)get_store().find_element_user(RATEV2RESPONSE$0, 0);
            if (target == null)
            {
                target = (noNamespace.RateV2ResponseType)get_store().add_element_user(RATEV2RESPONSE$0);
            }
            target.set(rateV2Response);
        }
    }
    
    /**
     * Appends and returns a new empty "RateV2Response" element
     */
    public noNamespace.RateV2ResponseType addNewRateV2Response()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV2ResponseType target = null;
            target = (noNamespace.RateV2ResponseType)get_store().add_element_user(RATEV2RESPONSE$0);
            return target;
        }
    }
    
    /**
     * Nils the "RateV2Response" element
     */
    public void setNilRateV2Response()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV2ResponseType target = null;
            target = (noNamespace.RateV2ResponseType)get_store().find_element_user(RATEV2RESPONSE$0, 0);
            if (target == null)
            {
                target = (noNamespace.RateV2ResponseType)get_store().add_element_user(RATEV2RESPONSE$0);
            }
            target.setNil();
        }
    }
}
